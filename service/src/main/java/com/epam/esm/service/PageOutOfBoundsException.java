package com.epam.esm.service;

/**
 * Out of bounds exception. Occurs when client wants
 * to get entities on specified page bu there is not
 * specified page. If page is less than one and greater
 * than pages amount
 */
public class PageOutOfBoundsException extends Exception {
    private final int currentPage;
    private final int maxPage;
    private final int minPage;

    public PageOutOfBoundsException(int currentPage, int maxPage, int minPage) {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.minPage = minPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getMinPage() {
        return minPage;
    }
}
