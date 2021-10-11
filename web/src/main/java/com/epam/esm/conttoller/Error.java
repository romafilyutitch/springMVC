package com.epam.esm.conttoller;

public class Error {
    private long errorCode;
    private String message;

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
