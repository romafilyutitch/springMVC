package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderservice) {
        this.orderService = orderservice;
    }

    @GetMapping
    public List<Order> showOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order showOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
