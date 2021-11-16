package com.epam.esm.validation;

import com.epam.esm.model.Tag;

/**
 * Validation interface to validate tag to
 * save or update
 */
public interface TagValidator {
    /**
     * Validated tag to save or update
     *
     * @param tag tag that need to be validated
     * @throws InvalidTagException if tag is invalid
     */
    void validate(Tag tag) throws InvalidTagException;
}
