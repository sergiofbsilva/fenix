package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

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
    private RegistrationStateSystem system;

    @Override
    public void runTask() throws Exception {
        configureNewRegistrationStateTypes();
        transitionAllRegistrationStateTypes();
    }

    private void transitionAllRegistrationStateTypes() {
        Bennu.getInstance().getRegistrationsSet()
            .forEach(registration -> registration.getRegistrationStatesSet()
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
        system = RegistrationStateSystem.getInstance();

        createNewStateTypes();
        configureNextStateTypes();
    }

    private void createNewStateTypes() {
        canceled = new RegistrationStateTypeNew("CANCELED", new LocalizedString(), new LocalizedString(), null, null, null);
        concluded = new RegistrationStateTypeNew("CONCLUDED", new LocalizedString(), new LocalizedString(), null, null, null);
        externalAbandon = new RegistrationStateTypeNew("EXTERNAL_ABANDON", new LocalizedString(), new LocalizedString(), null, null, null);
        flunked = new RegistrationStateTypeNew("FLUNKED", new LocalizedString(), new LocalizedString(), null, null, null);
        inactive = new RegistrationStateTypeNew("INACTIVE", new LocalizedString(), new LocalizedString(), null, null, null);
        internalAbandon = new RegistrationStateTypeNew("INTERNAL_ABANDON", new LocalizedString(), new LocalizedString(), null, null, null);
        interrupted = new RegistrationStateTypeNew("INTERRUPTED", new LocalizedString(), new LocalizedString(), null, null, null);
        mobility = new RegistrationStateTypeNew("MOBILITY", new LocalizedString(), new LocalizedString(), null, null, null);
        registered = new RegistrationStateTypeNew("REGISTERED", new LocalizedString(), new LocalizedString(), null, null, null);
        schoolPartConcluded = new RegistrationStateTypeNew("SCHOOLPARTCONCLUDED", new LocalizedString(), new LocalizedString(), null, null, null);
        studyPlanConcluded = new RegistrationStateTypeNew("STUDYPLANCONCLUDED", new LocalizedString(), new LocalizedString(), null, null, null);
        transited = new RegistrationStateTypeNew("TRANSITED", new LocalizedString(), new LocalizedString(), null, null, null);
        transition = new RegistrationStateTypeNew("TRANSITION", new LocalizedString(), new LocalizedString(), null, null, null);

        system.addRegistrationStateType(canceled);
        system.addRegistrationStateType(registered);
        system.addRegistrationStateType(concluded);
        system.addRegistrationStateType(externalAbandon);
        system.addRegistrationStateType(flunked);
        system.addRegistrationStateType(inactive);
        system.addRegistrationStateType(internalAbandon);
        system.addRegistrationStateType(interrupted);
        system.addRegistrationStateType(mobility);
        system.addRegistrationStateType(transited);
        system.addRegistrationStateType(transition);
        system.addRegistrationStateType(schoolPartConcluded);
        system.addRegistrationStateType(studyPlanConcluded);

        system.setCanceledState(canceled);
        system.setInitialState(registered);
        system.setConcludedState(concluded);
        system.setExternalAbandonState(externalAbandon);
        system.setFlunkedState(flunked);
        system.setInactiveState(inactive);
        system.setInternalAbandonState(internalAbandon);
        system.setInterruptedState(interrupted);
        system.setMobilityState(mobility);
        system.setTransitedState(transited);
        system.setTransitionState(transition);
        system.setSchoolPartConcludedState(schoolPartConcluded);
        system.setStudyPlanConcludedState(studyPlanConcluded);
    }

    private void configureNextStateTypes() {

        canceled = system.getCanceledState();
        registered = system.getInitialState();
        concluded = system.getConcludedState();
        externalAbandon = system.getExternalAbandonState();
        flunked = system.getFlunkedState();
        inactive = system.getInactiveState();
        internalAbandon = system.getInternalAbandonState();
        interrupted = system.getInterruptedState();
        mobility = system.getMobilityState();
        transited = system.getTransitedState();
        transition = system.getTransitionState();
        schoolPartConcluded = system.getSchoolPartConcludedState();
        studyPlanConcluded = system.getStudyPlanConcludedState();


        canceled.addValidNextStateType(registered);

        externalAbandon.addValidNextStateType(registered);

        flunked.addValidNextStateType(canceled);
//        flunked.addValidNextStateType(mobility);
//        flunked.addValidNextStateType(internalAbandon);
//        flunked.addValidNextStateType(registered);
//        flunked.addValidNextStateType(externalAbandon);

        inactive.addValidNextStateType(registered);

        internalAbandon.addValidNextStateType(registered);

        interrupted.addValidNextStateType(canceled);
//        interrupted.addValidNextStateType(registered);
//        interrupted.addValidNextStateType(internalAbandon);
//        interrupted.addValidNextStateType(externalAbandon);
//        interrupted.addValidNextStateType(mobility);
//        interrupted.addValidNextStateType(schoolPartConcluded);

        mobility.addValidNextStateType(concluded);
//        mobility.addValidNextStateType(studyPlanConcluded);
//        mobility.addValidNextStateType(schoolPartConcluded);
//        mobility.addValidNextStateType(registered);
//        mobility.addValidNextStateType(canceled);
//        mobility.addValidNextStateType(interrupted);
//        mobility.addValidNextStateType(flunked);
//        mobility.addValidNextStateType(internalAbandon);
//        mobility.addValidNextStateType(externalAbandon);

        registered.addValidNextStateType(concluded);
//        registered.addValidNextStateType(studyPlanConcluded);
//        registered.addValidNextStateType(schoolPartConcluded);
//        registered.addValidNextStateType(canceled);
//        registered.addValidNextStateType(interrupted);
//        registered.addValidNextStateType(flunked);
//        registered.addValidNextStateType(internalAbandon);
//        registered.addValidNextStateType(externalAbandon);
//        registered.addValidNextStateType(mobility);
//        registered.addValidNextStateType(transition);
//        registered.addValidNextStateType(transited);
//        registered.addValidNextStateType(inactive);

        schoolPartConcluded.setDefaultNextStateType(concluded);
//        schoolPartConcluded.addValidNextStateType(concluded);
//        schoolPartConcluded.addValidNextStateType(studyPlanConcluded);
//        schoolPartConcluded.addValidNextStateType(canceled);
//        schoolPartConcluded.addValidNextStateType(internalAbandon);
//        schoolPartConcluded.addValidNextStateType(externalAbandon);
//        schoolPartConcluded.addValidNextStateType(mobility);
//        schoolPartConcluded.addValidNextStateType(interrupted);

        transition.addValidNextStateType(transited);
//        transition.addValidNextStateType(canceled);
//        transition.addValidNextStateType(registered);
//        transition.addValidNextStateType(concluded);
    }


}
