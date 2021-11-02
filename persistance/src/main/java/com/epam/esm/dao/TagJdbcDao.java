package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.apache.catalina.mapper.Mapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Repository
public class TagJdbcDao implements TagDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Tag> findPage(int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag", Tag.class);
        query.setFirstResult(5 * page - 5);
        query.setMaxResults(5 * page);
        return query.list();
    }

    @Override
    public Optional<Tag> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Tag tag = session.find(Tag.class, id);
        return Optional.ofNullable(tag);
    }

    @Override
    public Tag save(Tag entity) {
        Session session = sessionFactory.getCurrentSession();
        Serializable id = session.save(entity);
        return session.find(Tag.class, id);
    }

    @Override
    public Tag update(Tag entity) {
        Session session = sessionFactory.getCurrentSession();
        session.update(entity);
        return session.find(Tag.class, entity.getId());
    }

    @Override
    public void delete(long id) {
        Session session = sessionFactory.getCurrentSession();
        Tag tag = session.find(Tag.class, id);
        session.delete(tag);
    }

    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("select count(*) from Tag", Long.class);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }

    @Override
    public int getTotalPages() {
        int totalElements = getTotalElements();
        int totalPages = totalElements / 5;
        return totalElements % 5 == 0 ? totalPages : ++totalPages;
    }

    @Override
    public Optional<Tag> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select from Tag where name = ?1", Tag.class);
        query.setParameter(1, name);
        return query.uniqueResultOptional();
    }

    @Override
    public List<Tag> findCertificateTagsPage(long certificateId, int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select t from Certificate c join c.tags t where c.id = ?1", Tag.class);
        query.setParameter(1, certificateId);
        query.setFirstResult(5 * page - 5);
        query.setMaxResults(5 * page);
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
    public int getCertificateTagsTotalPages(long certificateId) {
        int totalElements = getCertificateTagsTotalElements(certificateId);
        int totalPages = totalElements / 5;
        return totalElements % 5 == 0 ? totalPages : ++totalPages;
    }

    @Override
    public int getCertificateTagsTotalElements(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.find(Certificate.class, certificateId);
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
