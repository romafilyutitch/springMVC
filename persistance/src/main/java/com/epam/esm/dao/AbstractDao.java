package com.epam.esm.dao;

import com.epam.esm.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Dao layer abstract dao class. If it is need to make dao for some
 * entity. You may extend this class to get basic CRUD operations.
 * Uses MySQL database to to save and manipulate entities.
 * @param <T> entity which dao operates
 */
@Component
public abstract class AbstractDao<T extends Entity> implements Dao<T> {
    private final String findAllSql;
    private final String findByIdSql;
    private final String saveSql;
    private final String updateSql;
    private final String deleteSql;

    @Autowired
    protected DataSource dataSource;

    public AbstractDao(String findAllSql, String finByIdSql, String saveSql, String updateSql, String deleteSql) {
        this.findAllSql = findAllSql;
        this.findByIdSql = finByIdSql;
        this.saveSql = saveSql;
        this.updateSql = updateSql;
        this.deleteSql = deleteSql;
    }

    /**
     * Performs select database request to find all entities
     * @return list of all entities from database table
     */
    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(findAllSql)) {
            while (resultSet.next()) {
                T entity = mapResultSetToEntity(resultSet);
                entities.add(entity);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return entities;
    }

    /**
     * Performs select database request to find entity with passed id.
     * May return empty optional if entity was not found
     * @param id id of entity that need to be found
     * @return Optional that contains entity if entity was found or
     * empty optional otherwise
     */
    @Override
    public Optional<T> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(findByIdSql)) {
            findByIdStatement.setLong(1, id);
            ResultSet resultSet = findByIdStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Performs insert database request to save entity properties to
     * database table. Assign generated id to saved entity
     * @param entity entity that need to be saved
     * @return saved entity with generated id
     */
    @Override
    public T save(T entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement saveStatement = connection.prepareStatement(saveSql, Statement.RETURN_GENERATED_KEYS)) {
            mapEntityToSavePreparedStatement(saveStatement, entity);
            saveStatement.executeUpdate();
            ResultSet generatedKeys = saveStatement.getGeneratedKeys();
            generatedKeys.next();
            long generatedKey = generatedKeys.getLong(1);
            entity.setId(generatedKey);
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Performs update database request to replace entity table
     * values with new values.
     * @param entity entity that need to be updated
     * @return updated entity
     */
    @Override
    public T update(T entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            mapEntityToUpdatePreparedStatement(updateStatement, entity);
            updateStatement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Performs delete database request to delete entity that has passed id
     * @param id id of entity that need to be deleted
     */
    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Maps ResultSet to particular entity. Implementation of
     * template method pattern. Subclassed must override that method
     * to define how to map ResultSet to needed entity
     * @param resultSet from which need get values and map entity
     * @return mapped entity from Result set
     * @throws SQLException if exception with database operations occur
     */
    protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;

    /**
     * Maps entity properties to prepared statement. Implementation of
     * template method pattern. Subclasses must override that methods
     * to define how to map Entity values to prepared statement
     * @param saveStatement prepared statement that accept entity's properties
     * @param entity entity which properties need to map to save statement
     * @throws SQLException if exceptions with database operations occur
     */
    protected abstract void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, T entity) throws SQLException;

    /**
     * Maps entity properties to prepared update statement. Implementation of
     * template method pattern. Subclassed must override that method
     * to define ho to map Entity values to prepared update statement
     * @param updateStatement prepared statement that accept entity's properties
     * @param entity entity witch properties need to map to updated statement
     * @throws SQLException if excepton with database operations occur
     */
    protected abstract void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, T entity) throws SQLException;
}
