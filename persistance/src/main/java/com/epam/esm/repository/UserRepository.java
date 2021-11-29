package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("select u from User u left join u.orders o order by sum(o.cost) desc")
    Page<User> sortByUsersByCostDesc(Pageable pageable);

    @Query(value = "select t from User u left join u.orders o left join o.certificate c left join c.tags t where u.id = ?1 group by t.id order by count(t.id) desc")
    Page<Tag> sortUserTagsByCountDesc(long userId, Pageable pageable);

    @Query("from User u left join u.orders o where o.id = ?1")
    User findByOrderId(long orderId);

    Optional<User> findByUsername(String username);
}
