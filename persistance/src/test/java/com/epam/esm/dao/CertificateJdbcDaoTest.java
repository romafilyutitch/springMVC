package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Certificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = PersistanceConfig.class)
@ActiveProfiles("dev")
@SpringJUnitConfig(classes = PersistanceConfig.class)
class CertificateJdbcDaoTest {

    @Autowired
    private CertificateJdbcDao dao;

    private Certificate certificate;

    @BeforeEach
    void setUp() {
        certificate = new Certificate("name", "description", 1.2, 22);
        certificate = dao.save(certificate);
    }

    @AfterEach
    void tearDown() {
        dao.delete(certificate.getId());
    }

    @Test
    public void findAll_shouldReturnSavedAllSavedCertificates() {
        List<Certificate> allCertificates = dao.findAll();

        assertTrue(allCertificates.contains(certificate));
    }

    @Test
    public void findById_shouldReturnSavedCertificateIfSavedCertificateIdPassed() {
        Optional<Certificate> foundCertificate = dao.findById(certificate.getId());

        assertTrue(foundCertificate.isPresent());
        assertEquals(certificate, foundCertificate.get());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoCertificateWithPassedId() {
        dao.delete(certificate.getId());
        Optional<Certificate> foundCertificate = dao.findById(certificate.getId());

        assertFalse(foundCertificate.isPresent());
    }

    @Test
    public void findWithParameters_shouldReturnCertificateWithByCertificateParameters() {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        parameters.put("partOfName", certificate.getName());
        List<Certificate> certificateWithParameters = dao.findWithParameters(parameters);

        assertTrue(certificateWithParameters.contains(certificate));
    }

    @Test
    public void save_shouldReturnSavedCertificateWithAssignedId() {
        assertNotNull(certificate.getId());
    }

    @Test
    public void update_shouldUpdateCertificate() {
        certificate.setPrice(1000.0);
        Certificate updatedCertificate = dao.update(certificate);

        assertEquals(certificate, updatedCertificate);
    }

    @Test
    public void delete_shouldDeleteSavedCertificate() {
        dao.delete(certificate.getId());
        Optional<Certificate> foundCertificate = dao.findById(certificate.getId());

        assertFalse(foundCertificate.isPresent());
    }
}