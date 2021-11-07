package com.epam.esm.page;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.UserController;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RestCertificateLinksBuilder implements CertificateLinksBuilder {
    private final CertificateService service;

    @Autowired
    public RestCertificateLinksBuilder(CertificateService service) {
        this.service = service;
    }

    @Override
    public Certificate buildLinks(Certificate entity) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(entity.getId())).withSelfRel();
        entity.add(selfLink);
        if (!entity.getTags().isEmpty()) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(entity.getId(), 0, 10)).withRel("tags");
            Link ordersLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(entity.getId(), 0, 10)).withRel("orders");
            entity.add(tagsLink, ordersLink);
        }
        return entity;
    }

    @Override
    public CollectionModel<Certificate> buildPageLinks(List<Certificate> entities, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Certificate entity : entities) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(entity.getId())).withRel("certificate");
            entity.add(certificateLink);
        }
        Link self = linkTo(methodOn(CertificateController.class).showCertificates(null, currentOffset, currentLimit)).withSelfRel();
        Link next = linkTo(methodOn(CertificateController.class).showCertificates(null, currentOffset + currentLimit, currentLimit)).withRel("next");
        Link prev = linkTo(methodOn(CertificateController.class).showCertificates(null, currentOffset - currentLimit, currentLimit)).withRel("previous");
        return CollectionModel.of(entities, self, next, prev);
    }

    @Override
    public CollectionModel<Certificate> buildPageLinks(List<Certificate> entities, LinkedHashMap<String, String> parameters, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Certificate entity : entities) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(entity.getId())).withRel("certificate");
            entity.add(certificateLink);
        }
        Link self = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset, currentLimit)).withSelfRel();
        Link next = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset + currentLimit, currentLimit)).withRel("next");
        Link prev = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset - currentLimit, currentLimit)).withRel("previous");
        return CollectionModel.of(entities, self, next, prev);
    }

    @Override
    public Tag buildTagLinks(Certificate certificate, Tag tag) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTag(certificate.getId(), tag.getId())).withSelfRel();
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(certificate.getId())).withRel("certificate");
        tag.add(selfLink, certificateLink);
        return tag;
    }

    @Override
    public CollectionModel<Tag> buildCertificateTagsPage(Certificate certificate, List<Tag> tags, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Tag tag : tags) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTag(certificate.getId(), tag.getId())).withRel("tag");
            tag.add(tagLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset, currentLimit)).withSelfRel();
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset - currentLimit, currentLimit)).withRel("previous");
        return CollectionModel.of(tags, selfLink, nextPageLink, previousPageLink);
    }

    @Override
    public CollectionModel<Order> buildCertificateOrdersPage(Certificate certificate, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset, currentLimit)).withSelfRel();
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset + currentLimit, currentLimit)).withRel("nextPage");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset - currentLimit, currentLimit)).withRel("previousPage");
        return CollectionModel.of(orders, selfLink, nextPageLink, previousPageLink);
    }
}
