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
    private static final String SAVE_CERTIFICATE_ORDER_SQL = "insert into certificate_order (cost, certificate_id, user_id) values (?, ?, ?)";
    private static final String FIND_ORDER_BY_CERTIFICATE_ID_SQL = "select id, cost, date, certificate_id from certificate_order where certificate_id = ?";
    private static final String FIND_USER_ORDERS_PAGE_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ? limit ?, 5";
    private static final String COUNT_USER_ORDERS_SQL = "select count(*) from certificate_order where user_id = ?";
    private static final String COUNT_COLUMN = "count(*)";

    private final CertificateDao certificateDao;

    @Autowired
    public OrderJdbcDao(CertificateDao certificateDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.certificateDao = certificateDao;
    }

    @Override
    public List<Order> findPage(long page) {
        List<Order> allOrders = super.findPage(page);
        allOrders.forEach(this::addCertificateToOrder);
        return allOrders;
    }

    @Override
    public Optional<Order> findById(Long id) {
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
    public List<Order> findAllUserOrders(Long userId) {
        List<Order> foundOrders = template.query(FIND_ALL_USER_ORDERS_SQL, MAPPER, userId);
        foundOrders.forEach(this::addCertificateToOrder);
        return foundOrders;
    }

    @Override
    public Order makeUserOrder(Long userId, Order order) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        template.update(creator -> {
            PreparedStatement saveStatement = creator.prepareStatement(SAVE_CERTIFICATE_ORDER_SQL, Statement.RETURN_GENERATED_KEYS);
            saveStatement.setDouble(1, order.getCost());
            saveStatement.setLong(2, order.getCertificate().getId());
            saveStatement.setLong(3, userId);
            return saveStatement;
        }, generatedKeyHolder);
        long id = generatedKeyHolder.getKey().longValue();
        order.setId(id);
        return order;
    }

    @Override
    public Optional<Order> findByCertificateId(Long certificateId) {
        List<Order> orders = template.query(FIND_ORDER_BY_CERTIFICATE_ID_SQL, MAPPER, certificateId);
        orders.forEach(this::addCertificateToOrder);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Override
    public List<Order> findUserOrdersPage(Long userId, long page) {
        List<Order> orders = template.query(FIND_USER_ORDERS_PAGE_SQL, MAPPER, userId, (ROWS_PER_PAGE * page) - ROWS_PER_PAGE);
        orders.forEach(this::addCertificateToOrder);
        return orders;
    }

    @Override
    public long getUserOrdersTotalPages(Long userId) {
        long rows = template.queryForObject(COUNT_USER_ORDERS_SQL, (rs, rowNum) -> rs.getLong(COUNT_COLUMN), userId);
        long pages = (rows / ROWS_PER_PAGE);
        return rows % ROWS_PER_PAGE == 0 ? pages : ++pages;
    }

    @Override
    public long getUserOrdersTotalElements(Long userId) {
        return template.queryForObject(COUNT_USER_ORDERS_SQL, (rs, rowNum) -> rs.getLong(COUNT_COLUMN), userId);
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

    private void addCertificateToOrder(Order order) {
        Optional<Certificate> optionalCertificate = certificateDao.findByOrderId(order.getId());
        order.setCertificate(optionalCertificate.orElseThrow(DaoException::new));
    }
}
