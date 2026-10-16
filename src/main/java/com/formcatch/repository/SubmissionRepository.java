package com.formcatch.repository;

import com.formcatch.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Page<Submission> findByFormIdOrderBySubmittedAtDesc(Long formId, Pageable pageable);

    long countByFormId(Long formId);

    Optional<Submission> findByFormIdAndDedupToken(Long formId, String dedupToken);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.form.id = :formId AND s.ipAddress = :ip AND s.submittedAt >= :since")
    long countByFormIdAndIpAddressAfter(@Param("formId") Long formId,
                                        @Param("ip") String ip,
                                        @Param("since") LocalDateTime since);

    boolean existsByFormIdAndIpAddress(Long formId, String ipAddress);

    /** 删除某表单下的全部提交（用于删除表单前清理，避免外键约束冲突） */
    @Modifying
    @Query("DELETE FROM Submission s WHERE s.form.id = :formId")
    int deleteByFormId(@Param("formId") Long formId);
}
