package com.epam.rest.service;

import com.epam.rest.model.Certificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateService {
    List<Certificate> findAll();

    Optional<Certificate> findById(Long id);

    Optional<Certificate> findByName(String name);

    List<Certificate> findByTagsNames(List<String> tagNames);

    Certificate save(Certificate certificate) throws CertificateExistsException;

    Certificate update(Long id, Certificate certificate);

    void delete(Long id);
}
