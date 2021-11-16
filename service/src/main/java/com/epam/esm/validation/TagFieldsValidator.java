package com.epam.esm.validation;

import com.epam.esm.model.Tag;
import org.springframework.stereotype.Component;

/**
 * Validated tag fields values.
 * Tag name must be not empty
 */
@Component
public class TagFieldsValidator implements TagValidator {
    /**
     * Validates tag to save or update
     * @param resource tag that need to be validated
     * @throws InvalidTagException if tag is invalid
     */
    public void validate(Tag resource) throws InvalidTagException {
        String name = resource.getName();
        if (name == null || name.isEmpty()) {
            throw new InvalidTagException();
        }
    }
}
