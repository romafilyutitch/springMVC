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
    /**
     * builds user order links
     * @param user user
     * @param order user order
     * @return order that has built links
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    Order buildUserOrderLinks(User user, Order order) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

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
    CollectionModel<Order> buildUserOrdersPageLinks(User user, List<Order> orders, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

}
