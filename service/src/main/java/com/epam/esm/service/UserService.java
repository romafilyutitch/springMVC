package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;

public interface UserService extends Service<User> {

    Order orderCertificate(User user, Certificate certificate);

    User findByName(String name);

    User findRichestUser();

    Tag findRichestUserPopularTag();

    List<Order> findUserOrderPage(User user, int page) throws PageOutOfBoundsException;

    int getUserOrdersTotalPages(User user);

    int getUserOrdersTotalElements(User user);
}
