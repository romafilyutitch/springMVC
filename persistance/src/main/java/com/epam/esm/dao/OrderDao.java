package com.epam.esm.dao;

import com.epam.esm.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends Dao<Order> {
    List<Order> findAllUserOrders(long userId);

    List<Order> findUserOrdersPage(long userId, long page);

    int getUserOrdersTotalPages(long userId);

    int getUserOrdersTotalElements(long userId);

    Order makeUserOrder(long userId, Order order);

    Optional<Order> findByCertificateId(long certificateId);
}
