package com.epam.esm.service;

import com.epam.esm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);
}
