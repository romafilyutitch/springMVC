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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Repository
public class TagJdbcDao implements TagDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateTemplate template;

    @Override
    public List<Tag> findPage(int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag", Tag.class);
        query.setFirstResult(5 * page);
        query.setMaxResults(5 * page + 5);
        return query.list();
    }

    @Override
    public Optional<Tag> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("from Tag where id = ?1", Tag.class);
        List<Tag> foundedTags = query.list();
        return foundedTags.isEmpty() ? Optional.empty() : Optional.of(foundedTags.get(0));
    }

    @Override
    public Tag save(Tag entity) {
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query<Tag> query = session.createQuery("update Tag set name = ?1 where id = ?2", Tag.class);
        query.setParameter(1, entity.getName());
        query.setParameter(2, entity.getId());
        query.executeUpdate();
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(long id) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("delete from Tag where id = ?1", Tag.class);
        query.setParameter(1, id);
        query.executeUpdate();
    }

    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Integer> query = session.createQuery("select count(*) from Tag", Integer.class);
        List<Integer> list = query.list();
        return list.get(0);
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
        List<Tag> list = query.list();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Tag> findCertificateTagsPage(long certificateId, int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select Tag from Certificate where id = ?1", Tag.class);
        query.setParameter(1, certificateId);
        query.setFirstResult(5 * page);
        query.setMaxResults(5 * page + 5);
        return query.list();
    }

    @Override
    public List<Tag> findAllCertificateTags(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Tag Certificate where id = ?1", Certificate.class);
        query.setParameter(1, certificateId);
        List<Certificate> list = query.list();
        Certificate certificate = list.get(0);
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
        Query<Integer> query = session.createQuery("select count(Tag) from Certificate where id = ?1", Integer.class);
        query.setParameter(1, certificateId);
        List<Integer> list = query.list();
        return list.get(0);
    }

    @Override
    public Optional<Tag> findCertificateTag(long certificateId, long tagId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("select Tag from Certificate where Certificate.id = ?1 and tag.id = ?2", Tag.class);
        query.setParameter(1, certificateId);
        query.setParameter(2, tagId);
        List<Tag> list = query.list();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
