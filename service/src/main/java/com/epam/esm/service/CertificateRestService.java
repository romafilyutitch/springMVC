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
     * Finds all certificates.
     * May return empty list if there is not certificates
     *
     * @return list of all certificates.
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
     * Finds certificates that matches passed find parameters.
     * May return empty list if there is no certificates that matches
     * passed parameters.
     *
     * @param findParameters parameters that need to find certificates that
     *                       certificates must matches
     * @return list of certificates that matches passed parameters
     */
    @Override
    public List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters) throws PageOutOfBoundsException {
        String pageValue = findParameters.get("page");
        if (pageValue != null) {
            int page = Integer.parseInt(pageValue);
            if (page < 1 || page > certificateDao.getTotalPages()) {
                throw new PageOutOfBoundsException(page, certificateDao.getTotalPages(), 1);
            }
        }
        List<Certificate> foundCertificates = certificateDao.findWithParameters(findParameters);
        logger.info("Certificates with parameters were found " + foundCertificates);
        return foundCertificates;
    }

    @Override
    public Certificate findById(long id) throws ResourceNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            logger.info("Certificate was found by id " + optionalCertificate.get());
            return optionalCertificate.get();
        } else {
            logger.error("Certificate with id wasn't found " + id);
            throw new ResourceNotFoundException(id);
        }
    }

    @Override
    public Certificate save(Certificate certificate) throws InvalidResourceException {
        certificateValidator.validate(certificate);
        Certificate savedCertificate = certificateDao.save(certificate);
        logger.info("New certificate was validated and saved successfully " + savedCertificate);
        return savedCertificate;
    }

    @Override
    public Certificate update(Certificate certificate) throws InvalidResourceException {
        certificateValidator.validate(certificate);
        Certificate updatedCertificate = certificateDao.update(certificate);
        logger.info("Certificate was validated and updated successfully " + updatedCertificate);
        return updatedCertificate;
    }


    @Override
    public void delete(Certificate certificate) {
        certificateDao.delete(certificate.getId());
        logger.info(String.format("Certificate was deleted %s", certificate));
    }

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

    @Override
    public void deleteCertificateTag(Certificate certificate, Tag tag) {
        tagDao.delete(tag.getId());
        logger.info("Certificate tag was deleted successfully");
    }


    @Override
    public Tag findCertificateTag(Certificate certificate, long tagId) throws ResourceNotFoundException {
        Optional<Tag> optionalTag = tagDao.findCertificateTag(certificate.getId(), tagId);
        if (optionalTag.isPresent()) {
            logger.info(String.format("Certificate tag was found %s", optionalTag.get()));
            return optionalTag.get();
        } else {
            logger.error(String.format("Certificate tag with id %d wasn't found ", tagId));
            throw new ResourceNotFoundException(tagId);
        }
    }

    @Override
    public List<Tag> findCertificateTagsPage(Certificate foundCertificate, int page) throws PageOutOfBoundsException {
        if (page < 1 || page > tagDao.getCertificateTagsTotalPages(foundCertificate.getId())) {
            throw new PageOutOfBoundsException(page, tagDao.getCertificateTagsTotalPages(foundCertificate.getId()), 1);
        }
        List<Tag> tagsPage = tagDao.findCertificateTagsPage(foundCertificate.getId(), page);
        logger.info(String.format("Certificate tags on page %d were found %s", page, tagsPage));
        return tagsPage;
    }

    @Override
    public int getCertificateTagsTotalPages(Certificate certificate) {
        return tagDao.getCertificateTagsTotalPages(certificate.getId());
    }

    @Override
    public int getCertificateTagsTotalElements(Certificate certificate) {
        return tagDao.getCertificateTagsTotalElements(certificate.getId());
    }

    @Override
    public int getTotalElements() {
        return certificateDao.getTotalElements();
    }

    @Override
    public int getTotalPages() {
        return certificateDao.getTotalPages();
    }

    @Override
    public Order findCertificateOrder(Certificate certificate) throws OrderNotFoundException {
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
