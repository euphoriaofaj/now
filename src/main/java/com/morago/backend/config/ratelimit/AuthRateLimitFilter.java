package com.morago.backend.config.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morago.backend.service.ratelimit.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return !(p.endsWith("/auth/login") || p.endsWith("/auth/refresh"));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(req);

        String clientIp = extractClientIp(wrapped);

        RateLimitService.Result result;
        String key;

        if (path.endsWith("/auth/login")) {
            String username = tryExtractUsername(wrapped);
            key = clientIp + "|" + (username == null ? "unknown" : username);
            log.debug("[RATE] /auth/login ip='{}' username='{}' => key='{}'", clientIp, username, key);
            result = rateLimitService.tryConsumeLogin(key);
        } else {
            String refreshHash = tryExtractRefreshHash(wrapped);
            key = clientIp + "|" + (refreshHash == null ? "unknown" : refreshHash);
            log.debug("[RATE] /auth/refresh ip='{}' tokenHash='{}' => key='{}'", clientIp, refreshHash, key);
            result = rateLimitService.tryConsumeRefresh(key);
        }

        if (!result.allowed()) {
            log.warn("[RATE] BLOCK path='{}' key='{}' retryAfter={}s", path, key, result.retryAfterSeconds());
            res.setStatus(429);
            res.setHeader("Retry-After", String.valueOf(result.retryAfterSeconds()));
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded. Please retry later.\"}");
            return; // важно
        }

        res.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
        log.debug("[RATE] ALLOW path='{}' key='{}' remaining={}", path, key, result.remaining());
        chain.doFilter(wrapped, res);
    }

    private String tryExtractUsername(CachedBodyHttpServletRequest req) {
        try {
            JsonNode root = objectMapper.readTree(req.getCachedBody());
            JsonNode n = root.get("username"); // если фронт шлёт другое поле — поменяй здесь
            return (n != null && !n.isNull()) ? n.asText().trim() : null;
        } catch (Exception ignore) {}
        return null;
    }

    private String tryExtractRefreshHash(CachedBodyHttpServletRequest req) {
        try {
            JsonNode root = objectMapper.readTree(req.getCachedBody());
            JsonNode n = root.get("refreshToken");
            return (n != null && !n.isNull()) ? sha256Hex(n.asText()) : null;
        } catch (Exception ignore) {}
        return null;
    }

    private String extractClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return comma > 0 ? xff.substring(0, comma).trim() : xff.trim();
        }
        String rip = req.getHeader("X-Real-IP");
        if (rip != null && !rip.isBlank()) return rip.trim();
        return req.getRemoteAddr();
    }

    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return "hash_error";
        }
    }
}