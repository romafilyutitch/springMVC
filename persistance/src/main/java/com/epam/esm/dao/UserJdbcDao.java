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
            "(select user.id from user " +
            "left join certificate_order on certificate_order.user_id = user.id " +
            "group by user.id order by sum(certificate_order.cost) desc limit 0, 1) " +
            "group by tag.id order by count(tag.id) desc limit 0,1 ";
    private final OrderDao orderDao;

    @Autowired
    public UserJdbcDao(OrderDao orderDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.orderDao = orderDao;
    }
    /**
     * Finds and returns entities on specified page
     * @param page that need to be finds
     * @return entities on passed page
     */
    @Override
    public List<User> findPage(int page) {
        List<User> allUsers = super.findPage(page);
        allUsers.forEach(this::addOrdersToUser);
        return allUsers;
    }
    /**
     * Finds and returns entity that have passed id
     *
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity with passed id exists
     * or empty optional otherwise
     */
    @Override
    public Optional<User> findById(long id) {
        Optional<User> optionalUser = super.findById(id);
        optionalUser.ifPresent(this::addOrdersToUser);
        return optionalUser;
    }
    /**
     * Finds and returns riches user.
     * Riches user is the user that has maximum of orders cost.
     * @return user that has maximum orders cost
     */
    @Override
    public User findRichestUser() {
        User user = template.queryForObject(FIND_RICHEST_USER_SQL, MAPPER);
        addOrdersToUser(user);
        return user;
    }
    /**
     * Finds and returns riches user popular tag.
     * Popular tag is tag that uses most frequently amount richest
     * user orders
     * @return popular tag
     */
    @Override
    public Tag findRichestUserPopularTag() {
        return template.queryForObject(FIND_RICHEST_USER_POPULAR_TAG, (rs, rowNum) -> {
            long id = rs.getLong("tag.id");
            String name = rs.getString("tag.name");
            return new Tag(id, name);
        });
    }

    /**
     * Maps entity values to save PreparedStatement
     * @param saveStatement PreparedStatement that need to be set entity values for save
     * @param entity        entity that need to be saved
     * @throws SQLException if exception in database occurs
     */
    @Override
    protected void setSaveValues(PreparedStatement saveStatement, User entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getSurname());
    }

    /**
     * Maps entity values to update PreparedStatement
     * @param updateStatement Prepared statement that need to be set entity values for update
     * @param entity          entity that need to be updated
     * @throws SQLException if exception in database occurs
     */
    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, User entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getSurname());
        updateStatement.setLong(3, entity.getId());
    }

    private void addOrdersToUser(User user) {
        List<Order> userOrders = orderDao.findAllUserOrders(user.getId());
        user.setOrders(userOrders);
    }
}
