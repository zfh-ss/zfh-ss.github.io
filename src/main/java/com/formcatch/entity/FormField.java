package com.formcatch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 表单字段实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fc_form_field")
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    /** 字段标识（用于 JSON key，英文下划线） */
    @Column(nullable = false, length = 50)
    private String name;

    /** 字段显示标签 */
    @Column(nullable = false, length = 200)
    private String label;

    /** 字段类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FieldType type;

    /** 是否必填 */
    @Column(nullable = false)
    private boolean required = false;

    /** 占位提示 */
    @Column(length = 200)
    private String placeholder;

    /** 默认值 */
    @Column(length = 500)
    private String defaultValue;

    /** 选项列表（RADIO/CHECKBOX/SELECT 使用，逗号分隔） */
    @Column(columnDefinition = "TEXT")
    private String options;

    /** 排序 */
    @Column(nullable = false)
    private Integer sortOrder = 0;
}
