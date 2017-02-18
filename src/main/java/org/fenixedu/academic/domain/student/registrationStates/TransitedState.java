package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class TransitedState extends TransitedState_Base {
    
    public TransitedState() {
        super();
    }


    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.TRANSITED;
    }
}
