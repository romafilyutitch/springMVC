package com.epam.esm.dao;

import com.epam.esm.configuration.PersistanceConfiguration;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfiguration.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
class CertificateRepositoryTest {

    @Autowired
    private CertificateRepository repository;

    @Test
    public void findAll_shouldReturnSavedAllSavedCertificates() {
        List<Certificate> certificates = repository.findAll();

        assertEquals(1, certificates.size());
        Certificate certificate = certificates.get(0);
        assertEquals(1L, certificate.getId());
        assertEquals("free music listen certificate", certificate.getName());
        assertEquals("spotify free music listening", certificate.getDescription());
        assertEquals(200.50, certificate.getPrice());
        assertEquals(20, certificate.getDuration());
        Set<Tag> tags = certificate.getTags();
        assertEquals(3, tags.size());
        assertTrue(tags.contains(new Tag(1L, "spotify")));
        assertTrue(tags.contains(new Tag(2L, "music")));
        assertTrue(tags.contains(new Tag(3L, "art")));
    }


    @Test
    public void findById_shouldReturnSavedCertificateIfSavedCertificateIdPassed() {
        Optional<Certificate> optionalCertificate = repository.findById(1L);

        assertTrue(optionalCertificate.isPresent());
        Certificate foundCertificate = optionalCertificate.get();
        assertEquals(1L, foundCertificate.getId());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoCertificateWithPassedId() {
        Optional<Certificate> optionalCertificate = repository.findById(10L);

        assertFalse(optionalCertificate.isPresent());
    }


    @Test
    public void save_shouldReturnSavedCertificateWithAssignedId() {
        Certificate certificate = new Certificate("football training certificate", "free football training with team", 1000.0, 20);
        Certificate savedCertificate = repository.save(certificate);

        assertTrue(savedCertificate.getId() != 0);
        assertEquals("football training certificate", savedCertificate.getName());
        assertEquals("free football training with team", savedCertificate.getDescription());
        assertEquals(1000.0, certificate.getPrice());
        assertEquals(20, certificate.getDuration());
    }

    @Test
    public void update_shouldUpdateCertificate() {
        Optional<Certificate> optionalCertificate = repository.findById(1L);
        Certificate foundCertificate = optionalCertificate.get();
        foundCertificate.setPrice(3000.0);
        assertEquals(3000.0, foundCertificate.getPrice());
    }

    @Test
    public void delete_shouldDeleteSavedCertificate() {
        Optional<Certificate> optionalSavedCertificate = repository.findById(1L);
        assertTrue(optionalSavedCertificate.isPresent());
        Certificate certificate = optionalSavedCertificate.get();
        repository.delete(certificate);
        Optional<Certificate> optionalCertificate = repository.findById(1L);

        assertFalse(optionalCertificate.isPresent());
    }

    @Test
    public void findByOderId_shouldFindCertificate() {
        Optional<Certificate> optionalCertificate = repository.findByOrderId(1L);
        assertTrue(optionalCertificate.isPresent());
        Certificate certificate = optionalCertificate.get();
        assertEquals(1L, certificate.getId());
        assertEquals("free music listen certificate", certificate.getName());
        assertEquals("spotify free music listening", certificate.getDescription());
        assertEquals(200.50, certificate.getPrice());
        assertEquals(20, certificate.getDuration());
        Set<Tag> tags = certificate.getTags();
        assertEquals(3, tags.size());
        assertTrue(tags.contains(new Tag(1L, "spotify")));
        assertTrue(tags.contains(new Tag(2L, "music")));
        assertTrue(tags.contains(new Tag(3L, "art")));
    }
}