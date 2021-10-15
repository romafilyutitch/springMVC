package com.epam.esm.validation;

import com.epam.esm.model.Certificate;

public interface CertificateValidator {
    void validate(Certificate certificate) throws InvalidCertificateException;
}
