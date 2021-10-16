package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;

import com.epam.esm.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = PersistanceConfig.class)
@ActiveProfiles("dev")
@SpringJUnitConfig(classes = PersistanceConfig.class)
class TagJdbcDaoTest {

    @Autowired
    private TagJdbcDao dao;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("tag");
        tag = dao.save(tag);
    }

    @AfterEach
    void tearDown() {
        dao.delete(tag.getId());
    }

    @Test
    public void save_shouldReturnTagWithId() {
        assertNotNull(tag.getId());
    }

    @Test
    public void findAll_shouldReturnAllTags() {
        List<Tag> allTags = dao.findAll();

        assertTrue(allTags.contains(tag));
    }

    @Test
    public void findById_shouldReturnTagIfTagSaved() {
        Optional<Tag> foundTag = dao.findById(tag.getId());

        assertTrue(foundTag.isPresent());;
        assertEquals(tag, foundTag.get());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoTag() {
        dao.delete(tag.getId());
        Optional<Tag> foundTag = dao.findById(tag.getId());

        assertFalse(foundTag.isPresent());
    }

    @Test
    public void findByName_shouldReturnTagIfTagWithNameSaved() {
        Optional<Tag> foundTag = dao.findByName(tag.getName());

        assertTrue(foundTag.isPresent());
        assertEquals(tag, foundTag.get());
    }

    @Test
    public void findByName_shouldReturnEmptyOptionalIfThereIsNoTagWithName() {
        dao.delete(tag.getId());
        Optional<Tag> foundTag = dao.findByName(tag.getName());

        assertFalse(foundTag.isPresent());
    }

    @Test
    public void update_shouldUpdateSavedTag() {
        tag.setName("updated");
        Tag updated = dao.update(tag);

        assertEquals(tag, updated);
        assertEquals("updated", updated.getName());
    }

    @Test
    public void delete_shouldDeleteSavedTag() {
        dao.delete(tag.getId());
        Optional<Tag> foundTag = dao.findById(tag.getId());

        assertFalse(foundTag.isPresent());
    }
}