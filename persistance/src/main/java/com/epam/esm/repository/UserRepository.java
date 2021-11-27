package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "from User u left join u.orders o left join o.certificate c left join c.tags t group by u.id order by sum(o.cost) desc limit 0,1", nativeQuery = true)
    User findRichestUser();

    @Query(value = "select t from User u left join u.orders o left join o.certificate c left join c.tags t where u.id = (select u.id from User u left join u.orders o left group by u.id order by sum(o.cost) desc limit 0,1) group by u.id order by count(t.id) desc limit 0,1", nativeQuery = true)
    Tag findRichestUserPopularTag();

    @Query("from User u left join u.orders o where o.id = ?1")
    User findByOrderId(long orderId);

    Optional<User> findByUsername(String username);
}
