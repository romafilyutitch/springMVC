package com.epam.esm.security;

import com.epam.esm.model.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity")
public class UserSecurity {

    private final UserService userService;

    @Autowired
    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean hasUserId(Authentication authentication, long userId) throws UsernameNotFoundException {
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

    public boolean canOrder(Authentication authentication, User orderUser) throws UsernameNotFoundException {
        String username = (String) authentication.getPrincipal();
        User foundUser = userService.findByUsername(username);
        boolean isAdmin = foundUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        } else {
            long foundUserId = foundUser.getId();
            long orderUserId = orderUser.getId();
            return foundUserId == orderUserId;
        }
    }
}
