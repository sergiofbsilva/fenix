/**
 * Copyright © 2002 Instituto Superior Técnico
 * <p>
 * This file is part of FenixEdu Academic.
 * <p>
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.domain.student.registrationStates;

import static org.fenixedu.academic.predicate.AccessControl.check;

import java.util.*;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.domain.util.workflow.IState;
import org.fenixedu.academic.domain.util.workflow.StateBean;
import org.fenixedu.academic.domain.util.workflow.StateMachine;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.predicate.RegistrationStatePredicates;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.EnrolmentAction;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

/**
 *
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 *
 */
public class RegistrationState extends RegistrationState_Base implements IState {

    static {
        getRelationRegistrationStateRegistration().addListener(new RelationAdapter<RegistrationState, Registration>() {

            @Override
            public void afterAdd(RegistrationState state, Registration registration) {
                super.afterAdd(state, registration);

                if (registration != null && state != null) {
                    new RegistrationStateLog(state, EnrolmentAction.ENROL, AccessControl.getPerson());
                }
            }

            @Override
            public void beforeRemove(RegistrationState state, Registration registration) {
                super.beforeRemove(state, registration);

                if (registration != null && state != null) {
                    new RegistrationStateLog(state, EnrolmentAction.UNENROL, AccessControl.getPerson());
                }
            }

        });
    }

    public static Comparator<RegistrationState> DATE_COMPARATOR = new Comparator<RegistrationState>() {
        @Override
        public int compare(RegistrationState leftState, RegistrationState rightState) {
            int comparationResult = leftState.getStateDate().compareTo(rightState.getStateDate());
            return (comparationResult == 0) ? leftState.getExternalId().compareTo(rightState.getExternalId()) : comparationResult;
        }
    };

    // TODO: ACDM-1113
    public static Comparator<RegistrationState> DATE_AND_STATE_TYPE_COMPARATOR = new Comparator<RegistrationState>() {
        @Override
        public int compare(RegistrationState leftState, RegistrationState rightState) {
            int comparationResult = DATE_COMPARATOR.compare(leftState, rightState);
            if (comparationResult != 0) {
                return comparationResult;
            }
            // TODO the new classe is not comparable
            comparationResult = leftState.getStateType().getOid().compareTo(rightState.getStateType().getOid());
            return (comparationResult == 0) ? leftState.getExternalId().compareTo(rightState.getExternalId()) : comparationResult;
        }
    };

    public RegistrationState() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public RegistrationState(Registration registration, Person person, DateTime dateTime, RegistrationStateTypeNew stateType) {
        this.setRegistration(registration);
        this.setResponsiblePerson(person);
        this.setStateDate(dateTime);
        this.setRegistrationStateType(stateType);
    }

    @Deprecated
    private static RegistrationState createState(Registration registration, Person person, DateTime dateTime,
                                                 RegistrationStateTypeNew stateType) {
        return new RegistrationState(registration, person, dateTime, stateType);
    }

    protected void init(Registration registration, Person responsiblePerson, DateTime stateDate) {
        setStateDate(stateDate != null ? stateDate : new DateTime());
        setRegistration(registration);
        setResponsiblePerson(responsiblePerson != null ? responsiblePerson : AccessControl.getPerson());
    }

    protected void init(Registration registration) {
        init(registration, null, null);
    }

    @Override
    final public IState nextState() {
        return nextState(new StateBean(defaultNextStateType().toString()));
    }

    protected RegistrationStateTypeNew defaultNextStateType() {
        throw new DomainException("error.no.default.nextState.defined");
    }

    @Override
    public IState nextState(final StateBean bean) {
        return createState(getRegistration(), bean.getResponsible(), bean.getStateDateTime(),
                FenixFramework.getDomainObject(bean.getNextState()));
    }

    @Override
    final public void checkConditionsToForward() {
        checkConditionsToForward(new RegistrationStateBean(defaultNextStateType()));
    }

    @Override
    public void checkConditionsToForward(final StateBean bean) {
        if (getValidNextStates().isEmpty()) {
            // TODO generic message
            throw new DomainException("error.impossible.to.forward.from.studyPlanConcluded");
        }
        checkCurriculumLinesForStateDate(bean);
    }

    private void checkCurriculumLinesForStateDate(final StateBean bean) {
        final ExecutionYear year = ExecutionYear.readByDateTime(bean.getStateDateTime());
        final RegistrationStateTypeNew nextStateType = FenixFramework.getDomainObject(bean.getNextState());

        if (nextStateType.canHaveCurriculumLinesOnCreation()) {
            return;
        }

        if (getRegistration().hasAnyEnroledEnrolments(year)) {
            throw new DomainException("RegisteredState.error.registration.has.enroled.enrolments.for.execution.year",
                    year.getName());
        }
    }

    public RegistrationStateTypeNew getStateType() {
        return super.getRegistrationStateType();
    }

    public ExecutionYear getExecutionYear() {
        return ExecutionYear.readByDateTime(getStateDate());
    }

