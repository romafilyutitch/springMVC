package com.epam.esm.dao;

import com.epam.esm.config.DevConfig;
import com.epam.esm.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DevConfig.class)
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
class OrderJdbcDaoTest {
    @Autowired
    private OrderJdbcDao dao;

    @Test
    public void findPage_shouldReturnFirstPage() {
        List<Order> ordersPage = dao.findPage(1);

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
        Optional<Order> order = dao.findByCertificateId(1);
        assertTrue(order.isPresent());
    }

    @Test
    public void findByCertificateId_shouldReturnEmptyOptionalIfThereIsNoOrder() {
        Optional<Order> optionalOrder = dao.findByCertificateId(0);
        assertFalse(optionalOrder.isPresent());
    }

    @Test
    public void findUserOrderPage_shouldReturnUserOrdersFirstPage() {
        List<Order> firstPage = dao.findUserOrdersPage(1, 1);
        assertEquals(1, firstPage.size());
        Order order = firstPage.get(0);
        assertEquals(1, order.getId());
        assertEquals(200.50, order.getCost());
        assertNotNull(order.getCertificate());
    }

    @Test
    public void getUserOrdersTotalPages_shouldReturnOne() {
        int userOrdersPages = dao.getUserOrdersTotalPages(1);
        assertEquals(1, userOrdersPages);
    }

    @Test
    public void getUserOrdersTotalElements_shouldReturnOne() {
        int userOrdersTotalElements = dao.getUserOrdersTotalElements(1);
        assertEquals(1, userOrdersTotalElements);
    }
}