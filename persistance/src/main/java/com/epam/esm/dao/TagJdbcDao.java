package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Repository
public class TagJdbcDao extends AbstractDao<Tag> implements TagDao {

    public TagJdbcDao() {
        super(Tag.class.getSimpleName());
    }

    /**
     * Finds and returns entities on specified page
     * @param offset current page offset
     * @param limit  current page limit
     * @return entities on passed page
     */
    @Override
    public List<Tag> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag", Tag.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    /**
     * Finds and returns entity that have passed id
     *
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity with passed id exists
     * or empty optional otherwise
     */
    @Override
    public Optional<Tag> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Tag tag = session.get(Tag.class, id);
        return Optional.ofNullable(tag);
    }

    /**
     * Finds tag that has passed name.
     * May return empty optional if there is no tag with passed name
     *
     * @param name of tag that need to be found
     * @return optional tag if there is tag with passed name
     * or empty tag otherwise
     */
    @Override
    public Optional<Tag> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag where name = ?1", Tag.class);
        query.setParameter(1, name);
        return query.uniqueResultOptional();
    }

    /**
     * Finds and returns specified certificate tags page
     * @param certificateId id of certificate whose page need to be found
     * @param offset current page offset
     * @param limit current page limit
     * @return list of tags on specified page
     */
    @Override
    public List<Tag> findCertificateTagsPage(long certificateId, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select t from Certificate c join c.tags t where c.id = ?1", Tag.class);
        query.setParameter(1, certificateId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    /**
     * Finds and returns all certificate tags
     * @param certificateId id of certificate whose tags need to be found
     * @return list of certificate tags
     */
    @Override
    public List<Tag> findAllCertificateTags(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Certificate where id = ?1", Certificate.class);
        query.setParameter(1, certificateId);
        Certificate certificate = query.uniqueResult();
        return certificate.getTags();
    }

    /**
     * Counts and returns certificate tags elements amount
     * @param certificateId id of certificate whose tags amount need to be counted
     * @return amount of specified certificate tags
     */
    @Override
    public int getCertificateTagsTotalElements(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, certificateId);
        List<Tag> certificateTags = certificate.getTags();
        return certificateTags.size();
    }

    /**
     * Finds specified certificate specified tag
     * @param certificateId id of certificate whose tag need to be found
     * @param tagId id of certificate tag that need to be found
     * @return specified certificate specified tag if there is certificate tag
     * or empty optional otherwise
     */
    @Override
    public Optional<Tag> findCertificateTag(long certificateId, long tagId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select t from Certificate c join c.tags t where c.id = ?1 and t.id = ?2", Tag.class);
        query.setParameter(1, certificateId);
        query.setParameter(2, tagId);
        return query.uniqueResultOptional();
    }
}
