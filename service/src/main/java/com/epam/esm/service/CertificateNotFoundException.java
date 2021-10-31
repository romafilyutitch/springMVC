package com.epam.esm.service;

public class CertificateNotFoundException extends ResourceNotFoundException {
    public CertificateNotFoundException(long id) {
        super(id);
    }
}
