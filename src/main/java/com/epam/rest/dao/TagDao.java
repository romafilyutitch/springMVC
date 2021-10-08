package com.epam.rest.dao;

import com.epam.rest.model.Tag;

import java.util.Optional;

public interface TagDao extends Dao<Tag> {
    Optional<Tag> findByName(String name);
}
