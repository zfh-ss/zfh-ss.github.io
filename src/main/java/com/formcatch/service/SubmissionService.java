package com.formcatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formcatch.entity.FieldType;
import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.entity.Submission;
import com.formcatch.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    @Value("${formcatch.dedup-strategies:cookie,ip}")
    private String dedupStrategies;

    @Value("${formcatch.max-submissions-per-ip-per-day:50}")
    private int maxPerIpPerDay;

    /**
     * 校验并保存提交
     * @param form   目标表单
     * @param data   前端传来的 key=value 数据（field.name -> 值或值列表）
     * @param ip     提交者 IP
     * @param ua     User-Agent
     * @param token  Cookie 去重 token
     * @return 保存后的 Submission
     * @throws ValidationException 校验失败
     * @throws ClosedException     表单已关闭
     * @throws DuplicateException  重复提交
     * @throws QuotaException      名额已满
     */
    @Transactional
    public Submission submit(Form form, Map<String, Object> data,
                             String ip, String ua, String token) {

        if (!form.isAccepting()) {
            throw new ClosedException("该表单已关闭或超过截止时间");
        }

        // 名额检查
        if (form.getQuota() != null && form.getQuota() > 0) {
            long count = submissionRepository.countByFormId(form.getId());
            if (count >= form.getQuota()) {
                throw new QuotaException("名额已满");
            }
        }

        // 防重复检查
        String[] strategies = dedupStrategies.split(",");
        Set<String> active = new HashSet<>(Arrays.asList(strategies));

        if (active.contains("cookie") && token != null && !token.isBlank()) {
            if (submissionRepository.findByFormIdAndDedupToken(form.getId(), token).isPresent()) {
                throw new DuplicateException("您已经提交过该表单");
            }
        }

        if (active.contains("ip") && ip != null && !ip.isBlank()) {
            if (submissionRepository.existsByFormIdAndIpAddress(form.getId(), ip)) {
                // 不强制拦截，但限制单 IP 每日总量
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                long dailyCount = submissionRepository.countByFormIdAndIpAddressAfter(
                        form.getId(), ip, todayStart);
                if (dailyCount >= maxPerIpPerDay) {
                    throw new DuplicateException("您今日提交次数已达上限");
                }
            }
        }

        // 字段校验
        Map<String, Object> sanitized = validateFields(form, data);

        Submission submission = new Submission();
        submission.setForm(form);
        submission.setIpAddress(ip);
        submission.setUserAgent(ua);
        submission.setDedupToken(token);

        try {
            submission.setDataJson(objectMapper.writeValueAsString(sanitized));
        } catch (Exception e) {
            throw new ValidationException("数据序列化失败: " + e.getMessage());
        }

        return submissionRepository.save(submission);
    }

    /** 字段级校验 + 清洗 */
    private Map<String, Object> validateFields(Form form, Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (FormField field : form.getFields()) {
            Object raw = data.get(field.getName());
            boolean empty = raw == null ||
                    (raw instanceof String s && s.isBlank()) ||
                    (raw instanceof Collection<?> c && c.isEmpty());

            if (empty) {
                if (field.isRequired()) {
                    throw new ValidationException("【" + field.getLabel() + "】是必填项");
                }
                result.put(field.getName(), null);
                continue;
            }

            // 按类型做格式校验
            Object value = switch (field.getType()) {
                case EMAIL -> validateEmail(field, raw);
                case NUMBER -> validateNumber(field, raw);
                case DATE -> validateDate(field, raw);
                case SELECT, RADIO, CHECKBOX -> validateOptions(field, raw);
                default -> raw;
            };
            result.put(field.getName(), value);
        }
        return result;
    }

    private String validateEmail(FormField field, Object raw) {
        String v = raw.toString().trim();
        if (!Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$").matcher(v).matches()) {
            throw new ValidationException("【" + field.getLabel() + "】邮箱格式不正确");
        }
        return v;
    }

    private Double validateNumber(FormField field, Object raw) {
        try {
            return Double.parseDouble(raw.toString().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("【" + field.getLabel() + "】必须是数字");
        }
    }

    private String validateDate(FormField field, Object raw) {
        String v = raw.toString().trim();
        try {
            LocalDate.parse(v); // ISO-8601
        } catch (DateTimeParseException e) {
            throw new ValidationException("【" + field.getLabel() + "】日期格式应为 YYYY-MM-DD");
        }
        return v;
    }

    private Object validateOptions(FormField field, Object raw) {
        if (field.getOptions() == null || field.getOptions().isBlank()) return raw;
        Set<String> allowed = Set.of(field.getOptions().split("\\s*,\\s*"));
        if (raw instanceof Collection<?> list) {
            for (Object item : list) {
                if (!allowed.contains(item.toString().trim())) {
                    throw new ValidationException("【" + field.getLabel() + "】包含无效选项");
                }
            }
            return list;
        } else {
            String v = raw.toString().trim();
            if (!allowed.contains(v)) {
                throw new ValidationException("【" + field.getLabel() + "】选项不在允许范围内");
            }
            return v;
        }
    }

    /** 查询提交列表 */
    public Page<Submission> listByForm(Long formId, Pageable pageable) {
        return submissionRepository.findByFormIdOrderBySubmittedAtDesc(formId, pageable);
    }

    public Optional<Submission> findById(Long id) {
        return submissionRepository.findById(id);
    }

    public long countByForm(Long formId) {
        return submissionRepository.countByFormId(formId);
    }

    @Transactional
    public void delete(Long id) {
        submissionRepository.deleteById(id);
    }

    // ===== 自定义异常 =====
    public static class SubmissionException extends RuntimeException {
        public SubmissionException(String msg) { super(msg); }
    }
    public static class ValidationException extends SubmissionException {
        public ValidationException(String msg) { super(msg); }
    }
    public static class ClosedException extends SubmissionException {
        public ClosedException(String msg) { super(msg); }
    }
    public static class DuplicateException extends SubmissionException {
        public DuplicateException(String msg) { super(msg); }
    }
    public static class QuotaException extends SubmissionException {
        public QuotaException(String msg) { super(msg); }
    }
}
