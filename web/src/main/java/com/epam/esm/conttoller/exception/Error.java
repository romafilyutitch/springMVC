package com.epam.esm.conttoller.exception;

/**
 * Custom error class to display
 * when exception in controller occurs
 */
public class Error {
    private final long errorCode;
    private final String message;

    public Error(long errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
