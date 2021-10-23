package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderJdbcDao extends AbstractDao<Order> implements OrderDao {
    private static final String TABLE_NAME = "certificate_order";
    private static final List<String> COLUMNS = Arrays.asList("cost", "date");
    private static final RowMapper<Order> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        double cost = rs.getDouble("cost");
        LocalDateTime date = rs.getObject("date", LocalDateTime.class);
        return new Order(id, cost, date);
    };
    private static final String FIND_ORDERS_BY_USER_ID_SQL = "select id, cost, date from certificate_order where user_id = ?";

    private final CertificateDao certificateDao;

    @Autowired
    public OrderJdbcDao(CertificateDao certificateDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.certificateDao = certificateDao;
    }

    @Override
    public List<Order> findAll() {
        List<Order> allOrders = super.findAll();
        allOrders.forEach(this::addCertificatesToOrder);
        return allOrders;
    }

    private void addCertificatesToOrder(Order order) {
        List<Certificate> orderCertificates = certificateDao.findByOrderId(order.getId());
        order.setCertificates(orderCertificates);
    }

    @Override
    public Optional<Order> findById(Long id) {
        Optional<Order> optionalOrder = super.findById(id);
        optionalOrder.ifPresent(this::addCertificatesToOrder);
        return optionalOrder;
    }

    @Override
    public Order save(Order entity) {
        super.save(entity);
        List<Certificate> certificates = entity.getCertificates();
        List<Certificate> savedCertificates = certificates.stream().map(certificateDao::save).collect(Collectors.toList());
        entity.setCertificates(savedCertificates);
        return entity;
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        List<Order> foundOrders = template.query(FIND_ORDERS_BY_USER_ID_SQL, MAPPER, userId);
        foundOrders.forEach(this::addCertificatesToOrder);
        return foundOrders;
    }

    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Order entity) throws SQLException {
        saveStatement.setDouble(1, entity.getCost());
        saveStatement.setObject(2, entity.getOrderDate());
    }

    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, Order entity) throws SQLException {
        updateStatement.setDouble(1, entity.getCost());
        updateStatement.setObject(2, entity.getOrderDate());
        updateStatement.setLong(3, entity.getId());
    }
}
