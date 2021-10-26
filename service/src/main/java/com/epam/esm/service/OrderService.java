package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {
   List<Order> findAll(long page);

   Order findById(Long id);

   Order findCertificateOrder(Long certificateId);

   Order makeOrder(Long id, Long userId);

   long getTotalElements();

   long getTotalPages();

   List<Order> findUserOrders(User user, long page);

   long getUserOrdersTotalPages(User user);

   long getUserOrdersTotalElements(User user);
}
