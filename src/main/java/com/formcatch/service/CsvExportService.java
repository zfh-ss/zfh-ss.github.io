package com.formcatch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.entity.Submission;
import com.formcatch.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSV 导出服务
 */
@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    /**
     * 导出某表单的全部提交为 CSV（含 UTF-8 BOM，Excel 友好）
     */
    public byte[] export(Form form) throws Exception {
        List<Submission> submissions = submissionRepository.findByFormIdOrderBySubmittedAtDesc(
                form.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent();

        List<FormField> fields = form.getFields().stream()
                .sorted(Comparator.comparing(FormField::getSortOrder))
                .collect(Collectors.toList());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // BOM，让 Excel 正确识别 UTF-8
        bos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {
            // 表头：提交时间 + 各字段标签 + IP
            List<String> headers = new ArrayList<>();
            headers.add("提交时间");
            for (FormField f : fields) headers.add(f.getLabel());
            headers.add("IP");
            pw.println(headers.stream().map(CsvExportService::escape).collect(Collectors.joining(",")));

            // 数据行
            for (Submission sub : submissions) {
                Map<String, Object> data = parseJson(sub.getDataJson());
                List<String> row = new ArrayList<>();
                row.add(sub.getSubmittedAt().toString());
                for (FormField f : fields) {
                    Object v = data.get(f.getName());
                    row.add(formatValue(v));
                }
                row.add(sub.getIpAddress() == null ? "" : sub.getIpAddress());
                pw.println(row.stream().map(CsvExportService::escape).collect(Collectors.joining(",")));
            }
        }
        return bos.toByteArray();
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String formatValue(Object v) {
        if (v == null) return "";
        if (v instanceof Collection<?> c) {
            return c.stream().map(Object::toString).collect(Collectors.joining("; "));
        }
        return v.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        // CSV 规范：包含逗号/引号/换行时用双引号包裹，并把内部双引号翻倍
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuote ? "\"" + escaped + "\"" : escaped;
    }
}
