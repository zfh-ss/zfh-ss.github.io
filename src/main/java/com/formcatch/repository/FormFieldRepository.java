package com.formcatch.repository;

import com.formcatch.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {
    List<FormField> findByFormIdOrderBySortOrderAsc(Long formId);
    void deleteByFormId(Long formId);
}
