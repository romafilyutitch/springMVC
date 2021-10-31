package com.epam.esm.dao;

import com.epam.esm.model.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Dao layer base interface that defines standard CRUD
 * operations with entities.
 *
 * @param <T> entities with witch dao layer operates
 */
public interface Dao<T extends Entity> {
    /**
     * Finds and returns entities on specified page
     * @param page that need to be finds
     * @return entities on passed page
     */
    List<T> findPage(int page);

    /**
     * Finds and returns entity that have passed id
     *
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity with passed id exists
     * or empty optional otherwise
     */
    Optional<T> findById(long id);

    /**
     * Perform entity save operation. And assigns calculates by database id to saved entity
     *
     * @param entity entity that need to be saved
     * @return saved entity with assigned id
     */
    T save(T entity);

    /**
     * Performs entity update operation. Save oll entity properties to database fields.
     *
     * @param entity entity that need to be updated
     * @return updated entity
     */
    T update(T entity);

    /**
     * Performs entity delete operation. Deletes entity from database that have passed id
     *
     * @param id id of entity that need to be deleted
     */
    void delete(long id);

    /**
     * Counts all entities rand returns saved entities amount
     * @return saved entities amount
     */
    int getTotalElements();

    /**
     * Counts all entities and computes
     * pages amount
     * @return pages amount
     */
    int getTotalPages();
}
