package com.epam.esm.validation;

public class InvalidCertificateException extends Exception {
    private final String code = "03";

    public String getCode() {
        return code;
    }
}
