package com.epam.rest.dao;

import com.epam.rest.model.GiftCertificate;

import java.util.Optional;

public interface GiftCertificateDao extends Dao<GiftCertificate> {
    Optional<GiftCertificate> findByName(String name);

}
