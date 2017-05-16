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

import org.apache.noggit.JSONUtil;
import org.fenixedu.academic.domain.student.registrationStates.conditions.RegistrationStateCondition;
import org.fenixedu.academic.domain.util.workflow.IState;
import org.fenixedu.academic.domain.util.workflow.StateBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 *
 */

public class RegistrationStateType extends RegistrationStateType_Base {

    public RegistrationStateType(String code, LocalizedString name, boolean active, boolean student, boolean terminal) {
        setCode(code);
        setName(name);
        setActive(active);
        setStudent(student);
        setTerminal(terminal);
        setPreConditions(new JsonArray());
        setPostConditions(new JsonArray());
    }

    public Set<String> getValidNextStates() {
        return getValidNextStateTypeSet().stream().map(RegistrationStateType::getCode).collect(Collectors.toSet());
    }

    public boolean isCanceled() {
        return getCanceled();
    }
    
    public boolean isActive() {
        return getActive();
    }

    public boolean isStudent() {
        return getStudent();
    }

    public boolean isTerminal() {
        return getTerminal();
    }

    public boolean isReingressable() {
        final RegistrationStateType initialState = RegistrationStateSystem.getInstance().getInitialState();
        return getValidNextStateTypeSet().stream().anyMatch(s -> s.equals(initialState));
    }

    public boolean isValidSourceLink() {
        return getValidSource();
    }

    public boolean isToForceGratuityCreation() {
        return getForceGratuityCreation();
    }

    public boolean isInactive() {
        return !getActive();
    }

    public String getQualifiedName() {
        return getName().getContent();
    }

    public String getFullyQualifiedName() {
       return getName().getContent();
    }

    public List<Class<? extends RegistrationStateCondition>> getPreConditionClasses() {
        return parseConditionClasses(super::getPreConditions);
    }

    public void setPreConditionClasses(List<Class<? extends RegistrationStateCondition>> classes) {
        setPreConditions(serializeConditionClasses(classes));
    }

    public List<Class<? extends RegistrationStateCondition>> getPostConditionClasses() {
        return parseConditionClasses(super::getPostConditions);
    }

    public void setPostConditionClasses(List<Class<? extends RegistrationStateCondition>> classes) {
        setPostConditions(serializeConditionClasses(classes));
    }

    private Class<? extends RegistrationStateCondition> forName(String className) {
        try {
            return (Class<? extends RegistrationStateCondition>) Class.forName(className);
        }catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Class<? extends RegistrationStateCondition>> parseConditionClasses(Supplier<JsonElement> supplier) {
        return StreamUtils.of(supplier.get().getAsJsonArray()).map(JsonElement::getAsString).map(this::forName).collect(Collectors
                .toList());
    }

    private JsonElement serializeConditionClasses(List<Class<? extends RegistrationStateCondition>> classes) {
        return classes.stream().map(Class::getName).map(JsonPrimitive::new).collect(StreamUtils.toJsonArray());
    }
}
