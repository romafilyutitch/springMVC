package com.epam.esm.dao;

import com.epam.esm.model.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
    protected final int rowsPerPage = 10;
    private final String entityName;

    public AbstractDao(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public T save(T entity) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();
        return entity;
    }

    @Override
    public T update(T entity) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
    }

    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(String.format("select count(*) from %s", entityName), Long.class);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }

    @Override
    public int getTotalPages() {
        int totalElements = getTotalElements();
        int totalPages = totalElements / rowsPerPage;
        return totalElements % rowsPerPage == 0 ? totalPages : ++totalPages;
    }
}
