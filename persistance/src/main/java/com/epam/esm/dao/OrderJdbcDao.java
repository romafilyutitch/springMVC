package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderJdbcDao extends AbstractDao<Order> implements OrderDao {

    public OrderJdbcDao() {
        super(Order.class.getSimpleName());
    }

    @Override
    public List<Order> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Order> query = session.createQuery("from Order", Order.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public Optional<Order> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Order order = session.get(Order.class, id);
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findAllUserOrders(long userId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Order> query = session.createQuery("from Order where user_id = ?1", Order.class);
        query.setParameter(1, userId);
        return query.list();
    }

    @Override
    public List<Order> findUserOrdersPage(long userId, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Order> query = session.createQuery("from Order where user_id = ?1", Order.class);
        query.setParameter(1, userId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public int getUserOrdersTotalElements(long userId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("select count(*) from Order where user_id = ?1", Long.class);
        query.setParameter(1, userId);
        Long userOrdersTotalElements = query.uniqueResult();
        return userOrdersTotalElements.intValue();
    }

    @Override
    public void setUserToOrder(long userId, long orderId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, userId);
        Order order = session.get(Order.class, orderId);
        user.getOrders().add(order);
        session.update(user);
        transaction.commit();
    }

    @Override
    public List<Order> findCertificateOrders(long certificateId, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Order> query = session.createQuery("from Order where certificate.id = ?1", Order.class);
        query.setParameter(1, certificateId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public int getCertificateOrdersTotalElements(long certificateId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("select count(*) from Order where certificate.id = ?1", Long.class);
        query.setParameter(1, certificateId);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }
}
