package com.epam.esm.service;

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
