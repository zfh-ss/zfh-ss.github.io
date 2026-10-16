package com.formcatch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 表单字段类型枚举
 */
public enum FieldType {
    TEXT,        // 单行文本
    TEXTAREA,    // 多行文本
    RADIO,       // 单选
    CHECKBOX,    // 多选
    NUMBER,      // 数字
    DATE,        // 日期
    EMAIL,       // 邮箱
    SELECT,      // 下拉框
    FILE         // 文件上传
}
