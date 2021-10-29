package com.epam.esm.service;

public class OrderNotFoundException extends ResourceNotFoundException {

    public OrderNotFoundException(long resourceId) {
        super(resourceId);
    }
}
