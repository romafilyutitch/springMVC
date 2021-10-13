package com.epam.esm.dao;

import com.epam.esm.model.Certificate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Dao layer interface that defines additional operations
 * for certificate entity.
 */
public interface CertificateDao extends Dao<Certificate> {

    List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters);
}
