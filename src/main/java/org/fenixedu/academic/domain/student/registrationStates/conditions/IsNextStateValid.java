package org.fenixedu.academic.domain.student.registrationStates.conditions;

import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.conditions.RegistrationStateCondition;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import com.google.common.collect.Lists;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class IsNextStateValid extends RegistrationStateCondition {

    @Override
    public List<String> getTransitionBlockers(RegistrationState from, RegistrationStateBean to) {
        if (from == null || from.isValidNextStateType(to.getStateType())) {
            return Collections.EMPTY_LIST;
        }
        return Lists.newArrayList(BundleUtil.getString(Bundle.ACADEMIC, "error.invalid.next.state", from.getStateType()
                    .getName().getContent(), to.getStateType().getName().getContent()));
    }
}
