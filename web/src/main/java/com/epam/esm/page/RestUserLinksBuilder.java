package com.epam.esm.page;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.UserController;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RestUserLinksBuilder implements UserLinksBuilder {
    private final UserService service;

    @Autowired
    public RestUserLinksBuilder(UserService service) {
        this.service = service;
    }

    @Override
    public User buildLinks(User entity) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(UserController.class).showUser(entity.getId())).withSelfRel();
        entity.add(selfLink);
        if (!entity.getOrders().isEmpty()) {
            Link ordersLink = linkTo(methodOn(UserController.class).showUserOrders(entity.getId(), 0, 10)).withRel("orders");
            entity.add(ordersLink);
        }
        return entity;
    }

    @Override
    public CollectionModel<User> buildPageLinks(List<User> entities, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(UserController.class).showUsers(currentOffset, currentLimit)).withSelfRel();
        for (User entity : entities) {
            Link userLink = linkTo(methodOn(UserController.class).showUser(entity.getId())).withRel("user");
            entity.add(userLink);
        }
        Link nextLink = linkTo(methodOn(UserController.class).showUsers(currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousLink = linkTo(methodOn(UserController.class).showUsers(currentOffset  - currentLimit, currentLimit)).withRel("previous");
        return CollectionModel.of(entities, selfLink, nextLink, previousLink);
    }

    @Override
    public Order buildUserOrderLinks(User user, Order order) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(UserController.class).showUserOrder(user.getId(), order.getId())).withSelfRel();
        Link userLink  = linkTo(methodOn(UserController.class).showUser(user.getId())).withRel("user");
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(order.getCertificate().getId())).withRel("certificate");
        order.add(selfLink, userLink, certificateLink);
        return order;
    }

    @Override
    public CollectionModel<Order> buildUserOrdersPageLinks(User user, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        for (Order order : orders) {
            Link orderLink = linkTo(methodOn(UserController.class).showUserOrder(user.getId(), order.getId())).withRel("order");
            order.add(orderLink);
        }
        Link selfLink = linkTo(methodOn(UserController.class).showUserOrders(user.getId(), currentOffset, currentLimit)).withSelfRel();
        Link nextLink = linkTo(methodOn(UserController.class).showUserOrders(user.getId(), currentOffset + currentLimit, currentLimit)).withRel("next");
        Link previousLink = linkTo(methodOn(UserController.class).showUserOrders(user.getId(), currentOffset - currentLimit, currentLimit)).withRel("previous");
        return CollectionModel.of(orders, selfLink, nextLink, previousLink);
    }
}
