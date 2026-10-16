package com.formcatch;

import com.formcatch.entity.FieldType;
import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.entity.Submission;
import com.formcatch.repository.FormRepository;
import com.formcatch.repository.SubmissionRepository;
import com.formcatch.service.FormService;
import com.formcatch.service.SubmissionService;
import com.formcatch.service.SubmissionService.ClosedException;
import com.formcatch.service.SubmissionService.DuplicateException;
import com.formcatch.service.SubmissionService.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FormCatchApplicationTests {

    @Autowired FormService formService;
    @Autowired SubmissionService submissionService;
    @Autowired FormRepository formRepository;
    @Autowired SubmissionRepository submissionRepository;

    @Test
    void contextLoads() {
        assertNotNull(formService);
    }

    @Test
    void createForm_shouldGenerateUniqueSlug() {
        Form form = formService.create("测试表单", "一个测试");
        assertNotNull(form.getId());
        assertNotNull(form.getSlug());
        assertEquals(8, form.getSlug().length());
        assertEquals("测试表单", form.getTitle());
        assertTrue(form.isEnabled());

        // slug 唯一
        assertTrue(formRepository.existsBySlug(form.getSlug()));
    }

    @Test
    void createAndSubmit_withAllFieldTypes() {
        Form form = formService.create("全类型表单", "测试各种字段");

        formService.addField(form, "name", "姓名", FieldType.TEXT,
                true, "请输入姓名", null, null, 0);
        formService.addField(form, "email", "邮箱", FieldType.EMAIL,
                true, null, null, null, 1);
        formService.addField(form, "age", "年龄", FieldType.NUMBER,
                false, null, null, null, 2);

        // 正确数据
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "李四");
        data.put("email", "lisi@example.com");
        data.put("age", "20");

        Submission sub = submissionService.submit(form, data,
                "127.0.0.1", "test-ua", "test-token-1");
        assertNotNull(sub.getId());
        assertEquals(1, submissionRepository.countByFormId(form.getId()));
    }

    @Test
    void submit_shouldRejectBadEmail() {
        Form form = formService.create("邮箱校验", null);
        formService.addField(form, "email", "邮箱", FieldType.EMAIL,
                true, null, null, null, 0);

        Map<String, Object> data = Map.of("email", "not-an-email");
        assertThrows(ValidationException.class, () ->
                submissionService.submit(form, data, "127.0.0.1", "ua", "tok-bad"));
    }

    @Test
    void submit_shouldRejectDuplicateByCookie() {
        Form form = formService.create("防重复测试", null);
        formService.addField(form, "name", "姓名", FieldType.TEXT,
                true, null, null, null, 0);

        Map<String, Object> data1 = Map.of("name", "A");
        submissionService.submit(form, data1, "1.1.1.1", "ua", "same-token");

        Map<String, Object> data2 = Map.of("name", "B");
        assertThrows(DuplicateException.class, () ->
                submissionService.submit(form, data2, "2.2.2.2", "ua", "same-token"));
    }

    @Test
    void disabledForm_shouldRejectSubmission() {
        Form form = formService.create("关闭测试", null);
        form.setEnabled(false);
        formRepository.save(form);

        Map<String, Object> data = Map.of("f", "v");
        assertThrows(ClosedException.class, () ->
                submissionService.submit(form, data, "1.1.1.1", "ua", "t"));
    }

    @Test
    void toggleEnabled_works() {
        Form form = formService.create("开关测试", null);
        assertTrue(form.isEnabled());
        Form toggled = formService.toggleEnabled(form.getId());
        assertFalse(toggled.isEnabled());
        Form toggledAgain = formService.toggleEnabled(form.getId());
        assertTrue(toggledAgain.isEnabled());
    }
}
