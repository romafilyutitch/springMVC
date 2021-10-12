package com.epam.esm.service;

/**
 * Service layer exception that is thrown when new certificate
 * if saved bu there is certificate with such name already saved in database
 */
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
