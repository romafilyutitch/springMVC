package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.CertificateValidator;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidTagException;
import com.epam.esm.validation.TagValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificateRestServiceTest {
    private CertificateRestService service;

    private Certificate certificate;

    private CertificateDao certificateDao;

    private TagDao tagDao;

    private CertificateValidator certificateFieldsValidator;

    private TagValidator tagFieldsValidator;

    @BeforeEach
    public void setUp() {
        certificate = new Certificate("test", "test", 1.1, 2);
        certificateDao = mock(CertificateDao.class);
        tagDao = mock(TagDao.class);
        certificateFieldsValidator = mock(CertificateValidator.class);
        tagFieldsValidator = mock(TagValidator.class);
        service = new CertificateRestService(certificateDao, tagDao, certificateFieldsValidator, tagFieldsValidator);
    }


    @Test
    public void findAll_shouldReturnAllCertificates() {
        List<Certificate> mockCertificates = Collections.singletonList(certificate);
        when(certificateDao.findAll()).thenReturn(mockCertificates);

        List<Certificate> allCertificates = service.findAll();

        assertEquals(mockCertificates, allCertificates);
        verify(certificateDao).findAll();
    }

    @Test
    public void findById_shouldReturnSavedCertificate() throws CertificateNotFoundException {
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        Certificate foundCertificate = service.findById(1L);

        assertEquals(certificate, foundCertificate);
        verify(certificateDao).findById(1L);
    }

    @Test
    public void findById_shouldThrowExceptionWhenThereIsNoCertificateWithId() {
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CertificateNotFoundException.class, () -> service.findById(1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void findWithParameters_shouldReturnCertificatesThatMatchesPassedParameters() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("partOfName", "e");
        map.put("partOfDescription", "e");
        List<Certificate> singletonCertificate = Collections.singletonList(certificate);
        when(certificateDao.findWithParameters(map)).thenReturn(singletonCertificate);

        List<Certificate> certificatesWithParameters = service.findAllWithParameters(map);

        assertEquals(singletonCertificate, certificatesWithParameters);
        verify(certificateDao).findWithParameters(map);
    }


    @Test
    public void save_shouldSaveCertificate() throws InvalidCertificateException {
        Certificate unsaved = new Certificate(null, "saved", "saved", 1.1, 1, LocalDateTime.now(), LocalDateTime.now());
        Certificate saved = new Certificate(1L, "saved", "saved", 1.1, 1, LocalDateTime.now(), LocalDateTime.now());
        when(certificateDao.save(unsaved)).thenReturn(saved);
        doNothing().when(certificateFieldsValidator).validate(unsaved);

        Certificate savedCertificate = service.save(unsaved);

        assertEquals(saved, savedCertificate);
        verify(certificateDao).save(unsaved);
        verify(certificateFieldsValidator).validate(unsaved);
    }

    @Test
    public void save_shouldThrowExceptionIfSavedCertificateIsInvalid() throws InvalidCertificateException {
        Certificate invalidCertificate = new Certificate(null, "", "", 1.1, 1, LocalDateTime.now(), LocalDateTime.now());
        doThrow(InvalidCertificateException.class).when(certificateFieldsValidator).validate(invalidCertificate);

        assertThrows(InvalidCertificateException.class, () -> service.save(invalidCertificate));

        verify(certificateFieldsValidator).validate(invalidCertificate);
    }


    @Test
    public void update_shouldUpdateCertificate() throws CertificateNotFoundException {
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));
        certificate.setName("updated");
        when(certificateDao.update(certificate)).thenReturn(certificate);

        Certificate updated = service.update(1L, certificate);

        assertEquals(certificate, updated);
        verify(certificateDao).findById(1L);
        verify(certificateDao).update(certificate);
    }

    @Test
    public void update_shouldThrowExceptionWhenThereIsNoCertificateWithId() {
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CertificateNotFoundException.class, () -> service.update(1L, certificate));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void delete_shouldThrowExceptionWhenThereIsNoCertificateWithId() {
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CertificateNotFoundException.class, () -> service.delete(1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void delete_shouldDeleteCertificate() {
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        assertDoesNotThrow(() -> service.delete(1L));

        verify(certificateDao).findById(1L);
        verify(certificateDao).delete(1L);

    }


    @Test
    public void findCertificateTag_shouldGetCertificateTag() throws TagNotFoundException, CertificateNotFoundException {
        Tag tag = new Tag(1L, "Tag");
        certificate.getTags().add(tag);
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        Tag certificateTag = service.findCertificateTag(1L, 1L);

        assertEquals(tag, certificateTag);
        verify(certificateDao).findById(1L);

    }

    @Test
    public void findCertificateTag_shouldThrowExceptionWhenCertificateNotFound() {
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CertificateNotFoundException.class, () -> service.findCertificateTag(1L, 1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void findCertificateTag_shouldThrowExceptionWhenTagNotFound() {
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        assertThrows(TagNotFoundException.class, () -> service.findCertificateTag(1L, 1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void deleteCertificateTag_shouldDeleteTag() {
        Tag tag = new Tag(1L, "tag");
        certificate.getTags().add(tag);
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        assertDoesNotThrow(() -> service.deleteCertificateTag(1L, 1L));

        verify(certificateDao).findById(1L);

    }

    @Test
    public void deleteCertificateTag_shouldThrowExceptionWhenCertificateNotFound() {
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CertificateNotFoundException.class, () -> service.deleteCertificateTag(1L, 1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void deleteCertificateTag_shouldThrowExceptionWhenTagNotFound() {
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));

        assertThrows(TagNotFoundException.class, () -> service.deleteCertificateTag(1L, 1L));

        verify(certificateDao).findById(1L);
    }

    @Test
    public void addTags_shouldThrowExceptionWhenCertificateNotFound() throws InvalidTagException {
        Tag tag = new Tag(1L, "tag");
        when(certificateDao.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(tagFieldsValidator).validate(tag);
        assertThrows(CertificateNotFoundException.class, () -> service.addTags(1L, Collections.singletonList(tag)));

        verify(certificateDao).findById(1L);
        verify(tagFieldsValidator).validate(tag);
    }

    @Test
    public void addTags_shouldAddTagsToCertificate() throws CertificateNotFoundException, InvalidTagException {
        Tag tag = new Tag(1L, "tag");
        certificate.getTags().add(tag);
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));
        when(certificateDao.update(certificate)).thenReturn(certificate);
        doNothing().when(tagFieldsValidator).validate(tag);

        Certificate upadtedCertificate = service.addTags(1L, Collections.singletonList(tag));

        assertEquals(certificate, upadtedCertificate);
        verify(certificateDao).findById(1L);
        verify(tagFieldsValidator).validate(tag);
    }

    @Test
    public void addTags_shouldThrowExceptionIfTagIsInvalid() throws InvalidTagException {
        Tag invalidTag = new Tag("");
        when(certificateDao.findById(1L)).thenReturn(Optional.of(certificate));
        doThrow(InvalidTagException.class).when(tagFieldsValidator).validate(invalidTag);

        assertThrows(InvalidTagException.class, () -> service.addTags(1L, Collections.singletonList(invalidTag)));

        verify(tagFieldsValidator).validate(invalidTag);
    }
}