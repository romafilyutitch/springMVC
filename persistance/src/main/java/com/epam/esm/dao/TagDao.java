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

    /**
     * Finds and returns specified certificate tags page
     * @param certificateId id of certificate whose page need to be found
     * @param offset current page offset
     * @param limit current page limit
     * @return list of tags on specified page
     */
    List<Tag> findCertificateTagsPage(long certificateId, int offset, int limit);

    /**
     * Finds and returns all certificate tags
     * @param certificateId id of certificate whose tags need to be found
     * @return list of certificate tags
     */
    List<Tag> findAllCertificateTags(long certificateId);

    /**
     * Counts and returns certificate tags elements amount
     * @param certificateId id of certificate whose tags amount need to be counted
     * @return amount of specified certificate tags
     */
    int getCertificateTagsTotalElements(long certificateId);

    /**
     * Finds specified certificate specified tag
     * @param certificateId id of certificate whose tag need to be found
     * @param tagId id of certificate tag that need to be found
     * @return specified certificate specified tag if there is certificate tag
     * or empty optional otherwise
     */
    Optional<Tag> findCertificateTag(long certificateId, long tagId);
}
