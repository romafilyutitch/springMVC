package com.epam.esm.service;

/**
 * Service layer exception that is thrown when it is need
 * to find certificate but certificate with passed id not exists in database
 */
public class CertificateNotFoundException extends Exception {
    private final String code = "01";
    private final long certificateId;

    public CertificateNotFoundException(long certificateId) {
        this.certificateId = certificateId;
    }

    public String getCode() {
        return code;
    }

    public long getCertificateId() {
        return certificateId;
    }
}
