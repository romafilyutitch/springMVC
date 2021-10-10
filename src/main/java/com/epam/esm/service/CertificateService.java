package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface CertificateService {
    List<Certificate> findAll();

    Optional<Certificate> findById(Long id);

    Optional<Certificate> findByName(String name);

    List<Certificate> findByTagName(String tagName);

    Certificate save(Certificate certificate) throws CertificateExistsException;

    Certificate update(Long id, Certificate certificate);

    List<Certificate> searchByName(String name);

    void delete(Long id);

    List<Certificate> sortByNameThenDate(List<Certificate> certificates, boolean nameAcceding, boolean dateAcceding);

    Certificate addTags(Long certificateId, List<Tag> tags);
}
