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
import java.util.stream.Collectors;

@Repository
public class OrderJdbcDao extends AbstractDao<Order> implements OrderDao {
    private static final String TABLE_NAME = "certificate_order";
    private static final List<String> COLUMNS = Arrays.asList("cost", "date", "user_id", "certificate_id");
    private static final RowMapper<Order> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        double cost = rs.getDouble("cost");
        LocalDateTime date = rs.getObject("date", LocalDateTime.class);
        long certificateId = rs.getLong("certificate_id");
        Certificate certificate = new Certificate(certificateId);
        return new Order(id, cost, date, certificate);
    };
    private static final String FIND_ORDERS_BY_USER_ID_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ?";

    private final CertificateDao certificateDao;

    @Autowired
    public OrderJdbcDao(CertificateDao certificateDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.certificateDao = certificateDao;
    }

    @Override
    public List<Order> findAll() {
        List<Order> allOrders = super.findAll();
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
    public List<Order> findByUserId(Long userId) {
        List<Order> foundOrders = template.query(FIND_ORDERS_BY_USER_ID_SQL, MAPPER, userId);
        foundOrders.forEach(this::addCertificateToOrder);
        return foundOrders;
    }

    @Override
    public Order makeUserOrder(Long userId, Order order) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        template.update(creator -> {
            PreparedStatement saveStatement = creator.prepareStatement("insert into certificate_order (cost, certificate_id, user_id) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
        List<Order> orders = template.query("select id, cost, date, certificate_id from certificate_order where certificate_id = ?", MAPPER, certificateId);
        orders.forEach(this::addCertificateToOrder);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
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
        Optional<Certificate> optionalCertificate = certificateDao.findById(order.getCertificate().getId());
        order.setCertificate(optionalCertificate.orElseThrow(DaoException::new));
    }
}
