package com.epam.esm.dao;

import com.epam.esm.model.Certificate;

import java.util.List;
import java.util.Optional;

/**
 * Dao layer interface that defines additional operations
 * for certificate entity.
 */
public interface CertificateDao extends Dao<Certificate> {
    /**
     * Finds Certificate that has passed name.
     * May return empty optional to show that there is no
     * certificate with passed name
     * @param name of certificate that need to be found
     * @return Optional that contains certificate if there is
     * certificate with passed name of empty optional otherwise
     */
    Optional<Certificate> findByName(String name);

    /**
     * Finds Certificates that have tag with passed name.
     * May return empty list
     * @param tagName name of tag that need to be found
     * @return list of certificates that have tag with passed name or
     * empty list if there is not tag with passed name
     */
    List<Certificate> findByTagName(String tagName);

    /**
     * Finds Certificate which part of name is passed
     * argument.
     * @param name part of certificate name that need to be found
     * @return list of certificate which names contains passed argument
     * or empty list
     */
    List<Certificate> searchByName(String name);
}
