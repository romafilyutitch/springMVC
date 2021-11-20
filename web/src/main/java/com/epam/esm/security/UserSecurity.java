package com.epam.esm.security;

import com.epam.esm.model.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserService userService;

    @Autowired
    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean hasUserId(Authentication authentication, long userId) {
        String username = (String) authentication.getPrincipal();
        User foundUser = userService.findByUsername(username);
        boolean isAdmin = foundUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        } else {
            return foundUser.getId() == userId;
        }
    }
}
