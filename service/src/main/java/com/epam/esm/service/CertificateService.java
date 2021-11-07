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

    /**
     * Finds all certificates that match passed parameters
     * @param findParameters parameters by which need to find certificates
     * @param offset current page offset
     * @param limit current page limit
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     * @return list of certificate that match passed parameters
     */
    List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters, int offset, int limit) throws InvalidPageException, PageOutOfBoundsException;

    /**
     * Add passed tags to passed certificate
     * @param certificate certificate to which need to add tags
     * @param tags tags that need to be added to passed certificate
     * @return certificate with added tags
     * @throws InvalidResourceException if passed tag is invalid
     */
    Certificate addTags(Certificate certificate, List<Tag> tags) throws InvalidResourceException;

    /**
     * Deletes passed certificate passed tag
     * @param certificate whose tag need to be deleted
     * @param tag that need to be deleted
     */
    void deleteCertificateTag(Certificate certificate, Tag tag);

    /**
     * Finds and returns passed certificate tag that has passed id
     * @param certificate whose tag need to be find
     * @param tagId id of tag need to be found
     * @return found certificate that has passed id
     * @throws ResourceNotFoundException if there is no tag with passed id that belongs to passed certificate
     */
    Tag findCertificateTag(Certificate certificate, long tagId) throws ResourceNotFoundException;

    /**
     * Finds and returns passed certificate tags page
     * @param foundCertificate certificate whose tags page need to be found
     * @param offset current page offset
     * @param  limit current page limit
     * @return list of certificate tags on passed page
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    List<Tag> findCertificateTagsPage(Certificate foundCertificate, int offset, int limit) throws InvalidPageException, PageOutOfBoundsException;

    /**
     * Computes and returns certificate tags amount
     * @param certificate whose tags amount need to be counted
     * @return certificate tags amount
     */
    int getCertificateTagsTotalElements(Certificate certificate);

    /**
     * Finds passed certificate order.
     * @param certificate whose order need to be found
     * @param offset current page offset
     * @param limit current page limit
     * @return order that has passed certificate
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException is offset or limit is negative
     */
    List<Order> findCertificateOrders(Certificate certificate, int offset, int limit) throws PageOutOfBoundsException, InvalidPageException;

    /**
     * Finds certificate order
     * @param certificate certificate
     * @param orderId certificate order with id
     * @return certificate order that has passed id
     * @throws ResourceNotFoundException if certificate order is not found
     */
    Order findCertificateOrder(Certificate certificate, long orderId) throws ResourceNotFoundException;

}
