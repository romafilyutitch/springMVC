package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.InvalidResourceException;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Service layer certificate interface that defines
 * certificate operations
 */
public interface CertificateService extends Service<Certificate> {

    List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters) throws PageOutOfBoundsException;

    Certificate addTags(Certificate certificate, List<Tag> tags) throws InvalidResourceException;

    void deleteCertificateTag(Certificate certificate, Tag tag);

    Tag findCertificateTag(Certificate certificate, long tagId) throws ResourceNotFoundException;

    List<Tag> findCertificateTagsPage(Certificate foundCertificate, int page) throws PageOutOfBoundsException;

    int getCertificateTagsTotalPages(Certificate certificate);

    int getCertificateTagsTotalElements(Certificate certificate);

    Order findCertificateOrder(Certificate certificate) throws OrderNotFoundException;
}
