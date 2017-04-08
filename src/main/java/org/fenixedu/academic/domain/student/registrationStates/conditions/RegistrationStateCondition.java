package org.fenixedu.academic.domain.student.registrationStates.conditions;

import java.util.List;

import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

/***
 * This abstract class is used when transiting to a
 * {@link org.fenixedu.academic.domain.student.registrationStates.RegistrationState} if it configured as a pre or post condition.
 *
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public abstract class RegistrationStateCondition {

    public LocalizedString getName() {
        return BundleUtil.getLocalizedString(Bundle.ACADEMIC, getClass().getName() + ".name");
    }

    public LocalizedString getDescription() {
        return BundleUtil.getLocalizedString(Bundle.ACADEMIC, getClass().getName() + ".description");
    }
    
    /***
     * This method is called when a transition to a registration state configured with pre conditions is triggered.
     * If the return list is not empty the transition does not occur.
     * @param from the previous {@link RegistrationState}. {@value null} if does not exist.
     * @param to the new state represented by an instance of {@link RegistrationStateBean}
     * @return a list of error pre-condition messages
     */
    public abstract List<String> getTransitionBlockers(RegistrationState from, RegistrationStateBean to);
}
