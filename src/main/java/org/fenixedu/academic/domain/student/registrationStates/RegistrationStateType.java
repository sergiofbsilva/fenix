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

import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 *
 */

@Deprecated
public enum RegistrationStateType {

    REGISTERED(true, true), // Este estado faz sentido ser representado por um boolean, para saber se o registo está activo ou não

    MOBILITY(true, true), // Se houver alguma forma de perceber se um registo é de Mobilidade, não há necessidade deste estado

    CANCELED(false, false), // Um registo não activo, sem processo de conclusão por substituir este estado

    CONCLUDED(false, true), // Conclusion Process... Cada caso está já visto e comentado

    FLUNKED(false, false), // Relacionado com prescrições / alunos prescritos

    INTERRUPTED(false, false), // Comentado caso a caso

    SCHOOLPARTCONCLUDED(false, true), // Comentado caso a caso

    INTERNAL_ABANDON(false, false), // É usado para representar as trocas de curso

    EXTERNAL_ABANDON(false, false), // Comentado caso a caso

    TRANSITION(false, true), // Este é usado para representar as Registration que estão em transição de Pré-bolonha para Pós-bolonha

    TRANSITED(false, true), // Este é usado para representar as Registration que foram transitadas de Pré-bolonha para Pós-bolonha

    STUDYPLANCONCLUDED(false, true), // Este só é usado nas implementações de RegistrationState

    INACTIVE(false, false); //Closed state for the registrations regarding the AFA & MA protocols

    private RegistrationStateType(final boolean active, final boolean canHaveCurriculumLinesOnCreation) {
        this.active = active;
        this.canHaveCurriculumLinesOnCreation = canHaveCurriculumLinesOnCreation;
    }

    private boolean active;

    private boolean canHaveCurriculumLinesOnCreation;

    public String getName() {
        return name();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isInactive() {
        return !active;
    }

    public boolean canHaveCurriculumLinesOnCreation() {
        return canHaveCurriculumLinesOnCreation;
    }

    public String getQualifiedName() {
        return RegistrationStateTypeNew.class.getSimpleName() + "." + name();
    }

    public String getFullyQualifiedName() {
        return RegistrationStateTypeNew.class.getName() + "." + name();
    }

    public String getDescription() {
        return BundleUtil.getString(Bundle.ENUMERATION, getQualifiedName());
    }

}
