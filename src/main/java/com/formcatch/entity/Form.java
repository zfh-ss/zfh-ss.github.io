package com.formcatch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fc_form")
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 公开访问的短链接 slug */
    @Column(nullable = false, unique = true, length = 32)
    private String slug;

    /** 表单标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 表单说明 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 是否开启（关闭后不可填写） */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 截止时间（null 表示永不截止） */
    private LocalDateTime deadline;

    /** 名额限制（null 表示不限制） */
    private Integer quota;

    /** 防重复提交策略，逗号分隔：cookie,ip,email */
    @Column(length = 100)
    private String dedupStrategies;

    /** 密码访问（null 或空表示无需密码） */
    @Column(length = 100)
    private String accessCode;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<FormField> fields = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** 当前状态是否可填写 */
    public boolean isAccepting() {
        if (!enabled) return false;
        if (deadline != null && LocalDateTime.now().isAfter(deadline)) return false;
        return true;
    }
}
