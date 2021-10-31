package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService {
    private static final Logger logger = LogManager.getLogger(CertificateRestService.class);
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final UserValidator userValidator;

    @Autowired
    public UserRestService(UserDao userDao, OrderDao orderDao, UserValidator userValidator) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.userValidator = userValidator;
    }

    @Override
    public List<User> findPage(int page) throws PageOutOfBoundsException {
        if (page < 1 || page > userDao.getTotalPages()) {
            throw new PageOutOfBoundsException(page, userDao.getTotalPages(), 1);
        }
        List<User> usersPage = userDao.findPage(page);
        logger.info(String.format("Users on page %d were found", page));
        return usersPage;
    }

    @Override
    public User findById(long id) throws ResourceNotFoundException {
        Optional<User> optionalUser = userDao.findById(id);
        if (optionalUser.isPresent()) {
            logger.info(String.format("User was found by id %s", optionalUser.get()));
            return optionalUser.get();
        } else {
            logger.error(String.format("User with id %d wasn't found", id));
            throw new ResourceNotFoundException(id);
        }
    }

    @Override
    public User findByName(String name) {
        return userDao.findByName(name).get();
    }

    @Override
    public User findRichestUser() {
        User richestUser = userDao.findRichestUser();
        logger.info(String.format("Richest user was found %s", richestUser));
        return richestUser;
    }

    @Override
    public Tag findRichestUserPopularTag() {
        Tag richestUserPopularTag = userDao.findRichestUserPopularTag();
        logger.info(String.format("Richest user popular tag is %s", richestUserPopularTag));
        return richestUserPopularTag;
    }

    @Override
    public int getTotalElements() {
        return userDao.getTotalElements();
    }

    @Override
    public int getTotalPages() {
        return userDao.getTotalPages();
    }

    @Override
    public User save(User entity) throws InvalidResourceException {
        userValidator.validate(entity);
        User savedUser = userDao.save(entity);
        logger.info(String.format("User was saved %s", savedUser));
        return savedUser;
    }

    @Override
    public User update(User entity) throws InvalidResourceException {
        userValidator.validate(entity);
        User updatedUser = userDao.update(entity);
        logger.info(String.format("User was updated %s", updatedUser));
        return updatedUser;
    }

    @Override
    public void delete(User entity) {
        userDao.delete(entity.getId());
        logger.info(String.format("User was deleted %s", entity));
    }

    @Override
    public Order orderCertificate(User user, Certificate certificate) {
        Order order = new Order(certificate.getPrice(), certificate);
        Order savedOrder = orderDao.save(order);
        orderDao.setUserToOrder(user.getId(), savedOrder.getId());
        logger.info(String.format("User order was saved %s", savedOrder));
        return order;
    }

    @Override
    public List<Order> findUserOrderPage(User user, int page) throws PageOutOfBoundsException {
        if (page < 1 || page > orderDao.getUserOrdersTotalPages(user.getId())) {
            throw new PageOutOfBoundsException(page, orderDao.getUserOrdersTotalPages(user.getId()), 1);
        }
        List<Order> userOrdersPage = orderDao.findUserOrdersPage(user.getId(), page);
        logger.info(String.format("User orders on page %d were found %s", page, userOrdersPage));
        return userOrdersPage;
    }

    @Override
    public int getUserOrdersTotalPages(User user) {
        return orderDao.getUserOrdersTotalPages(user.getId());
    }

    @Override
    public int getUserOrdersTotalElements(User user) {
        return orderDao.getUserOrdersTotalElements(user.getId());
    }
}
