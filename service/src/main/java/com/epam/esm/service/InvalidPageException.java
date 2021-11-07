package com.epam.esm.service;

public class InvalidPageException extends Throwable {
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
