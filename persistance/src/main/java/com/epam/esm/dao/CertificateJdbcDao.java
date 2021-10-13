package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificateBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Realisation of abstract dao for certificate that use gift_certificate table to
 * perform CRUD operations with database table. Uses SQL queries to perform those operations.
 * Certificates contains list of tags that related to each other as many to many relationship so class
 * also operates certificate_tag table to link tag with certificate and use tag table to save certificate tags
 */
@Repository
public class CertificateJdbcDao implements CertificateDao {
    private static final String FIND_ALL_CERTIFICATES_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id";
    private static final String FIND_CERTIFICATE_BY_ID_SQL = String.format("%s where gift_certificate.id = ?", FIND_ALL_CERTIFICATES_SQL);
    private static final String SAVE_CERTIFICATE_SQL = "insert into gift_certificate (name, description, price, duration) values (?, ?, ?, ?)";
    private static final String UPDATE_CERTIFICATE_SQL = "update gift_certificate set name = ?, description = ?, price = ?, duration = ? where id = ?";
    private static final String DELETE_CERTIFICATE_SQL = "delete from gift_certificate where id = ?";
    private static final String SAVE_CERTIFICATE_TAG_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private static final String FIND_CERTIFICATE_TAG_LINK_SQL = "select * from certificate_tag where certificate_id = ? and tag_id = ?";

    private final JdbcOperations jdbcOperations;
    private final FindCertificateBuilder findCertificateBuilder;
    private final TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(JdbcOperations jdbcOperations, FindCertificateBuilder findCertificateBuilder, TagDao tagDao) {
        this.jdbcOperations = jdbcOperations;
        this.findCertificateBuilder = findCertificateBuilder;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters) {
        String findSql = findCertificateBuilder.buildSql(findParameters);
        List<String> findCertificateParametersValues = findCertificateBuilder.getSqlValues(findParameters);
        List<Certificate> certificates = jdbcOperations.query(findSql, this::mapCertificate, findCertificateParametersValues);
        return mapTagsToCertificates(certificates);
    }

    @Override
    public List<Certificate> findAll() {
        List<Certificate> certificates = jdbcOperations.query(FIND_ALL_CERTIFICATES_SQL, this::mapCertificate);
        return mapTagsToCertificates(certificates);
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        List<Certificate> certificates = jdbcOperations.query(FIND_CERTIFICATE_BY_ID_SQL, this::mapCertificate, id);
        if (certificates.isEmpty()) {
            return Optional.empty();
        }
        ArrayList<Certificate> certificateArrayList = mapTagsToCertificates(certificates);
        return Optional.of(certificateArrayList.get(0));
    }

    @Override
    public Certificate save(Certificate entity) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(statementCreator -> {
            PreparedStatement saveStatement = statementCreator.prepareStatement(SAVE_CERTIFICATE_SQL, Statement.RETURN_GENERATED_KEYS);
            saveStatement.setString(1, entity.getName());
            saveStatement.setString(2, entity.getDescription());
            saveStatement.setDouble(3, entity.getPrice());
            saveStatement.setInt(4, entity.getDuration());
            return saveStatement;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        saveAndLinkTagsWithCertificate(entity);
        Optional<Certificate> foundCertificate = findById(id);
        return foundCertificate.orElseThrow(DaoException::new);
    }

    @Override
    public Certificate update(Certificate entity) {
        jdbcOperations.update(UPDATE_CERTIFICATE_SQL, entity.getName(), entity.getDescription(), entity.getPrice(), entity.getDuration(), entity.getId());
        saveAndLinkTagsWithCertificate(entity);
        Optional<Certificate> foundCertificate = findById(entity.getId());
        return foundCertificate.orElseThrow(DaoException::new);
    }

    @Override
    public void delete(Long id) {
        jdbcOperations.update(DELETE_CERTIFICATE_SQL, id);
    }

    private Certificate mapCertificate(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("gift_certificate.id");
        String name = resultSet.getString("gift_certificate.name");
        String description = resultSet.getString("gift_certificate.description");
        double price = resultSet.getDouble("gift_certificate.price");
        int duration = resultSet.getInt("gift_certificate.duration");
        LocalDateTime createDate = resultSet.getObject("gift_certificate.create_date", LocalDateTime.class);
        LocalDateTime lastUpdateDate = resultSet.getObject("gift_certificate.last_update_date", LocalDateTime.class);
        long tagId = resultSet.getLong("tag.id");
        String tagName = resultSet.getString("tag.name");
        Tag tag = new Tag(tagId, tagName);
        Certificate certificate = new Certificate(id, name, description, price, duration, createDate, lastUpdateDate);
        certificate.getTags().add(tag);
        return certificate;
    }

    private ArrayList<Certificate> mapTagsToCertificates(List<Certificate> certificates) {
        Map<Long, Certificate> certificateMap = new HashMap<>();
        certificates.forEach(certificate -> {
            Certificate savedCertificate = certificateMap.get(certificate.getId());
            if (savedCertificate == null) {
                certificateMap.put(certificate.getId(), certificate);
            } else {
                List<Tag> savedCertificateTags = savedCertificate.getTags();
                savedCertificateTags.addAll(certificate.getTags());
                certificateMap.put(savedCertificate.getId(), savedCertificate);
            }
        });
        return new ArrayList<>(certificateMap.values());
    }

    private void saveAndLinkTagsWithCertificate(Certificate entity) {
        List<Tag> tags = entity.getTags();
        tags.forEach(tag -> {
            Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
            Tag savedTag = optionalTag.orElseGet(() -> tagDao.save(tag));
            SqlRowSet sqlRowSet = jdbcOperations.queryForRowSet(FIND_CERTIFICATE_TAG_LINK_SQL, entity.getId(), savedTag.getId());
            if (sqlRowSet.isLast()) {
                jdbcOperations.update(SAVE_CERTIFICATE_TAG_SQL, entity.getId(), savedTag.getId());
            }
            tag.setId(savedTag.getId());
        });
    }
}
