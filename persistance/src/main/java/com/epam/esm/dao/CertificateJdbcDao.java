package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificateBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
public class CertificateJdbcDao extends AbstractDao<Certificate> implements CertificateDao {
    private static final String SAVE_CERTIFICATE_TAG_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private static final String FIND_CERTIFICATE_TAG_LINK_SQL = "select * from certificate_tag where certificate_id = ? and tag_id = ?";
    private static final RowMapper<Certificate> mapper = (rs, rowNum) -> {
        long id = rs.getLong("gift_certificate.id");
        String name = rs.getString("gift_certificate.name");
        String description = rs.getString("gift_certificate.description");
        double price = rs.getDouble("gift_certificate.price");
        int duration = rs.getInt("gift_certificate.duration");
        LocalDateTime createDate = rs.getObject("gift_certificate.create_date", LocalDateTime.class);
        LocalDateTime lastUpdateDate = rs.getObject("gift_certificate.last_update_date", LocalDateTime.class);
        return new Certificate(id, name, description, price, duration, createDate, lastUpdateDate);
    };

    private final FindCertificateBuilder findCertificateBuilder;
    private final TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(FindCertificateBuilder findCertificateBuilder, TagDao tagDao) {
        super("gift_certificate", mapper);
        this.findCertificateBuilder = findCertificateBuilder;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findWithParameters(LinkedHashMap<String, String> findParameters) {
        String findSql = findCertificateBuilder.buildSql(findParameters);
        List<String> findCertificateParametersValues = findCertificateBuilder.getSqlValues(findParameters);
        List<Certificate> certificates = template.query(findSql, mapper, findCertificateParametersValues.toArray());
        return mapTagsToCertificates(certificates);
    }

    @Override
    public List<Certificate> findAll() {
        List<Certificate> certificates = super.findAll();
        certificates.forEach(certificate -> {
            List<Long> certificateTagsId = template.query("select certificate_tag.tag_id from certificate_tag where certificate_tag.certificate_id = ?", (rs, rowNum) -> rs.getLong("id"), certificate.getId());
            certificateTagsId.forEach(tagId -> {
                Optional<Tag> byId = tagDao.findById(tagId);
                Tag tag = byId.orElseThrow(DaoException::new);
                certificate.getTags().add(tag);
            });
        });
        return certificates;
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Optional<Certificate> certificate = super.findById(id);
        certificate.ifPresent(certificate1 -> {
            List<Long> certificateTagsId = template.query("select certificate_tag.tag_id from certificate_tag where certificate_tag.certificate_id = ?", (rs, rowNum) -> rs.getLong("id"), certificate1.getId());
            certificateTagsId.forEach(tagId -> {
                Optional<Tag> byId = tagDao.findById(tagId);
                Tag tag = byId.orElseThrow(DaoException::new);
                certificate1.getTags().add(tag);
            });
        });
        return certificate;
    }

    @Override
    public Certificate save(Certificate entity) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(statementCreator -> {
            PreparedStatement saveStatement = statementCreator.prepareStatement(SAVE_CERTIFICATE_SQL, Statement.RETURN_GENERATED_KEYS);
            saveStatement.setString(1, entity.getName());
            saveStatement.setString(2, entity.getDescription());
            saveStatement.setDouble(3, entity.getPrice());
            saveStatement.setInt(4, entity.getDuration());
            return saveStatement;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        entity.setId(id);
        saveAndLinkTagsWithCertificate(entity);
        Optional<Certificate> foundCertificate = findById(id);
        return foundCertificate.orElseThrow(DaoException::new);
    }

    @Override
    public Certificate update(Certificate entity) {
        template.update(UPDATE_CERTIFICATE_SQL, entity.getName(), entity.getDescription(), entity.getPrice(), entity.getDuration(), entity.getId());
        saveAndLinkTagsWithCertificate(entity);
        Optional<Certificate> foundCertificate = findById(entity.getId());
        return foundCertificate.orElseThrow(DaoException::new);
    }

    @Override
    public void delete(Long id) {
        template.update(DELETE_CERTIFICATE_SQL, id);
    }

    private ArrayList<Certificate> mapTagsToCertificates(List<Certificate> certificates) {
        Map<Long, Certificate> certificateMap = new LinkedHashMap<>();
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
            SqlRowSet sqlRowSet = template.queryForRowSet(FIND_CERTIFICATE_TAG_LINK_SQL, entity.getId(), savedTag.getId());
            if (sqlRowSet.isLast()) {
                template.update(SAVE_CERTIFICATE_TAG_SQL, entity.getId(), savedTag.getId());
            }
            tag.setId(savedTag.getId());
        });
    }
}
