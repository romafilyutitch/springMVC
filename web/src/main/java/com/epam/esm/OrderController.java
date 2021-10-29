package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.service.CertificateNotFoundException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.TagNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderservice) {
        this.orderService = orderservice;
    }

    @GetMapping
    public PagedModel<Order> showOrders(@RequestParam(value = "page", required = false, defaultValue = "1") long page) throws TagNotFoundException, CertificateNotFoundException {
        List<Order> orders = orderService.findAll(page);
        for (Order order : orders) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(order.getId())).withRel("certificate");
            order.add(certificateLink);
        }
        Link selfLink = linkTo(methodOn(OrderController.class).showOrders(page)).withSelfRel();
        Link firstPageLink = linkTo(methodOn(OrderController.class).showOrders(1)).withRel("firstPage");
        Link lastPageLink = linkTo(methodOn(OrderController.class).showOrders(orderService.getTotalPages())).withRel("lastPage");
        Link nextPageLink = linkTo(methodOn(OrderController.class).showOrders(page + 1)).withRel("nextPage");
        Link previousPageLink = linkTo(methodOn(OrderController.class).showOrders(page - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPageLink);
        links.add(lastPageLink);
        links.add(nextPageLink);
        links.add(previousPageLink);
        if (page == 1) {
            links.remove(previousPageLink);
        } else if (page == orderService.getTotalPages()) {
            links.remove(nextPageLink);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(orders.size(), page, orderService.getTotalElements(), orderService.getTotalPages());
        return orders.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(orders, metadata, links);
    }

    @GetMapping("/{id}")
    public Order showOrder(@PathVariable Long id) throws TagNotFoundException, CertificateNotFoundException {
        Order foundOrder = orderService.findById(id);
        Link selfOrder = linkTo(methodOn(OrderController.class).showOrder(id)).withSelfRel();
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(foundOrder.getCertificate().getId())).withRel("certificate");
        foundOrder.add(selfOrder);
        foundOrder.add(certificateLink);
        return foundOrder;
    }
}
