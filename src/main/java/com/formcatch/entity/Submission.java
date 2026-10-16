package com.formcatch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表单提交实体（动态内容存 JSON）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fc_submission", indexes = {
    @Index(name = "idx_sub_form", columnList = "form_id"),
    @Index(name = "idx_sub_ip", columnList = "ip_address")
})
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    /** 动态表单数据，JSON 格式 */
    @Column(nullable = false, columnDefinition = "CLOB")
    private String dataJson;

    /** 提交者 IP */
    @Column(length = 64)
    private String ipAddress;

    /** User-Agent */
    @Column(length = 500)
    private String userAgent;

    /** 防重复 Cookie token */
    @Column(length = 64)
    private String dedupToken;

    /** 提交时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
