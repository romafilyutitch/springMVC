package com.epam.esm.dao;

import com.epam.esm.model.Certificate;

import java.util.List;
import java.util.Optional;

public interface CertificateDao extends Dao<Certificate> {
    Optional<Certificate> findByName(String name);

    List<Certificate> findByTagName(String tagName);

    List<Certificate> searchByName(String name);
}
