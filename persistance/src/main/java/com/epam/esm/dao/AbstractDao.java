package com.epam.esm.dao;

import com.epam.esm.model.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
@Transactional
public abstract class AbstractDao<T extends Entity> implements Dao<T> {
    @Autowired
    protected SessionFactory sessionFactory;
    private final String entityName;

    public AbstractDao(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Perform entity save operation. And assigns calculates by database id to saved entity
     *
     * @param entity entity that need to be saved
     * @return saved entity with assigned id
     */
    @Override
    public T save(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        return entity;
    }

    /**
     * Performs entity update operation. Save oll entity properties to database fields.
     *
     * @param entity entity that need to be updated
     * @return updated entity
     */
    @Override
    public T update(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(entity);
        return entity;
    }

    /**
     * Performs entity delete operation. Deletes entity from database that have passed id
     *
     * @param entity entity that need to be deleted
     */
    @Override
    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(entity);
    }

    /**
     * Counts all entities rand returns saved entities amount
     *
     * @return saved entities amount
     */
    @Override
    public int getTotalElements() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(String.format("select count(*) from %s", entityName), Long.class);
        Long totalElements = query.uniqueResult();
        return totalElements.intValue();
    }
}
