package com.epam.esm.service;

public class UsernameExistsException extends Exception {
    private final String username;

    public UsernameExistsException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
