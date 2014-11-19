package org.fenixedu.academic.ui.spring.controller.teacher.authorization;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.utl.ist.fenix.tools.spreadsheet.SheetData;
import pt.utl.ist.fenix.tools.spreadsheet.SpreadsheetBuilder;
import pt.utl.ist.fenix.tools.spreadsheet.WorkbookExportFormat;

@Service
public class AuthorizationService {

    public List<Department> getDepartments() {
        return Department.readActiveDepartments();
    }

    public List<TeacherCategory> getCategories() {
        return Bennu.getInstance().getTeacherCategorySet().stream().sorted().collect(Collectors.toList());
    }

    public List<ExecutionSemester> getExecutionPeriods() {
        return Bennu.getInstance().getExecutionPeriodsSet().stream()
                .sorted(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR.reversed()).collect(Collectors.toList());
    }

    public TeacherAuthorization createTeacherAuthorization(FormBean bean) {
        return createTeacherAuthorization(bean.getUser(), bean.getDepartment(), bean.getPeriod(), bean.getCategory(),
                bean.getContracted(), bean.getLessonHours());
    }

    @Atomic(mode = TxMode.WRITE)
    public TeacherAuthorization createTeacherAuthorization(User user, Department department, ExecutionSemester semester,
            TeacherCategory category, Boolean contracted, Double lessonHours) {

        Teacher teacher;

        if (user.getPerson().getTeacher() == null) {
            teacher = new Teacher(user.getPerson());
        }

        teacher = user.getPerson().getTeacher();

        return TeacherAuthorization.createOrUpdate(teacher, department, semester, category, contracted, lessonHours);
    }

    @Atomic(mode = TxMode.WRITE)
    public TeacherCategory createTeacherCategory(String code, LocalizedString name, Integer weight) {
        return new TeacherCategory(code, name, weight);
    }

    private Stream<TeacherAuthorization> getAuthorizations(Department department) {

        if (department == null) {
            return Bennu.getInstance().getTeacherAuthorizationSet().stream();
        }

        return department.getTeacherAuthorizationStream();

    }

    public List<TeacherAuthorization> getRevokedAuthorizations() {
        Comparator<TeacherAuthorization> byRevokeTime = (a1, a2) -> {
            return a1.getRevokeTime().compareTo(a2.getRevokeTime());
        };

        return Bennu.getInstance().getRevokedTeacherAuthorizationSet().stream().sorted(byRevokeTime.reversed())
                .collect(Collectors.toList());
    }

    public List<TeacherAuthorization> searchAuthorizations(final FormBean search) {
        return getAuthorizations(search.getDepartment()).filter(t -> t.getExecutionSemester().equals(search.getPeriod()))
                .collect(Collectors.toList());
    }

    @Atomic(mode = TxMode.WRITE)
    public void revoke(TeacherAuthorization authorization) {
        authorization.revoke();
    }

    @Atomic(mode = TxMode.WRITE)
    public void createCategory(CategoryBean form) {
        new TeacherCategory(form.getCode(), form.getName(), form.getWeight());
    }

    @Atomic(mode = TxMode.WRITE)
    public void editCategory(TeacherCategory category, CategoryBean form) {
        category.setCode(form.getCode());
        category.setName(form.getName());
        category.setWeight(form.getWeight());
    }

    public void dumpCSV(FormBean search, OutputStream out) throws IOException {

        SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet("authorizations", new SheetData<TeacherAuthorization>(searchAuthorizations(search)) {

            @Override
            protected void makeLine(TeacherAuthorization item) {
                addCell("username", item.getTeacher().getPerson().getUser().getUsername());
                addCell("user", item.getTeacher().getPerson().getUser().getName());
                addCell("contracted", item.isContracted());
                addCell("deparment", item.getDepartment().getNameI18n().getContent());
                addCell("period", item.getExecutionSemester().getQualifiedName());
                addCell("category", item.getTeacherCategory().getName().getContent());
                addCell("hours", item.getLessonHours());
                addCell("authorized-by-username", item.getAuthorizer().getUsername());
                addCell("authorized-by-user", item.getAuthorizer().getName());
            }
        });

        builder.build(WorkbookExportFormat.CSV, out);
    }
}
