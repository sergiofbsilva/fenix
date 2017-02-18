package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class TransitionalState extends TransitionalState_Base {
    
    public TransitionalState() {
        super();
    }


    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.TRANSITION;
    }
}
