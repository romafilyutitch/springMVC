package com.epam.esm.validation;

import com.epam.esm.model.User;

public interface UserValidator {
    void validate(User user) throws InvalidUserException;
}
