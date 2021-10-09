package com.epam.rest.service;

import com.epam.rest.dao.CertificateDao;
import com.epam.rest.dao.CertificateTagDao;
import com.epam.rest.dao.TagDao;
import com.epam.rest.model.Certificate;
import com.epam.rest.model.CertificateTag;
import com.epam.rest.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CertificateRestService implements CertificateService {
    private CertificateDao certificateDao;
    private CertificateTagDao certificateTagDao;
    private TagDao tagDao;

    @Autowired
    public CertificateRestService(CertificateDao certificateDao, CertificateTagDao certificateTagDao, TagDao tagDao) {
        this.certificateDao = certificateDao;
        this.certificateTagDao = certificateTagDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findAll() {
        List<Certificate> allCertificates = certificateDao.findAll();
        return allCertificates;
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Optional<Certificate> foundCertificate = certificateDao.findById(id);
        return foundCertificate;
    }

    @Override
    public Optional<Certificate> findByName(String name) {
        Optional<Certificate> foundCertificate = certificateDao.findByName(name);
        return foundCertificate;
    }

    @Override
    public List<Certificate> findByTagsNames(List<String> tagNames) {
        List<Certificate> giftCertificates = new ArrayList<>();
        for (String tagName : tagNames) {
            Optional<Tag> foundTag = tagDao.findByName(tagName);
            List<CertificateTag> foundCertificateTagList = certificateTagDao.findByTagId(foundTag.get().getId());
            for (CertificateTag certificateTag : foundCertificateTagList) {
                Optional<Certificate> foundCertificate = certificateDao.findById(certificateTag.getCertificateId());
                giftCertificates.add(foundCertificate.get());
            }
        }
        return giftCertificates;
    }

    @Override
    public Certificate save(Certificate certificate) throws CertificateExistsException {
        Certificate savedCertificate = certificateDao.save(certificate);
        savedCertificate.setCreateDate(LocalDateTime.now());
        return savedCertificate;
    }

    @Override
    public Certificate update(Long id, Certificate certificate) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            certificate.setId(id);
            Certificate updatedCertificate = certificateDao.update(certificate);
            updatedCertificate.setLastUpdateDate(LocalDateTime.now());
            return updatedCertificate;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        certificateDao.delete(id);
    }

}
