package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class RegisteredState extends RegisteredState_Base {
    
    public RegisteredState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.REGISTERED;
    }
}
