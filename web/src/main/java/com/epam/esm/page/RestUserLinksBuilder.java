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

    /**
     * builds links for passed entity
     * @param entity entity to build links
     * @return entity that has  built links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset if greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
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

    /**
     * Builds links for passed entity page
     * @param entities entities to build links
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return entities that have build links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
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

    /**
     * builds user order links
     * @param user user
     * @param order user order
     * @return order that has built links
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    @Override
    public Order buildUserOrderLinks(User user, Order order) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Link selfLink = linkTo(methodOn(UserController.class).showUserOrder(user.getId(), order.getId())).withSelfRel();
        Link userLink  = linkTo(methodOn(UserController.class).showUser(user.getId())).withRel("user");
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(order.getCertificate().getId())).withRel("certificate");
        order.add(selfLink, userLink, certificateLink);
        return order;
    }

    /**
     * builds user orders page links
     * @param user user
     * @param orders user orders
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return orders that have built links
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
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
