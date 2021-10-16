package com.epam.esm.dao;

import com.epam.esm.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;


/**
 * Dao layer abstract dao class. If it is need to make dao for some
 * entity. You may extend this class to get basic CRUD operations.
 * Uses MySQL database to to save and manipulate entities.
 * @param <T> entity which dao operates
 */
@Component
public abstract class AbstractDao<T extends Entity> implements Dao<T> {
    @Autowired
    private DataSource dataSource;
    @Autowired
    protected JdbcTemplate template;

    private final String tableName;
    private final List<String> columns;
    private final RowMapper<T> rowMapper;

    protected final String findAllSql;
    protected final String findByIdSql;
    protected final String saveSql;
    protected final String updateSql;
    protected final String deleteSql;


    public AbstractDao(String tableName, List<String> columns, RowMapper<T> rowMapper) {
        this.tableName = tableName;
        this.columns = columns;
        this.rowMapper = rowMapper;
        findAllSql = String.format("select id, %s from %s", String.join(",", columns), tableName);
        findByIdSql = String.format("select id, %s from %s where id = ?", String.join(",", columns), tableName);
        StringJoiner saveJoiner = new StringJoiner(",");
        columns.forEach(column -> saveJoiner.add("?"));
        saveSql = String.format("insert into %s (%s) values (%s)", tableName, String.join(",", columns), saveJoiner);
        StringJoiner updateJoiner = new StringJoiner(",");
        columns.forEach(column -> updateJoiner.add(column + "=?"));
        updateSql = String.format("update %s set %s where id = ?", tableName, updateJoiner);
        deleteSql = String.format("delete from %s where id = ?", tableName);
    }

    /**
     * Performs select database request to find all entities
     * @return list of all entities from database table
     */
    @Override
    public List<T> findAll() {
        return template.query(findAllSql, rowMapper);
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
        List<T> query = template.query(findByIdSql, rowMapper, id);
        return query.isEmpty() ? Optional.empty() : Optional.of(query.get(0));
    }

    @Override
    public T save(T entity) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(statementCreator -> {
            PreparedStatement saveStatement = statementCreator.prepareStatement(saveSql, Statement.RETURN_GENERATED_KEYS);
            setSaveValues(saveStatement, entity);
            return saveStatement;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return findById(id).orElseThrow(DaoException::new);
    }

    @Override
    public T update(T entity) {
        template.update(updateSql, ps -> setUpdateValues(ps, entity));
        return entity;
    }

    /**
     * Performs delete database request to delete entity that has passed id
     * @param id id of entity that need to be deleted
     */
    @Override
    public void delete(Long id) {
        template.update(deleteSql, id);
    }

    protected abstract void setSaveValues(PreparedStatement saveStatement, T entity) throws SQLException;

    protected abstract void setUpdateValues(PreparedStatement updateStatement, T entity) throws SQLException;
}
