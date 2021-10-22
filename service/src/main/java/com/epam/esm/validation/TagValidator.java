package com.epam.esm.validation;

import com.epam.esm.model.Tag;

public interface TagValidator {
    void validate(Tag tag) throws InvalidTagException;
}
