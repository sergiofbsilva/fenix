package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class MobilityState extends MobilityState_Base {
    
    public MobilityState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.MOBILITY;
    }
}
