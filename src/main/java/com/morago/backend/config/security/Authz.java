package com.morago.backend.config.security;

import com.morago.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component("authz")
@RequiredArgsConstructor
public class Authz {
    private final UserRepository userRepository;

    public boolean isSelf(Long targetUserId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || targetUserId == null) return false;

        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) return false;

        return userRepository.existsByIdAndUsername(targetUserId, username);
    }
}