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
package org.fenixedu.academic.dto.student;

import java.io.Serializable;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.util.workflow.StateBean;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class RegistrationStateBean implements Serializable {

    private Person responsible;

    private DateTime created;

    private Registration registration;

    private RegistrationStateType stateType;

    private String remarks;


    public RegistrationStateBean(RegistrationState state) {
        responsible = state.getResponsiblePerson();
        created = state.getStateDate();
        registration = state.getRegistration();
        stateType = state.getStateType();
        remarks = state.getRemarks();
    }
    
    public RegistrationStateBean(Registration registration) {
        super();
        setRegistration(registration);
    }

    public RegistrationStateBean(final RegistrationStateType type) {
        setStateType(type);
    }

    public Registration getRegistration() {
        return registration;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setStateType(final RegistrationStateType stateType) {
        this.stateType = stateType;
    }

    public RegistrationStateType getStateType() {
        return stateType;
    }

    public Person getResponsible() {
        return responsible;
    }

    public void setResponsible(Person responsible) {
        this.responsible = responsible;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }
}
