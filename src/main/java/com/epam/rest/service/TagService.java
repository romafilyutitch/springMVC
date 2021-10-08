package com.epam.rest.service;

import com.epam.rest.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    List<Tag> findAll();

    Optional<Tag> findById(Long id);

    Tag save(Tag tag);

    Tag update(Tag tag);

    void delete(Long id);
}
