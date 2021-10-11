package com.epam.esm.service;

public class CertificateNotFoundException extends Exception {
    private static final long CODE = 1;
    private final long certificateId;

    public CertificateNotFoundException(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getCode() {
        return CODE;
    }

    public long getCertificateId() {
        return certificateId;
    }
}
