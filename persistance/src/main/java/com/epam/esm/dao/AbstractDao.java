package com.epam.esm.dao;

import com.epam.esm.model.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Dao layer abstract dao class. If it is need to make dao for some
 * entity. You may extend this class to get basic CRUD operations.
 * Uses MySQL database to to save and manipulate entities.
 *
 * @param <T> entity which dao operates
 */
@Component
public abstract class AbstractDao<T extends Entity> implements Dao<T> {
    @Autowired
    protected SessionFactory sessionFactory;
    private final String entityName;

    public AbstractDao(String entityName) {
        this.entityName = entityName;
    }

    @Override
    @Transactional
    public T save(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        return entity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.update(entity);
        return entity;
    }

    @Override
    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(entity);
    }

    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(String.format("select count(*) from %s", entityName), Long.class);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }
}
