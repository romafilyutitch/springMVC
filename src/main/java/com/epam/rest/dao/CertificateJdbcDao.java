package com.epam.rest.dao;

import com.epam.rest.model.Certificate;
import com.epam.rest.model.Tag;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private static final String FIND_ALL_SQL = "select gift_certificate.*, tag.* from gift_certificate left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id left join tag on certificate_tag.tag_id = tag.id";
    private static final String FIND_BY_ID_SQL = "select gift_certificate.*, tag.* from gift_certificate left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id left join tag on certificate_tag.tag_id = tag.id where gift_certificate.id = ?";
    private static final String FIND_BY_NAME_SQL = "select gift_certificate.*, tag.* from gift_certificate left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id left join tag on certificate_tag.tag_id = tag.id where gift_certificate.name = ?";;
    private static final String SAVE_SQL = "insert into gift_certificate (name, description, price, duration) values (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "update gift_certificate set name = ?, description = ?, price = ?, duration = ? where id = ?";
    private static final String DELETE_SQL = "delete from gift_certificate where id = ?";

    public CertificateJdbcDao() {
        super(FIND_ALL_SQL, FIND_BY_ID_SQL, SAVE_SQL, UPDATE_SQL, DELETE_SQL);
    }

    @Override
    public Certificate save(Certificate entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement saveCertificateStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement saveTagStatement = connection.prepareStatement("insert into tag (name) values (?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement saveCertificateTagStatement = connection.prepareStatement("insert into certificate_tag (certificate_id, tag_id) values (?, ?)");
             PreparedStatement findTagByNameStatement = connection.prepareStatement("select * from tag where name = ?")) {
            connection.setAutoCommit(false);
            try {
                saveCertificateStatement.setString(1, entity.getName());
                saveCertificateStatement.setString(2, entity.getDescription());
                saveCertificateStatement.setDouble(3, entity.getPrice());
                saveCertificateStatement.setInt(4, entity.getDuration());
                saveCertificateStatement.executeUpdate();
                ResultSet certificateKeys = saveCertificateStatement.getGeneratedKeys();
                certificateKeys.next();
                long certificateId = certificateKeys.getLong("GENERATED_KEY");
                entity.setId(certificateId);
                List<Tag> tags = entity.getTags();
                for (Tag tag : tags) {
                    findTagByNameStatement.setString(1, tag.getName());
                    ResultSet resultSet = findTagByNameStatement.executeQuery();
                    long tagId;
                    if (resultSet.next()) {
                        tagId = resultSet.getLong("id");
                    } else {
                        saveTagStatement.setString(1, tag.getName());
                        saveTagStatement.executeUpdate();
                        ResultSet tagGeneratedKeys = saveTagStatement.getGeneratedKeys();
                        tagGeneratedKeys.next();
                        tagId = tagGeneratedKeys.getLong("GENERATED_KEY");
                        tag.setId(tagId);
                    }
                    tag.setId(tagId);
                    saveCertificateTagStatement.setLong(1, certificateId);
                    saveCertificateTagStatement.setLong(2, tagId);
                    saveCertificateTagStatement.executeUpdate();
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Certificate> findAll() {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
        Statement findAllStatement = connection.createStatement();
        ResultSet resultSet = findAllStatement.executeQuery(FIND_ALL_SQL)) {
            while (resultSet.next()) {
                Certificate foundCertificate = mapResultSetToEntity(resultSet);
                long tagId = resultSet.getLong("tag.id");
                String tagName = resultSet.getString("tag.name");
                Tag tag = new Tag(tagId, tagName);
                if (tag.getName() == null) {
                    certificateMap.put(foundCertificate.getId(), foundCertificate);
                } else {
                    if (certificateMap.containsKey(foundCertificate.getId())) {
                        Certificate savedCertificate = certificateMap.get(foundCertificate.getId());
                        savedCertificate.getTags().add(tag);
                        certificateMap.put(savedCertificate.getId(), savedCertificate);
                    } else {
                        foundCertificate.getTags().add(tag);
                        certificateMap.put(foundCertificate.getId(), foundCertificate);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return new ArrayList<>(certificateMap.values());
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            findByIdStatement.setLong(1, id);
            ResultSet resultSet = findByIdStatement.executeQuery();
            while (resultSet.next()) {
                Certificate foundCertificate = mapResultSetToEntity(resultSet);
                long tagId = resultSet.getLong("tag.id");
                String tagName = resultSet.getString("tag.name");
                Tag tag = new Tag(tagId, tagName);
                if (certificateMap.containsKey(foundCertificate.getId())) {
                    Certificate savedCertificate = certificateMap.get(foundCertificate.getId());
                    savedCertificate.getTags().add(tag);
                    certificateMap.put(savedCertificate.getId(), savedCertificate);
                } else {
                    foundCertificate.getTags().add(tag);
                    certificateMap.put(foundCertificate.getId(), foundCertificate);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(certificateMap.get(id));
    }

    @Override
    public Certificate update(Certificate entity) {
        //@todo fix this bug
        try(Connection connection = dataSource.getConnection();
        PreparedStatement findByIdStatement = connection.prepareStatement(FIND_BY_ID_SQL);
        PreparedStatement updateStatement = connection.prepareStatement(UPDATE_SQL)) {
            findByIdStatement.setLong(1, entity.getId());
            ResultSet resultSet = findByIdStatement.executeQuery();
            if (resultSet.next()) {
                Certificate foundCertificate = mapResultSetToEntity(resultSet);
                updateStatement.setString(1, entity.getName() == null ? foundCertificate.getDescription() : entity.getName());
                updateStatement.setString(2, entity.getDescription() == null ? foundCertificate.getDescription() : entity.getDescription());
                updateStatement.setDouble(3, entity.getPrice() == null ? foundCertificate.getPrice() : entity.getPrice());
                updateStatement.setInt(4, entity.getDuration() == null ? foundCertificate.getDuration() : entity.getDuration());
                updateStatement.setLong(5, entity.getId());
                updateStatement.executeUpdate();
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    protected Certificate mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        double price = resultSet.getDouble("price");
        int duration = resultSet.getInt("duration");
        LocalDateTime createDate = resultSet.getObject("create_date", LocalDateTime.class);
        LocalDateTime lastUpdateDate = resultSet.getObject("last_update_date", LocalDateTime.class);
        return new Certificate(id, name, description, price, duration, createDate, lastUpdateDate);
    }

    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, Certificate entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getDescription());
        saveStatement.setDouble(3, entity.getPrice());
        saveStatement.setInt(4, entity.getDuration());
    }

    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, Certificate entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getDescription());
        updateStatement.setDouble(3, entity.getPrice());
        updateStatement.setInt(4, entity.getDuration());
        updateStatement.setLong(5, entity.getId());
    }

    @Override
    public Optional<Certificate> findByName(String name) {
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

    @Override
    public List<Certificate> findByTagName(String tagName) {
        return null;
    }
}
