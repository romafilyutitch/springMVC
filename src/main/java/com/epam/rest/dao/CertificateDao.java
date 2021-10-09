package com.epam.rest.dao;

import com.epam.rest.model.Certificate;

import java.util.Optional;

public interface CertificateDao extends Dao<Certificate> {
    Optional<Certificate> findByName(String name);

}
