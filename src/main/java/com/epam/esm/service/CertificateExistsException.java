package com.epam.esm.service;

public class CertificateExistsException extends Exception {
    private static final long CODE = 2;
    private final long certificateId;

    public CertificateExistsException(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getCertificateId() {
        return certificateId;
    }

    public long getCode() {
        return CODE;
    }
}
