package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {
   List<Order> findAll(int page);

   Order findById(Long id);

   Order findCertificateOrder(Long certificateId);

   Order makeOrder(Long id, Long userId);
}
