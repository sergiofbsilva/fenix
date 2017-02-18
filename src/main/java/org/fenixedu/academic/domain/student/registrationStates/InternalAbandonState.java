package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class InternalAbandonState extends InternalAbandonState_Base {
    
    public InternalAbandonState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.INTERNAL_ABANDON;
    }
}
