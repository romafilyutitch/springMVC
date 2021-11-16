package com.epam.esm.service;

import com.epam.esm.model.Entity;
import com.epam.esm.validation.InvalidResourceException;

import java.util.List;

public interface Service<T extends Entity> {

    /**
     * Finds and returns entities on specified page
     *
     * @param offset current page offset
     * @param limit  current page limit
     * @return list of entities on passed page
     * @throws PageOutOfBoundsException offset is greater then total elements
     * @throws InvalidPageException     is offset or limit is negative
     */
    List<T> findPage(int offset, int limit) throws InvalidPageException, PageOutOfBoundsException;

    /**
     * Finds and returns entity that has passed id
     *
     * @param id of entity that need to be found
     * @return entity that has passed id
     * @throws ResourceNotFoundException if there is no entity with passed id
     */
    T findById(long id) throws ResourceNotFoundException;

    /**
     * Saves entity and returns saved entity with assigned id
     *
     * @param entity that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     */
    T save(T entity) throws InvalidResourceException;

    /**
     * Updated entity and returns updated entity
     *
     * @param entity that need to be updated
     * @return updated entity
     * @throws InvalidResourceException  if updated entity is invalid
     * @throws ResourceNotFoundException if updated entity is not saved and cannot be found
     */
    T update(T entity) throws InvalidResourceException, ResourceNotFoundException;

    /**
     * Deletes saved entity
     *
     * @param entity entity that need to be saved
     */
    void delete(T entity);

    /**
     * Computes and returns amount of entity elements
     *
     * @return saved entities amount
     */
    int getTotalElements();
}
