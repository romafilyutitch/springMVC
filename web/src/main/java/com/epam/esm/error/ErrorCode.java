package com.epam.esm.error;

/**
 * Error code enum that contains error codes constants
 */
public enum ErrorCode {
    NOT_FOUND("40401"), INVALID("40002"), PAGE_OUT_OF_BOUNDS("40403");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
