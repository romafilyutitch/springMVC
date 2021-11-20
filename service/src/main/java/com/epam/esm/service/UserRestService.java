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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService, UserDetailsService {
    private static final Logger logger = LogManager.getLogger(CertificateRestService.class);
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestService(UserDao userDao, OrderDao orderDao, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Finds and returns entities on specified page
     *
     * @param offset current page offset
     * @param limit  current page limit
     * @return list of entities on passed page
     * @throws PageOutOfBoundsException offset is greater then total elements
     * @throws InvalidPageException     is offset or limit is negative
     */
    @Override
    public List<User> findPage(int offset, int limit) throws InvalidPageException, PageOutOfBoundsException {
        checkPage(offset, limit, userDao.getTotalElements());
        return userDao.findPage(offset, limit);
    }

    /**
     * Finds and returns entity that has passed id
     *
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
     *
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
     *
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
     *
     * @return saved entities amount
     */
    @Override
    public int getTotalElements() {
        return userDao.getTotalElements();
    }

    /**
     * Saves entity and returns saved entity with assigned id
     *
     * @param entity that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     */
    @Override
    public User save(User entity) throws InvalidResourceException {
        userValidator.validate(entity);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User savedUser = userDao.save(entity);
        logger.info(String.format("User was saved %s", savedUser));
        return savedUser;
    }

    /**
     * Updated entity and returns updated entity
     *
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
     *
     * @param entity entity that need to be saved
     */
    @Override
    public void delete(User entity) {
        userDao.delete(entity);
        logger.info(String.format("User was deleted %s", entity));
    }

    /**
     * Makes certificate order by passed users
     *
     * @param user        that need to order certificate
     * @param certificate that need to be ordered
     * @return made order
     */
    @Override
    public Order orderCertificate(User user, Certificate certificate) {
        Order order = new Order(certificate.getPrice(), certificate);
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderDao.save(order);
        orderDao.setUserToOrder(user.getId(), savedOrder.getId());
        logger.info(String.format("User order was saved %s", savedOrder));
        return savedOrder;
    }

    /**
     * Finds and returns user orders specified page
     *
     * @param user   user
     * @param offset current page offset
     * @param limit  current page limit
     * @return list of found orders no passed page
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException     if offset or limit is negative
     */
    @Override
    public List<Order> findUserOrderPage(User user, int offset, int limit) throws PageOutOfBoundsException, InvalidPageException {
        checkPage(offset, limit, orderDao.getUserOrdersTotalElements(user.getId()));
        return orderDao.findUserOrdersPage(user.getId(), offset, limit);
    }

    /**
     * Computes and returns user's order amount
     *
     * @param user whose orders amount need to be counted
     * @return user's orders amount
     */
    @Override
    public int getUserOrdersTotalElements(User user) {
        return orderDao.getUserOrdersTotalElements(user.getId());
    }

    /**
     * Funds user that made passe order
     *
     * @param order order whose user need to be found
     * @return user that made passed order
     */
    @Override
    public User findOrderUser(Order order) {
        return userDao.findByOrderId(order.getId());
    }

    /**
     * Finds user order that has passed id
     *
     * @param foundUser user whose order need to be found
     * @param orderId   order id
     * @return user order that has passed id
     * @throws ResourceNotFoundException if order is not found
     */
    @Override
    public Order findUserOrder(User foundUser, long orderId) throws ResourceNotFoundException {
        Optional<Order> foundOrder = orderDao.findById(orderId);
        if (foundOrder.isPresent()) {
            return foundOrder.get();
        } else {
            throw new ResourceNotFoundException(orderId);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userDao.findByName(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(String.format("User with name %s was not found", username)));
        Collection<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getRoles().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    private void checkPage(int offset, int limit, int totalElements) throws InvalidPageException, PageOutOfBoundsException {
        if (offset < 0 || limit <= 0) {
            throw new InvalidPageException(offset, limit);
        }
        if (offset >= totalElements) {
            throw new PageOutOfBoundsException(offset, totalElements);
        }
    }
}
