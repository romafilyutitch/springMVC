package com.epam.esm.service;

/**
 * Pagination exception class. Occurs when current offset is greater then total elements amount
 */
public class PageOutOfBoundsException extends Exception {
    private final int currentOffset;
    private final int totalElements;

    public PageOutOfBoundsException(int currentOffset, int totalElements) {
        this.currentOffset = currentOffset;
        this.totalElements = totalElements;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public int getTotalElements() {
        return totalElements;
    }
}
