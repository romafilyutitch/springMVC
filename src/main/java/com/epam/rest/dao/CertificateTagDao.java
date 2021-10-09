package com.epam.rest.dao;

import com.epam.rest.model.CertificateTag;

import java.util.List;
import java.util.Optional;

public interface CertificateTagDao extends Dao<CertificateTag> {

    List<CertificateTag> findByCertificateId(Long id);

    List<CertificateTag> findByTagId(Long id);

    Optional<CertificateTag> findByCertificateIdAndTagId(Long certificateId, Long tagId);
}
