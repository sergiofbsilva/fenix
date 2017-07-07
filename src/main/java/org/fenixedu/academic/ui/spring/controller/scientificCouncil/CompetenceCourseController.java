package org.fenixedu.academic.ui.spring.controller.scientificCouncil;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Department_Base;
import org.fenixedu.academic.domain.degreeStructure.CurricularStage;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.service.services.bolonhaManager.EditCompetenceCourse;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.academic.ui.spring.controller.ScientificCouncilSpringApplication;
import org.fenixedu.academic.ui.spring.controller.student.StudentGroupingController;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
@SpringFunctionality(app = ScientificCouncilSpringApplication.class, title = "label.competence.courses")
@RequestMapping("/scientific-council/competence-courses")
public class CompetenceCourseController {
    
    private String resolveView(String templateName) {
        return "fenixedu-academic/competence-courses/" + templateName;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String home(@RequestParam(required = false) DepartmentUnit departmentUnit, Model model) {
        model.addAttribute("departmentUnits", getDepartmentUnits());
        model.addAttribute("departmentUnit", departmentUnit);

        if (departmentUnit != null) {
            setGroupMembers(departmentUnit, model);
            model.addAttribute("scientificAreaUnits", departmentUnit.getScientificAreaUnits());
        }
        return resolveView("home");
    }

    @RequestMapping(value = "toggle", method = RequestMethod.GET)
    public String toggle(Model model, @RequestParam DepartmentUnit departmentUnit, @RequestParam CompetenceCourse
            competenceCourse, RedirectAttributes redirectAttributes) {
        try {
            CurricularStage changed =
                    (competenceCourse.getCurricularStage().equals(CurricularStage.PUBLISHED) ? CurricularStage.APPROVED : CurricularStage
                            .PUBLISHED);
            EditCompetenceCourse.runEditCompetenceCourse(competenceCourse.getExternalId(), changed);
        } catch (FenixServiceException e) {
            redirectAttributes.addFlashAttribute("error", (BundleUtil.getString(Bundle.BOLONHA, e.getMessage())));
        } catch (DomainException e) {
            redirectAttributes.addFlashAttribute("error", (BundleUtil.getString(Bundle.DOMAIN_EXCEPTION, e.getMessage())));
        }
        redirectAttributes.addAttribute("departmentUnit", departmentUnit.getExternalId());
        return "redirect:/scientific-council/competence-courses";
    }

    private void setScientificAreaUnits(DepartmentUnit departmentUnit, Model model) {
    }

    private Set<DepartmentUnit> getDepartmentUnits() {
        return Bennu.getInstance().getDepartmentsSet().stream().sorted(Comparator.comparing(Department::getName)).map
                (Department::getDepartmentUnit)
                .collect
                (Collectors.toSet());
    }

    private void setGroupMembers(DepartmentUnit departmentUnit, Model model) {
        Group competenceCoursesManagementGroup = departmentUnit.getDepartment().getCompetenceCourseMembersGroup();
        if (competenceCoursesManagementGroup != null) {
            model.addAttribute("groupMembers", competenceCoursesManagementGroup.getMembers().collect(Collectors.toSet()));
        }
    }
}
