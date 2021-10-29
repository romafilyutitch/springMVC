package com.epam.esm.service;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderRestService implements OrderService {
    private final OrderDao orderDao;
    private final CertificateDao certificateDao;

    @Autowired
    public OrderRestService(OrderDao orderDao, CertificateDao certificateDao) {
        this.orderDao = orderDao;
        this.certificateDao = certificateDao;
    }

    @Override
    public List<Order> findAll(long page) {
        return orderDao.findPage(page);
    }

    @Override
    public Order findById(Long id) {
        return orderDao.findById(id).get();
    }

    @Override
    public Order findCertificateOrder(Long certificateId) {
        return orderDao.findByCertificateId(certificateId).get();
    }

    @Override
    public Order makeOrder(Long id, Long userId) {
        Optional<Certificate> optionalCertificate = certificateDao.findById(id);
        Certificate certificate = optionalCertificate.get();
        Order order = new Order(certificate.getPrice(), certificate);
        return orderDao.makeUserOrder(userId, order);
    }

    @Override
    public long getTotalElements() {
        return orderDao.getTotalElements();
    }

    @Override
    public long getTotalPages() {
        return orderDao.getTotalPages();
    }

    @Override
    public List<Order> findUserOrders(User user, long page) {
        return orderDao.findUserOrdersPage(user.getId(), page);
    }

    @Override
    public long getUserOrdersTotalPages(User user) {
        return orderDao.getUserOrdersTotalPages(user.getId());
    }

    @Override
    public long getUserOrdersTotalElements(User user) {
        return orderDao.getUserOrdersTotalElements(user.getId());
    }
}
