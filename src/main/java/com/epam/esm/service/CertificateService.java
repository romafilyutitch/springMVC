package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;

import java.util.List;

public interface CertificateService {
    List<Certificate> findAll();

    Certificate findById(Long id) throws CertificateNotFoundException;

    List<Certificate> findByTagName(String tagName);

    Certificate save(Certificate certificate) throws CertificateExistsException;

    Certificate update(Long id, Certificate certificate) throws CertificateNotFoundException;

    List<Certificate> searchByName(String name);

    void delete(Long id) throws CertificateNotFoundException;

    List<Certificate> sortByNameThenDate(List<Certificate> certificates, boolean nameAcceding, boolean dateAcceding);

    Certificate addTags(Long certificateId, List<Tag> tags) throws CertificateNotFoundException;

    void deleteCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException;

    Tag findCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException;
}
