package com.epam.esm.service;

/**
 * Service layer exception that is thrown when it is need
 * to find tag but tag with passed id not exists in database
 */
public class TagNotFoundException extends Exception {
    private static final int CODE = 3;
    private final long tagId;

    public TagNotFoundException(long tagId) {
        this.tagId = tagId;
    }

    public static int getCode() {
        return CODE;
    }

    public long getTagId() {
        return tagId;
    }
}
