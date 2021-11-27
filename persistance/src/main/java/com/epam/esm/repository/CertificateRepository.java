package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Query("select c from Order o left join o.certificate c where c.id = ?1")
    Optional<Certificate> findByOrderId(long orderId);



}
