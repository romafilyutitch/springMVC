package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Realisation of abstract dao for certificate that use gift_certificate table to
 * perform CRUD operations with database table. Uses SQL queries to perform those operations.
 * Certificates contains list of tags that related to each other as many to many relationship so class
 * also operates certificate_tag table to link tag with certificate and use tag table to save certificate tags
 */
@Component
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private static final String FIND_ALL_CERTIFICATES_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id";
    private static final String FIND_ALL_CERTIFICATES_BY_TAG_NAME = String.format("%s where tag.name = ?", FIND_ALL_CERTIFICATES_SQL);
    private static final String FIND_CERTIFICATE_BY_ID_SQL = String.format("%s where gift_certificate.id = ?", FIND_ALL_CERTIFICATES_SQL);
    private static final String FIND_CERTIFICATE_BY_NAME_SQL = String.format("%s where gift_certificate.name = ?", FIND_ALL_CERTIFICATES_SQL);
    private static final String SEARCH_CERTIFICATE_BY_NAME = String.format("%s where gift_certificate.name like ?", FIND_ALL_CERTIFICATES_SQL);
    private static final String SAVE_CERTIFICATE_SQL = "insert into gift_certificate (name, description, price, duration) values (?, ?, ?, ?)";
    private static final String UPDATE_CERTIFICATE_SQL = "update gift_certificate set name = ?, description = ?, price = ?, duration = ? where id = ?";
    private static final String DELETE_CERTIFICATE_SQL = "delete from gift_certificate where id = ?";
    private static final String SAVE_CERTIFICATE_TAG_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private static final String FIND_CERTIFICATE_TAG_LINK_SQL = "select * from certificate_tag where certificate_id = ? and tag_id = ?";

    private final TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(TagDao tagDao) {
        super(FIND_ALL_CERTIFICATES_SQL, FIND_CERTIFICATE_BY_ID_SQL, SAVE_CERTIFICATE_SQL, UPDATE_CERTIFICATE_SQL, DELETE_CERTIFICATE_SQL);
        this.tagDao = tagDao;
    }

    /**
     * Performs insert query to database to save certificate. Also
     * saves certificate in tag table and insert row to link tags with saved
     * certificate by adding row in certificate_tag table
     * @param entity entity that need to be saved
     * @return saved certificate with assigned id
     */
    @Override
    public Certificate save(Certificate entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement saveCertificateStatement = connection.prepareStatement(SAVE_CERTIFICATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            try {
                mapEntityToSavePreparedStatement(saveCertificateStatement, entity);
                saveCertificateStatement.executeUpdate();
                ResultSet certificateKeys = saveCertificateStatement.getGeneratedKeys();
                certificateKeys.next();
                long certificateId = certificateKeys.getLong(1);
                entity.setId(certificateId);
                List<Tag> tags = entity.getTags();
                saveCertificateTags(entity, connection, tags);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Finds all certificates from database and returns. Also finds certificate
     * tags that certificate tag by finding them in tag table
     * @return List of all certificates
     */
    @Override
    public List<Certificate> findAll() {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(FIND_ALL_CERTIFICATES_SQL)) {
            addTagsToFoundCertificate(certificateMap, resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return new ArrayList<>(certificateMap.values());
    }

    /**
     * Finds certificate that has passed id and its tags
     * @param id id of entity that need to be found
     * @return optional certificate if there is certificate with passed id
     * or empty optional if there is not certificate with passed id
     */
    @Override
    public Optional<Certificate> findById(Long id) {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_CERTIFICATE_BY_ID_SQL)) {
            findByIdStatement.setLong(1, id);
            ResultSet resultSet = findByIdStatement.executeQuery();
            addTagsToFoundCertificate(certificateMap, resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(certificateMap.get(id));
    }

    /**
     * Performs updated operations to updated entity. Also saves tags if its new tags and links them together.
     * If there is tag, then links lik with entity.
     * @param entity entity that need to be updated
     * @return updated entity
     */
    @Override
    public Certificate update(Certificate entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_CERTIFICATE_BY_ID_SQL);
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_CERTIFICATE_SQL);) {
            findByIdStatement.setLong(1, entity.getId());
            ResultSet resultSet = findByIdStatement.executeQuery();
            resultSet.next();
            Certificate foundCertificate = mapResultSetToEntity(resultSet);
            setUpdateOnlyPassedValues(entity, updateStatement, foundCertificate);
            updateStatement.executeUpdate();
            List<Tag> tags = entity.getTags();
            saveCertificateTags(entity, connection, tags);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        Optional<Certificate> optionalCertificate = findById(entity.getId());
        return optionalCertificate.orElseThrow(DaoException::new);
    }

    /**
     * Uses ResultSet get methods to map ResultSet to Certificate
     * @param resultSet from which need get values and map entity
     * @return Certificate from ResultSet
     * @throws SQLException if exception with database operations occurs
     */
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

    /**
     * Uses ResultSet set methods to set into save prepared statement
     * Certificate's properties
     * @param saveStatement prepared statement that accept entity's properties
     * @param entity entity which properties need to map to save statement
     * @throws SQLException if exception with database operations occurs
     */
    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, Certificate entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getDescription());
        saveStatement.setDouble(3, entity.getPrice());
        saveStatement.setInt(4, entity.getDuration());
    }

    /**
     * Uses ResultSet set methods to set into updated prepared statement
     * Certificate's properties
     * @param updateStatement prepared statement that accept entity's properties
     * @param entity entity witch properties need to map to updated statement
     * @throws SQLException if exception with database operations occurs
     */
    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, Certificate entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getDescription());
        updateStatement.setDouble(3, entity.getPrice());
        updateStatement.setInt(4, entity.getDuration());
        updateStatement.setLong(5, entity.getId());
    }

    /**
     * Uses select from certificate database statement to find
     * certificate that has passed name
     * @param name of certificate that need to be found
     * @return optional certificate if there is certificate with passed name
     * or empty optional otherwise
     */
    @Override
    public Optional<Certificate> findByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByNameStatement = connection.prepareStatement(FIND_CERTIFICATE_BY_NAME_SQL)) {
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

    /**
     * Uses select from certificate table to find certificates
     * that have tag with passed name.
     * @param tagName name of tag that need to be found
     * @return list of certificates that have tag with passed tag name
     */
    @Override
    public List<Certificate> findByTagName(String tagName) {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findCertificatesByTagNameStatement = connection.prepareStatement(FIND_ALL_CERTIFICATES_BY_TAG_NAME)) {
            findCertificatesByTagNameStatement.setString(1, tagName);
            ResultSet resultSet = findCertificatesByTagNameStatement.executeQuery();
            while(resultSet.next()) {
                Certificate certificate = mapResultSetToEntity(resultSet);
                certificateMap.put(certificate.getId(), certificate);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        Set<Long> ids = certificateMap.keySet();
        List<Certificate> certificates = new ArrayList<>();
        for(Long id : ids) {
            certificates.add(findById(id).orElseThrow(DaoException::new));
        }
        return certificates;
    }

    /**
     * Performs select like statement to find certificate by part of name.
     * @param name part of certificate name that need to be found.
     * @return certificate that have passed name as part of ists name
     */
    @Override
    public List<Certificate> searchByName(String name) {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findCertificatesByTagNameStatement = connection.prepareStatement(SEARCH_CERTIFICATE_BY_NAME)) {
            findCertificatesByTagNameStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = findCertificatesByTagNameStatement.executeQuery();
            addTagsToFoundCertificate(certificateMap, resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return new ArrayList<>(certificateMap.values());
    }

    private void setUpdateOnlyPassedValues(Certificate entity, PreparedStatement updateStatement, Certificate foundCertificate) throws SQLException {
        updateStatement.setString(1, entity.getName() == null ? foundCertificate.getDescription() : entity.getName());
        updateStatement.setString(2, entity.getDescription() == null ? foundCertificate.getDescription() : entity.getDescription());
        updateStatement.setDouble(3, entity.getPrice() == null ? foundCertificate.getPrice() : entity.getPrice());
        updateStatement.setInt(4, entity.getDuration() == null ? foundCertificate.getDuration() : entity.getDuration());
        updateStatement.setLong(5, entity.getId());
    }

    private void saveCertificateTags(Certificate entity, Connection connection, List<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
            long savedTagId = optionalTag.isPresent() ? optionalTag.get().getId() : tagDao.save(tag).getId();
            tag.setId(savedTagId);
            boolean isTagLinked = isTagLiked(connection, entity, tag);
            if (!isTagLinked) {
                linkTagWithCertificate(connection, entity.getId(), tag);
            }
        }
    }

    private boolean isTagLiked(Connection connection, Certificate entity, Tag savedTag) throws SQLException {
        try (PreparedStatement findCertificateTagStatement = connection.prepareStatement(FIND_CERTIFICATE_TAG_LINK_SQL)) {
            findCertificateTagStatement.setLong(1, entity.getId());
            findCertificateTagStatement.setLong(2, savedTag.getId());
            ResultSet resultSet1 = findCertificateTagStatement.executeQuery();
            return resultSet1.next();
        }
    }

    private void linkTagWithCertificate(Connection connection, long certificateId, Tag savedTag) throws SQLException {
        try (PreparedStatement saveCertificateTagStatement = connection.prepareStatement(SAVE_CERTIFICATE_TAG_SQL)) {
            saveCertificateTagStatement.setLong(1, certificateId);
            saveCertificateTagStatement.setLong(2, savedTag.getId());
            saveCertificateTagStatement.executeUpdate();
        }
    }

    private void addTagsToFoundCertificate(Map<Long, Certificate> certificateMap, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Certificate foundCertificate = mapResultSetToEntity(resultSet);
            String tagName = resultSet.getString("tag.name");
            Optional<Tag> optionalTag = tagDao.findByName(tagName);
            certificateMap.putIfAbsent(foundCertificate.getId(), foundCertificate);
            Certificate savedCertificate = certificateMap.get(foundCertificate.getId());
            optionalTag.ifPresent(tag -> savedCertificate.getTags().add(tag));
        }
    }
}
