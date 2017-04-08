package org.fenixedu.academic.domain.student.registrationStates.conditions;

import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import com.google.common.collect.Lists;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class CheckCurriculumLines extends RegistrationStateCondition {

    @Override
    public List<String> getTransitionBlockers(RegistrationState from, RegistrationStateBean to) {
        final ExecutionYear year = ExecutionYear.readByDateTime(to.getCreated());

        if (to.getRegistration().hasAnyEnroledEnrolments(year)) {
            return Lists.newArrayList(BundleUtil
                    .getString(Bundle.ACADEMIC, "RegisteredState.error.registration.has.enroled.enrolments.for.execution.year",
                            year.getName()));
        }

        return Collections.EMPTY_LIST;
    }
}
