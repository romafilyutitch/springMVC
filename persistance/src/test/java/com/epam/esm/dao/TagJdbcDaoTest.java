package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = PersistanceConfig.class)
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringJUnitConfig(classes = PersistanceConfig.class)
class TagJdbcDaoTest {

    @Autowired
    private TagJdbcDao dao;

    @Test
    public void save_shouldReturnTagWithId() {
        Tag tag = new Tag("tag");
        Tag savedTag = dao.save(tag);

        assertNotNull(savedTag.getId());
        assertEquals(tag.getName(), savedTag.getName());
    }

    @Test
    public void findAll_shouldReturnAllTags() {
        List<Tag> allTags = dao.findAll();

        assertEquals(3, allTags.size());
        Tag first = allTags.get(0);
        Tag second = allTags.get(1);
        Tag third = allTags.get(2);
        assertEquals(1, first.getId());
        assertEquals("spotify", first.getName());
        assertEquals(2, second.getId());
        assertEquals("music", second.getName());
        assertEquals(3, third.getId());
        assertEquals("art", third.getName());
    }

    @Test
    public void findById_shouldReturnTagIfTagSaved() {
        Optional<Tag> optionalTag = dao.findById(1L);

        assertTrue(optionalTag.isPresent());
        Tag foundTag = optionalTag.get();
        assertEquals(1, foundTag.getId());
        assertEquals("spotify", foundTag.getName());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoTag() {
        Optional<Tag> optionalTag = dao.findById(10L);

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void findByName_shouldReturnTagIfTagWithNameSaved() {
        Optional<Tag> optionalTag = dao.findByName("spotify");

        assertTrue(optionalTag.isPresent());
        Tag foundTag = optionalTag.get();
        assertEquals(1L, foundTag.getId());
        assertEquals("spotify", foundTag.getName());
    }

    @Test
    public void findByName_shouldReturnEmptyOptionalIfThereIsNoTagWithName() {
        Optional<Tag> optionalTag = dao.findByName("health");

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void update_shouldUpdateSavedTag() {
        Optional<Tag> optionalTag = dao.findById(1L);
        Tag tag = optionalTag.get();
        tag.setName("health");
        Tag updated = dao.update(tag);

        assertEquals("health", updated.getName());
    }

    @Test
    public void delete_shouldDeleteSavedTag() {
        dao.delete(1L);
        Optional<Tag> optionalTag = dao.findById(1L);

        assertFalse(optionalTag.isPresent());
    }
}