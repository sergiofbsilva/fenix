/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.fenixedu.academic.domain.student.registrationStates;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.fenixedu.academic.domain.util.workflow.IState;
import org.fenixedu.academic.domain.util.workflow.StateBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */

public class RegistrationStateTypeNew extends RegistrationStateTypeNew_Base implements IState {

    public RegistrationStateTypeNew(String code, LocalizedString name, LocalizedString description, Boolean active, Boolean student, Boolean terminal) {
        setCode(code);
        setName(name);
        setDescription(description);
        setActive(active);
        setStudent(student);
        setTerminal(terminal);
    }

    public Set<String> getValidNextStates() {
        return getValidNextStatesTypeSet().stream().map(stateType -> stateType.getCode()).collect(Collectors.toSet());
    }

    @Override
    public IState nextState() {
        return null;
    }

    @Override
    public IState nextState(StateBean bean) {
        return null;
    }

    @Override
    public void checkConditionsToForward() {}

    @Override
    public void checkConditionsToForward(StateBean bean) {

    }


    public boolean isStudent() {
        return Optional.ofNullable(getStudent()).orElse(false);
    }

    public boolean isTerminal() {
        return Optional.ofNullable(getTerminal()).orElse(false);
    }

    public boolean isActive() {
        return Optional.ofNullable(getActive()).orElse(false);
    }

    public boolean isReingressable() {
        final RegistrationStateTypeNew initialState = RegistrationStateSystem.getInstance().getInitialState();
        return getValidNextStatesTypeSet().stream().anyMatch(s -> s.equals(initialState));
    }

    public boolean canHaveCurriculumLinesOnCreation() {
        // TODO ACDM-1113
        // Isto é suposto representar o quê?
        return true;
    }

}
