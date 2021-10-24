package com.epam.esm.dao;

import com.epam.esm.model.Order;
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

    private final OrderDao orderDao;

    @Autowired
    public UserJdbcDao(OrderDao orderDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.orderDao = orderDao;
    }

    @Override
    public List<User> findAll() {
        List<User> allUsers = super.findAll();
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
