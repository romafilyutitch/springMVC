package com.epam.esm.service;

/**
 * Pagination exception class. Occurs when page offset or page limit is negative
 */
public class InvalidPageException extends Exception {
    private final int offset;
    private final int limit;


    public InvalidPageException(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
