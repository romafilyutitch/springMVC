package com.epam.esm.dao;

import com.epam.esm.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends Dao<Order> {
    /**
     * Finds and returns user orders by user id
     * @param userId id of user which orders need to be found
     * @return list of user orders
     */
    List<Order> findAllUserOrders(long userId);

    /**
     * Finds and returns passed user passed orders page
     * @param userId user id which orders need to be found
     * @param page user orders page that need to be found
     * @return user orders passed page
     */
    List<Order> findUserOrdersPage(long userId, int page);

    /**
     * Counts user orders pages amount.
     * @param userId id of user which orders pages need to be count
     * @return amount of user orders pages
     */
    int getUserOrdersTotalPages(long userId);

    /**
     * Counts user orders amount.
     * @param userId id of user which orders amount need to be count
     * @return amount of user orders
     */
    int getUserOrdersTotalElements(long userId);

    /**
     * Sets user to saved order.
     * @param userId id of user that need to be saved to order
     * @param orderId order id that need to be set to user
     */
    void setUserToOrder(long userId, long orderId);

    /**
     * Finds certificate order by passed certificate id
     * @param certificateId whose order need to be found
     * @return order that contains certificate with passed id
     * or empty order otherwise
     */
    List<Order> findByCertificateId(long certificateId);
}
