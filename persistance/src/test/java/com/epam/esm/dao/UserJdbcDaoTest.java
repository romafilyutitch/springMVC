package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
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
class UserJdbcDaoTest {

    @Autowired
    private UserJdbcDao dao;


    @Test
    public void findPage_mustReturnFirstPage() {
        List<User> usersPage = dao.findPage(0, 10);
        assertEquals(1, usersPage.size());
        User user = usersPage.get(0);
        assertEquals(1, user.getId());
        assertEquals("user", user.getName());
        assertEquals("test", user.getSurname());
    }

    @Test
    public void findById_shouldReturnUserWithId() {
        Optional<User> optionalUser = dao.findById(1);
        assertTrue(optionalUser.isPresent());
    }

    @Test
    public void findById_shouldReturnEmptyOptionalIfThereIsNoUserWithId() {
        Optional<User> optionalUser = dao.findById(0);
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void findRichestUser_shouldReturnNotNullUser() {
        User richestUser = dao.findRichestUser();
        assertNotNull(richestUser);
    }

    @Test
    public void findRichesUserPopularTag_shouldReturnNotNullTag() {
        Tag popularTag = dao.findRichestUserPopularTag();
        assertNotNull(popularTag);
    }

    


}