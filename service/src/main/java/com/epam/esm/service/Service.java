package com.epam.esm.service;

import com.epam.esm.model.Entity;
import com.epam.esm.validation.InvalidResourceException;

import java.util.List;

public interface Service<T extends Entity> {
    List<T> findPage(int page) throws PageOutOfBoundsException;

    T findById(long id) throws ResourceNotFoundException;

    T save(T entity) throws InvalidResourceException;

    T update(T entity) throws InvalidResourceException, ResourceNotFoundException;

    void delete(T entity);

    int getTotalElements();

    int getTotalPages();
}
