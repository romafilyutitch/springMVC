package com.epam.esm.service;

/**
 * Out of bounds exception. Occurs when client wants
 * to get entities on specified page bu there is not
 * specified page. If page is less than one and greater
 * than pages amount
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
