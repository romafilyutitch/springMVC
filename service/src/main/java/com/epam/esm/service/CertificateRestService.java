package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.CertificateTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class CertificateRestService implements CertificateService {
    private CertificateDao certificateDao;
    private TagDao tagDao;

    @Autowired
    public CertificateRestService(CertificateDao certificateDao, TagDao tagDao) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findAll() {
        return certificateDao.findAll();
    }

    @Override
    public Certificate findById(Long id) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            return optionalCertificate.get();
        } else {
            throw new CertificateNotFoundException(id);
        }
    }

    @Override
    public List<Certificate> findByTagName(String tagName) {
        return certificateDao.findByTagName(tagName);
    }

    @Override
    public Certificate save(Certificate certificate) throws CertificateExistsException {
        Optional<Certificate> optionalCertificate = certificateDao.findByName(certificate.getName());
        if (optionalCertificate.isPresent()) {
            throw new CertificateExistsException(optionalCertificate.get().getId());
        }
        Certificate savedCertificate = certificateDao.save(certificate);
        savedCertificate.setCreateDate(LocalDateTime.now());
        return savedCertificate;
    }

    @Override
    public Certificate update(Long id, Certificate certificate) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            certificate.setId(id);
            Certificate updatedCertificate = certificateDao.update(certificate);
            updatedCertificate.setLastUpdateDate(LocalDateTime.now());
            return updatedCertificate;
        } else {
            throw new CertificateNotFoundException(id);
        }
    }

    @Override
    public void delete(Long id) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        if (optionalCertificate.isPresent()) {
            certificateDao.delete(id);
        } else {
            throw new CertificateNotFoundException(id);
        }
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
    public Certificate addTags(Long certificateId, List<Tag> tags) throws CertificateNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            List<Tag> certificateTags = certificate.getTags();
            certificateTags.addAll(tags);
            return certificateDao.update(certificate);
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }

    @Override
    public void deleteCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Optional<Tag> optionalTag = tagDao.findById(tagId);
            if(optionalTag.isPresent()) {
                tagDao.delete(tagId);
            } else {
                throw new TagNotFoundException(tagId);
            }
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }

    @Override
    public Tag findCertificateTag(Long certificateId, Long tagId) throws CertificateNotFoundException, TagNotFoundException {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        if (optionalCertificate.isPresent()) {
            Optional<Tag> optionalTag = tagDao.findById(tagId);
            if (optionalTag.isPresent()) {
                return optionalTag.get();
            } else {
                throw new TagNotFoundException(tagId);
            }
        } else {
            throw new CertificateNotFoundException(certificateId);
        }
    }
}
