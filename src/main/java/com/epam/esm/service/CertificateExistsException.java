package com.epam.esm.service;

public class CertificateExistsException extends Exception {
    private Long certificateId;

    public CertificateExistsException(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Long getCertificateId() {
        return certificateId;
    }
}
