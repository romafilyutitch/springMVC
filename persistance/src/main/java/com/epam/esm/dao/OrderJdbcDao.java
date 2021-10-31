package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderJdbcDao extends AbstractDao<Order> implements OrderDao {
    private static final String TABLE_NAME = "certificate_order";
    private static final List<String> COLUMNS = Arrays.asList("cost", "date", "user_id", "certificate_id");
    private static final RowMapper<Order> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        double cost = rs.getDouble("cost");
        LocalDateTime date = rs.getObject("date", LocalDateTime.class);
        return new Order(id, cost, date);
    };
    private static final String FIND_ALL_USER_ORDERS_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ?";
    private static final String FIND_ORDER_BY_CERTIFICATE_ID_SQL = "select id, cost, date, certificate_id from certificate_order where certificate_id = ?";
    private static final String FIND_USER_ORDERS_PAGE_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ? limit ?, 5";
    private static final String COUNT_USER_ORDERS_SQL = "select count(*) from certificate_order where user_id = ?";
    private static final String SET_USER_ID_TO_ORDER_SQL = "update certificate_order set user_id = ? where id = ?";
    private static final String COUNT_COLUMN = "count(*)";

    private final CertificateDao certificateDao;

    @Autowired
    public OrderJdbcDao(CertificateDao certificateDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.certificateDao = certificateDao;
    }

    @Override
    public List<Order> findPage(int page) {
        List<Order> allOrders = super.findPage(page);
        allOrders.forEach(this::addCertificateToOrder);
        return allOrders;
    }

    @Override
    public Optional<Order> findById(long id) {
        Optional<Order> optionalOrder = super.findById(id);
        optionalOrder.ifPresent(this::addCertificateToOrder);
        return optionalOrder;
    }

    @Override
    public Order save(Order entity) {
        super.save(entity);
        Certificate certificate = entity.getCertificate();
        Certificate savedCertificate = certificateDao.save(certificate);
        entity.setCertificate(savedCertificate);
        return entity;
    }

    @Override
    public List<Order> findAllUserOrders(long userId) {
        List<Order> foundOrders = template.query(FIND_ALL_USER_ORDERS_SQL, MAPPER, userId);
        foundOrders.forEach(this::addCertificateToOrder);
        return foundOrders;
    }

    @Override
    public void setUserToOrder(long userId, long orderId) {
        template.update(SET_USER_ID_TO_ORDER_SQL, userId, orderId);
    }

    @Override
    public Optional<Order> findByCertificateId(long certificateId) {
        List<Order> orders = template.query(FIND_ORDER_BY_CERTIFICATE_ID_SQL, MAPPER, certificateId);
        orders.forEach(this::addCertificateToOrder);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Override
    public List<Order> findUserOrdersPage(long userId, long page) {
        List<Order> orders = template.query(FIND_USER_ORDERS_PAGE_SQL, MAPPER, userId, (ROWS_PER_PAGE * page) - ROWS_PER_PAGE);
        orders.forEach(this::addCertificateToOrder);
        return orders;
    }

    @Override
    public int getUserOrdersTotalPages(long userId) {
        int rows = template.queryForObject(COUNT_USER_ORDERS_SQL, (rs, rowNum) -> rs.getInt(COUNT_COLUMN), userId);
        int pages = (rows / ROWS_PER_PAGE);
        return rows % ROWS_PER_PAGE == 0 ? pages : ++pages;
    }

    @Override
    public int getUserOrdersTotalElements(long userId) {
        return template.queryForObject(COUNT_USER_ORDERS_SQL, (rs, rowNum) -> rs.getInt(COUNT_COLUMN), userId);
    }

    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Order entity) throws SQLException {
        saveStatement.setDouble(1, entity.getCost());
        saveStatement.setObject(2, entity.getOrderDate());
        saveStatement.setLong(3, entity.getCertificate().getId());
    }

    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, Order entity) throws SQLException {
        updateStatement.setDouble(1, entity.getCost());
        updateStatement.setObject(2, entity.getOrderDate());
        updateStatement.setLong(3, entity.getCertificate().getId());
        updateStatement.setLong(4, entity.getId());
    }

    private void addCertificateToOrder(Order order) {
        Optional<Certificate> optionalCertificate = certificateDao.findByOrderId(order.getId());
        order.setCertificate(optionalCertificate.orElseThrow(DaoException::new));
    }
}
