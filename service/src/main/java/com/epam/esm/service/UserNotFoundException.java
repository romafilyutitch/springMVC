package com.epam.esm.service;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(long id) {
        super(id);
    }
}
