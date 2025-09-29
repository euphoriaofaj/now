package com.morago.backend.service.passwordReset;

import com.morago.backend.entity.PasswordReset;
import com.morago.backend.entity.User;
import com.morago.backend.exception.passwordReset.MissingResetFieldsException;
import com.morago.backend.exception.passwordReset.MissingVerifyFieldsException;
import com.morago.backend.repository.PasswordResetRepository;
import com.morago.backend.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.morago.backend.exception.passwordReset.InvalidResetTokenException;
import com.morago.backend.exception.passwordReset.InvalidResetCodeException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService{
    private final PasswordResetRepository passwordResetRepository;
    private final UserService userService;

    private static final Duration EXPIRES_IN = Duration.ofMinutes(15);
    private static final SecureRandom RNG = new SecureRandom();

    @Transactional
    public void startReset(String phone) {
        final User user;
        try {
            user = userService.findByUsernameOrThrow(phone);
        } catch (RuntimeException e) {
            return;
        }

        int code = 1000 + RNG.nextInt(9000);
        String token = UUID.randomUUID().toString().replace("-", "");

        PasswordReset pr = PasswordReset.builder()
                .user(user)
                .phone(phone)
                .resetCode(code)
                .token(token)
                .expiresAt(LocalDateTime.now().plus(EXPIRES_IN))
                .used(false)
                .build();

        passwordResetRepository.save(pr);

        // TODO: SMS notification?
        log.info("[DEV ONLY] Password reset code for {} is {}", phone, code);
    }

    @Transactional
    public String verifyCode(String phone, Integer code) {
        if (phone == null || phone.isBlank() || code == null) {
            throw new MissingVerifyFieldsException();
        }

        PasswordReset pr = passwordResetRepository
                .findByPhoneAndResetCodeAndUsedFalseAndExpiresAtAfter(phone, code, LocalDateTime.now())
                .orElseThrow(InvalidResetCodeException::new);

        return pr.getToken();
    }

    @Transactional
    public void confirm(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new MissingResetFieldsException();
        }

        PasswordReset pr = passwordResetRepository.findByToken(token)
                .orElseThrow(InvalidResetTokenException::new);

        if (Boolean.TRUE.equals(pr.getUsed()) || pr.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException();
        }

        User user = pr.getUser();
        userService.setPasswordWithoutOldCheck(user.getId(), newPassword, newPassword);
        pr.setUsed(true);
    }
}
