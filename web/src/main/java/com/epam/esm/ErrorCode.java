package com.epam.esm;

public enum ErrorCode {
    NOT_FOUND("40401"), INVALID("40002"), PAGE_OUT_OF_BOUNDS("40403");

    private String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
