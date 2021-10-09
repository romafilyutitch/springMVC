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
public class CertificateRestService implements GiftCertificateService {
    private CertificateDao giftCertificateDao;
    private CertificateTagDao certificateTagDao;
    private TagDao tagDao;

    @Autowired
    public CertificateRestService(CertificateDao giftCertificateDao, CertificateTagDao certificateTagDao, TagDao tagDao) {
        this.giftCertificateDao = giftCertificateDao;
        this.certificateTagDao = certificateTagDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<Certificate> findAll() {
        List<Certificate> allCertificates = giftCertificateDao.findAll();
        for (Certificate certificate : allCertificates) {
            addTagsToCertificate(certificate);
        }
        return allCertificates;
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        Optional<Certificate> foundCertificate = giftCertificateDao.findById(id);
        foundCertificate.ifPresent(this::addTagsToCertificate);
        return foundCertificate;
    }

    @Override
    public Optional<Certificate> findByName(String name) {
        Optional<Certificate> foundCertificate = giftCertificateDao.findByName(name);
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
                Optional<Certificate> foundCertificate = giftCertificateDao.findById(certificateTag.getCertificateId());
                giftCertificates.add(foundCertificate.get());
            }
        }
        return giftCertificates;
    }

    @Override
    public Certificate save(Certificate certificate) throws CertificateExistsException {
        Optional<Certificate> foundCertificate = giftCertificateDao.findByName(certificate.getName());
        if (foundCertificate.isPresent()) {
            throw new CertificateExistsException();
        }
        Certificate savedCertificate = giftCertificateDao.save(certificate);
        List<Tag> tags = savedCertificate.getTags();
        for (Tag tag : tags) {
            Optional<Tag> foundTag = tagDao.findByName(tag.getName());
            if (foundTag.isEmpty()) {
                Tag savedTag = tagDao.save(tag);
                CertificateTag certificateTag = new CertificateTag(savedCertificate.getId(), savedTag.getId());
                certificateTagDao.save(certificateTag);
            } else {
                CertificateTag certificateTag = new CertificateTag(savedCertificate.getId(), foundTag.get().getId());
                certificateTagDao.save(certificateTag);
            }
        }
        return savedCertificate;
    }

    @Override
    public Certificate update(Long id, Certificate certificate) {
        return giftCertificateDao.update(certificate);
    }

    @Override
    public void delete(Long id) {
        giftCertificateDao.delete(id);
    }

    private void addTagsToCertificate(Certificate certificate) {
        List<CertificateTag> certificateTagList = certificateTagDao.findByCertificateId(certificate.getId());
        for (CertificateTag certificateTag : certificateTagList) {
            Optional<Tag> tagDaoById = tagDao.findById(certificateTag.getTagId());
            certificate.getTags().add(tagDaoById.get());
        }
    }
}
