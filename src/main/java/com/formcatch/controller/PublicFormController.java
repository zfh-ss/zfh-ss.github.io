package com.formcatch.controller;

import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.service.CsvExportService;
import com.formcatch.service.FormService;
import com.formcatch.service.SubmissionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 公开填写 /f/{slug} 及提交处理
 */
@Slf4j
@Controller
@RequestMapping("/f")
@RequiredArgsConstructor
public class PublicFormController {

    private final FormService formService;
    private final SubmissionService submissionService;
    private final CsvExportService csvExportService;

    @Value("${formcatch.public-path:/f/}")
    private String publicPath;

    /** 公开填写页面 */
    @GetMapping("/{slug}")
    public String viewForm(@PathVariable String slug,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {
        Optional<Form> opt = formService.findBySlug(slug);
        if (opt.isEmpty()) {
            return "error/404";
        }
        Form form = opt.get();
        model.addAttribute("form", form);
        model.addAttribute("submissionCount",
                submissionService.countByForm(form.getId()));

        // 如果已提交过，提示
        String token = getOrCreateToken(request, response);
        model.addAttribute("token", token);
        model.addAttribute("closed", !form.isAccepting());

        return "public/form-view";
    }

    /** 处理提交（multipart form） */
    @PostMapping("/{slug}/submit")
    public String submitForm(@PathVariable String slug,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             RedirectAttributes ra) {
        Form form = formService.findBySlug(slug).orElseThrow();

        Map<String, Object> data = new LinkedHashMap<>();
        for (FormField field : form.getFields()) {
            String[] values = request.getParameterValues(field.getName());
            if (values == null || values.length == 0) {
                data.put(field.getName(), null);
            } else if (field.getType() == com.formcatch.entity.FieldType.CHECKBOX) {
                data.put(field.getName(), Arrays.asList(values));
            } else {
                data.put(field.getName(), values[0]);
            }
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        String ua = request.getHeader("User-Agent");
        String token = getOrCreateToken(request, response);

        try {
            submissionService.submit(form, data, ip, ua, token);
            ra.addFlashAttribute("success", true);
            return "redirect:/f/" + slug + "?success=1";
        } catch (SubmissionService.SubmissionException e) {
            // 向前端显示错误
            modelMerge(form, data, e.getMessage(), request);
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/f/" + slug + "?error=1";
        } catch (Exception e) {
            log.error("submit error", e);
            ra.addFlashAttribute("error", "服务器错误：" + e.getMessage());
            return "redirect:/f/" + slug + "?error=1";
        }
    }

    /** 通过 REST 方式提交（适合前端 AJAX / 无 JavaScript 场景） */
    @PostMapping(value = "/{slug}/api", consumes = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> submitJson(@PathVariable String slug,
                                          @RequestBody Map<String, Object> body,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        Form form = formService.findBySlug(slug).orElse(null);
        Map<String, Object> result = new LinkedHashMap<>();
        if (form == null) {
            result.put("ok", false);
            result.put("message", "表单不存在");
            return result;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        String token = getOrCreateToken(request, response);

        try {
            submissionService.submit(form, body, ip, ua, token);
            result.put("ok", true);
            result.put("message", "提交成功");
        } catch (SubmissionService.SubmissionException e) {
            result.put("ok", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("ok", false);
            result.put("message", "服务器错误");
            log.error("api submit error", e);
        }
        return result;
    }

    /** 导出 CSV */
    @GetMapping("/{slug}/export")
    public void exportCsv(@PathVariable String slug,
                          HttpServletResponse response) throws Exception {
        Form form = formService.findBySlug(slug).orElseThrow();
        byte[] data = csvExportService.export(form);
        String filename = "form-" + form.getSlug() + "-" + System.currentTimeMillis() + ".csv";
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }

    private String getOrCreateToken(HttpServletRequest request, HttpServletResponse response) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("fc_dedup".equals(c.getName())) {
                    token = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
        }
        if (token == null || token.isBlank()) {
            token = UUID.randomUUID().toString().replace("-", "");
            Cookie cookie = new Cookie("fc_dedup", token);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1 年
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
        }
        return token;
    }

    /** 临时存储错误态用的 request 信息 —— 仅占位，redirect 后由 FlashAttribute 驱动 */
    private void modelMerge(Form form, Map<String, Object> data, String err, HttpServletRequest req) {
        // 本实现通过 redirect + flash 回显；如需要回显到同页可在这里扩展
    }
}
