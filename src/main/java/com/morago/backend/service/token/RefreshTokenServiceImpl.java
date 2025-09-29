package com.morago.backend.service.token;

import com.morago.backend.config.utils.JWTProperties;
import com.morago.backend.config.utils.JWTUtils;
import com.morago.backend.dto.tokens.JWTResponse;
import com.morago.backend.entity.RefreshToken;
import com.morago.backend.entity.User;
import com.morago.backend.repository.RefreshTokenRepository;
import com.morago.backend.service.profile.TranslatorProfileService;
import com.morago.backend.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JWTProperties jwtProperties;
    private final JWTUtils jwtUtils;
    private final TranslatorProfileService translatorProfileService;

    @Override
    public void createRefreshToken(String username, String jwtTokenString) {
        User user = userService.findByUsernameOrThrow(username);
        RefreshToken token = RefreshToken.builder()
                .token(jwtTokenString)
                .user(user)
                .expirationTime(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshExpirationMs())))
                .build();

        refreshTokenRepository.save(token);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    @Transactional
    public JWTResponse refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = getValidTokenOrThrow(requestRefreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);

        deleteByToken(requestRefreshToken);
        createRefreshToken(user.getUsername(), newRefreshToken);

        return new JWTResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String username, String refreshTokenNullable) {
        User user = userService.findByUsernameOrThrow(username);

        if (refreshTokenNullable == null || refreshTokenNullable.isBlank()) {
            refreshTokenRepository.deleteByUser(user);
        } else {
            refreshTokenRepository.deleteByTokenAndUser(refreshTokenNullable, user);
        }

        translatorProfileService.setOnlineStatus(user, false);
    }
}
