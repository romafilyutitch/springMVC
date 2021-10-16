package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificatesSqlBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private static final String TABLE_NAME = "gift_certificate";
    private static final List<String> COLUMNS = Arrays.asList("name", "description", "price", "duration", "create_date", "last_update_date");
    private static final RowMapper<Certificate> MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("gift_certificate.id");
        String name = rs.getString("gift_certificate.name");
        String description = rs.getString("gift_certificate.description");
        double price = rs.getDouble("gift_certificate.price");
        int duration = rs.getInt("gift_certificate.duration");
        LocalDateTime createDate = rs.getObject("gift_certificate.create_date", LocalDateTime.class);
        LocalDateTime lastUpdateDate = rs.getObject("gift_certificate.last_update_date", LocalDateTime.class);
        return new Certificate(id, name, description, price, duration, createDate, lastUpdateDate);
    };
    private static final String FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID = "select id, certificate_id, tag_id from certificate_tag where certificate_tag.certificate_id = ?";
    private static final String FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID_AND_TAG_ID = "select id, certificate_id, tag_id from certificate_tag where certificate_id = ? and tag_id = ?";
    private static final String SAVE_CERTIFICATE_TAG = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private final FindCertificatesSqlBuilder findCertificatesSqlBuilder;
    private final TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(FindCertificatesSqlBuilder findCertificatesSqlBuilder, TagDao tagDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.findCertificatesSqlBuilder = findCertificatesSqlBuilder;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters) {
        String findSql = findCertificatesSqlBuilder.buildSql(findParameters);
        List<String> findCertificateParametersValues = findCertificatesSqlBuilder.getSqlValues(findParameters);
        List<Certificate> allCertificates = template.query(findSql, MAPPER, findCertificateParametersValues.toArray());
        allCertificates.forEach(this::addTagsToCertificate);
        LinkedHashMap<Long, Certificate> certificatesMap = new LinkedHashMap<>();
        allCertificates.forEach(certificate -> certificatesMap.put(certificate.getId(), certificate));
        return new ArrayList<>(certificatesMap.values());
    }

    @Override
    public List<Certificate> findAll() {
        List<Certificate> allCertificates = super.findAll();
        allCertificates.forEach(this::addTagsToCertificate);
        return allCertificates;
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Optional<Certificate> certificate = super.findById(id);
        certificate.ifPresent(this::addTagsToCertificate);
        return certificate;
    }

    @Override
    public Certificate save(Certificate entity) {
        Certificate savedCertificate = super.save(entity);
        savedCertificate.getTags().addAll(entity.getTags());
        saveAndLinkTagsWithCertificate(savedCertificate);
        return findById(savedCertificate.getId()).orElseThrow(DaoException::new);
    }

    @Override
    public Certificate update(Certificate entity) {
        Certificate updatedCertificate = super.update(entity);
        updatedCertificate.getTags().addAll(entity.getTags());
        saveAndLinkTagsWithCertificate(updatedCertificate);
        return findById(updatedCertificate.getId()).orElseThrow(DaoException::new);
    }

    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Certificate entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getDescription());
        saveStatement.setDouble(3, entity.getPrice());
        saveStatement.setInt(4, entity.getDuration());
        saveStatement.setObject(5, entity.getCreateDate());
        saveStatement.setObject(6, entity.getLastUpdateDate());
    }

    @Override
    protected void setUpdateValues(PreparedStatement updateStatement, Certificate entity) throws SQLException {
        updateStatement.setString(1, entity.getName());
        updateStatement.setString(2, entity.getDescription());
        updateStatement.setDouble(3, entity.getPrice());
        updateStatement.setInt(4, entity.getDuration());
        updateStatement.setObject(5, entity.getCreateDate());
        updateStatement.setObject(6, entity.getLastUpdateDate());
        updateStatement.setLong(7, entity.getId());
    }

    private void addTagsToCertificate(Certificate certificate) {
        List<Long> certificateTagsIds = template.query(FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID, (rs, rowNum) -> rs.getLong("tag_id"), certificate.getId());
        certificateTagsIds.forEach(tagId -> certificate.getTags().add(tagDao.findById(tagId).orElseThrow(DaoException::new)));
    }

    private void saveAndLinkTagsWithCertificate(Certificate entity) {
        List<Tag> tags = entity.getTags();
        tags.forEach(tag -> {
            Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
            Tag savedTag = optionalTag.orElseGet(() -> tagDao.save(tag));
            SqlRowSet sqlRowSet = template.queryForRowSet(FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID_AND_TAG_ID, entity.getId(), savedTag.getId());
            if (sqlRowSet.isLast()) {
                template.update(SAVE_CERTIFICATE_TAG, entity.getId(), savedTag.getId());
            }
            tag.setId(savedTag.getId());
        });
    }
}
