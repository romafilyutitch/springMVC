package com.epam.esm.service;

/**
 * Resource not found exception if there is no saved found order
 */
public class OrderNotFoundException extends ResourceNotFoundException {

    public OrderNotFoundException(long resourceId) {
        super(resourceId);
    }
}
