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
package org.fenixedu.academic.domain.phd.candidacy.activities;

import java.util.List;

import org.fenixedu.academic.domain.caseHandling.PreConditionNotValidException;
import org.fenixedu.academic.domain.phd.PhdProgramCandidacyProcessState;
import org.fenixedu.academic.domain.phd.PhdProgramDocumentUploadBean;
import org.fenixedu.academic.domain.phd.candidacy.PhdProgramCandidacyProcess;
import org.fenixedu.bennu.core.domain.User;

public class UploadCandidacyReview extends PhdProgramCandidacyProcessActivity {

    @Override
    protected void activityPreConditions(PhdProgramCandidacyProcess process, User userView) {
        if (process.isInState(PhdProgramCandidacyProcessState.PENDING_FOR_COORDINATOR_OPINION)) {
            if ((process.isAllowedToManageProcess(userView)
                    || process.getIndividualProgramProcess().isCoordinatorForPhdProgram(userView.getPerson()))) {
                return;
            }
        }

        if (process.isInState(PhdProgramCandidacyProcessState.WAITING_FOR_SCIENTIFIC_COUNCIL_RATIFICATION)) {
            if (process.isAllowedToManageProcess(userView)) {
                return;
            }
        }

        throw new PreConditionNotValidException();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PhdProgramCandidacyProcess executeActivity(PhdProgramCandidacyProcess process, User userView, Object object) {

        for (final PhdProgramDocumentUploadBean each : (List<PhdProgramDocumentUploadBean>) object) {
            if (each.hasAnyInformation()) {
                process.addDocument(each, userView != null ? userView.getPerson() : null);
            }
        }

        return process;
    }

}
