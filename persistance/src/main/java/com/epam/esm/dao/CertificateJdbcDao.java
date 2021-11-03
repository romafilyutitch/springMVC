package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificatesSqlBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Realisation of abstract dao for certificate that use gift_certificate table to
 * perform CRUD operations with database table. Uses SQL queries to perform those operations.
 * Certificates contains list of tags that related to each other as many to many relationship so class
 * also operates certificate_tag table to link tag with certificate and use tag table to save certificate tags
 */
@Repository
public class CertificateJdbcDao implements CertificateDao {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private FindCertificatesSqlBuilder builder;

    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = builder.buildSql(findParameters, criteriaBuilder);
        Query<Certificate> query = session.createQuery(criteriaQuery);
        System.out.println(query.getQueryString());
        System.out.println(query.list());
        return query.list();
    }

    @Override
    public Optional<Certificate> findByOrderId(long orderId) {
        Session session = sessionFactory.getCurrentSession();
        Order order = session.get(Order.class, orderId);
        return Optional.of(order.getCertificate());
    }

    @Override
    public List<Certificate> findPage(int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Certificate", Certificate.class);
        query.setFirstResult(5 * page - 5);
        query.setMaxResults(5 * page);
        return query.list();
    }

    @Override
    public Optional<Certificate> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, id);
        return Optional.ofNullable(certificate);
    }

    @Override
    public Certificate save(Certificate entity) {
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        return entity;
    }

    @Override
    public Certificate update(Certificate entity) {
        Session session = sessionFactory.getCurrentSession();
        session.update(entity);
        return entity;
    }

    @Override
    public void delete(long id) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, id);
        session.delete(certificate);
    }

    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("select count(id) from Certificate", Long.class);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }

    @Override
    public int getTotalPages() {
        int totalElements = getTotalElements();
        int totalRows = totalElements / 5;
        return totalElements % 5 == 0 ? totalRows : ++totalRows;
    }
}
