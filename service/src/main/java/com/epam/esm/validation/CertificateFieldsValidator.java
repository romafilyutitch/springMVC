package com.epam.esm.validation;

import com.epam.esm.model.Certificate;
import org.springframework.stereotype.Component;

/**
 * Validates certificate's fields values.
 * name must be not empty
 * description must be not empty
 * price must be positive
 * duration must be positive
 */
@Component
public class CertificateFieldsValidator implements CertificateValidator {
    /**
     * Validates certificate's fields to save or update
     *
     * @param resource that need to be validated
     * @throws InvalidCertificateException if certificate is invalid
     */
    public void validate(Certificate resource) throws InvalidCertificateException {
        String name = resource.getName();
        String description = resource.getDescription();
        double price = resource.getPrice();
        int duration = resource.getDuration();
        if (name == null || name.isEmpty()) {
            throw new InvalidCertificateException();
        }
        if (description == null || description.isEmpty()) {
            throw new InvalidCertificateException();
        }
        if (price < 0) {
            throw new InvalidCertificateException();
        }
        if (duration < 0) {
            throw new InvalidCertificateException();
        }
    }
}
