package com.epam.esm.service;

public class TagNotFoundException extends ResourceNotFoundException {
    public TagNotFoundException(long tagId) {
        super(tagId);
    }
}
