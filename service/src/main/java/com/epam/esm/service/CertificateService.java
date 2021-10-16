package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidTagException;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Service layer certificate interface that defines
 * certificate operations
 */
public interface CertificateService {
    /**
     * Finds all certificates.
     * May return empty list if there is no certificates
     * @return list of certificates
     */
    List<Certificate> findAll();

    /**
     * Finds certificates that matches passed find parameters.
     * May return empty list if there is no certificates that matches
     * passed parameters.
     * @param findParameters parameters that need to find certificates that
     *                       certificates must matches
     * @return list of certificates that matches passed parameters
     */
    List<Certificate> findAllWithParameters(LinkedHashMap<String, String> findParameters);

    /**
     * Finds Certificate that has passed id.
     * @param id of certificate that need to be found
     * @return certificate that has passed id
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    Certificate findById(Long id) throws CertificateNotFoundException;

    /**
     * Performs save certificate operation. Saved only certificates with unique names
     * @param certificate that need to be saved
     * @throws InvalidCertificateException if passed certificate is invalid
     * @return saved certificate
     */
    Certificate save(Certificate certificate) throws InvalidCertificateException;

    /**
     * updates certificate that have passed id.
     * Replaces certificate with new certificate data
     * @param id certificate that need to be updated
     * @param certificate data that need to be write
     * @return updated certificate
     * @throws CertificateNotFoundException if there is not certificated with passed id
     * @throws InvalidCertificateException if passed certificate is invalid
     */
    Certificate update(Long id, Certificate certificate) throws CertificateNotFoundException, InvalidCertificateException;

    /**
     * Performs certificate delete operation.
     * @param id of certificate that need to be deleted
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    void delete(Long id) throws CertificateNotFoundException;

    /**
     * Performs add tags to certificate operation
     * @param certificateId id of certificate that need to be found
     * @param tags list of tags that need to be added to certificate
     * @return certificate with added tags
     * @throws CertificateNotFoundException if there is not certificate with passed id
     * @throws InvalidTagException if passed tag is invalid
     */
    Certificate addTags(Long certificateId, List<Tag> tags) throws CertificateNotFoundException, InvalidTagException;

    /**
     * Performs delete certificate tag operation
     * @param certificateId id of certificate that need to be found
     * @param tagId id of tag that need to be deleted
     * @throws CertificateNotFoundException if there is no certificate with passed id
     * @throws TagNotFoundException if there is no tag with passed id
     */
    void deleteCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException;

    /**
     * Finds certificate with passed id and finds in certificate tag with passed id
     * @param certificateId id of certificate that need to be found
     * @param tagId id of certificate tag that need to be found
     * @return certificate tag that has passed id
     * @throws CertificateNotFoundException if there is no certificate with passed id
     * @throws TagNotFoundException if there is no tag with passed id
     */
    Tag findCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException;
}
