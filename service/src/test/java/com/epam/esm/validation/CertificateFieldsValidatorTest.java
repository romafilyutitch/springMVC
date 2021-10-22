package com.epam.esm.validation;

import com.epam.esm.model.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertificateFieldsValidatorTest {
    private CertificateFieldsValidator validator;
    private Certificate validCertificate;
    private Certificate invalidCertificate;

    @BeforeEach
    void setUp() {
        validator = new CertificateFieldsValidator();
        validCertificate = new Certificate(null, "valid", "valid", 22.1, 23, LocalDateTime.now(), LocalDateTime.now());
        invalidCertificate = new Certificate(null, "", "", -1.2, -3, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    public void validate_shouldNotThrowExceptionIfCertificateIsValid() {
        assertDoesNotThrow(() -> validator.validate(validCertificate));
    }

    @Test
    public void validate_shouldThrowExceptionIfCertificateIsInvalid() {
        assertThrows(InvalidCertificateException.class, () -> validator.validate(invalidCertificate));
    }
}