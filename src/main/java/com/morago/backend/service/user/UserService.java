package com.morago.backend.service.user;

import com.morago.backend.dto.password.ChangePasswordRequestDto;
import com.morago.backend.dto.user.UserRegistrationRequestDto;
import com.morago.backend.dto.user.UserRegistrationResponseDto;
import com.morago.backend.dto.user.UserUpdateProfileRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileResponseDto;
import com.morago.backend.entity.User;

public interface UserService {
    User findByUsernameOrThrow(String username);
    User findByIdOrThrow(Long id);
    User getCurrentUser();
    Long getCurrentUserId();
    User findByIdForUpdateOrThrow(Long id);

    UserRegistrationResponseDto registerUser(UserRegistrationRequestDto dto);
    UserRegistrationResponseDto registerTranslator(UserRegistrationRequestDto dto);


    //for admin
    void changePassword(Long userId, ChangePasswordRequestDto dto);
    UserUpdateProfileResponseDto updateProfile(Long userId, UserUpdateProfileRequestDto dto);

    //for 'me'
    UserUpdateProfileResponseDto updateMyProfile(UserUpdateProfileRequestDto dto);
    void changeMyPassword(ChangePasswordRequestDto dto);
    void deleteMyProfile();
    void setPasswordWithoutOldCheck(Long userId, String newPassword, String confirmPassword);

    UserRegistrationResponseDto createUser(UserRegistrationRequestDto dto);
    void deleteUser(Long id);
}