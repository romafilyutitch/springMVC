package com.epam.esm.dao;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao extends Dao<User> {
    Optional<User> findByName(String name);

    User findRichestUser();

    Tag findRichestUserPopularTag();

    List<Order> findUserOrders(Long userId, long page);

    long findUserOrdersTotalPages();

    long findUserOrdersTotalElements();
}
