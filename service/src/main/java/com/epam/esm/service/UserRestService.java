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
    /**
     * Finds and returns entities on specified page
     * @param page page of entities that need to be found
     * @return list of entities on passed page
     * @throws PageOutOfBoundsException if page number is less then 1 and greater that pages amount
     */
    @Override
    public List<User> findPage(int page) throws PageOutOfBoundsException {
        if (page < 1 || page > userDao.getTotalPages()) {
            throw new PageOutOfBoundsException(page, userDao.getTotalPages(), 1);
        }
        List<User> usersPage = userDao.findPage(page);
        logger.info(String.format("Users on page %d were found", page));
        return usersPage;
    }
    /**
     * Finds and returns entity that has passed id
     * @param id of entity that need to be found
     * @return entity that has passed id
     * @throws ResourceNotFoundException if there is no entity with passed id
     */
    @Override
    public User findById(long id) throws ResourceNotFoundException {
        Optional<User> optionalUser = userDao.findById(id);
        if (optionalUser.isPresent()) {
            logger.info(String.format("User was found by id %s", optionalUser.get()));
            return optionalUser.get();
        } else {
            logger.error(String.format("User with id %d wasn't found", id));
            throw new UserNotFoundException(id);
        }
    }
    /**
     * Finds and returns richest user.
     * Richest user is user that has maximum of total orders cost
     * @return richest user
     */
    @Override
    public User findRichestUser() {
        User richestUser = userDao.findRichestUser();
        logger.info(String.format("Richest user was found %s", richestUser));
        return richestUser;
    }
    /**
     * Finds and returns richest user popular tag.
     * Richest user popular tag is tag that uses most
     * frequently amount richest user orders
     * @return popular tag
     */
    @Override
    public Tag findRichestUserPopularTag() {
        Tag richestUserPopularTag = userDao.findRichestUserPopularTag();
        logger.info(String.format("Richest user popular tag is %s", richestUserPopularTag));
        return richestUserPopularTag;
    }
    /**
     * Computes and returns amount of entity elements
     * @return saved entities amount
     */
    @Override
    public int getTotalElements() {
        return userDao.getTotalElements();
    }
    /**
     * Computes and returns amount of entities pages
     * @return amount of entities pages
     */
    @Override
    public int getTotalPages() {
        return userDao.getTotalPages();
    }
    /**
     * Saves entity and returns saved entity with assigned id
     * @param entity that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     */
    @Override
    public User save(User entity) throws InvalidResourceException {
        userValidator.validate(entity);
        User savedUser = userDao.save(entity);
        logger.info(String.format("User was saved %s", savedUser));
        return savedUser;
    }
    /**
     * Updated entity and returns updated entity
     * @param entity that need to be updated
     * @return updated entity
     * @throws InvalidResourceException if updated entity is invalid
     */
    @Override
    public User update(User entity) throws InvalidResourceException {
        userValidator.validate(entity);
        User updatedUser = userDao.update(entity);
        logger.info(String.format("User was updated %s", updatedUser));
        return updatedUser;
    }
    /**
     * Deletes saved entity
     * @param entity entity that need to be saved
     */
    @Override
    public void delete(User entity) {
        userDao.delete(entity);
        logger.info(String.format("User was deleted %s", entity));
    }
    /**
     * Makes certificate order by passed users
     * @param user that need to order certificate
     * @param certificate that need to be ordered
     * @return made order
     */
    @Override
    public Order orderCertificate(User user, Certificate certificate) {
        Order order = new Order(certificate.getPrice(), certificate);
        Order savedOrder = orderDao.save(order);
        orderDao.setUserToOrder(user.getId(), savedOrder.getId());
        logger.info(String.format("User order was saved %s", savedOrder));
        return savedOrder;
    }
    /**
     * Finds and returns user orders specified page
     * @param user whose orders need to be found
     * @param page user's orders page that need to be found
     * @return list of found orders no passed page
     * @throws PageOutOfBoundsException if page is less then 1 and greater then pages amount
     */
    @Override
    public List<Order> findUserOrderPage(User user, int page) throws PageOutOfBoundsException {
        if (page < 1 || page > orderDao.getUserOrdersTotalPages(user.getId())) {
            throw new PageOutOfBoundsException(page, orderDao.getUserOrdersTotalPages(user.getId()), 1);
        }
        List<Order> userOrdersPage = orderDao.findUserOrdersPage(user.getId(), page);
        logger.info(String.format("User orders on page %d were found %s", page, userOrdersPage));
        return userOrdersPage;
    }
    /**
     * Computes and returns user's orders pages amount
     * @param user whose orders pages need to be counted
     * @return user's orders pages amount
     */
    @Override
    public int getUserOrdersTotalPages(User user) {
        return orderDao.getUserOrdersTotalPages(user.getId());
    }
    /**
     * Computes and returns user's order amount
     * @param user whose orders amount need to be counted
     * @return user's orders amount
     */
    @Override
    public int getUserOrdersTotalElements(User user) {
        return orderDao.getUserOrdersTotalElements(user.getId());
    }
}
