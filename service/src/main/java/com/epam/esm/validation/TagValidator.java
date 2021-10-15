package com.epam.esm.validation;

import com.epam.esm.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagValidator {
    public void validate(Tag resource) throws InvalidTagException {
        String name = resource.getName();
        if (name == null || name.isEmpty()) {
            throw new InvalidTagException();
        }
    }
}
