package com.epam.esm.validation;

import com.epam.esm.model.User;

/**
 * Validation interface to save or update
 */
public interface UserValidator {
    /**
     * Validate user to save or update
     * @param user that need to be saved
     * @throws InvalidUserException if user is invalid
     */
    void validate(User user) throws InvalidUserException;
}
