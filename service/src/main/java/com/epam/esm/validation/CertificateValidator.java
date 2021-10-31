package com.epam.esm.validation;

import com.epam.esm.model.Certificate;

/**
 * Validate certificates to save or update certificate
 */
public interface CertificateValidator {
    /**
     * Validates certificate to save or updated
     * @param certificate that need to be validated
     * @throws InvalidCertificateException if certificate is invalid
     */
    void validate(Certificate certificate) throws InvalidCertificateException;
}
