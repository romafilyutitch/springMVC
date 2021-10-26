package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public PagedModel<Order> showOrders(@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        List<Order> orders = orderService.findAll(page);
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(orders.size(), page, 6, 2);
        return PagedModel.of(orders, metadata);
    }

    @GetMapping("/{id}")
    public Order showOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
