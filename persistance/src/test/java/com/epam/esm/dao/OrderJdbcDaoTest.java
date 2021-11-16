package com.epam.esm.dao;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistanceConfig.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
@Transactional
class OrderJdbcDaoTest {
    @Autowired
    private OrderJdbcDao dao;

    @Test
    public void findPage_shouldReturnFirstPage() {
        List<Order> ordersPage = dao.findPage(0, 10);

        assertEquals(1, ordersPage.size());
        Order order = ordersPage.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void save_shouldReturnSavedOrder() {
        Optional<Order> optionalOrder = dao.findById(1);
        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void findAllUserOrders_shouldReturnUserOrder() {
        List<Order> allUserOrders = dao.findAllUserOrders(1);
        assertEquals(1, allUserOrders.size());
        Order order = allUserOrders.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void findByCertificateId_shouldReturnOrderById() {
        List<Order> orders = dao.findCertificateOrders(1, 0, 10);
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        Order order = orders.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void findByCertificateId_shouldReturnEmptyOptionalIfThereIsNoOrder() {
        List<Order> orders = dao.findCertificateOrders(0, 0, 10);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void findUserOrderPage_shouldReturnUserOrdersFirstPage() {
        List<Order> orders = dao.findUserOrdersPage(1, 0, 10);
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        Order order = orders.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void getUserOrdersTotalElements_shouldReturnOne() {
        int userOrdersTotalElements = dao.getUserOrdersTotalElements(1);
        assertEquals(1, userOrdersTotalElements);
    }
}