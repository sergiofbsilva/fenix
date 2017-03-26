package org.fenixedu.academic.domain.student.registrationStates;

/**
 * Created by Fernando on 18-03-2017.
 */
public interface RegistrationStateTypeInterface {
    default void checkRulesToDelete(RegistrationState state) {
        // Default implementations are NOPS
    }
    default void init(RegistrationState state) {
        // Default implementations are NOPS
    }
}
