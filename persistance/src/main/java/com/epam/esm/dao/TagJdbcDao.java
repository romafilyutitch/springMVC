package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Component
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

    /**
     * Uses ResultSet get methods do map Tag id and name from ResultSet.
     * @param resultSet from which need get values and map entity
     * @return Tag mapped from ResultSet
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected Tag mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Tag(id, name);
    }

    /**
     * Uses PreparedStatement set statement to set Tag's properties to
     * save PreparedStatement.
     * @param saveStatement prepared statement that accept entity's properties
     * @param entity entity which properties need to map to save statement
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, Tag entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
    }

    /**
     * Uses PreparedStatement set methods to set Tag's properties
     * to update PreparedStatement
     * @param updateStatement prepared statement that accept entity's properties
     * @param entity entity witch properties need to map to updated statement
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, Tag entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setLong(2, entity.getId());
    }

    /**
     * Finds tag that has passed name fom database
     * @param name of tag that need to be found
     * @return Optional tag if there is tag with passed name
     * ot empty optional otherwise
     */
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
