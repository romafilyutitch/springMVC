package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.InvalidUserException;
import com.epam.esm.validation.UserValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRestServiceTest {
    private final UserDao userDao = mock(UserDao.class);
    private final OrderDao orderDao = mock(OrderDao.class);
    private final UserValidator userValidator = mock(UserValidator.class);
    private final UserRestService service = new UserRestService(userDao, orderDao, userValidator);
    private final User user = new User(1, "user", "user");
    private final Certificate certificate = new Certificate(1, "test", "test", 100.0, 10, LocalDateTime.now(), LocalDateTime.now());
    private final Order order = new Order(certificate.getPrice(), certificate);

    @Test
    public void findPage_shouldReturnUsersOnPage() throws PageOutOfBoundsException {
        List<User> users = Collections.singletonList(user);
        when(userDao.getTotalPages()).thenReturn(1);
        when(userDao.findPage(1)).thenReturn(users);

        List<User> usersOnPage = service.findPage(1);

        assertEquals(users, usersOnPage);
        verify(userDao, atLeastOnce()).getTotalPages();
        verify(userDao).findPage(1);
    }

    @Test
    public void findPage_shouldThrowExceptionWhenThereIsNoPage() {
        when(userDao.getTotalPages()).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findPage(100));

        verify(userDao, atLeastOnce()).getTotalPages();
    }

    @Test
    public void findById_shouldReturnUser() throws ResourceNotFoundException {
        when(userDao.findById(1)).thenReturn(Optional.of(user));

        User foundUser = service.findById(1);

        assertEquals(user, foundUser);
        verify(userDao).findById(1);
    }

    @Test
    public void findById_shouldThrowExceptionIfThereIsNoUsersWithId() {
        when(userDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1));

        verify(userDao).findById(1);
    }

    @Test
    public void findRichestUser_shouldReturnUser() {
        when(userDao.findRichestUser()).thenReturn(user);

        User richestUser = service.findRichestUser();

        assertEquals(user, richestUser);
        verify(userDao).findRichestUser();
    }

    @Test
    public void findRichestUserPopularTag_shouldReturnUserTag() {
        Tag popularTag = new Tag(1, "tag");
        when(userDao.findRichestUserPopularTag()).thenReturn(popularTag);

        Tag foundTag = service.findRichestUserPopularTag();

        assertEquals(popularTag, foundTag);
        verify(userDao).findRichestUserPopularTag();
    }

    @Test
    public void getTotalElements_shouldReturnNotNegativeValue() {
        when(userDao.getTotalElements()).thenReturn(1);

        int totalElements = service.getTotalElements();

        assertTrue(totalElements >= 0);
        assertEquals(1, totalElements);
        verify(userDao).getTotalElements();
    }

    @Test
    public void getTotalPages_shouldReturnNotNegativeValue() {
        when(userDao.getTotalPages()).thenReturn(1);

        int totalPages = service.getTotalPages();

        assertTrue(totalPages >= 0);
        assertEquals(1, totalPages);
        verify(userDao).getTotalPages();
    }

    @Test
    public void save_shouldSaveUser() throws InvalidResourceException {
        when(userDao.save(user)).thenReturn(user);

        User savedUser = service.save(user);

        assertEquals(user, savedUser);
        verify(userDao).save(user);
    }

    @Test
    public void save_shouldThrowExceptionIfUserIsInvalid() throws InvalidUserException {
        doThrow(InvalidUserException.class).when(userValidator).validate(user);

        assertThrows(InvalidResourceException.class, () -> service.save(user));

        verify(userValidator).validate(user);
    }

    @Test
    public void update_shouldUpdateUser() throws InvalidResourceException {
        user.setName("updated");
        when(userDao.update(user)).thenReturn(user);

        User updatedUser = service.update(user);

        assertEquals(user, updatedUser);
        verify(userDao).update(user);
    }

    @Test
    public void update_shouldThrowExceptionIfUserIsInvalid() throws InvalidUserException {
        doThrow(InvalidUserException.class).when(userValidator).validate(user);

        assertThrows(InvalidResourceException.class, () -> service.update(user));

        verify(userValidator).validate(user);
    }

    @Test
    public void delete_shouldDeleteUser() {
        doNothing().when(userDao).delete(user);

        service.delete(user);

        verify(userDao).delete(user);
    }

    @Test
    public void orderCertificate_musReturnOrder() {
        when(orderDao.save(order)).thenReturn(order);
        doNothing().when(orderDao).setUserToOrder(user.getId(), order.getId());

        Order madeOrder = service.orderCertificate(user, certificate);

        assertEquals(order, madeOrder);
        verify(orderDao).save(order);
        verify(orderDao).setUserToOrder(user.getId(), order.getId());
    }

    @Test
    public void findUserOrdersPage_shouldReturnOrders() throws PageOutOfBoundsException {
        List<Order> orders = Collections.singletonList(order);
        when(orderDao.getUserOrdersTotalPages(user.getId())).thenReturn(1);
        when(orderDao.findUserOrdersPage(user.getId(), 1)).thenReturn(orders);

        List<Order> ordersOnPage = service.findUserOrderPage(user, 1);

        assertEquals(orders, ordersOnPage);
        verify(orderDao, atLeastOnce()).getUserOrdersTotalPages(user.getId());
        verify(orderDao).findUserOrdersPage(user.getId(), 1);
    }

    @Test
    public void findUserOrderPage_shouldThrowExceptionIfPageOutOfBounds() {
        when(orderDao.getUserOrdersTotalPages(user.getId())).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findUserOrderPage(user, 100));

        verify(orderDao, atLeastOnce()).getUserOrdersTotalPages(user.getId());
    }

    @Test
    public void getUserOrdersTotalPages_shouldReturnPositiveValue() {
        when(orderDao.getUserOrdersTotalPages(user.getId())).thenReturn(1);

        int userOrdersTotalPages = service.getUserOrdersTotalPages(user);

        assertTrue(userOrdersTotalPages >= 0);
        assertEquals(1, userOrdersTotalPages);
        verify(orderDao).getUserOrdersTotalPages(user.getId());
    }

    @Test
    public void getUserOrdersTotalElements_shouldReturnPositiveValue() {
        when(orderDao.getUserOrdersTotalElements(user.getId())).thenReturn(1);

        int userOrdersTotalElements = service.getUserOrdersTotalElements(user);

        assertTrue(userOrdersTotalElements >= 0);
        assertEquals(1, userOrdersTotalElements);
        verify(orderDao).getUserOrdersTotalElements(user.getId());
    }

}