package com.epam.rest.dao;

import com.epam.rest.model.CertificateTag;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CertificateTagJdbcDao extends AbstractDao<CertificateTag> implements CertificateTagDao {
    private static final String FIND_ALL_SQL = "select * from certificate_tag";
    private static final String FIND_BY_ID_SQL = "select * from certificate_tag where id = ?";
    private static final String SAVE_SQL = "insert into certificate_tag (certificate_id, tag_id) values (?, ?)";
    private static final String UPDATE_SQL = "update certificate_tag set certificate_id = ?, tag_id = ? where id = ?";
    private static final String DELETE_SQL = "delete from certificate_tag where id = ?";
    public static final String FIND_BY_CERTIFICATE_ID_SQL = "select * from certificate_tag where certificate_id = ?";

    public CertificateTagJdbcDao() {
        super(FIND_ALL_SQL, FIND_BY_ID_SQL, SAVE_SQL, UPDATE_SQL, DELETE_SQL);
    }

    @Override
    protected CertificateTag mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long certificateId = resultSet.getLong("certificate_id");
        long tagId = resultSet.getLong("tag_id");
        return new CertificateTag(id, certificateId, tagId);
    }

    @Override
    protected void mapEntityToSavePreparedStatement(PreparedStatement saveStatement, CertificateTag entity) throws SQLException {
        saveStatement.setLong(1, entity.getCertificateId());
        saveStatement.setLong(2, entity.getTagId());
    }

    @Override
    protected void mapEntityToUpdatePreparedStatement(PreparedStatement updateStatement, CertificateTag entity) throws SQLException {
        updateStatement.setLong(1, entity.getCertificateId());
        updateStatement.setLong(2, entity.getTagId());
        updateStatement.setLong(3, entity.getId());
    }

    @Override
    public List<CertificateTag> findByCertificateId(Long id) {
        List<CertificateTag> certificateTags = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByCertificateIdStatement = connection.prepareStatement(FIND_BY_CERTIFICATE_ID_SQL)) {
            findByCertificateIdStatement.setLong(1, id);
            ResultSet resultSet = findByCertificateIdStatement.executeQuery();
            while (resultSet.next()) {
                CertificateTag certificateTag = mapResultSetToEntity(resultSet);
                certificateTags.add(certificateTag);
            }
            return certificateTags;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<CertificateTag> findByTagId(Long id) {
        List<CertificateTag> certificateTags = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByTagIdStatement = connection.prepareStatement("select * from certificate_tag where tag_id = ?")) {
            findByTagIdStatement.setLong(1, id);
            ResultSet resultSet = findByTagIdStatement.executeQuery();
            while (resultSet.next()) {
                CertificateTag certificateTag = mapResultSetToEntity(resultSet);
                certificateTags.add(certificateTag);
            }
            return certificateTags;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
