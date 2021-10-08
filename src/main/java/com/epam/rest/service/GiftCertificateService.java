package com.epam.rest.service;

import com.epam.rest.dao.GiftCertificateJdbcDao;
import com.epam.rest.model.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GiftCertificateService {
    private GiftCertificateJdbcDao dao;

    @Autowired
    public GiftCertificateService(GiftCertificateJdbcDao dao) {
        this.dao = dao;
    }

    public List<GiftCertificate> findAll() {
        return dao.findAll();
    }
}
