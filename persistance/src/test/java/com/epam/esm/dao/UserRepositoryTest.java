package com.epam.esm.dao;

import com.epam.esm.configuration.PersistanceConfiguration;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfiguration.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void findPage_mustReturnFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> usersPage = repository.findAll(pageable).getContent();
        assertEquals(1, usersPage.size());
        User user = usersPage.get(0);
        assertEquals(1, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("test", user.getPassword());
    }

    @Test
    public void findById_shouldReturnUserWithId() {
        Optional<User> optionalUser = repository.findById(1L);
        assertTrue(optionalUser.isPresent());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoUserWithId() {
        Optional<User> optionalUser = repository.findById(0L);
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void findRichestUser_shouldReturnNotNullUser() {
        List<User> users = repository.sortByUsersByCostDesc(PageRequest.of(0, 10)).getContent();
        users.forEach(System.out::println);
    }

    @Test
    public void findRichesUserPopularTag_shouldReturnNotNullTag() {
        List<Tag> tags = repository.sortUserTagsByCountDesc(1L, PageRequest.of(0, 10)).getContent();
        System.out.println(tags);
    }
}