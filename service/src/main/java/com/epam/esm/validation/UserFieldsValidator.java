package com.epam.esm.validation;

import com.epam.esm.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFieldsValidator implements UserValidator {
    @Override
    public void validate(User user) throws InvalidUserException {
        String name = user.getName();
        String surname = user.getSurname();
        if (name == null || name.isEmpty()) {
            throw new InvalidUserException();
        }
        if (surname == null || surname.isEmpty()) {
            throw new InvalidUserException();
        }
    }
}
