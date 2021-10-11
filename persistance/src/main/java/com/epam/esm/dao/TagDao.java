package com.epam.esm.dao;

import com.epam.esm.model.Tag;

import java.util.Optional;

public interface TagDao extends Dao<Tag> {
    Optional<Tag> findByName(String name);
}
