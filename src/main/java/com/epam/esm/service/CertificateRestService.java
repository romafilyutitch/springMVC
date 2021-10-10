package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.CertificateTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
        return certificateDao.findAll();
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        return certificateDao.findById(id);
    }

    @Override
    public Optional<Certificate> findByName(String name) {
        return certificateDao.findByName(name);
    }

    @Override
    public List<Certificate> findByTagName(String tagName) {
        return certificateDao.findByTagName(tagName);
    }

    @Override
    public Certificate save(Certificate certificate) throws CertificateExistsException {
        Certificate savedCertificate = certificateDao.save(certificate);
        savedCertificate.setCreateDate(LocalDateTime.now());
        return savedCertificate;
    }

    @Override
    public Certificate update(Long id, Certificate certificate) {
            certificate.setId(id);
            Certificate updatedCertificate = certificateDao.update(certificate);
            updatedCertificate.setLastUpdateDate(LocalDateTime.now());
            return updatedCertificate;
    }

    @Override
    public void delete(Long id) {
        certificateDao.delete(id);
    }

    @Override
    public List<Certificate> searchByName(String name) {
        return certificateDao.searchByName(name);
    }

    @Override
    public List<Certificate> sortByNameThenDate(List<Certificate> certificates, boolean nameAcceding, boolean dateAcceding) {
        Comparator<Certificate> nameComparator = Comparator.comparing(Certificate::getName);
        Comparator<Certificate> dateComparator = Comparator.comparing(Certificate::getCreateDate);
        nameComparator = nameAcceding ? nameComparator : nameComparator.reversed();
        dateComparator = dateAcceding ? dateComparator : dateComparator.reversed();
        Comparator<Certificate> compositeComparator = nameComparator.thenComparing(dateComparator);
        certificates.sort(compositeComparator);
        return certificates;
    }

    @Override
    public Certificate addTags(Long certificateId, List<Tag> tags) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        Certificate certificate = optionalCertificate.get();
        List<Tag> certificateTags = certificate.getTags();
        certificateTags.addAll(tags);
        return certificateDao.update(certificate);
    }
}
