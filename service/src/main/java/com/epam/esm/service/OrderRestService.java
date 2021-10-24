package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderRestService implements OrderService {
    private final OrderDao orderDao;

    @Autowired
    public OrderRestService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orderDao.findById(id).get();
    }
}
