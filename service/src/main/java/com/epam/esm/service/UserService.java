package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;

public interface UserService extends Service<User> {

    /**
     * Makes certificate order by passed users
     * @param user that need to order certificate
     * @param certificate that need to be ordered
     * @return made order
     */
    Order orderCertificate(User user, Certificate certificate);

    /**
     * Finds and returns richest user.
     * Richest user is user that has maximum of total orders cost
     * @return richest user
     */
    User findRichestUser();

    /**
     * Finds and returns richest user popular tag.
     * Richest user popular tag is tag that uses most
     * frequently amount richest user orders
     * @return popular tag
     */
    Tag findRichestUserPopularTag();

    /**
     * Finds and returns user orders specified page
     * @param user user
     * @param offset current page offset
     * @param limit current page limit
     * @return list of found orders no passed page
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    List<Order> findUserOrderPage(User user, int offset, int limit) throws InvalidPageException, PageOutOfBoundsException;

    /**
     * Computes and returns user's order amount
     * @param user whose orders amount need to be counted
     * @return user's orders amount
     */
    int getUserOrdersTotalElements(User user);

    /**
     * Funds user that made passe order
     * @param order order whose user need to be found
     * @return user that made passed order
     */
    User findOrderUser(Order order);

    /**
     * Finds user order that has passed id
     * @param foundUser user whose order need to be found
     * @param orderId order id
     * @return user order that has passed id
     * @throws ResourceNotFoundException if order is not found
     */
    Order findUserOrder(User foundUser, long orderId) throws ResourceNotFoundException;
}
