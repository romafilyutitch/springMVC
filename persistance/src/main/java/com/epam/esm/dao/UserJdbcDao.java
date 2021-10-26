package com.epam.esm.dao;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcDao extends AbstractDao<User> implements UserDao {
    private static final String TABLE_NAME = "user";
    private static final List<String> COLUMNS = Arrays.asList("name", "surname");
    private static final RowMapper<User> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        return new User(id, name, surname);
    };
    private static final String FIND_RICHEST_USER_SQL = "select user.id, user.name, user.surname from user " +
            "left join certificate_order on certificate_order.user_id = user.id " +
            "left join gift_certificate on certificate_order.certificate_id = gift_certificate.id " +
            "left join certificate_tag on gift_certificate.id = certificate_tag.certificate_id " +
            "left join tag on tag.id = certificate_tag.tag_id " +
            "group by user.id order by sum(certificate_order.cost) desc limit 0, 1";
    private static final String FIND_RICHEST_USER_POPULAR_TAG = "select tag.id, tag.name from certificate_order " +
            "left join gift_certificate on certificate_order.certificate_id = gift_certificate.id " +
            "left join certificate_tag on gift_certificate.id = certificate_tag.certificate_id " +
            "left join tag on tag.id = certificate_tag.tag_id " +
            "where certificate_order.user_id = " +
            "(select user.id from user group by user.id order by sum(certificate_order.cost) desc limit 0, 1) " +
            "group by tag.name order by count(tag.name) desc limit 0,1 ";
    private final OrderDao orderDao;

    @Autowired
    public UserJdbcDao(OrderDao orderDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.orderDao = orderDao;
    }

    @Override
    public List<User> findAll(long page) {
        List<User> allUsers = super.findAll(page);
        allUsers.forEach(this::addOrdersToUser);
        return allUsers;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> optionalUser = super.findById(id);
        optionalUser.ifPresent(this::addOrdersToUser);
        return optionalUser;
    }

    @Override
    public Optional<User> findByName(String name) {
        List<User> users = template.query("select id, name, surname from user where name = ?", MAPPER, name);
        users.forEach(this::addOrdersToUser);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public User findRichestUser() {
        User user = template.queryForObject(FIND_RICHEST_USER_SQL, MAPPER);
        addOrdersToUser(user);
        return user;
    }

    @Override
    public Tag findRichestUserPopularTag() {
        return template.queryForObject(FIND_RICHEST_USER_POPULAR_TAG, (rs, rowNum) -> {
            long id = rs.getLong("tag.id");
            String name = rs.getString("tag.name");
            return new Tag(id, name);
        });
    }

    @Override
    protected void setSaveValues(PreparedStatement saveStatement, User entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getSurname());
    }

    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, User entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getSurname());
        updateStatement.setLong(3, entity.getId());
    }

    private void addOrdersToUser(User user) {
        List<Order> userOrders = orderDao.findByUserId(user.getId());
        user.setOrders(userOrders);
    }
}
