package com.epam.esm.dao;

import com.epam.esm.builder.FindCertificatesSqlBuilder;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

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
    private static final String FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID_AND_TAG_ID_SQL = "select id, certificate_id, tag_id from certificate_tag where certificate_id = ? and tag_id = ?";
    private static final String SAVE_CERTIFICATE_TAG_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private static final String FIND_CERTIFICATE_BY_ORDER_ID_SQL = "select gift_certificate.id, " +
            "gift_certificate.name, " +
            "gift_certificate.description, " +
            "gift_certificate.price, " +
            "gift_certificate.duration, " +
            "gift_certificate.create_date, " +
            "gift_certificate.last_update_date " +
            "from gift_certificate " +
            "left join certificate_order on certificate_order.certificate_id = gift_certificate.id " +
            "where certificate_order.id = ?";

    private final FindCertificatesSqlBuilder findCertificatesSqlBuilder;
    private final TagDao tagDao;

    @Autowired
    public CertificateJdbcDao(FindCertificatesSqlBuilder findCertificatesSqlBuilder, TagDao tagDao) {
        super(TABLE_NAME, COLUMNS, MAPPER);
        this.findCertificatesSqlBuilder = findCertificatesSqlBuilder;
        this.tagDao = tagDao;
    }

    /**
     * Finds all certificates that matches passed parameters such as tag name,
     * part of name, part of description. Also performs sort certificates operation
     * if there is sort parameters in passed map. Can be extended if it needs to add new find parameter later.
     * Uses FindCertificateSqlBuilder that make all work to build right sql query and find parameter values.
     * Certificate tags are linked with certificate with many-to-many relationship so method uses intermediate
     * table that links entities together (certificate_tag) and tag dao to link tags with certificate.
     *
     * @param findParameters find certificates parameters
     * @return Certificates that matches passed find parameters
     */
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

    /**
     * Finds and returns all certificates from database. Don't use find parameters.
     * If it's need to find certificates that matches some parameters use windWithParameters methods.
     * Certificate tag are linked with certificate with many-to-many relationship so method uses intermediate
     * table that links entities together (certificate_tag) and tag dao to link tags with certificate.
     *
     * @return list of all certificates from database
     */
    @Override
    public List<Certificate> findPage(int page) {
        List<Certificate> allCertificates = super.findPage(page);
        allCertificates.forEach(this::addTagsToCertificate);
        return allCertificates;
    }

    /**
     * Finds certificate from database that has passed id.
     * May return empty optional if there is no certificate with passed id in database.
     * Certificate tag  are linked with certificate with many-to-many relationship so method uses intermediate
     * table that links entities together (certificate_tag) and tag dao to link tags with certificate
     *
     * @param id id of entity that need to be found.
     * @return Optional with certificate with passed id if certificate is found or
     * empty optional otherwise
     */
    @Override
    public Optional<Certificate> findById(long id) {
        Optional<Certificate> certificate = super.findById(id);
        certificate.ifPresent(this::addTagsToCertificate);
        return certificate;
    }

    /**
     * Saves passed certificate in database and returns certificate with id
     * generated by database.
     * Certificate tag are linked with certificate with many-to-many relationship so methods uses intermediate
     * tables that links entities together (certificate_tag) and tag dao to save certificate tags and link them together
     *
     * @param entity entity that need to be saved.
     * @return Saved certificate with assigned id generated by database
     */
    @Override
    public Certificate save(Certificate entity) {
        Certificate savedCertificate = super.save(entity);
        savedCertificate.getTags().addAll(entity.getTags());
        saveAndLinkTagsWithCertificate(savedCertificate);
        return findById(savedCertificate.getId()).orElseThrow(DaoException::new);
    }

    /**
     * Updates passed certificate in database and returns updated certificate.
     * Certificate tags are linked with certificates with many-to-many relationship so method uses intermediate
     * table that links entities together(certificate_tag) and tag dao to save unsaved tags and link saved tags with
     * certificate.
     *
     * @param ceritficate entity that need to be updated
     * @return updated certificate
     */
    @Override
    public Certificate update(Certificate ceritficate) {
        Optional<Certificate> optionalCertificate = findById(ceritficate.getId());
        Certificate certificateFromTable = optionalCertificate.orElseThrow(DaoException::new);
        certificateFromTable.setName(ceritficate.getName() == null ? certificateFromTable.getName() : ceritficate.getName());
        certificateFromTable.setDescription(ceritficate.getDescription() == null ? certificateFromTable.getDescription() : ceritficate.getDescription());
        certificateFromTable.setPrice(ceritficate.getPrice() == 0.0 ? certificateFromTable.getPrice() : ceritficate.getPrice());
        certificateFromTable.setDuration(ceritficate.getDuration() == 0 ? certificateFromTable.getDuration() : ceritficate.getDuration());
        certificateFromTable.getTags().addAll(ceritficate.getTags());
        Certificate updatedCertificate = super.update(certificateFromTable);
        updatedCertificate.getTags().addAll(ceritficate.getTags());
        saveAndLinkTagsWithCertificate(updatedCertificate);
        return findById(updatedCertificate.getId()).orElseThrow(DaoException::new);
    }

    @Override
    public Optional<Certificate> findByOrderId(long orderId) {
        List<Certificate> foundCertificates = template.query(FIND_CERTIFICATE_BY_ORDER_ID_SQL, MAPPER, orderId);
        foundCertificates.forEach(this::addTagsToCertificate);
        return foundCertificates.isEmpty() ? Optional.empty() : Optional.of(foundCertificates.get(0));
    }

    /**
     * Sets certificate fields values to PreparedStatement to right save object values in
     * database certificate table
     *
     * @param saveStatement PreparedStatement that need to be set entity values for save
     * @param entity        entity that need to be saved
     * @throws SQLException if exception with database occurs
     */
    @Override
    protected void setSaveValues(PreparedStatement saveStatement, Certificate entity) throws SQLException {
        saveStatement.setString(1, entity.getName());
        saveStatement.setString(2, entity.getDescription());
        saveStatement.setDouble(3, entity.getPrice());
        saveStatement.setInt(4, entity.getDuration());
        saveStatement.setObject(5, entity.getCreateDate());
        saveStatement.setObject(6, entity.getLastUpdateDate());
    }

    /**
     * Sets certificate fields values to PreparedStatement to right update object values
     * in database certificate table
     *
     * @param updateStatement Prepared statement that need to be set entity values for update
     * @param entity          entity that need to be updated
     * @throws SQLException if exception with database occurs
     */
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
        List<Tag> certificateTags = tagDao.findAllCertificateTags(certificate.getId());
        certificate.setTags(certificateTags);
    }

    private void saveAndLinkTagsWithCertificate(Certificate entity) {
        List<Tag> tags = entity.getTags();
        tags.forEach(tag -> {
            Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
            Tag savedTag = optionalTag.orElseGet(() -> tagDao.save(tag));
            SqlRowSet sqlRowSet = template.queryForRowSet(FIND_CERTIFICATE_TAG_BY_CERTIFICATE_ID_AND_TAG_ID_SQL, entity.getId(), savedTag.getId());
            if (sqlRowSet.isLast()) {
                template.update(SAVE_CERTIFICATE_TAG_SQL, entity.getId(), savedTag.getId());
            }
            tag.setId(savedTag.getId());
        });
    }
}
