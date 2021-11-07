package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.CertificateValidator;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidResourceException;
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
    private CertificateDao certificateDao;
    private TagDao tagDao;
    private OrderDao orderDao;
    private CertificateValidator certificateFieldsValidator;
    private TagValidator tagFieldsValidator;
    private Certificate certificate;

    @BeforeEach
    public void setUp() {
        certificate = new Certificate(1, "name", "description", 100.0, 100, LocalDateTime.now(), LocalDateTime.now());
        certificateDao = mock(CertificateDao.class);
        tagDao = mock(TagDao.class);
        orderDao = mock(OrderDao.class);
        certificateFieldsValidator = mock(CertificateValidator.class);
        tagFieldsValidator = mock(TagValidator.class);
        service = new CertificateRestService(certificateDao, tagDao, orderDao, certificateFieldsValidator, tagFieldsValidator);
    }

    @Test
    public void findPage_shouldReturnFirstPage() throws PageOutOfBoundsException {
        when(certificateDao.getTotalPages()).thenReturn(1);
        List<Certificate> certificates = Collections.singletonList(certificate);
        when(certificateDao.findPage(1)).thenReturn(certificates);
        List<Certificate> foundPage = service.findPage(1);

        assertEquals(certificates, foundPage);

        verify(certificateDao).getTotalPages();
        verify(certificateDao).findPage(1);
    }

    @Test
    public void findPage_shouldThrowExceptionIfPageOutOfBounds() {
        when(certificateDao.getTotalPages()).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findPage(100));

        verify(certificateDao, atLeastOnce()).getTotalPages();
    }

    @Test
    public void findWithParameters_shouldReturnCertificatesThatMatchesPassedParameters() throws PageOutOfBoundsException {
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
    public void findById_shouldReturnCertificateById() throws ResourceNotFoundException {
        when(certificateDao.findById(1)).thenReturn(Optional.of(certificate));
        Certificate foundCertificate = service.findById(1);

        assertEquals(certificate, foundCertificate);
        verify(certificateDao).findById(1);
    }

    @Test
    public void findById_shouldThrowExceptionIfThereIsNoCertificateWithId() {
        when(certificateDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1));

        verify(certificateDao).findById(1);
    }

    @Test
    public void save_shouldReturnSavedCertificate() throws InvalidResourceException {
        doNothing().when(certificateFieldsValidator).validate(certificate);
        when(certificateDao.save(certificate)).thenReturn(certificate);

        Certificate savedCertificate = service.save(certificate);

        assertEquals(certificate, savedCertificate);
        verify(certificateFieldsValidator).validate(certificate);
        verify(certificateDao).save(certificate);
    }

    @Test
    public void save_shouldThrowExceptionWhenCertificateIsInvalid() throws InvalidResourceException {
        doThrow(InvalidCertificateException.class).when(certificateFieldsValidator).validate(certificate);

        assertThrows(InvalidResourceException.class, () -> service.save(certificate));

        verify(certificateFieldsValidator).validate(certificate);
    }

    @Test
    public void update_shouldReturnUpdatedCertificate() throws InvalidResourceException, ResourceNotFoundException {
        when(certificateDao.findById(certificate.getId())).thenReturn(Optional.of(certificate));
        doNothing().when(certificateFieldsValidator).validate(certificate);
        when(certificateDao.update(certificate)).thenReturn(certificate);

        Certificate updatedCertificate = service.update(certificate);

        assertEquals(certificate, updatedCertificate);
        verify(certificateDao).findById(certificate.getId());
        verify(certificateFieldsValidator).validate(certificate);
        verify(certificateDao).update(certificate);
    }

    @Test
    public void update_shouldThrowExceptionWhenCertificateIsInvalid() throws InvalidResourceException {
        when(certificateDao.findById(certificate.getId())).thenReturn(Optional.of(certificate));
        doThrow(InvalidCertificateException.class).when(certificateFieldsValidator).validate(certificate);

        assertThrows(InvalidResourceException.class, () -> service.update(certificate));

        verify(certificateDao).findById(certificate.getId());
        verify(certificateFieldsValidator).validate(certificate);
    }

    @Test
    public void delete_shouldCertificate() {
        doNothing().when(certificateDao).delete(certificate);

        service.delete(certificate);

        verify(certificateDao).delete(certificate);
    }

    @Test
    public void addTags_shouldReturnCertificateWithTagAddedTags() throws InvalidResourceException {
        Tag tag = new Tag(1, "tag");
        List<Tag> tags = Collections.singletonList(tag);
        doNothing().when(tagFieldsValidator).validate(tag);
        when(certificateDao.update(certificate)).thenReturn(certificate);

        Certificate updatedCertificate = service.addTags(certificate, tags);

        assertTrue(updatedCertificate.getTags().contains(tag));
        assertEquals(certificate, updatedCertificate);
        verify(tagFieldsValidator).validate(tag);
        verify(certificateDao).update(certificate);

    }

    @Test
    public void addTags_shouldThrowExceptionIfTagIsInvalid() throws InvalidResourceException {
        Tag invalid = new Tag("");
        List<Tag> tags = Collections.singletonList(invalid);
        certificate.getTags().add(invalid);
        doThrow(InvalidTagException.class).when(tagFieldsValidator).validate(invalid);

        assertThrows(InvalidResourceException.class, () -> service.addTags(certificate, tags));
        verify(tagFieldsValidator).validate(invalid);
    }

    @Test
    public void deleteCertificateTag_shouldDeleteTag() {
        Tag tag = new Tag(1, "tag");
        doNothing().when(tagDao).delete(tag);
        service.deleteCertificateTag(certificate, tag);

        verify(tagDao).delete(tag);
    }

    @Test
    public void findCertificateTag_shouldFindTag() throws ResourceNotFoundException {
        Tag tag = new Tag(1, "tag");
        when(tagDao.findCertificateTag(certificate.getId(), tag.getId())).thenReturn(Optional.of(tag));

        Tag certificateTag = service.findCertificateTag(certificate, tag.getId());

        assertEquals(tag, certificateTag);
        verify(tagDao).findCertificateTag(certificate.getId(), tag.getId());
    }

    @Test
    public void findCertificateTag_shouldThrowExceptionIfThereIsNotTagWithId() {
        when(tagDao.findCertificateTag(certificate.getId(), 1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findCertificateTag(certificate, 1));

        verify(tagDao).findCertificateTag(certificate.getId(), 1);
    }

    @Test
    public void findCertificateTagsPage_shouldReturnFirstPage() throws PageOutOfBoundsException {
        Tag tag = new Tag(1, "tag");
        List<Tag> tags = Collections.singletonList(tag);
        when(tagDao.getCertificateTagsTotalPages(certificate.getId())).thenReturn(1);
        when(tagDao.findCertificateTagsPage(certificate.getId(), 1)).thenReturn(tags);

        List<Tag> tagsPage = service.findCertificateTagsPage(certificate, 1);

        assertEquals(tags, tagsPage);
        verify(tagDao).getCertificateTagsTotalPages(certificate.getId());
        verify(tagDao).findCertificateTagsPage(certificate.getId(), 1);
    }

    @Test
    public void findCertificateTagsPage_shouldThrowExceptionIfPageOutOfBounds() {
        when(tagDao.getCertificateTagsTotalPages(certificate.getId())).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findCertificateTagsPage(certificate, 100));

        verify(tagDao, atLeastOnce()).getCertificateTagsTotalPages(certificate.getId());
    }

    @Test
    public void getCertificateTagsTotalPages_shouldReturnOne() {
        when(tagDao.getCertificateTagsTotalPages(certificate.getId())).thenReturn(1);

        int certificateTagsPages = service.getCertificateTagsTotalPages(certificate);

        assertEquals(1, certificateTagsPages);
        verify(tagDao).getCertificateTagsTotalPages(certificate.getId());
    }

    @Test
    public void getCertificateTagsTotalElements_shouldReturnOne() {
        when(tagDao.getCertificateTagsTotalElements(certificate.getId())).thenReturn(1);

        int certificateTagsElements = service.getCertificateTagsTotalElements(certificate);

        assertEquals(1, certificateTagsElements);
        verify(tagDao).getCertificateTagsTotalElements(certificate.getId());
    }

    @Test
    public void getTotalElements_shouldReturnOne() {
        when(certificateDao.getTotalElements()).thenReturn(1);

        int totalElements = service.getTotalElements();

        assertEquals(1, totalElements);
        verify(certificateDao).getTotalElements();
    }

    @Test
    public void getTotalPages_shouldReturnOne() {
        when(certificateDao.getTotalPages()).thenReturn(1);

        int totalPages = service.getTotalPages();

        assertEquals(1, totalPages);
        verify(certificateDao).getTotalPages();
    }

    @Test
    public void findCertificateOrder_shouldReturnOrder() throws ResourceNotFoundException {
        Order order = new Order(1, certificate.getPrice(), LocalDateTime.now());
        order.setCertificate(certificate);
        when(orderDao.findCertificateOrders(certificate.getId())).thenReturn(Optional.of(order));

        Order certificateOrder = service.findCertificateOrder(certificate);

        assertEquals(order, certificateOrder);
        verify(orderDao).findCertificateOrders(certificate.getId());
    }

    @Test
    public void findCertificateOrder_shouldThrowExceptionIfThereIsNoCertificateOrder() {
        when(orderDao.findCertificateOrders(certificate.getId())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.findCertificateOrder(certificate));

        verify(orderDao).findCertificateOrders(certificate.getId());
    }
}