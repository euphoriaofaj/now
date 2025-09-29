package com.morago.backend.service.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RateLimitServiceImpl implements RateLimitService {

    private static final int LOGIN_LIMIT = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(1);

    private static final int REFRESH_LIMIT = 10;
    private static final Duration REFRESH_WINDOW = Duration.ofMinutes(5);

    private static final class CounterWindow {
        Instant windowStart;
        int count;

        CounterWindow(Instant start, int c) {
            this.windowStart = start;
            this.count = c;
        }
    }

    private final Map<String, CounterWindow> store = new ConcurrentHashMap<>();

    @Override
    public Result tryConsumeLogin(String key) {
        return tryConsume("login:" + key, LOGIN_LIMIT, LOGIN_WINDOW);
    }

    @Override
    public Result tryConsumeRefresh(String key) {
        return tryConsume("refresh:" + key, REFRESH_LIMIT, REFRESH_WINDOW);
    }

    private Result tryConsume(String namespacedKey, int limit, Duration window) {
        Instant now = Instant.now();
        AtomicBoolean consumed = new AtomicBoolean(false);

        CounterWindow cw = store.compute(namespacedKey, (k, old) -> {
            if (old == null) {
                consumed.set(true);
                return new CounterWindow(now, 1);
            }
            if (Duration.between(old.windowStart, now).compareTo(window) >= 0) {
                consumed.set(true);
                return new CounterWindow(now, 1);
            }
            if (old.count < limit) {
                old.count += 1;
                consumed.set(true);
                return old;
            }
            consumed.set(false);
            return old;
        });

        long elapsedSec = Math.min(window.getSeconds(),
                Math.max(0, Duration.between(cw.windowStart, now).getSeconds()));
        long windowLeftSec = Math.max(0, window.getSeconds() - elapsedSec);
        int remaining = Math.max(0, limit - cw.count);

        if (consumed.get()) {
            return Result.allowed(remaining);
        } else {
            long retryAfter = Math.max(1, windowLeftSec);
            return Result.blocked(retryAfter, remaining);
        }
    }
}