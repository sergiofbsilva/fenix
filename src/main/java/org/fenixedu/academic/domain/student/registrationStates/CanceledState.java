package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class CanceledState extends CanceledState_Base {
    
    public CanceledState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.CANCELED;
    }
}
