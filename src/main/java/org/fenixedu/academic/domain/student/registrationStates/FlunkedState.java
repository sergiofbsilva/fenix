package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class FlunkedState extends FlunkedState_Base {
    
    public FlunkedState() {
        super();
    }


    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.FLUNKED;
    }
}
