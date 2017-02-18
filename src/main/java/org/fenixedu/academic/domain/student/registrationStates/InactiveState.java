package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class InactiveState extends InactiveState_Base {
    
    public InactiveState() {
        super();
    }


    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.INACTIVE;
    }
}
