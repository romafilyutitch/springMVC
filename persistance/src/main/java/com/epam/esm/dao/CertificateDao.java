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

    /**
     * Finds certificates that matches passed parameters
     * such as tag names, part of name , part of description.
     * Also make sorting based on passed sorting parameters
     * @param findParameters parameters by which need to find certificates
     * @return certificates that match passed parameters
     */
    List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters);

    /**
     * Finds certificate by passed order id.
     * @param orderId order id by which need to find
     *                certificate
     * @return found certificate if there is order with passed certificate
     * or empty optional otherwise
     */
    Optional<Certificate> findByOrderId(long orderId);

}
