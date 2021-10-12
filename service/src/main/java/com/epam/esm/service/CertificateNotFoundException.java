package com.epam.esm.service;

/**
 * Service layer exception that is thrown when it is need
 * to find certificate but certificate with passed id not exists in database
 */
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
