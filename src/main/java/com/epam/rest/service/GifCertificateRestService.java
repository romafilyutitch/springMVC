package com.epam.rest.service;

import com.epam.rest.dao.CertificateTagDao;
import com.epam.rest.dao.GiftCertificateDao;
import com.epam.rest.dao.TagDao;
import com.epam.rest.model.CertificateTag;
import com.epam.rest.model.GiftCertificate;
import com.epam.rest.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GifCertificateRestService implements GiftCertificateService {
    private GiftCertificateDao giftCertificateDao;
    private CertificateTagDao certificateTagDao;
    private TagDao tagDao;

    @Autowired
    public GifCertificateRestService(GiftCertificateDao giftCertificateDao, CertificateTagDao certificateTagDao, TagDao tagDao) {
        this.giftCertificateDao = giftCertificateDao;
        this.certificateTagDao = certificateTagDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<GiftCertificate> findAll() {
        List<GiftCertificate> allCertificates = giftCertificateDao.findAll();
        for (GiftCertificate certificate : allCertificates) {
            addTagsToCertificate(certificate);
        }
        return allCertificates;
    }

    @Override
    public Optional<GiftCertificate> findById(Long id) {
        Optional<GiftCertificate> foundCertificate = giftCertificateDao.findById(id);
        foundCertificate.ifPresent(this::addTagsToCertificate);
        return foundCertificate;
    }

    @Override
    public Optional<GiftCertificate> findByName(String name) {
        Optional<GiftCertificate> foundCertificate = giftCertificateDao.findByName(name);
        foundCertificate.ifPresent(this::addTagsToCertificate);
        return foundCertificate;
    }

    @Override
    public List<GiftCertificate> findByTagsNames(List<String> tagNames) {
        List<GiftCertificate> giftCertificates = new ArrayList<>();
        for (String tagName : tagNames) {
            Optional<Tag> foundTag = tagDao.findByName(tagName);
            List<CertificateTag> foundCertificateTagList = certificateTagDao.findByTagId(foundTag.get().getId());
            for (CertificateTag certificateTag : foundCertificateTagList) {
                Optional<GiftCertificate> foundCertificate = giftCertificateDao.findById(certificateTag.getCertificateId());
                giftCertificates.add(foundCertificate.get());
            }
        }
        return giftCertificates;
    }

    @Override
    public GiftCertificate save(GiftCertificate certificate) throws CertificateExistsException {
        Optional<GiftCertificate> foundCertificate = giftCertificateDao.findByName(certificate.getName());
        if (foundCertificate.isPresent()) {
            throw new CertificateExistsException();
        }
        GiftCertificate savedCertificate = giftCertificateDao.save(certificate);
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
    public GiftCertificate update(Long id, GiftCertificate certificate) {
        return giftCertificateDao.update(certificate);
    }

    @Override
    public void delete(Long id) {
        giftCertificateDao.delete(id);
    }

    private void addTagsToCertificate(GiftCertificate certificate) {
        List<CertificateTag> certificateTagList = certificateTagDao.findByCertificateId(certificate.getId());
        for (CertificateTag certificateTag : certificateTagList) {
            Optional<Tag> tagDaoById = tagDao.findById(certificateTag.getTagId());
            certificate.getTags().add(tagDaoById.get());
        }
    }
}
