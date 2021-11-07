package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificatesQueryBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private final FindCertificatesQueryBuilder builder;

    @Autowired
    public CertificateJdbcDao(FindCertificatesQueryBuilder builder) {
        super(Certificate.class.getSimpleName());
        this.builder = builder;
    }

    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters, int offset, int limit) {
        findParameters.put("offset", Integer.toString(offset));
        findParameters.put("limit", Integer.toString(limit));
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = builder.buildSql(findParameters, session);
        return query.list();
    }

    @Override
    public Optional<Certificate> findByOrderId(long orderId) {
        Session session = sessionFactory.getCurrentSession();
        Order order = session.get(Order.class, orderId);
        return Optional.of(order.getCertificate());
    }

    @Override
    public List<Certificate> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Certificate", Certificate.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public Optional<Certificate> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, id);
        return Optional.ofNullable(certificate);
    }
}
