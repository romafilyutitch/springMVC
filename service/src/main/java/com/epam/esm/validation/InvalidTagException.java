package com.epam.esm.validation;

public class InvalidTagException extends Exception {
    private final String code = "04";

    public String getCode() {
        return code;
    }
}
