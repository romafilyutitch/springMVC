package com.epam.rest.dao;

import com.epam.rest.model.Certificate;
import com.epam.rest.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private static final String FIND_ALL_CERTIFICATES_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id";
    private static final String FIND_CERTIFICATE_BY_ID_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id where gift_certificate.id = ?";
    private static final String FIND_CERTIFICATE_BY_NAME_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id where gift_certificate.name = ?";
    private static final String SAVE_CERTIFICATE_SQL = "insert into gift_certificate (name, description, price, duration) values (?, ?, ?, ?)";
    private static final String UPDATE_CERTIFICATE_SQL = "update gift_certificate set name = ?, description = ?, price = ?, duration = ? where id = ?";
    private static final String DELETE_CERTIFICATE_SQL = "delete from gift_certificate where id = ?";
    public static final String SAVE_TAG_SQL = "insert into tag (name) values (?)";
    public static final String SAVE_CERTIFICATE_TAG_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    public static final String FIND_TAG_BY_NAME_SQL = "select * from tag where name = ?";


    private TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(TagDao tagDao) {
        super(FIND_ALL_CERTIFICATES_SQL, FIND_CERTIFICATE_BY_ID_SQL, SAVE_CERTIFICATE_SQL, UPDATE_CERTIFICATE_SQL, DELETE_CERTIFICATE_SQL);
        this.tagDao = tagDao;
    }

    @Override
    public Certificate save(Certificate entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement saveCertificateStatement = connection.prepareStatement(SAVE_CERTIFICATE_SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement saveTagStatement = connection.prepareStatement(SAVE_TAG_SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement saveCertificateTagStatement = connection.prepareStatement(SAVE_CERTIFICATE_TAG_SQL);
             PreparedStatement findTagByNameStatement = connection.prepareStatement(FIND_TAG_BY_NAME_SQL)) {
            connection.setAutoCommit(false);
            try {
                mapEntityToSavePreparedStatement(saveCertificateStatement, entity);
                saveCertificateStatement.executeUpdate();
                ResultSet certificateKeys = saveCertificateStatement.getGeneratedKeys();
                certificateKeys.next();
                long certificateId = certificateKeys.getLong("GENERATED_KEY");
                entity.setId(certificateId);
                List<Tag> tags = entity.getTags();
                for (Tag tag : tags) {
                    Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
                    long tagId;
                    if (optionalTag.isPresent()) {
                        tagId = optionalTag.get().getId();
                    } else {
                        tagId = tagDao.save(tag).getId();
                    }
                    saveCertificateTagStatement.setLong(1, certificateId);
                    saveCertificateTagStatement.setLong(2, tagId);
                    saveCertificateTagStatement.executeUpdate();
                }
                connection.commit();
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
        ResultSet resultSet = findAllStatement.executeQuery(FIND_ALL_CERTIFICATES_SQL)) {
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
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_CERTIFICATE_BY_ID_SQL)) {
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
        try(Connection connection = dataSource.getConnection();
            PreparedStatement findByIdStatement = connection.prepareStatement(FIND_CERTIFICATE_BY_ID_SQL);
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_CERTIFICATE_SQL);
            PreparedStatement ignored = connection.prepareStatement("select * from tag where name = ?");
            PreparedStatement saveCertificateTagStatement = connection.prepareStatement("insert into certificate_tag (certificate_id, tag_id) values (?, ?)");
            PreparedStatement findCertificateTagStatement = connection.prepareStatement("select * from certificate_tag where certificate_id = ? and tag_id = ?")) {
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
                List<Tag> tags = entity.getTags();
                for (Tag tag : tags) {
                    Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
                    long tagId;
                    if (optionalTag.isPresent()) {
                        tagId = optionalTag.get().getId();
                    } else {
                        tagId = tagDao.save(tag).getId();
                    }
                    tag.setId(tagId);
                    findCertificateTagStatement.setLong(1, entity.getId());
                    findCertificateTagStatement.setLong(2, tagId);
                    ResultSet resultSet1 = findCertificateTagStatement.executeQuery();
                    if (!resultSet1.next()) {
                        saveCertificateTagStatement.setLong(1, entity.getId());
                        saveCertificateTagStatement.setLong(2, tagId);
                        saveCertificateTagStatement.executeUpdate();
                    }
                }
                foundCertificate.setTags(tags);
                return foundCertificate;
            }
            return null;
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

    @Override
    public List<Certificate> findByTagName(String tagName) {
        return null;
    }
}
