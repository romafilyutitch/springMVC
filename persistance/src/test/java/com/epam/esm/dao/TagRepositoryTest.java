package com.epam.esm.dao;

import com.epam.esm.configuration.PersistanceConfiguration;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfiguration.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
class TagRepositoryTest {

    @Autowired
    private TagRepository repository;

    @Test
    public void save_shouldReturnTagWithId() {
        Tag tag = new Tag("tag");
        Tag savedTag = repository.save(tag);

        assertTrue(savedTag.getId() != 0);
        assertEquals(tag.getName(), savedTag.getName());
    }

    @Test
    public void findPage_shouldReturnTagsOnFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tag> tags = repository.findAll(pageable).getContent();
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
        Optional<Tag> optionalTag = repository.findById(1L);

        assertTrue(optionalTag.isPresent());
        Tag foundTag = optionalTag.get();
        assertEquals(1, foundTag.getId());
        assertEquals("spotify", foundTag.getName());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoTag() {
        Optional<Tag> optionalTag = repository.findById(10L);

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void findByName_shouldReturnTagIfTagWithNameSaved() {
        Optional<Tag> optionalTag = repository.findByName("spotify");

        assertTrue(optionalTag.isPresent());
        Tag foundTag = optionalTag.get();
        assertEquals(1L, foundTag.getId());
        assertEquals("spotify", foundTag.getName());
    }

    @Test
    public void findByName_shouldReturnEmptyOptionalIfThereIsNoTagWithName() {
        Optional<Tag> optionalTag = repository.findByName("health");

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void update_shouldUpdateSavedTag() {
        Optional<Tag> optionalTag = repository.findById(1L);
        Tag tag = optionalTag.get();
        tag.setName("health");
        assertEquals("health", tag.getName());
    }

    @Test
    public void delete_shouldDeleteSavedTag() {
        Optional<Tag> optionalSavedTag = repository.findById(1L);
        assertTrue(optionalSavedTag.isPresent());
        Tag savedTag = optionalSavedTag.get();
        repository.delete(savedTag);
        Optional<Tag> optionalTag = repository.findById(1L);

        assertFalse(optionalTag.isPresent());
    }

    @Test
    public void findCertificateTagsPage_shouldReturnTagsOnFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tag> certificateTagsPage = repository.findCertificateTagsPage(1, pageable).getContent();
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
    public void getCertificateTagsTotalElements_mustReturnThree() {
        int certificateTagsTotalElements = repository.getCertificateTagsTotalElements(1);
        assertEquals(3, certificateTagsTotalElements);
    }

    @Test
    public void findCertificateTag_shouldReturnCertificateTag() {
        Optional<Tag> optionalTag = repository.findCertificateTag(1, 1);
        assertTrue(optionalTag.isPresent());
        assertEquals(1, optionalTag.get().getId());
        assertEquals("spotify", optionalTag.get().getName());
    }
}