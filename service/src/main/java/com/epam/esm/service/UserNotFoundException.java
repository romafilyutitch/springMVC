package com.epam.esm.service;

/**
 * Resource not found exception. Occurs if there is no saved founded user
 */
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(long id) {
        super(id);
    }
}
