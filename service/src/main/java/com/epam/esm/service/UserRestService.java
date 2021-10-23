package com.epam.esm.service;

import com.epam.esm.dao.UserJdbcDao;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService {
    private final UserJdbcDao userJdbcDao;

    @Autowired
    public UserRestService(UserJdbcDao userJdbcDao) {
        this.userJdbcDao = userJdbcDao;
    }

    @Override
    public List<User> findAll() {
        return userJdbcDao.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJdbcDao.findById(id);
    }
}
