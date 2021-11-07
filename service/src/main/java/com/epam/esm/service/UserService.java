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
     * @param user whose orders need to be found
     * @param page user's orders page that need to be found
     * @return list of found orders no passed page
     * @throws PageOutOfBoundsException if page is less then 1 and greater then pages amount
     */
    List<Order> findUserOrderPage(User user, int offset, int limit) throws PageOutOfBoundsException;

    /**
     * Computes and returns user's orders pages amount
     * @param user whose orders pages need to be counted
     * @return user's orders pages amount
     */
    int getUserOrdersTotalPages(User user);

    /**
     * Computes and returns user's order amount
     * @param user whose orders amount need to be counted
     * @return user's orders amount
     */
    int getUserOrdersTotalElements(User user);

    User findOrderUser(Order order);

    Order findUserOrder(User foundUser, long orderId) throws ResourceNotFoundException;
}
