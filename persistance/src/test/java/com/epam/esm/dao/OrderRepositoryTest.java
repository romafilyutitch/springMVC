package com.epam.esm.dao;

import com.epam.esm.configuration.PersistanceConfiguration;
import com.epam.esm.model.Order;
import com.epam.esm.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfiguration.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
class OrderRepositoryTest {
    @Autowired
    private OrderRepository repository;

    @Test
    public void findPage_shouldReturnFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> ordersPage = repository.findAll(pageable).getContent();

        assertEquals(1, ordersPage.size());
        Order order = ordersPage.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void save_shouldReturnSavedOrder() {
        Optional<Order> optionalOrder = repository.findById(1L);
        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void findByCertificateId_shouldReturnOrderById() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = repository.findCertificateOrders(1L, pageable).getContent();
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        Order order = orders.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void findByCertificateId_shouldReturnListIfThereIsNoOrder() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = repository.findCertificateOrders(0, pageable).getContent();
        assertTrue(orders.isEmpty());
    }

    @Test
    public void findUserOrderPage_shouldReturnUserOrdersFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = repository.findUserOrdersPage(1, pageable).getContent();
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        Order order = orders.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void getUserOrdersTotalElements_shouldReturnOne() {
        int userOrdersTotalElements = repository.getUserOrdersTotalElements(1);
        assertEquals(1, userOrdersTotalElements);
    }
}