package com.morago.backend.service.ratelimit;

public interface RateLimitService {

    Result tryConsumeLogin(String key);

    Result tryConsumeRefresh(String key);


    record Result(boolean allowed, long retryAfterSeconds, int remaining) {

    public static Result allowed(int remaining) {
            return new Result(true, 0, remaining);
        }

        public static Result blocked(long retryAfterSeconds, int remaining) {
            return new Result(false, retryAfterSeconds, remaining);
        }
    }
}
