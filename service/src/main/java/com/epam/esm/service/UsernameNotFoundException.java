package com.epam.esm.service;

public class UsernameNotFoundException extends Exception {
    private final String username;

    public UsernameNotFoundException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
