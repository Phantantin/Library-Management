package com.zou.service;

import com.zou.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final UserService users;
    public void ownerOrAdmin(Long userId) {
        try {
            var actor = users.getCurrentUser();
            if (!actor.getId().equals(userId) && actor.getRole()!=UserRole.ROLE_ADMIN) throw new AccessDeniedException("Access denied");
        } catch (AccessDeniedException e) { throw e; }
        catch (Exception e) { throw new AccessDeniedException("Authentication required"); }
    }
}
