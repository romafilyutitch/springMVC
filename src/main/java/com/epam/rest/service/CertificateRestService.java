package com.epam.rest.service;

import com.epam.rest.dao.CertificateDao;
import com.epam.rest.dao.CertificateTagDao;
import com.epam.rest.dao.TagDao;
import com.epam.rest.model.Certificate;
import com.epam.rest.model.CertificateTag;
import com.epam.rest.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        for (Certificate certificate : allCertificates) {
            addTagsToCertificate(certificate);
        }
        return allCertificates;
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Optional<Certificate> foundCertificate = certificateDao.findById(id);
        foundCertificate.ifPresent(this::addTagsToCertificate);
        return foundCertificate;
    }

    @Override
    public Optional<Certificate> findByName(String name) {
        Optional<Certificate> foundCertificate = certificateDao.findByName(name);
        foundCertificate.ifPresent(this::addTagsToCertificate);
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
        Optional<Certificate> foundCertificate = certificateDao.findByName(certificate.getName());
        if (foundCertificate.isPresent()) {
            throw new CertificateExistsException();
        }
        Certificate savedCertificate = certificateDao.save(certificate);
        List<Tag> tags = savedCertificate.getTags();
        for (Tag tag : tags) {
            Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
            Tag savedTag = optionalTag.isEmpty() ? tagDao.save(tag) : optionalTag.get();
            CertificateTag certificateTag = new CertificateTag(savedCertificate.getId(), savedTag.getId());
            certificateTagDao.save(certificateTag);
        }
        return findById(savedCertificate.getId()).get();
    }

    @Override
    public Certificate update(Long id, Certificate certificate) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        Certificate foundCertificate = optionalCertificate.get();
        foundCertificate.setName(certificate.getName() == null ? foundCertificate.getName() : certificate.getName());
        foundCertificate.setDescription(certificate.getDescription() == null ? foundCertificate.getDescription() : certificate.getDescription());
        foundCertificate.setPrice(certificate.getPrice() == null ? foundCertificate.getPrice() : certificate.getPrice());
        foundCertificate.setDuration(certificate.getDuration() == null ? foundCertificate.getDuration() : certificate.getDuration());
        List<Tag> tags = certificate.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                Optional<Tag> optionalTag = tagDao.findByName(tag.getName());
                Tag savedTag = optionalTag.isEmpty() ? tagDao.save(tag) : optionalTag.get();
                Optional<CertificateTag> optionalCertificateTag = certificateTagDao.findByCertificateIdAndTagId(foundCertificate.getId(), savedTag.getId());
                if (optionalCertificateTag.isEmpty()) {
                    CertificateTag certificateTag = new CertificateTag(foundCertificate.getId(), savedTag.getId());
                    certificateTagDao.save(certificateTag);
                }
            }
        }
        Certificate updatedCertificate = certificateDao.update(foundCertificate);
        return findById(updatedCertificate.getId()).get();
    }

    @Override
    public void delete(Long id) {
        certificateDao.delete(id);
    }

    private void addTagsToCertificate(Certificate certificate) {
        List<CertificateTag> certificateTagList = certificateTagDao.findByCertificateId(certificate.getId());
        for (CertificateTag certificateTag : certificateTagList) {
            Optional<Tag> tagDaoById = tagDao.findById(certificateTag.getTagId());
            certificate.getTags().add(tagDaoById.get());
        }
    }
}
