package com.epam.rest.service;

import com.epam.rest.dao.TagDao;
import com.epam.rest.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TagRestService implements TagService {
    private TagDao tagDao;

    @Autowired
    public TagRestService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<Tag> findAll() {
        return tagDao.findAll();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagDao.findById(id);
    }

    @Override
    public Tag save(Tag tag) throws TagExistsException {
        String tagName = tag.getName();
        Optional<Tag> foundTag = tagDao.findByName(tagName);
        if (foundTag.isPresent()) {
            throw new TagExistsException();
        }
        return tagDao.save(tag);
    }

    @Override
    public Tag update(Tag tag) {
        return tagDao.update(tag);
    }

    @Override
    public void delete(Long id) {
        tagDao.delete(id);
    }
}
