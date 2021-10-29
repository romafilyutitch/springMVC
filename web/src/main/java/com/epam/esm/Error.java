package com.epam.esm;

/**
 * Custom error class to display
 * when exception in controller occurs
 */
public class Error {
    private ErrorCode errorCode;
    private final String message;

    public Error(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
