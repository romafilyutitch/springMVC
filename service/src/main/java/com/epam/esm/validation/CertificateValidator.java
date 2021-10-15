package com.epam.esm.validation;

import com.epam.esm.model.Certificate;
import org.springframework.validation.*;

import java.util.List;

public class CertificateValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Certificate.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        ValidationUtils.rejectIfEmpty(errors, "description", "description.empty");
        ValidationUtils.rejectIfEmpty(errors, "price", "price.empty");
        ValidationUtils.rejectIfEmpty(errors, "duration", "duration.empty");
        Certificate certificate = (Certificate) target;
        if (certificate.getPrice() < 0) {
            errors.rejectValue("price", "negativeprice");
        }
        if (certificate.getDuration() < 0) {
            errors.rejectValue("duration", "negativeDuration");
        }
    }
}
