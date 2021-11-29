package com.epam.esm.validation;

import com.epam.esm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFieldsValidatorTest {

    private UserFieldsValidator userFieldsValidator;
    private User validUser;
    private User invalidUser;

    @BeforeEach
    public void setUp() {
        userFieldsValidator = new UserFieldsValidator();
        validUser = new User(1L, "username", "password");
        invalidUser = new User(2L, "", "");
    }

    @Test
    public void validate_shouldValidateValidUser() {
        assertDoesNotThrow(() -> userFieldsValidator.validate(validUser));
    }

    @Test
    public void validate_shouldThrowExceptionIfUserIsInvalid() {
        assertThrows(InvalidUserException.class, () -> userFieldsValidator.validate(invalidUser));
    }



}