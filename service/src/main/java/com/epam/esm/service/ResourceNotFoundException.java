package com.epam.esm.service;

public class ResourceNotFoundException extends Exception {
    private final long resourceId;

    public ResourceNotFoundException(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getResourceId() {
        return resourceId;
    }
}
