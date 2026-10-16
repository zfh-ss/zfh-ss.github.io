package com.formcatch.repository;

import com.formcatch.entity.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Form> findByTitleContaining(String keyword, Pageable pageable);
}
