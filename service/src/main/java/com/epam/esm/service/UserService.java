package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    User findByName(String name);

    User makeOrder(Long userId, Long certificateId);

    User findRichestUser();

    Tag findRichestUserPopularTag();
}
