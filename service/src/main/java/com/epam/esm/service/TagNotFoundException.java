package com.epam.esm.service;

/**
 * Resource not found exception. Occurs if there is no saved founded tag
 */
public class TagNotFoundException extends ResourceNotFoundException {
    public TagNotFoundException(long tagId) {
        super(tagId);
    }
}
