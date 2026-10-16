package com.formcatch.service;

import com.formcatch.entity.FieldType;
import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.repository.FormRepository;
import com.formcatch.repository.SubmissionRepository;
import com.formcatch.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final SubmissionRepository submissionRepository;

    /** 创建空表单（仅标题） */
    @Transactional
    public Form create(String title, String description) {
        Form form = new Form();
        form.setTitle(title);
        form.setDescription(description);
        form.setEnabled(true);
        form.setSlug(generateUniqueSlug());
        return formRepository.save(form);
    }

    /** 保存（新建或更新） */
    @Transactional
    public Form save(Form form) {
        if (form.getSlug() == null || form.getSlug().isBlank()) {
            form.setSlug(generateUniqueSlug());
        }
        return formRepository.save(form);
    }

    /** 查找 by ID */
    public Optional<Form> findById(Long id) {
        return formRepository.findById(id);
    }

    /** 查找 by slug */
    public Optional<Form> findBySlug(String slug) {
        return formRepository.findBySlug(slug);
    }

    /** 分页查询全部 */
    public Page<Form> listAll(Pageable pageable) {
        return formRepository.findAll(pageable);
    }

    /** 搜索 */
    public Page<Form> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return formRepository.findAll(pageable);
        }
        return formRepository.findByTitleContaining(keyword.trim(), pageable);
    }

    /** 删除（先清理该表单下的提交，避免外键约束冲突） */
    @Transactional
    public void delete(Long id) {
        submissionRepository.deleteByFormId(id);
        formRepository.deleteById(id);
    }

    /** 切换开关 */
    @Transactional
    public Form toggleEnabled(Long id) {
        Form form = formRepository.findById(id).orElseThrow();
        form.setEnabled(!form.isEnabled());
        return formRepository.save(form);
    }

    /** 添加字段到表单 */
    @Transactional
    public FormField addField(Form form, String name, String label, FieldType type,
                              boolean required, String placeholder, String defaultValue,
                              String options, Integer sortOrder) {
        FormField field = new FormField();
        field.setForm(form);
        field.setName(name);
        field.setLabel(label);
        field.setType(type);
        field.setRequired(required);
        field.setPlaceholder(placeholder);
        field.setDefaultValue(defaultValue);
        field.setOptions(options);
        field.setSortOrder(sortOrder != null ? sortOrder : form.getFields().size());
        form.getFields().add(field);
        formRepository.save(form);
        return field;
    }

    /** 删除字段 */
    @Transactional
    public void removeField(Form form, Long fieldId) {
        form.getFields().removeIf(f -> f.getId().equals(fieldId));
        formRepository.save(form);
    }

    /** 设置表单字段列表（整体替换） */
    @Transactional
    public void setFields(Form form, List<FormField> fields) {
        form.getFields().clear();
        int i = 0;
        for (FormField f : fields) {
            f.setForm(form);
            f.setSortOrder(i++);
            form.getFields().add(f);
        }
        formRepository.save(form);
    }

    private String generateUniqueSlug() {
        String slug;
        do {
            slug = SlugGenerator.generate(8);
        } while (formRepository.existsBySlug(slug));
        return slug;
    }
}
