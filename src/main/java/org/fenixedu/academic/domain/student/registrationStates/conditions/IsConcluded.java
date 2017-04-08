package org.fenixedu.academic.domain.student.registrationStates.conditions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import com.google.common.collect.Lists;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class IsConcluded extends RegistrationStateCondition {

    @Override
    public List<String> getTransitionBlockers(RegistrationState from, RegistrationStateBean to) {
        Stream<ProgramConclusion> programConclusionStream = ProgramConclusion.conclusionsFor(to.getRegistration());
        if (programConclusionStream.filter(pc -> to.getStateType().equals(pc.getTargetState())).noneMatch(pc -> pc
                .isConclusionProcessed
                (to.getRegistration()))) {

            return Lists.newArrayList(BundleUtil.getString(Bundle.ACADEMIC, "error.invalid.next.conclusion.state", to.getStateType()
                    .getName().getContent()));
        }

        return Collections.EMPTY_LIST;
    }
}
