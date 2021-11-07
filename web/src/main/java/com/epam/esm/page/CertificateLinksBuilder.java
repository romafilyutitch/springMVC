package com.epam.esm.page;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;

import java.util.LinkedHashMap;
import java.util.List;

public interface CertificateLinksBuilder extends LinksBuilder<Certificate> {

    CollectionModel<Certificate> buildPageLinks(List<Certificate> entities, LinkedHashMap<String, String> parameters, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException;

    Tag buildTagLinks(Certificate certificate, Tag tag) throws ResourceNotFoundException, PageOutOfBoundsException;

    CollectionModel<Tag> buildCertificateTagsPage(Certificate certificate, List<Tag> tags, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException;

    CollectionModel<Order> buildCertificateOrdersPage(Certificate certificate, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException;
}
