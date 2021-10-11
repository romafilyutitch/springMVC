package com.epam.esm.dao;

import com.epam.esm.model.Entity;

import java.util.List;
import java.util.Optional;

public interface Dao<T extends Entity> {
    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T entity);

    T update(T entity);

    void delete(Long id);
}
