package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.CertificateValidator;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.TagValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer certificate certificate service implementation
 * of Service interface
 */
@Service
public class CertificateRestService implements CertificateService {
    private static final Logger logger = LogManager.getLogger(CertificateRestService.class);

    private final CertificateDao certificateDao;
    private final TagDao tagDao;
    private final OrderDao orderDao;

    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;

    @Autowired
    public CertificateRestService(CertificateDao certificateDao, TagDao tagDao, OrderDao orderDao, CertificateValidator certificateValidator, TagValidator tagValidator) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
        this.orderDao = orderDao;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
    }

    /**
     * Finds and returns entities on specified page
     *
     * @param offset current page offset
     * @param limit  current page limit
     * @return list of entities on passed page
     * @throws PageOutOfBoundsException offset is greater then total elements
     * @throws InvalidPageException     is offset or limit is negative
     */
    @Override
    public List<Certificate> findPage(int offset, int limit) throws InvalidPageException, PageOutOfBoundsException {
        checkPage(offset, limit, certificateDao.getTotalElements());
        return certificateDao.findPage(offset, limit);
    }

    /**
     * Finds all certificates that match passed parameters
     *
     * @param findParameters parameters by which need to find certificates
     * @param offset         current page offset
     * @param limit          current page limit
     * @return list of certificate that match passed parameters
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException     if offset or limit is negative
     */
    @Override
    public List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters, int offset, int limit) throws InvalidPageException, PageOutOfBoundsException {
        checkPage(offset, limit, certificateDao.getTotalElements());
        List<Certificate> foundCertificates = certificateDao.findWithParameters(findParameters, offset, limit);
        logger.info("Certificates with parameters were found " + foundCertificates);
        return foundCertificates;
    }

    /**
     * Finds and returns entity that has passed id
     *
     * @param id of entity that need to be found
     * @return entity that has passed id
     * @throws ResourceNotFoundException if there is no entity with passed id
     */
    @Override
    public Certificate findById(long id) throws ResourceNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            logger.info("Certificate was found by id " + optionalCertificate.get());
            return optionalCertificate.get();
        } else {
            logger.error("Certificate with id wasn't found " + id);
            throw new CertificateNotFoundException(id);
        }
    }

    /**
     * Saves entity and returns saved entity with assigned id
     *
     * @param certificate that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     */
    @Override
    public Certificate save(Certificate certificate) throws InvalidResourceException {
        certificateValidator.validate(certificate);
        List<Tag> tags = certificate.getTags();
        for (Tag tag : tags) {
            tagValidator.validate(tag);
        }
        List<Tag> tagsToSave = tags.stream().map(tag -> tagDao.findByName(tag.getName()).orElse(tag)).collect(Collectors.toList());
        certificate.setTags(tagsToSave);
        certificate.setCreateDate(LocalDateTime.now());
        Certificate savedCertificate = certificateDao.save(certificate);
        logger.info("New certificate was validated and saved successfully " + savedCertificate);
        return savedCertificate;
    }

    /**
     * Updated entity and returns updated entity
     *
     * @param certificate that need to be updated
     * @return updated entity
     * @throws InvalidResourceException  if updated entity is invalid
     * @throws ResourceNotFoundException if updated entity is not saved and cannot be found
     */
    @Override
    public Certificate update(Certificate certificate) throws InvalidResourceException, ResourceNotFoundException {
        Certificate certificateFromTable = findById(certificate.getId());
        certificateFromTable.setName(certificate.getName() == null ? certificateFromTable.getName() : certificate.getName());
        certificateFromTable.setDescription(certificate.getDescription() == null ? certificateFromTable.getDescription() : certificate.getDescription());
        certificateFromTable.setPrice(certificate.getPrice() == 0.0 ? certificateFromTable.getPrice() : certificate.getPrice());
        certificateFromTable.setDuration(certificate.getDuration() == 0 ? certificateFromTable.getDuration() : certificate.getDuration());
        List<Tag> tags = certificate.getTags();
        for (Tag tag : tags) {
            tagValidator.validate(tag);
        }
        List<Tag> tagsToUpdate = tags.stream().map(tag -> tagDao.findByName(tag.getName()).orElse(tag)).collect(Collectors.toList());
        List<Tag> certificateTags = certificateFromTable.getTags();
        tagsToUpdate.removeIf(certificateTags::contains);
        certificateTags.addAll(tagsToUpdate);
        certificateValidator.validate(certificateFromTable);
        certificateFromTable.setLastUpdateDate(LocalDateTime.now());
        Certificate updatedCertificate = certificateDao.update(certificateFromTable);
        logger.info("Certificate was validated and updated successfully " + updatedCertificate);
        return updatedCertificate;
    }

    /**
     * Deletes saved entity
     *
     * @param certificate entity that need to be saved
     */
    @Override
    public void delete(Certificate certificate) {
        certificateDao.delete(certificate);
        logger.info(String.format("Certificate was deleted %s", certificate));
    }

    /**
     * Add passed tags to passed certificate
     *
     * @param certificate certificate to which need to add tags
     * @param tags        tags that need to be added to passed certificate
     * @return certificate with added tags
     * @throws InvalidResourceException if passed tag is invalid
     */
    @Override
    public Certificate addTags(Certificate certificate, List<Tag> tags) throws InvalidResourceException {
        for (Tag tag : tags) {
            tagValidator.validate(tag);
        }
        List<Tag> tagsToUpdate = tags.stream().map(tag -> tagDao.findByName(tag.getName()).orElse(tag)).collect(Collectors.toList());
        List<Tag> certificateTags = certificate.getTags();
        tagsToUpdate.removeIf(certificateTags::contains);
        certificateTags.addAll(tagsToUpdate);
        certificate.setLastUpdateDate(LocalDateTime.now());
        Certificate updatedCertificate = certificateDao.update(certificate);
        logger.info("Certificate was updated with new tags " + updatedCertificate);
        return updatedCertificate;
    }

    /**
     * Deletes passed certificate passed tag
     *
     * @param certificate whose tag need to be deleted
     * @param tag         that need to be deleted
     */
    @Override
    public void deleteCertificateTag(Certificate certificate, Tag tag) {
        tagDao.delete(tag);
        certificate.setLastUpdateDate(LocalDateTime.now());
        certificateDao.update(certificate);
        logger.info("Certificate tag was deleted successfully");
    }

    /**
     * Finds and returns passed certificate tag that has passed id
     *
     * @param certificate whose tag need to be find
     * @param tagId       id of tag need to be found
     * @return found certificate that has passed id
     * @throws ResourceNotFoundException if there is no tag with passed id that belongs to passed certificate
     */
    @Override
    public Tag findCertificateTag(Certificate certificate, long tagId) throws ResourceNotFoundException {
        Optional<Tag> optionalTag = tagDao.findCertificateTag(certificate.getId(), tagId);
        if (optionalTag.isPresent()) {
            logger.info(String.format("Certificate tag was found %s", optionalTag.get()));
            return optionalTag.get();
        } else {
            logger.error(String.format("Certificate tag with id %d wasn't found ", tagId));
            throw new TagNotFoundException(tagId);
        }
    }

    /**
     * Finds and returns passed certificate tags page
     *
     * @param foundCertificate certificate whose tags page need to be found
     * @param offset           current page offset
     * @param limit            current page limit
     * @return list of certificate tags on passed page
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException     if offset or limit is negative
     */
    @Override
    public List<Tag> findCertificateTagsPage(Certificate foundCertificate, int offset, int limit) throws PageOutOfBoundsException, InvalidPageException {
        checkPage(offset, limit, tagDao.getCertificateTagsTotalElements(foundCertificate.getId()));
        return tagDao.findCertificateTagsPage(foundCertificate.getId(), offset, limit);
    }

    /**
     * Computes and returns certificate tags amount
     *
     * @param certificate whose tags amount need to be counted
     * @return certificate tags amount
     */
    @Override
    public int getCertificateTagsTotalElements(Certificate certificate) {
        return tagDao.getCertificateTagsTotalElements(certificate.getId());
    }

    /**
     * Computes and returns certificate orders amount
     *
     * @param certificate whose orders need to count
     * @return certificate orders amount
     */
    @Override
    public int getCertificateOrdersTotalElements(Certificate certificate) {
        return orderDao.getCertificateOrdersTotalElements(certificate.getId());
    }

    /**
     * Computes and returns amount of entity elements
     *
     * @return saved entities amount
     */
    @Override
    public int getTotalElements() {
        return certificateDao.getTotalElements();
    }

    /**
     * Finds passed certificate order.
     *
     * @param certificate whose order need to be found
     * @param offset      current page offset
     * @param limit       current page limit
     * @return order that has passed certificate
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException     is offset or limit is negative
     */
    @Override
    public List<Order> findCertificateOrders(Certificate certificate, int offset, int limit) throws InvalidPageException, PageOutOfBoundsException {
        checkPage(offset, limit, orderDao.getCertificateOrdersTotalElements(certificate.getId()));
        List<Order> certificateOrders = orderDao.findCertificateOrders(certificate.getId(), offset, limit);
        logger.info(String.format("Certificate with id %d orders were found %s", certificate.getId(), certificateOrders));
        return certificateOrders;
    }

    /**
     * Finds certificate order
     *
     * @param certificate certificate
     * @param orderId     certificate order with id
     * @return certificate order that has passed id
     * @throws ResourceNotFoundException if certificate order is not found
     */
    @Override
    public Order findCertificateOrder(Certificate certificate, long orderId) throws ResourceNotFoundException {
        Optional<Order> byId = orderDao.findById(orderId);
        if (byId.isPresent()) {
            logger.info(String.format("Order with id was found by id %s", byId.get()));
            return byId.get();
        } else {
            logger.error(String.format("Order with id %d wasn't found", orderId));
            throw new OrderNotFoundException(orderId);
        }
    }


    private void checkPage(int offset, int limit, int totalElements) throws InvalidPageException, PageOutOfBoundsException {
        if (offset < 0 || limit <= 0) {
            throw new InvalidPageException(offset, limit);
        }
        if (offset >= totalElements) {
            throw new PageOutOfBoundsException(offset, totalElements);
        }
    }
}
