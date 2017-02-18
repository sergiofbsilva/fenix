package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class InterruptedState extends InterruptedState_Base {
    
    public InterruptedState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.INTERRUPTED;
    }
}
