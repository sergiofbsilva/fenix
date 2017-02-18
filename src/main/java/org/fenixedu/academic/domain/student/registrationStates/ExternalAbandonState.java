package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class ExternalAbandonState extends ExternalAbandonState_Base {
    
    public ExternalAbandonState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.EXTERNAL_ABANDON;
    }
}