    public void delete() {
        check(this, RegistrationStatePredicates.deletePredicate);
        RegistrationState nextState = getNext();
        RegistrationState previousState = getPrevious();
        if (nextState != null && previousState != null
                && !previousState.getValidNextStates().contains(nextState.getStateType().getCode())) {
            throw new DomainException("error.cannot.delete.registrationState.incoherentState: "
                    + previousState.getStateType().getCode() + " -> " + nextState.getStateType().getCode());
        }
        deleteWithoutCheckRules();
    }

    public void deleteWithoutCheckRules() {
        final Registration registration = getRegistration();
        try {
            String responsablePersonName;
            if (getResponsiblePerson() != null) {
                responsablePersonName = getResponsiblePerson().getPresentationName();
            } else {
                responsablePersonName = "-";
            }

            org.fenixedu.academic.domain.student.RegistrationStateLog
                    .createRegistrationStateLog(getRegistration(), Bundle.MESSAGING,
                            "log.registration.registrationstate.removed", getStateType().getDescription().getContent(), getRemarks());
            setRegistration(null);
            setResponsiblePerson(null);
            setRootDomainObject(null);
            super.deleteDomainObject();
        } finally {
            registration.getStudent().updateStudentRole();
        }
    }

    public RegistrationState getNext() {
        List<RegistrationState> sortedRegistrationsStates =
                new ArrayList<RegistrationState>(getRegistration().getRegistrationStateSet());
        Collections.sort(sortedRegistrationsStates, DATE_COMPARATOR);
        for (ListIterator<RegistrationState> iter = sortedRegistrationsStates.listIterator(); iter.hasNext(); ) {
            RegistrationState state = iter.next();
            if (state.equals(this)) {
                if (iter.hasNext()) {
                    return iter.next();
                }
                return null;
            }
        }
        return null;
    }

    public RegistrationState getPrevious() {
        List<RegistrationState> sortedRegistrationsStates =
                new ArrayList<RegistrationState>(getRegistration().getRegistrationStateSet());
        Collections.sort(sortedRegistrationsStates, DATE_COMPARATOR);
        for (ListIterator<RegistrationState> iter = sortedRegistrationsStates.listIterator(sortedRegistrationsStates.size()); iter
                .hasPrevious(); ) {
            RegistrationState state = iter.previous();
            if (state.equals(this)) {
                if (iter.hasPrevious()) {
                    return iter.previous();
                }
                return null;
            }
        }
        return null;
    }

    public DateTime getEndDate() {
        RegistrationState state = getNext();
        return (state != null) ? state.getStateDate() : null;
    }

    public void setStateDate(YearMonthDay yearMonthDay) {
        super.setStateDate(yearMonthDay.toDateTimeAtMidnight());
    }

    public static RegistrationState createRegistrationState(Registration registration, Person responsible, DateTime creation,
                                                            RegistrationStateTypeNew stateType) {
        RegistrationStateBean bean = new RegistrationStateBean(registration);
        bean.setResponsible(responsible);
        bean.setStateDateTime(creation);
        bean.setStateType(stateType);
        return createRegistrationState(bean);
    }

    @Deprecated
    public static RegistrationState createRegistrationState(RegistrationStateBean bean) {
        RegistrationState createdState = null;

        final RegistrationState previousState = bean.getRegistration().getStateInDate(bean.getStateDateTime());
        if (previousState == null) {
            createdState =
                    RegistrationState.createState(bean.getRegistration(), null, bean.getStateDateTime(), bean.getStateType());
        } else {
            createdState = (RegistrationState) StateMachine.execute(previousState, bean);
        }
        createdState.setRemarks(bean.getRemarks());

        final RegistrationState nextState = createdState.getNext();
        if (nextState != null && !createdState.getValidNextStates().contains(nextState.getStateType().getCode())) {
            throw new DomainException("error.cannot.add.registrationState.incoherentState");
        }
        org.fenixedu.academic.domain.student.RegistrationStateLog.createRegistrationStateLog(bean.getRegistration(),
                Bundle.MESSAGING, "log.registration.registrationstate.added", bean.getStateType().getDescription().getContent(),
                bean.getRemarks());
        return createdState;
    }

    public boolean isActive() {
        //TODO ACDM-1113 Remove Hack
        if (getStateType() == null) return true;
        return getStateType().isActive();
    }

    public RegistrationStateType getOldStateType() {
        return null;
    }

    public boolean includes(final ExternalEnrolment externalEnrolment) {
        if (getStateType().equals(RegistrationStateSystem.getInstance().getMobilityState())) {
            final DateTime mobilityDate = getStateDate();
            return externalEnrolment.hasExecutionPeriod() && externalEnrolment.getExecutionYear().containsDate(mobilityDate);
        }

        throw new DomainException("RegistrationState.external.enrolments.only.included.in.mobility.states");
    }

    static public boolean hasAnyState(final Collection<RegistrationState> states, final Collection<RegistrationStateTypeNew> types) {
        return states.stream().anyMatch(state -> types.contains(state.getStateType()));
    }

    @Override
    public Set<String> getValidNextStates() {
        return new HashSet<>();
    }
}
