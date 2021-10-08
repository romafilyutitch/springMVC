package com.epam.rest.dao;

import com.epam.rest.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
        Statement findAllStatement = connection.createStatement();
        ResultSet resultSet = findAllStatement.executeQuery(findAllSql)) {
            while(resultSet.next()) {
                T entity = mapResultSetToEntity(resultSet);
                entities.add(entity);
            }
        } catch(SQLException e) {
            throw new DaoException(e);
        }
        return entities;
    }

    @Override
    public Optional<T> findById(Long id) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement findByIdStatement = connection.prepareStatement(findByIdSql)) {
            findByIdStatement.setLong(1, id);
            ResultSet resultSet = findByIdStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public T save(T entity) {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement saveStatement = connection.prepareStatement(saveSql, Statement.RETURN_GENERATED_KEYS)) {
            mapEntityToSavePreparedStatement(saveStatement, entity);
            saveStatement.executeUpdate();
            ResultSet generatedKeys = saveStatement.getGeneratedKeys();
            generatedKeys.next();
            long generatedKey = generatedKeys.getLong("GENERATED_KEY");
            entity.setId(generatedKey);
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

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

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setLong(1, id);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;

    protected abstract void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, T entity ) throws SQLException;

    protected abstract void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, T entity) throws SQLException;
}
