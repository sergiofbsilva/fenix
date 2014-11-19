package org.fenixedu.academic.ui.spring.controller.teacher.authorization;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringApplication(group = "#managers", path = "teacher-authorizations", title = "teacher.authorizations.title", hint = "teacher")
@SpringFunctionality(app = AuthorizationController.class, title = "teacher.authorizations.title")
@RequestMapping("/teacher/authorizations")
@Controller
public class AuthorizationController {

    @Autowired
    AuthorizationService service;

    private String view(String string) {
        return "fenixedu-academic/teacher/authorization/" + string;
    }

    @RequestMapping(method = GET, value = "categories")
    public String categories(Model model) {
        model.addAttribute("categories", service.getCategories());
        return view("categories/show");
    }

    @RequestMapping(method = GET, value = "categories/create")
    public String showCreate(Model model) {
        model.addAttribute("form", new CategoryBean());
        return view("categories/create");
    }

    @RequestMapping(method = GET, value = "categories/{category}")
    public String showEdit(Model model, @PathVariable TeacherCategory category) {
        model.addAttribute("form", category);
        return view("categories/create");
    }

    @RequestMapping(method = POST, value = "categories/{category}")
    public String createOrEdit(Model model, @Value("null") @PathVariable TeacherCategory category,
            @ModelAttribute CategoryBean form) {
        if (category != null) {
            service.editCategory(category, form);
        } else {
            service.createCategory(form);
        }
        return "redirect:/teacher/authorizations/categories";
    }

    @RequestMapping(method = GET, value = "download")
    public void download(Model model, @ModelAttribute FormBean search, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        service.dumpCSV(search, response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(method = GET, value = "upload")
    public String showUpload(Model model) {
        model.addAttribute("categories", service.getCategories());
        model.addAttribute("departments", service.getDepartments());
        return view("upload");
    }

    @RequestMapping(method = POST, value = "upload")
    public String upload(Model model) {
        return view("upload");
    }

    @RequestMapping(method = GET)
    public String home(Model model, @ModelAttribute FormBean search) {
        if (search.getPeriod() == null) {
            search.setPeriod(service.getExecutionPeriods().isEmpty() ? null : service.getExecutionPeriods().get(0));
        }
        model.addAttribute("search", search);
        model.addAttribute("departments", service.getDepartments());
        model.addAttribute("periods", service.getExecutionPeriods());
        model.addAttribute("authorizations", service.searchAuthorizations(search));
        return view("show");
    }

    @RequestMapping(method = GET, value = "create")
    public String showCreate(Model model, @Value("null") @ModelAttribute FormBean search) {
        if (search.getPeriod() == null) {
            search.setPeriod(service.getExecutionPeriods().isEmpty() ? null : service.getExecutionPeriods().get(0));
        }
        model.addAttribute("formBean", search);
        model.addAttribute("departments", service.getDepartments());
        model.addAttribute("periods", service.getExecutionPeriods());
        model.addAttribute("categories", service.getCategories());
        return view("create");
    }

    @RequestMapping(method = POST, value = "create")
    public String create(Model model, @ModelAttribute FormBean form) {
        service.createTeacherAuthorization(form);
        return home(model, form);
    }

    @RequestMapping(method = GET, value = "revoked")
    public String revoked(Model model) {
        model.addAttribute("authorizations", service.getRevokedAuthorizations());
        return view("revoked");
    }

    @RequestMapping(method = POST, value = "{authorization}/revoke")
    public String revoke(Model model, @PathVariable TeacherAuthorization authorization,
            @Value("null") @ModelAttribute FormBean search) {
        service.revoke(authorization);
        return home(model, search);
    }

}
