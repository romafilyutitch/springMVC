package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.repository.OffsetPageable;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.validation.InvalidResourceException;
import com.epam.esm.validation.InvalidUserException;
import com.epam.esm.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRestServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserValidator userValidator = mock(UserValidator.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserRestService service = new UserRestService(userRepository, orderRepository, userValidator, passwordEncoder);
    private final User user = new User(1, "user", "user");
    private final Certificate certificate = new Certificate(1, "test", "test", 100.0, 10, LocalDateTime.now(), LocalDateTime.now());
    private final Order order = new Order(certificate.getPrice(), certificate);

    @Test
    public void findPage_shouldReturnUsersOnPage() throws PageOutOfBoundsException, InvalidPageException {
        List<User> users = Collections.singletonList(user);
        Page<User> page = new PageImpl<>(users);
        when(userRepository.count()).thenReturn(1L);
        OffsetPageable offsetPageable = new OffsetPageable(0, 10);
        when(userRepository.findAll(offsetPageable)).thenReturn(page);

        List<User> usersOnPage = service.findPage(0, 10);

        assertEquals(users, usersOnPage);
        verify(userRepository, atLeastOnce()).count();
        verify(userRepository).findAll(offsetPageable);
    }

    @Test
    public void findPage_shouldThrowExceptionIfOffsetGreaterThenTotalElements() {
        when(userRepository.count()).thenReturn(1L);

        assertThrows(PageOutOfBoundsException.class, () -> service.findPage(10, 10));

        verify(userRepository).count();
    }

    @Test
    public void findPage_shouldThrowExceptionIfOffsetIsNegative() {
        when(userRepository.count()).thenReturn(1L);

        assertThrows(InvalidPageException.class, () -> service.findPage(-10, 10));

        verify(userRepository).count();
    }

    @Test
    public void findById_shouldReturnUser() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = service.findById(1);

        assertEquals(user, foundUser);
        verify(userRepository).findById(1L);
    }

    @Test
    public void findById_shouldThrowExceptionIfThereIsNoUsersWithId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1));

        verify(userRepository).findById(1L);
    }

    @Test
    public void findRichestUser_shouldReturnUser() {
        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        OffsetPageable offsetPageable = new OffsetPageable(0, 1);
        when(userRepository.sortByUsersByCostDesc(offsetPageable)).thenReturn(page);

        User richestUser = service.findRichestUser();

        assertEquals(user, richestUser);
        verify(userRepository).sortByUsersByCostDesc(offsetPageable);
    }

    @Test
    public void findRichestUserPopularTag_shouldReturnUserTag() {
        Tag popularTag = new Tag(1, "tag");
        Page<Tag> page = new PageImpl<>(Collections.singletonList(popularTag));
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));
        OffsetPageable offsetPageable = new OffsetPageable(0, 1);
        when(userRepository.sortByUsersByCostDesc(offsetPageable)).thenReturn(usersPage);
        when(userRepository.sortUserTagsByCountDesc(1, offsetPageable)).thenReturn(page);

        Tag foundTag = service.findRichestUserPopularTag();

        assertEquals(popularTag, foundTag);
        verify(userRepository).sortUserTagsByCountDesc(1, offsetPageable);
    }

    @Test
    public void getTotalElements_shouldReturnNotNegativeValue() {
        when(userRepository.count()).thenReturn(1L);

        int totalElements = service.getTotalElements();

        assertTrue(totalElements >= 0);
        assertEquals(1, totalElements);
        verify(userRepository).count();
    }

    @Test
    public void save_shouldSaveUser() throws InvalidResourceException, UsernameExistsException {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = service.save(user);

        assertEquals(user, savedUser);
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    public void save_shouldThrowExceptionIfUserIsInvalid() throws InvalidUserException {
        doThrow(InvalidUserException.class).when(userValidator).validate(user);

        assertThrows(InvalidResourceException.class, () -> service.save(user));

        verify(userValidator).validate(user);
    }

    @Test
    public void update_shouldUpdateUser() throws InvalidResourceException, UserNotFoundException {
        user.setUsername("updated");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User updatedUser = service.update(user);

        assertEquals(user, updatedUser);
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void update_shouldThrowExceptionIfUserIsInvalid() throws InvalidUserException {
        doThrow(InvalidUserException.class).when(userValidator).validate(user);

        assertThrows(InvalidResourceException.class, () -> service.update(user));

        verify(userValidator).validate(user);
    }

    @Test
    public void delete_shouldDeleteUser() {
        doNothing().when(userRepository).delete(user);

        service.delete(user);

        verify(userRepository).delete(user);
    }

    @Test
    public void orderCertificate_musReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order madeOrder = service.orderCertificate(user, certificate);

        assertEquals(order, madeOrder);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void findUserOrdersPage_shouldReturnOrders() throws PageOutOfBoundsException, InvalidPageException {
        List<Order> orders = Collections.singletonList(order);
        Page<Order> page = new PageImpl<>(orders);
        OffsetPageable offsetPageable = new OffsetPageable(0, 10);
        when(orderRepository.getUserOrdersTotalElements(user.getId())).thenReturn(1);
        when(orderRepository.findUserOrdersPage(user.getId(), offsetPageable)).thenReturn(page);

        List<Order> ordersOnPage = service.findUserOrderPage(user, 0, 10);

        assertEquals(orders, ordersOnPage);
        verify(orderRepository, atLeastOnce()).getUserOrdersTotalElements(user.getId());
        verify(orderRepository).findUserOrdersPage(user.getId(), offsetPageable);
    }

    @Test
    public void findUserOrderPage_shouldThrowExceptionIfOffsetIsGreaterThenTotalElements() {
        when(orderRepository.getUserOrdersTotalElements(user.getId())).thenReturn(1);

        assertThrows(PageOutOfBoundsException.class, () -> service.findUserOrderPage(user, 100, 10));

        verify(orderRepository).getUserOrdersTotalElements(user.getId());
    }

    @Test
    public void findUserOrderPage_shouldThrowExceptionIFOffsetIsNegative() {
        when(orderRepository.getUserOrdersTotalElements(user.getId())).thenReturn(1);

        assertThrows(InvalidPageException.class, () -> service.findUserOrderPage(user, -10, 10));

        verify(orderRepository).getUserOrdersTotalElements(user.getId());
    }

    @Test
    public void getUserOrdersTotalElements_shouldReturnPositiveValue() {
        when(orderRepository.getUserOrdersTotalElements(user.getId())).thenReturn(1);

        int userOrdersTotalElements = service.getUserOrdersTotalElements(user);

        assertTrue(userOrdersTotalElements >= 0);
        assertEquals(1, userOrdersTotalElements);
        verify(orderRepository).getUserOrdersTotalElements(user.getId());
    }

    @Test
    public void findByUsername_shouldReturnSavedUserByUsername() throws UsernameNotFoundException {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User foundUser = service.findByUsername(user.getUsername());
        assertEquals(user, foundUser);


        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    public void findByUserName_shouldThrowExceptionIfThereIsNoUserWithName() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.findByUsername(user.getUsername()));

        verify(userRepository).findByUsername(user.getUsername());
    }
}