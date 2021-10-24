package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRestService implements UserService {
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final CertificateDao certificateDao;

    @Autowired
    public UserRestService(UserDao userDao, OrderDao orderDao, CertificateDao certificateDao) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.certificateDao = certificateDao;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User findByName(String name) {
        return userDao.findByName(name).get();
    }

    @Override
    public User makeOrder(Long userId, Long certificateId) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(certificateId);
        Certificate certificate = optionalCertificate.get();
        Optional<User> optionalUser = userDao.findById(userId);
        User user = optionalUser.get();
        Order order = new Order(certificate.getPrice(), certificate);
        Order savedOrder = orderDao.makeUserOrder(user.getId(), order);
        Optional<User> foundUser = userDao.findById(userId);
        return foundUser.get();
    }
}
