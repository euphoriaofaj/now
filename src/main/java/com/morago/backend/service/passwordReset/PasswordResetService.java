package com.morago.backend.service.passwordReset;

public interface PasswordResetService {
    void startReset(String phone);
    String verifyCode(String phone, Integer code);
    void confirm(String token, String newPassword);
}
