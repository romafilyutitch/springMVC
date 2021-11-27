package com.epam.esm.repository;

import com.epam.esm.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from User u left join u.orders o where u.id = ?1")
    Page<Order> findUserOrdersPage(long userId, Pageable pageable);

    @Query("select count(o) from User u left join u.orders o where u.id = ?1")
    int getUserOrdersTotalElements(long userId);

    @Query("select o from User u left join u.orders o where u.id = ?1")
    Page<Order> findCertificateOrders(long certificateId, Pageable pageable);

    @Query("select count(*) from Order o where o.certificate.id = ?1")
    int getCertificateOrdersTotalElements(long certificateId);
}
