package com.epam.esm.validation;

import com.epam.esm.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

class TagValidatorTest {
    private TagValidator validator;
    private Tag validTag;
    private Tag invalidTag;

    @BeforeEach
    void setUp() {
        validator = new TagValidator();
        validTag = new Tag("valid");
        invalidTag = new Tag("");
    }

    @Test
    public void validate_shouldThrowExceptionIfTagIsInvalid() {
        assertThrows(InvalidTagException.class, () -> validator.validate(invalidTag));
    }

    @Test
    public void validate_shouldNotThrowExceptionIfTagIsValid() {
        assertDoesNotThrow(() -> validator.validate(validTag));
    }


}