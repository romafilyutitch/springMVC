package com.epam.esm.dao;

import com.epam.esm.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends Dao<Order> {
    List<Order> findAllUserOrders(Long userId);

    List<Order> findUserOrdersPage(Long userId, long page);

    long getUserOrdersTotalPages(Long userId);

    long getUserOrdersTotalElements(Long userId);

    Order makeUserOrder(Long userId, Order order);

    Optional<Order> findByCertificateId(Long certificateId);
}
