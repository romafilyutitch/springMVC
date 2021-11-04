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

    @Override
    public List<User> findPage(int page) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("from User", User.class);
        query.setFirstResult(rowsPerPage * page - rowsPerPage);
        query.setMaxResults(rowsPerPage * page);
        return query.list();
    }

    @Override
    public Optional<User> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        return Optional.ofNullable(user);
    }

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
}
