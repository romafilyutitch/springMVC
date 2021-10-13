package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Repository
public class TagJdbcDao implements TagDao {
    private static final String FIND_ALL_SQL = "select * from tag";
    private static final String FIND_BY_ID_SQL = "select * from tag where id = ?";
    private static final String FIND_BY_NAME_SQL = "select * from tag where name = ?";
    private static final String SAVE_SQL = "insert into tag (name) values (?)";
    private static final String UPDATE_SQL = "update tag set name = ? where id = ?";
    private static final String DELETE_SQL = "delete from tag where id = ?";

    private final JdbcOperations jdbcOperations;

    @Autowired
    public TagJdbcDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Tag> findAll() {
        return jdbcOperations.query(FIND_ALL_SQL, this::mapTag);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        try {
            Tag tag = jdbcOperations.queryForObject(FIND_BY_ID_SQL, this::mapTag, id);
            return Optional.ofNullable(tag);
        } catch (IncorrectResultSizeDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Tag save(Tag entity) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(statementCreator -> {
            PreparedStatement saveStatement = statementCreator.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            saveStatement.setString(1, entity.getName());
            return saveStatement;
        }, generatedKeyHolder);
        Long id = generatedKeyHolder.getKeyAs(Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        jdbcOperations.update(UPDATE_SQL, entity.getName(), entity.getId());
        return entity;
    }

    @Override
    public void delete(Long id) {
        jdbcOperations.update(DELETE_SQL, id);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        try {
            Tag tag = jdbcOperations.queryForObject(FIND_BY_NAME_SQL, this::mapTag, name);
            return Optional.ofNullable(tag);
        } catch (IncorrectResultSizeDataAccessException exception) {
            return Optional.empty();
        }
    }

    private Tag mapTag(ResultSet rs, int row) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Tag(id, name);
    }
}
