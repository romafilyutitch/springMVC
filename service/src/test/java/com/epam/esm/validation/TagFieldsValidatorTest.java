package com.epam.esm.validation;

import com.epam.esm.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagFieldsValidatorTest {
    private TagFieldsValidator validator;
    private Tag validTag;
    private Tag invalidTag;

    @BeforeEach
    void setUp() {
        validator = new TagFieldsValidator();
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