package com.epam.esm.page;

import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

public interface UserLinksBuilder extends LinksBuilder<User> {
    Order buildUserOrderLinks(User user, Order order) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    CollectionModel<Order> buildUserOrdersPageLinks(User user, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

}
