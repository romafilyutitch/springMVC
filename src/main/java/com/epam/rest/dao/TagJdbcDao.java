package com.epam.rest.dao;

import com.epam.rest.model.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TagJdbcDao extends AbstractDao<Tag> implements TagDao {
    private static final String FIND_ALL_SQL = "select * from tag";
    private static final String FIND_BY_ID_SQL = "select * from tag where id = ?";
    private static final String FIND_BY_NAME_SQL = "select * from tag where name = ?";
    private static final String SAVE_SQL = "insert into tag (name) values (?)";
    private static final String UPDATE_SQL = "update tag set name = ? where id = ?";
    private static final String DELETE_SQL = "delete from tag where id = ?";

    public TagJdbcDao() {
        super(FIND_ALL_SQL, FIND_BY_ID_SQL, SAVE_SQL, UPDATE_SQL, DELETE_SQL);
    }

    @Override
    protected Tag mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Tag(id, name);
    }

    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, Tag entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
    }

    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, Tag entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setLong(2, entity.getId());
    }

    @Override
    public Optional<Tag> findByName(String name) {
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
