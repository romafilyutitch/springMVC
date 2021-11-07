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
    private static final List<String> COLUMNS = Arrays.asList("cost", "date", "certificate_id");
    private static final RowMapper<Order> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        double cost = rs.getDouble("cost");
        LocalDateTime date = rs.getObject("date", LocalDateTime.class);
        return new Order(id, cost, date);
    };
    private static final String FIND_ALL_USER_ORDERS_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ?";
    private static final String FIND_ORDER_BY_CERTIFICATE_ID_SQL = "select id, cost, date, certificate_id from certificate_order where certificate_id = ? limit ?, ?";
    private static final String FIND_USER_ORDERS_PAGE_SQL = "select id, cost, date, certificate_id from certificate_order where user_id = ? limit ?, ?";
    private static final String COUNT_USER_ORDERS_SQL = "select count(*) from certificate_order where user_id = ?";
    private static final String SET_USER_ID_TO_ORDER_SQL = "update certificate_order set user_id = ? where id = ?";
    private static final String COUNT_CERTIFICATE_ORDERS_SQL = "select count(*) from certificate_order where certificate_id = ?";
    private static final String COUNT_COLUMN = "count(*)";

    private final CertificateDao certificateDao;

    @Autowired
    public OrderJdbcDao(CertificateDao certificateDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.certificateDao = certificateDao;
    }

    /**
     * Finds and returns entities on specified page
     * @param offset page offset
     * @param limit page limit
     * @return entities on passed page
     */
    @Override
    public List<Order> findPage(int offset, int limit) {
        List<Order> allOrders = super.findPage(offset, limit);
        allOrders.forEach(this::addCertificateToOrder);
        return allOrders;
    }
    /**
     * Finds and returns entity that have passed id
     *
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity with passed id exists
     * or empty optional otherwise
     */
    @Override
    public Optional<Order> findById(long id) {
        Optional<Order> optionalOrder = super.findById(id);
        optionalOrder.ifPresent(this::addCertificateToOrder);
        return optionalOrder;
    }
    /**
     * Perform entity save operation. And assigns calculates by database id to saved entity
     *
     * @param entity entity that need to be saved
     * @return saved entity with assigned id
     */
    @Override
    public Order save(Order entity) {
        Order savedOrder = super.save(entity);
        Certificate certificate = entity.getCertificate();
        Certificate savedCertificate = certificateDao.save(certificate);
        entity.setCertificate(savedCertificate);
        return findById(savedOrder.getId()).orElseThrow(DaoException::new);
    }
    /**
     * Finds and returns user orders by user id
     * @param userId id of user which orders need to be found
     * @return list of user orders
     */
    @Override
    public List<Order> findAllUserOrders(long userId) {
        List<Order> foundOrders = template.query(FIND_ALL_USER_ORDERS_SQL, MAPPER, userId);
        foundOrders.forEach(this::addCertificateToOrder);
        return foundOrders;
    }
    /**
     * Sets user to saved order.
     * @param userId id of user that need to be saved to order
     * @param orderId order id that need to be set to user
     */
    @Override
    public void setUserToOrder(long userId, long orderId) {
        template.update(SET_USER_ID_TO_ORDER_SQL, userId, orderId);
    }
    /**
     * Finds certificate order by passed certificate id
     * @param certificateId whose order need to be found
     * @return order that contains certificate with passed id
     * or empty order otherwise
     */
    @Override
    public List<Order> findCertificateOrders(long certificateId, int offset, int limit) {
        List<Order> orders = template.query(FIND_ORDER_BY_CERTIFICATE_ID_SQL, MAPPER, certificateId, offset, limit);
        orders.forEach(this::addCertificateToOrder);
        return orders;
    }
    /**
     * Finds and returns passed user passed orders page
     * @param userId user id which orders need to be found
     * @param offset page offset
     * @param limit page limit
     * @return user orders passed page
     */
    @Override
    public List<Order> findUserOrdersPage(long userId, int offset, int limit) {
        List<Order> orders = template.query(FIND_USER_ORDERS_PAGE_SQL, MAPPER, userId, offset, limit);
        orders.forEach(this::addCertificateToOrder);
        return orders;
    }

    /**
     * Counts user orders amount.
     * @param userId id of user which orders amount need to be count
     * @return amount of user orders
     */
    @Override
    public int getUserOrdersTotalElements(long userId) {
        return template.queryForObject(COUNT_USER_ORDERS_SQL, (rs, rowNum) -> rs.getInt(COUNT_COLUMN), userId);
    }

    @Override
    public int getCertificateOrdersTotalElements(long certificateId) {
        return template.queryForObject(COUNT_CERTIFICATE_ORDERS_SQL, (rs, rowNum) -> rs.getInt(COUNT_COLUMN), certificateId);
    }

    /**
     * Maps entity values to Prepared save statement
     * @param saveStatement PreparedStatement that need to be set entity values for save
     * @param entity        entity that need to be saved
     * @throws SQLException if exception in database occurs
     */
    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Order entity) throws SQLException {
        saveStatement.setDouble(1, entity.getCost());
        saveStatement.setObject(2, entity.getOrderDate());
        saveStatement.setLong(3, entity.getCertificate().getId());
    }

    /**
     * Maps entity values to Prepared updated statement
     * @param updateStatement Prepared statement that need to be set entity values for update
     * @param entity          entity that need to be updated
     * @throws SQLException if exception in database occurs
     */
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