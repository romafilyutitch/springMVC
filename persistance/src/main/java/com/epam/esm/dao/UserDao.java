package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

public interface UserDao extends Dao<User> {

    /**
     * Finds and returns riches user.
     * Riches user is the user that has maximum of orders cost.
     *
     * @return user that has maximum orders cost
     */
    User findRichestUser();

    /**
     * Finds and returns riches user popular tag.
     * Popular tag is tag that uses most frequently amount richest
     * user orders
     *
     * @return popular tag
     */
    Tag findRichestUserPopularTag();

    /**
     * Finds user that has order with passed id
     *
     * @param id order id
     * @return user that has order with passed id
     */
    User findByOrderId(long id);
}
