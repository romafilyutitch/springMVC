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

    /**
     * builds links for passed entity
     * @param entity entity to build links
     * @return entity that has  built links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset if greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
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

    /**
     * Builds links for passed entity page
     * @param entities entities to build links
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return entities that have build links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public CollectionModel<Certificate> buildPageLinks(List<Certificate> entities, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Certificate entity : entities) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(entity.getId())).withRel("certificate");
            entity.add(certificateLink);
        }
        int totalElements = service.getTotalElements();
        List<Link> links = makeCertificatePageLinks(entities, null, currentOffset, currentLimit, totalElements);
        PagedModel.PageMetadata pageMetadata = makePageMetadata(entities.size(), currentOffset, currentLimit, totalElements);
        return PagedModel.of(entities, pageMetadata, links);
    }

    /**
     * Build certificate page links base with parameters
     * @param entities certificates page
     * @param parameters find parameters
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return certificates that have built links
     * @throws ResourceNotFoundException if certificate if not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public CollectionModel<Certificate> buildPageLinks(List<Certificate> entities, LinkedHashMap<String, String> parameters, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Certificate entity : entities) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(entity.getId())).withRel("certificate");
            entity.add(certificateLink);
        }
        int totalElements = service.getTotalElements();
        List<Link> links = makeCertificatePageLinks(entities, parameters, currentOffset, currentLimit, totalElements);
        PagedModel.PageMetadata pageMetadata = makePageMetadata(entities.size(), currentOffset, currentLimit, totalElements);
        return PagedModel.of(entities, pageMetadata, links);
    }

    /**
     * Build certificate tag links
     * @param certificate certificate
     * @param tag certificate tag
     * @return tag that has built links
     * @throws ResourceNotFoundException if tag is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public Tag buildTagLinks(Certificate certificate, Tag tag) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTag(certificate.getId(), tag.getId())).withSelfRel();
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(certificate.getId())).withRel("certificate");
        tag.add(selfLink, certificateLink);
        return tag;
    }

    /**
     * build certificate tags page links
     * @param certificate certificate
     * @param tags certificate tags
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return tags that have build links
     * @throws ResourceNotFoundException if tag is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public CollectionModel<Tag> buildCertificateTagsPage(Certificate certificate, List<Tag> tags, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Tag tag : tags) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTag(certificate.getId(), tag.getId())).withRel("tag");
            tag.add(tagLink);
        }
        int certificateTagsTotalElements = service.getCertificateTagsTotalElements(certificate);
        List<Link> links = makeCertificateTagsPageLinks(certificate, tags, currentOffset, currentLimit, certificateTagsTotalElements);
        PagedModel.PageMetadata pageMetadata = makePageMetadata(tags.size(), currentOffset, currentLimit, certificateTagsTotalElements);
        return PagedModel.of(tags, pageMetadata, links);
    }

    /**
     * build certificate orders page links
     * @param certificate certificate
     * @param orders certificate orders
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return orders that have built links
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public CollectionModel<Order> buildCertificateOrdersPage(Certificate certificate, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for(Order order : orders) {
            Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(certificate.getId(), order.getId())).withRel("order");
            order.add(orderLink);
        }
        int certificateOrdersTotalElements = service.getCertificateOrdersTotalElements(certificate);
        List<Link> links = makeCertificateOrdersPageLinks(certificate, orders, currentOffset, currentLimit, certificateOrdersTotalElements);
        PagedModel.PageMetadata pageMetadata = makePageMetadata(orders.size(), currentOffset, currentLimit, certificateOrdersTotalElements);
        return PagedModel.of(orders, pageMetadata, links);
    }

    private List<Link> makeCertificatePageLinks(List<Certificate> certificates, LinkedHashMap<String, String> parameters, int currentOffset, int currentLimit, int totalElements) throws PageOutOfBoundsException, ResourceNotFoundException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset, currentLimit)).withSelfRel();
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificates(parameters, currentOffset - currentLimit, currentLimit)).withRel("previous");
        Link firstPageLink = linkTo(methodOn(CertificateController.class).showCertificates(parameters, 0, currentLimit)).withRel("first");
        Link lastPageLink = linkTo(methodOn(CertificateController.class).showCertificates(parameters, totalElements - currentLimit, currentLimit)).withRel("last");
        return Arrays.asList(selfLink, nextPageLink, previousPageLink, firstPageLink, lastPageLink);
    }

    private List<Link> makeCertificateTagsPageLinks(Certificate certificate, List<Tag> tags, int currentOffset, int currentLimit, int totalElements) throws PageOutOfBoundsException, ResourceNotFoundException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset, currentLimit)).withSelfRel();
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), currentOffset - currentLimit, currentLimit)).withRel("previous");
        Link firstPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), 0, currentLimit)).withRel("first");
        Link lastPageLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId(), totalElements - currentLimit, currentLimit)).withRel("last");
        return Arrays.asList(selfLink, nextPageLink, previousPageLink, firstPageLink, lastPageLink);
    }

    private List<Link> makeCertificateOrdersPageLinks(Certificate certificate, List<Order> orders, int currentOffset, int currentLimit, int totalElements) throws PageOutOfBoundsException, ResourceNotFoundException, InvalidPageException {
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset, currentLimit)).withSelfRel();
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), currentOffset - currentLimit, currentLimit)).withRel("previous");
        Link firstPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), 0, currentLimit)).withRel("first");
        Link lastPageLink = linkTo(methodOn(CertificateController.class).showCertificateOrders(certificate.getId(), totalElements - currentLimit, currentLimit)).withRel("last");
        return Arrays.asList(selfLink, nextPageLink, previousPageLink, firstPageLink, lastPageLink);
    }

    private PagedModel.PageMetadata makePageMetadata(int size, int offset, int limit, int totalElements) {
        int number = offset / limit + 1;
        number = offset % limit == 0 ? number : ++number;
        int pages = totalElements / limit;
        pages = totalElements % limit == 0 ? pages : ++pages;
        return new PagedModel.PageMetadata(size, number, totalElements, pages);
    }
}
