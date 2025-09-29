package com.morago.backend.service.profile;

import com.morago.backend.dto.user.UserProfileDto;
import com.morago.backend.entity.UserProfile;

import java.util.List;

public interface UserProfileService {
    UserProfile findByIdOrThrow(Long profileId);
    UserProfile findByUserIdOrThrow(Long userId);
    List<UserProfileDto> getAllUserProfiles();
}