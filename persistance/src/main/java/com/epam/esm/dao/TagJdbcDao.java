package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private static final String FIND_BY_NAME_SQL = "select id, name from tag where name = ?";
    private static final String FIND_TAGS_BY_USER_ID = "select tag.id, tag.name from tag left join certificate_tag on certificate_tag.tag_id = tag.id where certificate_tag.certificate_id = ?";

    public TagJdbcDao() {
        super(TABLE_NAME, COLUMNS, MAPPER);
    }

    /**
     * Sets Tag entity fields values to PreparedStatement to save entity in database.
     *
     * @param saveStatement PreparedStatement that need to be set entity values for save
     * @param entity        entity that need to be saved
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Tag entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
    }

    /**
     * Sets Tag entity values to PreparedStatement to update entity in database.
     *
     * @param updateStatement Prepared statement that need to be set entity values for update
     * @param entity          entity that need to be updated
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, Tag entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setLong(2, entity.getId());
    }

    /**
     * Finds Tag in database by passed name. May return empty
     * Optional if there is no tag with passed name
     *
     * @param name of tag that need to be found
     * @return Optional with Tag if there is Tag with passed name in database or
     * empry Optional otherwise
     */
    @Override
    public Optional<Tag> findByName(String name) {
        List<Tag> foundTag = template.query(FIND_BY_NAME_SQL, MAPPER, name);
        return foundTag.isEmpty() ? Optional.empty() : Optional.of(foundTag.get(0));
    }

    @Override
    public List<Tag> findByCertificateId(Long userId) {
        return template.query(FIND_TAGS_BY_USER_ID, MAPPER, userId);
    }
}
