package com.epam.esm.validation;

import com.epam.esm.model.User;
import org.springframework.stereotype.Component;

/**
 * Validated user fields values.
 * User name must be not empty
 * User surname must be not empty
 */
@Component
public class UserFieldsValidator implements UserValidator {
    /**
     * Validates user to save or update
     *
     * @param user that need to be validated
     * @throws InvalidUserException if user is invalid
     */
    @Override
    public void validate(User user) throws InvalidUserException {
        System.out.println(user);
        String name = user.getUsername();
        String password = user.getPassword();
        if (name == null || name.isEmpty()) {
            throw new InvalidUserException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidUserException();
        }
    }
}
