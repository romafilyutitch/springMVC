package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("select t from Certificate c left join c.tags t where c.id = ?1")
    Page<Tag> findCertificateTagsPage(long certificateId, Pageable pageable);

    @Query("select t from Certificate c left join c.tags t where c.id = ?1 and t.id = ?2")
    Optional<Tag> findCertificateTag(long certificateId, long tagId);

    @Query("select count(t) from Certificate c left join c.tags t where c.id = ?1")
    int getCertificateTagsTotalElements(long certificateId);
}
