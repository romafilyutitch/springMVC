package com.epam.esm.service;

import com.epam.esm.builder.FindCertificatesQueryBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OffsetPageable;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validation.CertificateValidator;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.InvalidTagException;
import com.epam.esm.validation.TagValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificateRestServiceTest {
    private CertificateRestService service;
    private CertificateRepository certificateRepository;
    private TagRepository tagRepository;
    private OrderRepository orderRepository;
    private CertificateValidator certificateFieldsValidator;
    private TagValidator tagFieldsValidator;
    private FindCertificatesQueryBuilder queryBuilder;
    private Certificate certificate;

    @Autowired

    @BeforeEach
    public void setUp() {
        certificate = new Certificate(1, "name", "description", 100.0, 100, LocalDateTime.now(), LocalDateTime.now());
        certificateRepository = mock(CertificateRepository.class);
        tagRepository = mock(TagRepository.class);
        orderRepository = mock(OrderRepository.class);
        certificateFieldsValidator = mock(CertificateValidator.class);
        tagFieldsValidator = mock(TagValidator.class);
        queryBuilder = mock(FindCertificatesQueryBuilder.class);
        service = new CertificateRestService(certificateRepository, tagRepository, orderRepository, queryBuilder, certificateFieldsValidator, tagFieldsValidator);
    }

    @Test
    public void findPage_shouldReturnCertificateOnFirstPage() throws PageOutOfBoundsException, InvalidPageException {
        List<Certificate> certificates = Collections.singletonList(certificate);
        Page<Certificate> page = new PageImpl<>(certificates);
        OffsetPageable offsetPageable = new OffsetPageable(0, 10);
        when(certificateRepository.count()).thenReturn(1L);
        when(certificateRepository.findAll(offsetPageable)).thenReturn(page);
        List<Certificate> foundPage = service.findPage(0, 10);

        assertEquals(certificates, foundPage);
        verify(certificateRepository).count();
        verify(certificateRepository).findAll(any(Pageable.class));

    }

    @Test
    public void findPage_shouldThrowExceptionIfOffsetGreaterThenTotalElements() {
        when(certificateRepository.count()).thenReturn(1L);

        assertThrows(PageOutOfBoundsException.class, () -> service.findPage(10, 10));

        verify(certificateRepository).count();
    }

    @Test
    public void findPage_shouldThrowExceptionIFOffsetIsNegative() {
        assertThrows(InvalidPageException.class, () -> service.findPage(-10, 10));
    }

    @Test
    public void findById_shouldReturnCertificateById() throws ResourceNotFoundException {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        Certificate foundCertificate = service.findById(1);

        assertEquals(certificate, foundCertificate);
        verify(certificateRepository).findById(1L);
    }

    @Test
    public void findById_shouldThrowExceptionIfThereIsNoCertificateWithId() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1));

        verify(certificateRepository).findById(1L);
    }

    @Test
    public void save_shouldReturnSavedCertificate() throws InvalidResourceException {
        doNothing().when(certificateFieldsValidator).validate(certificate);
        when(certificateRepository.save(certificate)).thenReturn(certificate);

        Certificate savedCertificate = service.save(certificate);

        assertEquals(certificate, savedCertificate);
        verify(certificateFieldsValidator).validate(certificate);
        verify(certificateRepository).save(certificate);
    }

    @Test
    public void save_shouldThrowExceptionWhenCertificateIsInvalid() throws InvalidResourceException {
        doThrow(InvalidCertificateException.class).when(certificateFieldsValidator).validate(certificate);

        assertThrows(InvalidResourceException.class, () -> service.save(certificate));

        verify(certificateFieldsValidator).validate(certificate);
    }

    @Test
    public void update_shouldReturnUpdatedCertificate() throws InvalidResourceException, ResourceNotFoundException {
        when(certificateRepository.findById(certificate.getId())).thenReturn(Optional.of(certificate));
        doNothing().when(certificateFieldsValidator).validate(certificate);

        Certificate updatedCertificate = service.update(certificate);

        assertEquals(certificate, updatedCertificate);
        verify(certificateRepository).findById(certificate.getId());
        verify(certificateFieldsValidator).validate(certificate);
    }

    @Test
    public void update_shouldThrowExceptionWhenCertificateIsInvalid() throws InvalidResourceException {
        doThrow(InvalidCertificateException.class).when(certificateFieldsValidator).validate(certificate);

        assertThrows(InvalidResourceException.class, () -> service.update(certificate));

        verify(certificateFieldsValidator).validate(certificate);
    }

    @Test
    public void delete_shouldCertificate() {
        doNothing().when(certificateRepository).delete(certificate);

        service.delete(certificate);

        verify(certificateRepository).delete(certificate);
    }

    @Test
    public void addTags_shouldReturnCertificateWithTagAddedTags() throws InvalidResourceException {
        Tag tag = new Tag(1, "tag");
        List<Tag> tags = Collections.singletonList(tag);
        doNothing().when(tagFieldsValidator).validate(tag);

        Certificate updatedCertificate = service.addTags(certificate, tags);

        assertTrue(updatedCertificate.getTags().contains(tag));
        assertEquals(certificate, updatedCertificate);
        verify(tagFieldsValidator).validate(tag);

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
        certificate.getTags().add(tag);
        service.deleteCertificateTag(certificate, tag);
        assertFalse(certificate.getTags().contains(tag));
    }

    @Test
    public void findCertificateTag_shouldFindTag() throws ResourceNotFoundException {
        Tag tag = new Tag(1, "tag");
        when(tagRepository.findCertificateTag(certificate.getId(), tag.getId())).thenReturn(Optional.of(tag));

        Tag certificateTag = service.findCertificateTag(certificate, tag.getId());

        assertEquals(tag, certificateTag);
        verify(tagRepository).findCertificateTag(certificate.getId(), tag.getId());
    }

    @Test
    public void findCertificateTag_shouldThrowExceptionIfThereIsNotTagWithId() {
        when(tagRepository.findCertificateTag(certificate.getId(), 1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findCertificateTag(certificate, 1));

        verify(tagRepository).findCertificateTag(certificate.getId(), 1);
    }

    @Test
    public void findCertificateTagsPage_shouldReturnTagsOnPage() throws PageOutOfBoundsException, InvalidPageException {
        Tag tag = new Tag(1, "tag");
        List<Tag> tags = Collections.singletonList(tag);
        Page<Tag> page = new PageImpl<>(tags);
        OffsetPageable offsetPageable = new OffsetPageable(0, 10);
        when(tagRepository.getCertificateTagsTotalElements(certificate.getId())).thenReturn(1);
        when(tagRepository.findCertificateTagsPage(certificate.getId(), offsetPageable)).thenReturn(page);

        List<Tag> tagsPage = service.findCertificateTagsPage(certificate, 0, 10);

        assertEquals(tags, tagsPage);
        verify(tagRepository).getCertificateTagsTotalElements(certificate.getId());
        verify(tagRepository).findCertificateTagsPage(certificate.getId(), offsetPageable);
    }

    @Test
    public void findCertificateTagsPage_shouldThrowExceptionIfPageOutOfBounds() {
        when(tagRepository.getCertificateTagsTotalElements(certificate.getId())).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findCertificateTagsPage(certificate, 10, 10));

        verify(tagRepository).getCertificateTagsTotalElements(certificate.getId());
    }

    @Test
    public void findCertificateTagsPage_shouldThrowExceptionIfOffsetIsInvalid() {
        assertThrows(InvalidPageException.class, () -> service.findCertificateTagsPage(certificate, -10, 10));
    }

    @Test
    public void getCertificateTagsTotalElements_shouldReturnOne() {
        when(tagRepository.getCertificateTagsTotalElements(certificate.getId())).thenReturn(1);

        int certificateTagsElements = service.getCertificateTagsTotalElements(certificate);

        assertEquals(1, certificateTagsElements);
        verify(tagRepository).getCertificateTagsTotalElements(certificate.getId());
    }

    @Test
    public void getTotalElements_shouldReturnOne() {
        when(certificateRepository.count()).thenReturn(1L);

        int totalElements = service.getTotalElements();

        assertEquals(1, totalElements);
        verify(certificateRepository).count();
    }

    @Test
    public void findCertificateOrders_shouldReturnOrdersOnPage() throws InvalidPageException, PageOutOfBoundsException {
        Order order = new Order(1, certificate.getPrice(), LocalDateTime.now());
        order.setCertificate(certificate);
        List<Order> orders = Collections.singletonList(order);
        Page<Order> page = new PageImpl<>(orders);
        OffsetPageable offsetPageable = new OffsetPageable(0, 10);
        when(orderRepository.getCertificateOrdersTotalElements(certificate.getId())).thenReturn(1);
        when(orderRepository.findCertificateOrders(certificate.getId(), offsetPageable)).thenReturn(page);

        List<Order> foundOrders = service.findCertificateOrders(certificate, 0, 10);

        assertEquals(orders, foundOrders);
        verify(orderRepository).getCertificateOrdersTotalElements(certificate.getId());
        verify(orderRepository).findCertificateOrders(certificate.getId(), offsetPageable);
    }

    @Test
    public void findCertificateOrders_shouldThrowExceptionIfOffsetGreaterThenTotalElements() {
        when(orderRepository.getCertificateOrdersTotalElements(certificate.getId())).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findCertificateOrders(certificate, 10, 10));

        verify(orderRepository).getCertificateOrdersTotalElements(certificate.getId());
    }

    @Test
    public void findCertificateOrders_shouldThrowExceptionIfOffsetIsInvalid() {
        assertThrows(InvalidPageException.class, () -> service.findCertificateTagsPage(certificate, -10, 10));
    }

}