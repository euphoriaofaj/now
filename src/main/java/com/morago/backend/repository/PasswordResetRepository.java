package com.morago.backend.repository;

import com.morago.backend.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findTopByPhoneOrderByCreatedAtDesc(String phone);

    Optional<PasswordReset> findByToken(String token);

    Optional<PasswordReset> findByPhoneAndResetCodeAndUsedFalseAndExpiresAtAfter(
            String phone, Integer resetCode, LocalDateTime now);

    void deleteByUser(com.morago.backend.entity.User user);
}