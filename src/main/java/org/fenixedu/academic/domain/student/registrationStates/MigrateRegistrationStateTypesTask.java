package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

/**
 * Created by Fernando on 18/02/2017.
 */
public class MigrateRegistrationStateTypesTask extends CustomTask {

    private RegistrationStateTypeNew canceled;
    private RegistrationStateTypeNew concluded;
    private RegistrationStateTypeNew externalAbandon;
    private RegistrationStateTypeNew flunked;
    private RegistrationStateTypeNew inactive;
    private RegistrationStateTypeNew internalAbandon;
    private RegistrationStateTypeNew interrupted;
    private RegistrationStateTypeNew mobility;
    private RegistrationStateTypeNew registered;
    private RegistrationStateTypeNew schoolPartConcluded;
    private RegistrationStateTypeNew studyPlanConcluded;
    private RegistrationStateTypeNew transited;
    private RegistrationStateTypeNew transition;

    @Override
    public void runTask() throws Exception {
        configureNewRegistrationStateTypes();
        transitionAllRegistrationStateTypes();
    }

    private void transitionAllRegistrationStateTypes() {
        Bennu.getInstance().getRegistrationsSet()
            .forEach(registration -> registration.getRegistrationStateSet()
                .forEach(registrationState -> {
                    RegistrationStateType type = registrationState.getOldStateType();
                    switch (type) {
                        case REGISTERED:
                            registrationState.setRegistrationStateType(registered);
                            break;
                        case CANCELED:
                            registrationState.setRegistrationStateType(canceled);
                            break;
                        case CONCLUDED:
                            registrationState.setRegistrationStateType(concluded);
                            break;
                        case FLUNKED:
                            registrationState.setRegistrationStateType(flunked);
                            break;
                        case INTERRUPTED:
                            registrationState.setRegistrationStateType(interrupted);
                            break;
                        case SCHOOLPARTCONCLUDED:
                            registrationState.setRegistrationStateType(schoolPartConcluded);
                            break;
                        case STUDYPLANCONCLUDED:
                            registrationState.setRegistrationStateType(studyPlanConcluded);
                            break;
                        case INTERNAL_ABANDON:
                            registrationState.setRegistrationStateType(internalAbandon);
                            break;
                        case EXTERNAL_ABANDON:
                            registrationState.setRegistrationStateType(externalAbandon);
                            break;
                        case MOBILITY:
                            registrationState.setRegistrationStateType(mobility);
                            break;
                        case TRANSITION:
                            registrationState.setRegistrationStateType(transition);
                            break;
                        case TRANSITED:
                            registrationState.setRegistrationStateType(transited);
                            break;
                        case INACTIVE:
                            registrationState.setRegistrationStateType(inactive);
                            break;
                    }}));
    }

    private void configureNewRegistrationStateTypes() {
        RegistrationStateSystem system = RegistrationStateSystem.getInstance();

        createNewStateTypes();
        configureNextStateTypes();
    }

    private void createNewStateTypes() {
        canceled = new RegistrationStateTypeNew("CANCELED", null, null, null, null, null);
        concluded = new RegistrationStateTypeNew("CONCLUDED", null, null, null, null, null);
        externalAbandon = new RegistrationStateTypeNew("EXTERNAL_ABANDON", null, null, null, null, null);
        flunked = new RegistrationStateTypeNew("FLUNKED", null, null, null, null, null);
        inactive = new RegistrationStateTypeNew("INACTIVE", null, null, null, null, null);
        internalAbandon = new RegistrationStateTypeNew("INTERNAL_ABANDON", null, null, null, null, null);
        interrupted = new RegistrationStateTypeNew("INTERRUPTED", null, null, null, null, null);
        mobility = new RegistrationStateTypeNew("MOBILITY", null, null, null, null, null);
        registered = new RegistrationStateTypeNew("REGISTERED", null, null, null, null, null);
        schoolPartConcluded = new RegistrationStateTypeNew("SCHOOLPARTCONCLUDED", null, null, null, null, null);
        studyPlanConcluded = new RegistrationStateTypeNew("STUDYPLANCONCLUDED", null, null, null, null, null);
        transited = new RegistrationStateTypeNew("TRANSITED", null, null, null, null, null);
        transition = new RegistrationStateTypeNew("TRANSITION", null, null, null, null, null);
    }

    private void configureNextStateTypes() {
        canceled.addValidNextStatesType(registered);

        externalAbandon.addValidNextStatesType(registered);

        flunked.addValidNextStatesType(canceled);
        flunked.addValidNextStatesType(registered);
        flunked.addValidNextStatesType(internalAbandon);
        flunked.addValidNextStatesType(externalAbandon);
        flunked.addValidNextStatesType(mobility);

        inactive.addValidNextStatesType(registered);

        internalAbandon.addValidNextStatesType(registered);

        interrupted.addValidNextStatesType(canceled);
        interrupted.addValidNextStatesType(registered);
        interrupted.addValidNextStatesType(internalAbandon);
        interrupted.addValidNextStatesType(externalAbandon);
        interrupted.addValidNextStatesType(mobility);
        interrupted.addValidNextStatesType(schoolPartConcluded);

        mobility.addValidNextStatesType(concluded);
        mobility.addValidNextStatesType(studyPlanConcluded);
        mobility.addValidNextStatesType(schoolPartConcluded);
        mobility.addValidNextStatesType(registered);
        mobility.addValidNextStatesType(canceled);
        mobility.addValidNextStatesType(interrupted);
        mobility.addValidNextStatesType(flunked);
        mobility.addValidNextStatesType(internalAbandon);
        mobility.addValidNextStatesType(externalAbandon);

        registered.addValidNextStatesType(concluded);
        registered.addValidNextStatesType(studyPlanConcluded);
        registered.addValidNextStatesType(schoolPartConcluded);
        registered.addValidNextStatesType(canceled);
        registered.addValidNextStatesType(interrupted);
        registered.addValidNextStatesType(flunked);
        registered.addValidNextStatesType(internalAbandon);
        registered.addValidNextStatesType(externalAbandon);
        registered.addValidNextStatesType(mobility);
        registered.addValidNextStatesType(transition);
        registered.addValidNextStatesType(transited);
        registered.addValidNextStatesType(inactive);

        schoolPartConcluded.setDefaultNextStateType(concluded);
        schoolPartConcluded.addValidNextStatesType(concluded);
        schoolPartConcluded.addValidNextStatesType(studyPlanConcluded);
        schoolPartConcluded.addValidNextStatesType(canceled);
        schoolPartConcluded.addValidNextStatesType(internalAbandon);
        schoolPartConcluded.addValidNextStatesType(externalAbandon);
        schoolPartConcluded.addValidNextStatesType(mobility);
        schoolPartConcluded.addValidNextStatesType(interrupted);

        transition.addValidNextStatesType(transited);
        transition.addValidNextStatesType(canceled);
        transition.addValidNextStatesType(registered);
        transition.addValidNextStatesType(concluded);
    }


}
