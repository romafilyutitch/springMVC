package com.epam.esm.dao;

import com.epam.esm.model.Order;

import java.util.List;

public interface OrderDao extends Dao<Order> {
    List<Order> findByUserId(Long userId);

    Order makeUserOrder(Long userId, Order order);
}
