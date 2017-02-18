package org.fenixedu.academic.domain.student.registrationStates;

@Deprecated
public class SchoolPartConcludedState extends SchoolPartConcludedState_Base {
    
    public SchoolPartConcludedState() {
        super();
    }

    @Override
    public RegistrationStateType getOldStateType() {
        return RegistrationStateType.SCHOOLPARTCONCLUDED;
    }
}
