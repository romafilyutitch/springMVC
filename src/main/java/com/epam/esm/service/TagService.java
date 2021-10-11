package com.epam.esm.service;

import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    List<Tag> findAll();

    Optional<Tag> findById(Long id);

    Tag save(Tag tag) throws TagExistsException;

    Tag update(Tag tag);

    void delete(Long id);
}
