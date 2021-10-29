package com.epam.esm.dao;

import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

/**
 * DAO layer tag interface to define tag specific operations
 * for tag entity
 */
public interface TagDao extends Dao<Tag> {
    /**
     * Finds tag that has passed name.
     * May return empty optional if there is no tag with passed name
     *
     * @param name of tag that need to be found
     * @return optional tag if there is tag with passed name
     * or empty tag otherwise
     */
    Optional<Tag> findByName(String name);

    List<Tag> findCertificateTagsPage(Long certificateId, int page);

    List<Tag> findAllCertificateTags(Long certificateId);

    int getCertificateTagsTotalPages(Long certificateId);

    long getCertificateTagsTotalElements(Long certificateId);
}
