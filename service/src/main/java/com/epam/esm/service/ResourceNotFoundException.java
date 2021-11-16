package com.epam.esm.service;

/**
 * Resource not found exception. Occurs if there is no founded saved resource
 */
public class ResourceNotFoundException extends Exception {
    private final long resourceId;

    public ResourceNotFoundException(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getResourceId() {
        return resourceId;
    }
}
