package com.epam.esm.service;

/**
 * Resource not found exception. Occurs when there is no saved found certificate
 */
public class CertificateNotFoundException extends ResourceNotFoundException {
    public CertificateNotFoundException(long id) {
        super(id);
    }
}
