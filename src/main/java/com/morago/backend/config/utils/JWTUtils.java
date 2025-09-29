package com.morago.backend.config.utils;

import com.morago.backend.entity.enumFiles.TokenType;
import com.morago.backend.exception.token.ExpireJwtTokenException;
import com.morago.backend.exception.token.InvalidJwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTUtils {
    private static final long CLOCK_SKEW_SEC = 60;

    private final JWTProperties jwtProperties;
    public JWTUtils(JWTProperties jwtProperties) { this.jwtProperties = jwtProperties; }

    private Key getAccessSigningKey() {
        byte[] key = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtProperties.getAccessSecret());
        return Keys.hmacShaKeyFor(key);
    }
    private Key getRefreshSigningKey() {
        byte[] key = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtProperties.getRefreshSecret());
        return Keys.hmacShaKeyFor(key);
    }
    private Key getSigningKey(TokenType type) {
        return (type == TokenType.ACCESS) ? getAccessSigningKey() : getRefreshSigningKey();
    }

    @PostConstruct
    void validateConfig() {
        requireNonEmpty(jwtProperties.getIssuer(), "issuer");
        requireNonEmpty(jwtProperties.getAccessSecret(), "accessSecret");
        requireNonEmpty(jwtProperties.getRefreshSecret(), "refreshSecret");

        getAccessSigningKey();
        getRefreshSigningKey();

        if (jwtProperties.getAccessExpirationMs() <= 0 || jwtProperties.getRefreshExpirationMs() <= 0) {
            throw new IllegalStateException("JWT expiration must be > 0");
        }
        if (jwtProperties.getAccessSecret().equals(jwtProperties.getRefreshSecret())) {
            throw new IllegalStateException("Access and Refresh secrets must be different");
        }
    }
    private static void requireNonEmpty(String v, String name) {
        if (v == null || v.isBlank()) throw new IllegalStateException("Missing JWT property: " + name);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getAccessExpirationMs());

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .claim("token_type", TokenType.ACCESS.name())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getRefreshExpirationMs());

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .claim("token_type", TokenType.REFRESH.name())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token, TokenType type) {
        return parse(token, type).getSubject();
    }

    public Set<String> getRolesFromToken(String token, TokenType type) {
        Object raw = parse(token, type).get("roles");
        if (raw instanceof List<?> list) {
            return list.stream().filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public void validateToken(String token, TokenType type) {
        parse(token, type);
    }

    private Claims parse(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .requireIssuer(jwtProperties.getIssuer())
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SEC)
                    .setSigningKey(getSigningKey(expectedType))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String actualType = claims.get("token_type", String.class);
            if (actualType == null || !actualType.equalsIgnoreCase(expectedType.name())) {
                throw new InvalidJwtTokenException();
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new ExpireJwtTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException();
        }
    }

    public static String stripBearer(String token) {
        if (token == null) return null;
        token = token.trim();
        return token.regionMatches(true, 0, "Bearer ", 0, 7) ? token.substring(7).trim() : token;
    }
}
