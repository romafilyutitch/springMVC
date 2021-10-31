package com.epam.esm.validation;

import com.epam.esm.model.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateFieldsValidator implements CertificateValidator {

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
