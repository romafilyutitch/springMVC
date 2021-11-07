package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.el.Expression;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcDao extends AbstractDao<User> implements UserDao {

    public UserJdbcDao() {
        super(User.class.getSimpleName());
    }

    /**
     * Finds and returns entities on specified page
     * @param offset current page offset
     * @param limit  current page limit
     * @return entities on passed page
     */
    @Override
    public List<User> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("from User", User.class);
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
    public Optional<User> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        return Optional.ofNullable(user);
    }

    /**
     * Finds and returns riches user.
     * Riches user is the user that has maximum of orders cost.
     * @return user that has maximum orders cost
     */
    @Override
    public User findRichestUser() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Join<User, Order> join = root.join("orders");
        criteriaQuery.select(root).groupBy(root.get("id")).orderBy(criteriaBuilder.desc(criteriaBuilder.sum(join.get("cost"))));
        Query<User> query = session.createQuery(criteriaQuery);
        return query.list().get(0);
    }

    /**
     * Finds and returns riches user popular tag.
     * Popular tag is tag that uses most frequently amount richest
     * user orders
     * @return popular tag
     */
    @Override
    public Tag findRichestUserPopularTag() {
        User richestUser = findRichestUser();
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<User> root = criteriaQuery.from(User.class);
        Join<Certificate, Tag> join = root.join("orders").join("certificate").join("tags");
        criteriaQuery.select(join).where(criteriaBuilder.equal(root.get("id"), richestUser.getId())).groupBy(join).orderBy(criteriaBuilder.desc(criteriaBuilder.count(join)));
        Query<Tag> query = session.createQuery(criteriaQuery);
        System.out.println(query.getQueryString());
        System.out.println(query.list());
        return query.list().get(0);
    }

    /**
     * Finds user that has order with passed id
     * @param id order id
     * @return user that has order with passed id
     */
    @Override
    public User findByOrderId(long id) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("select u from User u join u.orders o where o.id = ?1", User.class);
        query.setParameter(1, id);
        return query.uniqueResult();
    }
}


