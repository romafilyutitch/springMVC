package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.CertificateValidator;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidTagException;
import com.epam.esm.validation.TagValidator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Service layer certificate certificate service implementation
 * of Service interface
 */
@Component
public class CertificateRestService implements CertificateService {
    private final CertificateDao certificateDao;
    private final TagDao tagDao;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;

    public CertificateRestService(CertificateDao certificateDao, TagDao tagDao, CertificateValidator certificateValidator, TagValidator tagValidator) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
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
    public List<Certificate> findAll() {
        return certificateDao.findAll();
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
    public List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters) {
        return certificateDao.findWithParameters(findParameters);
    }

    /**
     * Finds certificate that has passed id
     *
     * @param id of certificate that need to be found
     * @return certificate that has passed id
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    @Override
    public Certificate findById(Long id) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            return optionalCertificate.get();
        } else {
            throw new CertificateNotFoundException(id);
        }
    }

    /**
     * Perform certificate save operation.
     * Saves certificate in database.
     * Sets certificate create time to current time
     *
     * @param certificate that need to be saved
     * @return saved certificate
     */
    @Override
    public Certificate save(Certificate certificate) throws InvalidCertificateException {
        certificateValidator.validate(certificate);
        return certificateDao.save(certificate);
    }

    /**
     * Performs update certificate operation.
     * Updates certificate in database. and save certificate
     * last update time to current time
     *
     * @param id          certificate that need to be updated
     * @param certificate data that need to be write
     * @return updated certificate
     * @throws CertificateNotFoundException if there is not certificate wit passed id
     */
    @Override
    public Certificate update(Long id, Certificate certificate) throws CertificateNotFoundException, InvalidCertificateException {
        certificateValidator.validate(certificate);
        Optional<Certificate> certificateFromDb = certificateDao.findById(id);
        if (certificateFromDb.isPresent()) {
            Certificate modifiedCertificate = modifyForUpdate(certificateFromDb.get(), certificate);
            return certificateDao.update(modifiedCertificate);
        } else {
            throw new CertificateNotFoundException(id);
        }
    }

    private Certificate modifyForUpdate(Certificate fromDb, Certificate fromRequest) {
        fromDb.setName(fromRequest.getName() == null ? fromDb.getName() : fromRequest.getName());
        fromDb.setDescription(fromRequest.getDescription() == null ? fromDb.getDescription() : fromRequest.getDescription());
        fromDb.setPrice(fromRequest.getPrice() == null ? fromDb.getPrice() : fromRequest.getPrice());
        fromDb.setDuration(fromRequest.getDuration() == null ? fromDb.getDuration() : fromRequest.getDuration());
        return fromDb;
    }

    /**
     * Performs delete certificate operation.
     * Delete certificate from database.
     *
     * @param id of certificate that need to be deleted
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @Override
    public void delete(Long id) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            certificateDao.delete(id);
        } else {
            throw new CertificateNotFoundException(id);
        }
    }

    /**
     * Finds certificate and add to certificate new tags.
     *
     * @param certificateId id of certificate that need to be found
     * @param tags          list of tags that need to be added to certificate
     * @return certificate with added tags
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @Override
    public Certificate addTags(Long certificateId, List<Tag> tags) throws CertificateNotFoundException, InvalidTagException {
        for (Tag tag : tags) {
            tagValidator.validate(tag);
        }
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            List<Tag> certificateTags = certificate.getTags();
            certificateTags.addAll(tags);
            return certificateDao.update(certificate);
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }

    /**
     * Performs delete certificate tag operation.
     * Finds Certificate with passed id and finds tag with passed id.
     *
     * @param certificateId id of certificate that need to be found
     * @param tagId         id of tag that need to be deleted
     * @throws CertificateNotFoundException if there is not certificate with passed id
     * @throws TagNotFoundException         if there is not tag with passed id
     */
    @Override
    public void deleteCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            List<Tag> tags = certificate.getTags();
            boolean tagExists = tags.stream().anyMatch(tag -> tag.getId().equals(tagId));
            if (tagExists) {
                tagDao.delete(tagId);
            } else {
                throw new TagNotFoundException(tagId);
            }
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }

    /**
     * Finds certificate tag with passed id.
     *
     * @param certificateId id of certificate that need to be found
     * @param tagId         id of certificate tag that need to be found
     * @return certificate tag with passed id
     * @throws CertificateNotFoundException if there is no certificate with passed id
     * @throws TagNotFoundException         if there is no tag with passed id
     */
    @Override
    public Tag findCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            List<Tag> tags = certificate.getTags();
            Optional<Tag> optionalTag = tags.stream().filter(tag -> tag.getId().equals(tagId)).findAny();
            if (optionalTag.isPresent()) {
                return optionalTag.get();
            } else {
                throw new TagNotFoundException(tagId);
            }
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }
}
