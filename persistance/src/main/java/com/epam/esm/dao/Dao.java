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
     * Finds and returns all entities from particular database
     * If there is not entities in database then empty list
     * will be returned
     *
     * @return list of entities from database
     */
    List<T> findAll(int page);

    /**
     * Finds and returns entity that have passed id
     *
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity with passed id exists
     * or empty optional otherwise
     */
    Optional<T> findById(Long id);

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
    void delete(Long id);

    long getTotalElements();

    long getTotalPages();
}
