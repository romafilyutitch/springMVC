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

    @Override
    public List<Tag> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag", Tag.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public Optional<Tag> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Tag tag = session.get(Tag.class, id);
        return Optional.ofNullable(tag);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag where name = ?1", Tag.class);
        query.setParameter(1, name);
        return query.uniqueResultOptional();
    }

    @Override
    public List<Tag> findCertificateTagsPage(long certificateId, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select t from Certificate c join c.tags t where c.id = ?1", Tag.class);
        query.setParameter(1, certificateId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public List<Tag> findAllCertificateTags(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Certificate where id = ?1", Certificate.class);
        query.setParameter(1, certificateId);
        Certificate certificate = query.uniqueResult();
        return certificate.getTags();
    }

    @Override
    public int getCertificateTagsTotalElements(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, certificateId);
        List<Tag> certificateTags = certificate.getTags();
        return certificateTags.size();
    }

    @Override
    public Optional<Tag> findCertificateTag(long certificateId, long tagId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select t from Certificate c join c.tags t where c.id = ?1 and t.id = ?2", Tag.class);
        query.setParameter(1, certificateId);
        query.setParameter(2, tagId);
        return query.uniqueResultOptional();
    }
}
