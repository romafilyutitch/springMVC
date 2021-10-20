package com.epam.esm;

/**
 * Custom error class to display
 * when exception in controller occurs
 */
public class Error {
    private final String errorCode;
    private final String message;

    public Error(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
