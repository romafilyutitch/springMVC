package com.epam.esm.page;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import org.springframework.hateoas.PagedModel;

import java.util.LinkedHashMap;
import java.util.List;

public interface CertificateLinksBuilder extends LinksBuilder<Certificate> {

    /**
     * Build certificate page links base with parameters
     *
     * @param entities      certificates page
     * @param parameters    find parameters
     * @param currentOffset current page offset
     * @param currentLimit  current page limit
     * @return certificates that have built links
     * @throws ResourceNotFoundException if certificate if not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is negative
     */
    PagedModel<Certificate> buildPageLinks(List<Certificate> entities, LinkedHashMap<String, String> parameters, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    /**
     * Build certificate tag links
     *
     * @param certificate certificate
     * @param tag         certificate tag
     * @return tag that has built links
     * @throws ResourceNotFoundException if tag is not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is negative
     */
    Tag buildTagLinks(Certificate certificate, Tag tag) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    /**
     * build certificate tags page links
     *
     * @param certificate   certificate
     * @param tags          certificate tags
     * @param currentOffset current page offset
     * @param currentLimit  current page limit
     * @return tags that have build links
     * @throws ResourceNotFoundException if tag is not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is negative
     */
    PagedModel<Tag> buildCertificateTagsPage(Certificate certificate, List<Tag> tags, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    /**
     * build certificate orders page links
     *
     * @param certificate   certificate
     * @param orders        certificate orders
     * @param currentOffset current page offset
     * @param currentLimit  current page limit
     * @return orders that have built links
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is negative
     */
    PagedModel<Order> buildCertificateOrdersPage(Certificate certificate, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;
}
