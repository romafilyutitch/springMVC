package com.epam.esm.dao;

import com.epam.esm.config.DevConfig;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DevConfig.class)
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CertificateJdbcDaoTest {

    @Autowired
    private CertificateJdbcDao dao;

    @Test
    public void findAll_shouldReturnSavedAllSavedCertificates() {
        List<Certificate> certificates = dao.findPage(1);

        assertEquals(1, certificates.size());
        Certificate certificate = certificates.get(0);
        assertEquals(1L, certificate.getId());
        assertEquals("free music listen certificate", certificate.getName());
        assertEquals("spotify free music listening", certificate.getDescription());
        assertEquals(200.50, certificate.getPrice());
        assertEquals(20, certificate.getDuration());
        List<Tag> tags = certificate.getTags();
        assertEquals(3, tags.size());
        assertEquals(1L, tags.get(0).getId());
        assertEquals("spotify", tags.get(0).getName());
        assertEquals(2L, tags.get(1).getId());
        assertEquals("music", tags.get(1).getName());
        assertEquals(3L, tags.get(2).getId());
        assertEquals("art", tags.get(2).getName());
    }


    @Test
    public void findById_shouldReturnSavedCertificateIfSavedCertificateIdPassed() {
        Optional<Certificate> optionalCertificate = dao.findById(1L);

        assertTrue(optionalCertificate.isPresent());
        Certificate foundCertificate = optionalCertificate.get();
        assertEquals(1L, foundCertificate.getId());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoCertificateWithPassedId() {
        Optional<Certificate> optionalCertificate = dao.findById(10L);

        assertFalse(optionalCertificate.isPresent());
    }


    @Test
    public void save_shouldReturnSavedCertificateWithAssignedId() {
        Certificate certificate = new Certificate("football training certificate", "free football training with team", 1000.0, 20);
        Certificate savedCertificate = dao.save(certificate);

        assertNotNull(savedCertificate.getId());
        assertEquals("football training certificate", savedCertificate.getName());
        assertEquals("free football training with team", savedCertificate.getDescription());
        assertEquals(1000.0, certificate.getPrice());
        assertEquals(20, certificate.getDuration());
    }

    @Test
    public void update_shouldUpdateCertificate() {
        Optional<Certificate> optionalCertificate = dao.findById(1L);
        Certificate foundCertificate = optionalCertificate.get();
        foundCertificate.setPrice(3000.0);
        Certificate updatedCertificate = dao.update(foundCertificate);

        assertEquals(3000.0, updatedCertificate.getPrice());
    }

    @Test
    public void delete_shouldDeleteSavedCertificate() {
        dao.delete(1L);
        Optional<Certificate> optionalCertificate = dao.findById(1L);

        assertFalse(optionalCertificate.isPresent());
    }
}