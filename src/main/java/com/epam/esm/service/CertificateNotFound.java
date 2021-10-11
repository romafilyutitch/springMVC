package com.epam.esm.service;

public class CertificateNotFound extends Exception {
    private Long certificateId;

    public CertificateNotFound(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Long getCertificateId() {
        return certificateId;
    }
}
