package com.epam.rest.dao;

import com.epam.rest.model.GiftCertificate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class GiftCertificateJdbcDao extends AbstractDao<GiftCertificate> implements GifCertificateDao {
    private static final String FIND_ALL_SQL = "select * from gift_certificate";
    private static final String FIND_BY_ID_SQL = "select * from gift_certificate where id = ?";
    private static final String FIND_BY_NAME_SQL = "select * from gift_certificate where name = ?";
    private static final String SAVE_SQL = "insert into gif_certificate (name, description, price, duration, create_date, last_update_date)" +
            " values (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "update gift_certificate set name = ?, description = ?, price = ?, duration = ?, create_date = ?, last_update_date = ? " +
            "where id = ?";
    private static final String DELETE_SQL = "delete from gift_certificate where id = ?";

    public GiftCertificateJdbcDao() {
        super(FIND_ALL_SQL, FIND_BY_ID_SQL, SAVE_SQL, UPDATE_SQL, DELETE_SQL);
    }

    @Override
    protected GiftCertificate mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        double price = resultSet.getDouble("price");
        int duration = resultSet.getInt("duration");
        LocalDateTime createDate = resultSet.getObject("create_date", LocalDateTime.class);
        LocalDateTime lastUpdatDate = resultSet.getObject("last_update_date", LocalDateTime.class);
        return new GiftCertificate(id, name, description, price, duration, createDate, lastUpdatDate);
    }

    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, GiftCertificate entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getDescription());
        saveStatement.setDouble(3, entity.getPrice());
        saveStatement.setInt(4, entity.getDuration());
        saveStatement.setObject(5, entity.getCreateDate());
        saveStatement.setObject(6, entity.getLastUpdateDate());
    }

    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, GiftCertificate entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getDescription());
        updateStatement.setDouble(3, entity.getPrice());
        updateStatement.setInt(4, entity.getDuration());
        updateStatement.setObject(5, entity.getCreateDate());
        updateStatement.setObject(6, entity.getLastUpdateDate());
        updateStatement.setLong(7, entity.getId());
    }

    @Override
    public Optional<GiftCertificate> findByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByNameStatement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            findByNameStatement.setString(1, name);
            ResultSet resultSet = findByNameStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
