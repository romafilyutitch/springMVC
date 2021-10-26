package com.epam.esm.service;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll(long page);

    Optional<User> findById(Long id);

    User findByName(String name);

    User makeOrder(Long userId, Long certificateId);

    User findRichestUser();

    Tag findRichestUserPopularTag();

    long getTotalElements();

    long getTotalPages();

    List<Order> findUserOrders(User user, long page);

    long getUserOrdersTotalPages();

    long getUsersOrdersTotalElements();
}
