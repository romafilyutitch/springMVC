package com.epam.rest.service;

import com.epam.rest.model.GiftCertificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateService {
    List<GiftCertificate> findAll();

    Optional<GiftCertificate> findById(Long id);

    Optional<GiftCertificate> findByName(String name);

    List<GiftCertificate> findByTagsNames(List<String> tagNames);

    GiftCertificate save(GiftCertificate certificate) throws CertificateExistsException;

    GiftCertificate update(Long id, GiftCertificate certificate);

    void delete(Long id);
}
