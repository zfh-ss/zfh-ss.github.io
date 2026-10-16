package com.formcatch.controller;

import com.formcatch.entity.Form;
import com.formcatch.entity.FormField;
import com.formcatch.repository.SubmissionRepository;
import com.formcatch.service.FormService;
import com.formcatch.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 管理后台（Thymeleaf 页面）
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FormService formService;
    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;

    /** 表单列表首页 */
    @GetMapping
    public String listForms(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Page<Form> forms = formService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("forms", forms);
        model.addAttribute("q", q);
        return "admin/index";
    }

    /** 创建表单（GET） */
    @GetMapping("/forms/new")
    public String newForm(Model model) {
        model.addAttribute("form", new Form());
        return "admin/form-edit";
    }

    /** 创建表单（POST） */
    @PostMapping("/forms/new")
    public String createForm(@RequestParam String title,
                             @RequestParam(required = false) String description) {
        Form form = formService.create(title, description);
        return "redirect:/admin/forms/" + form.getId() + "/edit";
    }

    /** 编辑表单（GET） */
    @GetMapping("/forms/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Form> opt = formService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin";
        Form form = opt.get();
        model.addAttribute("form", form);
        model.addAttribute("submissionCount", submissionRepository.countByFormId(id));
        return "admin/form-edit";
    }

    /** 编辑表单基本信息（POST） */
    @PostMapping("/forms/{id}/update")
    public String updateForm(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String accessCode,
                             @RequestParam(required = false) String dedupStrategies,
                             @RequestParam(required = false) String deadline,
                             @RequestParam(required = false) Integer quota,
                             RedirectAttributes ra) {
        Form form = formService.findById(id).orElseThrow();
        form.setTitle(title);
        form.setDescription(description);
        form.setAccessCode((accessCode == null || accessCode.isBlank()) ? null : accessCode);
        form.setDedupStrategies(dedupStrategies);
        if (deadline == null || deadline.isBlank()) {
            form.setDeadline(null);
        } else {
            form.setDeadline(LocalDateTime.parse(deadline));
        }
        if (quota == null || quota <= 0) {
            form.setQuota(null);
        } else {
            form.setQuota(quota);
        }
        formService.save(form);
        ra.addFlashAttribute("msg", "已保存");
        return "redirect:/admin/forms/" + id + "/edit";
    }

    /** 切换开关 */
    @PostMapping("/forms/{id}/toggle")
    public String toggleForm(@PathVariable Long id) {
        formService.toggleEnabled(id);
        return "redirect:/admin/forms/" + id + "/edit";
    }

    /** 删除表单 */
    @PostMapping("/forms/{id}/delete")
    public String deleteForm(@PathVariable Long id, RedirectAttributes ra) {
        formService.delete(id);
        ra.addFlashAttribute("msg", "已删除");
        return "redirect:/admin";
    }

    /** 保存字段列表（整体替换） */
    @PostMapping(value = "/forms/{id}/fields", consumes = "application/json;charset=UTF-8")
    @ResponseBody
    public java.util.Map<String, Object> saveFields(@PathVariable Long id,
                                                    @RequestBody java.util.List<com.formcatch.entity.FormField> fields) {
        java.util.Map<String, Object> r = new java.util.HashMap<>();
        try {
            Form form = formService.findById(id).orElseThrow();
            formService.setFields(form, fields);
            r.put("ok", true);
            r.put("message", "字段已保存");
        } catch (Exception e) {
            r.put("ok", false);
            r.put("message", e.getMessage());
        }
        return r;
    }

    /** 提交列表 */
    @GetMapping("/forms/{id}/submissions")
    public String listSubmissions(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        Form form = formService.findById(id).orElseThrow();
        var pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "submittedAt"));
        model.addAttribute("form", form);
        model.addAttribute("submissions", submissionService.listByForm(id, pageable));
        return "admin/submissions";
    }

    /** 提交详情 */
    @GetMapping("/submissions/{sid}")
    public String submissionDetail(@PathVariable Long sid, Model model) {
        var opt = submissionService.findById(sid);
        if (opt.isEmpty()) return "redirect:/admin";
        model.addAttribute("submission", opt.get());
        model.addAttribute("form", opt.get().getForm());
        return "admin/submission-detail";
    }

    /** 删除单条提交 */
    @PostMapping("/submissions/{sid}/delete")
    public String deleteSubmission(@PathVariable Long sid) {
        var opt = submissionService.findById(sid);
        if (opt.isPresent()) {
            Long fid = opt.get().getForm().getId();
            submissionService.delete(sid);
            return "redirect:/admin/forms/" + fid + "/submissions";
        }
        return "redirect:/admin";
    }
}
