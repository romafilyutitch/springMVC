package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserJdbcDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService {
    private final UserJdbcDao userJdbcDao;
    private final OrderDao orderDao;
    private final CertificateDao certificateDao;

    @Autowired
    public UserRestService(UserJdbcDao userJdbcDao, OrderDao orderDao, CertificateDao certificateDao) {
        this.userJdbcDao = userJdbcDao;
        this.orderDao = orderDao;
        this.certificateDao = certificateDao;
    }

    @Override
    public List<User> findAll() {
        return userJdbcDao.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJdbcDao.findById(id);
    }

    @Override
    public User makeOrder(Long certificateId) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        Certificate certificate = optionalCertificate.get();
        Double certificatePrice = certificate.getPrice();
        Order order = new Order(certificatePrice, certificate);
        Order savedOrder = orderDao.save(order);
    }
}
