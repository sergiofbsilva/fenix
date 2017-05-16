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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.conditions.RegistrationStateCondition;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.predicate.RegistrationStatePredicates;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 *
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 *
 */
public class RegistrationState extends RegistrationState_Base {

    public static Comparator<RegistrationState> DATE_COMPARATOR = new Comparator<RegistrationState>() {
        @Override
        public int compare(RegistrationState leftState, RegistrationState rightState) {
            int comparationResult = leftState.getStateDate().compareTo(rightState.getStateDate());
            return (comparationResult == 0) ? leftState.getExternalId().compareTo(rightState.getExternalId()) : comparationResult;
        }
    };

    private RegistrationState(Registration registration, Person responsiblePerson, DateTime stateDate, RegistrationStateType
            stateType, String remarks) {
        setRootDomainObject(Bennu.getInstance());
        setStateDate(stateDate != null ? stateDate : new DateTime());
        setRegistration(registration);
        setResponsiblePerson(responsiblePerson != null ? responsiblePerson : AccessControl.getPerson());
        setRegistrationStateType(stateType);
        setRemarks(remarks);
        RegistrationStateSystem.getInstance().getInterface(this).init(this);
    }

    private RegistrationState(RegistrationStateBean bean) {
        this(bean.getRegistration(), bean.getResponsible(), bean.getCreated(), bean.getStateType(), bean.getRemarks());
    }

    public RegistrationStateType getStateType() {
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
                && !previousState.getValidNextStateTypes().contains(nextState.getStateType())) {
            throw new DomainException("error.cannot.delete.registrationState.incoherentState: "
                    + previousState.getStateType().getName() + " -> " + nextState.getStateType().getName());
        }
        RegistrationStateSystem.getInstance().getInterface(this).checkRulesToDelete(this);
        deleteWithoutCheckRules();
    }

    public void deleteWithoutCheckRules() {
        final Registration registration = getRegistration();
        try {
            createLog(getRegistration(), "log.registration.registrationstate.removed", getStateType().getName().getContent(),
                    getRemarks());
            setRegistration(null);
            setResponsiblePerson(null);
            setRootDomainObject(null);
            setRegistrationStateType(null);
            super.deleteDomainObject();
        } finally {
            registration.getStudent().updateStudentRole();
        }
    }

    public RegistrationState getNext() {
        List<RegistrationState> sortedRegistrationsStates = getSortedRegistrationStates();

        int i = sortedRegistrationsStates.indexOf(this);
        if (i != -1 && i < sortedRegistrationsStates.size() - 1) {
            return sortedRegistrationsStates.get(i + 1);
        }
        return null;
    }

    public RegistrationState getPrevious() {
        List<RegistrationState> sortedRegistrationsStates = getSortedRegistrationStates();
        int i = sortedRegistrationsStates.indexOf(this);
        if (i != -1 && i > 0) {
            return sortedRegistrationsStates.get(i - 1);
        }
        return null;
    }

    private List<RegistrationState> getSortedRegistrationStates() {
        List<RegistrationState> sortedRegistrationsStates =
                new ArrayList<>(getRegistration().getRegistrationStatesSet());
        sortedRegistrationsStates.sort(DATE_COMPARATOR);
        return sortedRegistrationsStates;
    }

    public DateTime getEndDate() {
        RegistrationState state = getNext();
        return (state != null) ? state.getStateDate() : null;
    }

    public void setStateDate(YearMonthDay yearMonthDay) {
        super.setStateDate(yearMonthDay.toDateTimeAtMidnight());
    }

    @Atomic(mode = TxMode.WRITE)
    public static RegistrationState createRegistrationState(Registration registration, Person responsible, DateTime creation,
                                                            RegistrationStateType stateType, String remarks) {
        return new RegistrationState(registration, responsible, creation, stateType, remarks);
    }

    public static RegistrationState createRegistrationState(Registration registration, Person responsible, DateTime creation,
            RegistrationStateType stateType) {
        return new RegistrationState(registration, responsible, creation, stateType, null);
    }

    private static void checkTransitionConditions(RegistrationState from, RegistrationStateBean to,
            Supplier<List<Class<? extends RegistrationStateCondition>>> conditionsSupplier) {

        List<String> blockers = new ArrayList<>();

        for (Class<? extends RegistrationStateCondition> conditionClass : conditionsSupplier.get()) {
            RegistrationStateCondition registrationStateCondition = RegistrationStateSystem.getCondition(conditionClass);
            blockers.addAll(registrationStateCondition.getTransitionBlockers(from, to));
        }

        DomainException.throwWhenDeleteBlocked(blockers);
    }

    /**
     * Check {@link to} pre conditions when arriving at state
     * @param from the origin state
     * @param to the destination state
     */

    private static void checkTransitionPreConditions(RegistrationState from, RegistrationStateBean to) {
        checkTransitionConditions(from, to, () -> to.getStateType().getPreConditionClasses());
    }

    /**
     * Check {@link from} post conditions when leaving the state
     * @param from the origin state
     * @param to the destination state
     */
    private static void checkTransitionPostConditions(RegistrationState from, RegistrationStateBean to) {
        checkTransitionConditions(from, to, () -> from.getStateType().getPostConditionClasses());
    }
    

    @Atomic(mode = TxMode.WRITE)
    public static RegistrationState createRegistrationState(RegistrationStateBean bean) {

        final RegistrationState previousState = bean.getRegistration().getStateInDate(bean.getCreated());
        if (previousState != null) {
            checkTransitionPostConditions(previousState, bean);
        }
        checkTransitionPreConditions(previousState, bean);
        RegistrationState createdState = new RegistrationState(bean);
        final RegistrationState nextState = createdState.getNext();
        if (nextState != null) {
            RegistrationStateBean nextStateBean = new RegistrationStateBean(nextState);
            checkTransitionPostConditions(createdState, nextStateBean);
            checkTransitionPreConditions(createdState, nextStateBean);
        }

        bean.getRegistration().getStudent().updateStudentRole();

        createLog(bean.getRegistration(), "log.registration.registrationstate.added",
                bean.getStateType().getName().getContent(), bean.getRemarks());


        return createdState;
    }

    protected static void createLog(Registration registration, String key, String content, String remarks) {
        org.fenixedu.academic.domain.student.RegistrationStateLog
                .createRegistrationStateLog(registration, Bundle.MESSAGING, key, content, remarks);
    }

    public boolean isActive() {
        return getStateType().getActive();
    }

    public Set<RegistrationStateType> getValidNextStateTypes() {
        return getStateType().getValidNextStateTypeSet();
    }

    public boolean isValidNextStateType(RegistrationStateType stateType) {
        return getValidNextStateTypes().contains(stateType);
    }
}
