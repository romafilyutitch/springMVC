package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificatesQueryBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    /**
     * Finds certificates that matches passed parameters
     * such as tag names, part of name , part of description.
     * Also make sorting based on passed sorting parameters
     *
     * @param findParameters parameters by which need to find certificates
     * @param offset         current page offset
     * @param limit          current page limit
     * @return certificates that match passed parameters
     */
    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters, int offset, int limit) {
        findParameters.put("offset", Integer.toString(offset));
        findParameters.put("limit", Integer.toString(limit));
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = builder.buildSql(findParameters, session);
        return query.list();
    }

    /**
     * Finds certificate by passed order id.
     *
     * @param orderId order id by which need to find
     *                certificate
     * @return found certificate if there is order with passed certificate
     * or empty optional otherwise
     */
    @Override
    public Optional<Certificate> findByOrderId(long orderId) {
        Session session = sessionFactory.getCurrentSession();
        Order order = session.get(Order.class, orderId);
        return Optional.of(order.getCertificate());
    }

    /**
     * Finds and returns entities on specified page
     *
     * @param offset current page offset
     * @param limit  current page limit
     * @return entities on passed page
     */
    @Override
    public List<Certificate> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Certificate> query = session.createQuery("from Certificate", Certificate.class);
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
    public Optional<Certificate> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Certificate certificate = session.get(Certificate.class, id);
        return Optional.ofNullable(certificate);
    }
}
