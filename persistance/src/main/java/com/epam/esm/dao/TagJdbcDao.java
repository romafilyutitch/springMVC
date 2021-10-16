package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Realisation of abstract dao class for tag. Performs sql queries to
 * tag table to perform CRUD operations with tag
 */
@Repository
public class TagJdbcDao extends AbstractDao<Tag> implements TagDao {
    private static final String TABLE_NAME = "tag";
    private static final List<String> COLUMNS = Collections.singletonList("name");
    private static final RowMapper<Tag> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Tag(id, name);
    };

    private static final String FIND_BY_NAME_SQL = "select * from tag where name = ?";

    public TagJdbcDao() {
        super(TABLE_NAME, COLUMNS, MAPPER);
    }

    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Tag entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
    }

    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, Tag entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setLong(2, entity.getId());
    }

    @Override
    public Optional<Tag> findByName(String name) {
        List<Tag> foundTag = template.query(FIND_BY_NAME_SQL, MAPPER, name);
        return foundTag.isEmpty() ? Optional.empty() : Optional.of(foundTag.get(0));
    }
}
