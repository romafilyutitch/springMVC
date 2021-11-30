package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Role;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.repository.OffsetPageable;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService {
    private static final Logger logger = LogManager.getLogger(CertificateRestService.class);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserRestService(UserRepository userRepository, OrderRepository orderRepository, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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
        checkPage(offset, limit, (int) userRepository.count());
        Pageable pageable = new OffsetPageable(offset, limit);
        return userRepository.findAll(pageable).getContent();
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
        Optional<User> optionalUser = userRepository.findById(id);
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
        OffsetPageable offsetPageable = new OffsetPageable(0, 1);
        Page<User> page = userRepository.sortByUsersByCostDesc(offsetPageable);
        List<User> users = page.getContent();
        User richestUser = users.get(0);
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
        User richestUser = findRichestUser();
        OffsetPageable offsetPageable = new OffsetPageable(0, 1);
        Tag richestUserPopularTag = userRepository.sortUserTagsByCountDesc(richestUser.getId(), offsetPageable).getContent().get(0);
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
        return (int) userRepository.count();
    }

    /**
     * Saves entity and returns saved entity with assigned id
     *
     * @param entity that need to be saved
     * @return saved entity with assigned id
     * @throws InvalidResourceException if saved entity is invalid
     * @throws UsernameExistsException if there is user with passed username
     */
    @Override
    @Transactional
    public User save(User entity) throws InvalidResourceException, UsernameExistsException {
        userValidator.validate(entity);
        Optional<User> optionalUser = userRepository.findByUsername(entity.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameExistsException(entity.getUsername());
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.getRoles().add(new Role("ROLE_USER"));
        User savedUser = userRepository.save(entity);
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
    @Transactional
    public User update(User entity) throws InvalidResourceException, UserNotFoundException {
        userValidator.validate(entity);
        Optional<User> optionalUser = userRepository.findById(entity.getId());
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException(entity.getId());
        }
        User savedUser = optionalUser.get();
        savedUser.setUsername(entity.getUsername());
        savedUser.setUsername(passwordEncoder.encode(entity.getPassword()));
        logger.info(String.format("User was updated %s", savedUser));
        return savedUser;
    }

    /**
     * Deletes saved entity
     *
     * @param entity entity that need to be saved
     */
    @Override
    @Transactional
    public void delete(User entity) {
        userRepository.delete(entity);
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
    @Transactional
    public Order orderCertificate(User user, Certificate certificate) {
        Order order = new Order(certificate.getPrice(), certificate);
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        user.getOrders().add(savedOrder);
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
        checkPage(offset, limit, orderRepository.getUserOrdersTotalElements(user.getId()));
        Pageable pageable = new OffsetPageable(offset, limit);
        return orderRepository.findUserOrdersPage(user.getId(), pageable).getContent();
    }

    /**
     * Computes and returns user's order amount
     *
     * @param user whose orders amount need to be counted
     * @return user's orders amount
     */
    @Override
    public int getUserOrdersTotalElements(User user) {
        return orderRepository.getUserOrdersTotalElements(user.getId());
    }

    /**
     * Funds user that made passe order
     *
     * @param order order whose user need to be found
     * @return user that made passed order
     */
    @Override
    public User findOrderUser(Order order) {
        return userRepository.findByOrderId(order.getId());
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
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        if (foundOrder.isPresent()) {
            return foundOrder.get();
        } else {
            throw new ResourceNotFoundException(orderId);
        }
    }

    /**
     * Finds User that has passed username
     * @param username username that need to belong to user
     * @return user that has passed string as username
     * @throws UsernameNotFoundException if there is no user with passed username
     */
    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return optionalUser.get();
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
