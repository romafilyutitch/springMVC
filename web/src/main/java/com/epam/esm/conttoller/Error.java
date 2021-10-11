package com.epam.esm.conttoller;

public class Error {
    private long code;
    private String message;

    public Error(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
