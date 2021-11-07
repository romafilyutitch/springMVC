package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfig.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
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
    public void findPage_shouldReturnTagsOnPage() {
        List<Tag> tags = dao.findPage(0, 10);
        assertEquals(3, tags.size());
        Tag first = tags.get(0);
        Tag second = tags.get(1);
        Tag third = tags.get(2);
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
        Optional<Tag> optionalSavedTag = dao.findById(1);
        assertTrue(optionalSavedTag.isPresent());
        Tag savedTag = optionalSavedTag.get();
        dao.delete(savedTag);
        Optional<Tag> optionalTag = dao.findById(1L);

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void findCertificateTagsPage_shouldReturnTagsOnFirstPage() {
        List<Tag> certificateTagsPage = dao.findCertificateTagsPage(1, 0, 10);
        assertEquals(3, certificateTagsPage.size());
        Tag first = certificateTagsPage.get(0);
        Tag second = certificateTagsPage.get(1);
        Tag third = certificateTagsPage.get(2);
        assertEquals(1, first.getId());
        assertEquals("spotify", first.getName());
        assertEquals(2, second.getId());
        assertEquals("music", second.getName());
        assertEquals(3, third.getId());
        assertEquals("art", third.getName());
    }

    @Test
    public void findAllCertificateTags_shouldReturnAllCertificateTags() {
        List<Tag> allCertificateTags = dao.findAllCertificateTags(1);
        assertEquals(3, allCertificateTags.size());
        Tag first = allCertificateTags.get(0);
        Tag second = allCertificateTags.get(1);
        Tag third = allCertificateTags.get(2);
        assertEquals(1, first.getId());
        assertEquals("spotify", first.getName());
        assertEquals(2, second.getId());
        assertEquals("music", second.getName());
        assertEquals(3, third.getId());
        assertEquals("art", third.getName());
    }

    @Test
    public void getCertificateTagsTotalElements_mustReturnThree() {
        int certificateTagsTotalElements = dao.getCertificateTagsTotalElements(1);
        assertEquals(3, certificateTagsTotalElements);
    }

    @Test
    public void findCertificateTag_shouldReturnCertificateTag() {
        Optional<Tag> optionalTag = dao.findCertificateTag(1, 1);
        assertTrue(optionalTag.isPresent());
        assertEquals(1, optionalTag.get().getId());
        assertEquals("spotify", optionalTag.get().getName());
    }
}