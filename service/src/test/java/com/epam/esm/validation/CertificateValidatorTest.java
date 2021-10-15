package com.epam.esm.validation;

import com.epam.esm.model.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CertificateValidatorTest {
    private CertificateValidator validator;
    private Certificate validCertificate;
    private Certificate invalidCertificate;

    @BeforeEach
    void setUp() {
        validator = new CertificateValidator();
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