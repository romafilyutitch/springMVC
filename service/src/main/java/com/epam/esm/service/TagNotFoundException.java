package com.epam.esm.service;

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
