package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @Autowired
    public CertificateRestService(CertificateDao certificateDao, TagDao tagDao) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
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
     * Finds certificates that have tag with passed name
     *
     * @param tagName of tag that certificates have
     * @return list of certificates that have tag with passed name
     */
    @Override
    public List<Certificate> findByTagName(String tagName) {
        return certificateDao.findByTagName(tagName);
    }

    /**
     * Perform certificate save operation.
     * Saves certificate in database.
     * Sets certificate create time to current time
     *
     * @param certificate that need to be saved
     * @return saved certificate
     * @throws CertificateExistsException if there is other saved certificate with passed name
     */
    @Override
    public Certificate save(Certificate certificate) throws CertificateExistsException {
        Optional<Certificate> optionalCertificate = certificateDao.findByName(certificate.getName());
        if (optionalCertificate.isPresent()) {
            throw new CertificateExistsException(optionalCertificate.get().getId());
        }
        Certificate savedCertificate = certificateDao.save(certificate);
        savedCertificate.setCreateDate(LocalDateTime.now());
        return savedCertificate;
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
    public Certificate update(Long id, Certificate certificate) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            certificate.setId(id);
            Certificate updatedCertificate = certificateDao.update(certificate);
            updatedCertificate.setLastUpdateDate(LocalDateTime.now());
            return updatedCertificate;
        } else {
            throw new CertificateNotFoundException(id);
        }
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
     * Finds certificates which names contain passed name as part of name. Finds certificates by part of name
     * Uses certificates in database.
     *
     * @param name by which need to find certificate
     * @return list of certificates that have names that contains passed names.
     */
    @Override
    public List<Certificate> findByPartOfName(String name) {
        return certificateDao.searchByName(name);
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
    public Certificate addTags(Long certificateId, List<Tag> tags) throws CertificateNotFoundException {
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
