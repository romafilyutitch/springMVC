package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.DaoException;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

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
     * @param page page of entities that need to be found
     * @return list of entities on passed page
     * @throws PageOutOfBoundsException if page number is less then 1 and greater that pages amount
     */
    @Override
    public List<Certificate> findPage(int page) throws PageOutOfBoundsException {
        if (page < 1 || page > certificateDao.getTotalPages()) {
            throw new PageOutOfBoundsException(page, certificateDao.getTotalPages(), 1);
        }
        List<Certificate> certificatesPage = certificateDao.findPage(page);
        logger.info(String.format("certificates on page %d was found %s ", page, certificatesPage));
        return certificatesPage;
    }

    /**
     * Finds all certificates that match passed parameters
     * @param findParameters parameters by which need to find certificates
     * @return list of certificate that match passed parameters
     */
    @Override
    public List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters) {
        List<Certificate> foundCertificates = certificateDao.findWithParameters(findParameters);
        logger.info("Certificates with parameters were found " + foundCertificates);
        return foundCertificates;
    }
    /**
     * Finds and returns entity that has passed id
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
     * @param certificate that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     */
    @Override
    public Certificate save(Certificate certificate) throws InvalidResourceException {
        certificateValidator.validate(certificate);
        Certificate savedCertificate = certificateDao.save(certificate);
        logger.info("New certificate was validated and saved successfully " + savedCertificate);
        return savedCertificate;
    }
    /**
     * Updated entity and returns updated entity
     * @param certificate that need to be updated
     * @return updated entity
     * @throws InvalidResourceException if updated entity is invalid
     * @throws ResourceNotFoundException if updated entity is not saved and cannot be found
     */
    @Override
    public Certificate update(Certificate certificate) throws InvalidResourceException, ResourceNotFoundException {
        Certificate certificateFromTable = findById(certificate.getId());
        certificateFromTable.setName(certificate.getName() == null ? certificateFromTable.getName() : certificate.getName());
        certificateFromTable.setDescription(certificate.getDescription() == null ? certificateFromTable.getDescription() : certificate.getDescription());
        certificateFromTable.setPrice(certificate.getPrice() == 0.0 ? certificateFromTable.getPrice() : certificate.getPrice());
        certificateFromTable.setDuration(certificate.getDuration() == 0 ? certificateFromTable.getDuration() : certificate.getDuration());
        certificateFromTable.getTags().addAll(certificate.getTags());
        certificateValidator.validate(certificateFromTable);
        Certificate updatedCertificate = certificateDao.update(certificateFromTable);
        logger.info("Certificate was validated and updated successfully " + updatedCertificate);
        return updatedCertificate;
    }

    /**
     * Deletes saved entity
     * @param certificate entity that need to be saved
     */
    @Override
    public void delete(Certificate certificate) {
        certificateDao.delete(certificate);
        logger.info(String.format("Certificate was deleted %s", certificate));
    }
    /**
     * Add passed tags to passed certificate
     * @param certificate certificate to which need to add tags
     * @param tags tags that need to be added to passed certificate
     * @return certificate with added tags
     * @throws InvalidResourceException if passed tag is invalid
     */
    @Override
    public Certificate addTags(Certificate certificate, List<Tag> tags) throws InvalidResourceException {
        for (Tag tag : tags) {
            tagValidator.validate(tag);
        }
        List<Tag> certificateTags = certificate.getTags();
        certificateTags.addAll(tags);
        Certificate updatedCertificate = certificateDao.update(certificate);
        logger.info("Certificate was updated with new tags " + updatedCertificate);
        return updatedCertificate;
    }
    /**
     * Deletes passed certificate passed tag
     * @param certificate whose tag need to be deleted
     * @param tag that need to be deleted
     */
    @Override
    public void deleteCertificateTag(Certificate certificate, Tag tag) {
        tagDao.delete(tag);
        logger.info("Certificate tag was deleted successfully");
    }

    /**
     * Finds and returns passed certificate tag that has passed id
     * @param certificate whose tag need to be find
     * @param tagId id of tag need to be found
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
     * @param foundCertificate certificate whose tags page need to be found
     * @param page certificate tags page that need to be found
     * @return list of certificate tags on passed page
     * @throws PageOutOfBoundsException if page number is less than 1 and greater than pages amounts
     */
    @Override
    public List<Tag> findCertificateTagsPage(Certificate foundCertificate, int page) throws PageOutOfBoundsException {
        if (page < 1 || page > tagDao.getCertificateTagsTotalPages(foundCertificate.getId())) {
            throw new PageOutOfBoundsException(page, tagDao.getCertificateTagsTotalPages(foundCertificate.getId()), 1);
        }
        List<Tag> tagsPage = tagDao.findCertificateTagsPage(foundCertificate.getId(), page);
        logger.info(String.format("Certificate tags on page %d were found %s", page, tagsPage));
        return tagsPage;
    }
    /**
     * Computes and returns certificate tags pages amount
     * @param certificate whose tags pages need to be counted
     * @return certificate tags pages amount
     */
    @Override
    public int getCertificateTagsTotalPages(Certificate certificate) {
        return tagDao.getCertificateTagsTotalPages(certificate.getId());
    }
    /**
     * Computes and returns certificate tags amount
     * @param certificate whose tags amount need to be counted
     * @return certificate tags amount
     */
    @Override
    public int getCertificateTagsTotalElements(Certificate certificate) {
        return tagDao.getCertificateTagsTotalElements(certificate.getId());
    }
    /**
     * Computes and returns amount of entity elements
     * @return saved entities amount
     */
    @Override
    public int getTotalElements() {
        return certificateDao.getTotalElements();
    }
    /**
     * Computes and returns amount of entities pages
     * @return amount of entities pages
     */
    @Override
    public int getTotalPages() {
        return certificateDao.getTotalPages();
    }
    /**
     * Finds passed certificate order.
     * @param certificate whose order need to be found
     * @return order that has passed certificate
     * @throws ResourceNotFoundException if there is no orders that has passed certificate
     */
    @Override
    public Order findCertificateOrder(Certificate certificate) throws ResourceNotFoundException {
        Optional<Order> optionalOrder = orderDao.findByCertificateId(certificate.getId());
        if (optionalOrder.isPresent()) {
            logger.info(String.format("Certificate order was found %s", optionalOrder.get()));
            return optionalOrder.get();
        } else {
            logger.info(String.format("Order for certificate wasn't found %s", certificate));
            throw new OrderNotFoundException(certificate.getId());
        }
    }
}
