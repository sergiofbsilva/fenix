package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.UserLoginPeriod;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import java.util.HashMap;
import java.util.Map;

public class RegistrationStateSystem extends RegistrationStateSystem_Base {

    private Map<String, RegistrationStateTypeInterface> interfaceMap;

    {
        interfaceMap = new HashMap<>();
        interfaceMap.put(this.getConcludedState().getCode(), new RegistrationStateTypeInterface() {
            @Override
            public void checkRulesToDelete(RegistrationState state) {
                final Person person = AccessControl.getPerson();
                if (AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.REPEAT_CONCLUSION_PROCESS, state.getRegistration()
                        .getDegree(), person.getUser())) {
                    return;
                }

                if (!state.getRegistration().getSucessfullyFinishedDocumentRequests(DocumentRequestType.DEGREE_FINALIZATION_CERTIFICATE)
                        .isEmpty()) {
                    throw new DomainException("cannot.delete.concluded.state.of.registration.with.concluded.degree.finalization.request");
                }

                if (!state.getRegistration().getSucessfullyFinishedDocumentRequests(DocumentRequestType.DIPLOMA_REQUEST).isEmpty()) {
                    throw new DomainException("cannot.delete.concluded.state.of.registration.with.concluded.diploma.request");
                }
            }

            @Override
            public void init(RegistrationState state) {
                if (state.getRegistration().isBolonha() && !state.getRegistration().hasConcluded()) {
                    throw new DomainException("error.registration.is.not.concluded");
                }
                UserLoginPeriod.createOpenPeriod(state.getRegistration().getPerson().getUser());
            }
        });
        interfaceMap.put("STUDYPLANCONCLUDED", new RegistrationStateTypeInterface() {
            @Override
            public void init(RegistrationState state) {
                UserLoginPeriod.createOpenPeriod(state.getRegistration().getPerson().getUser());
            }
        });
    }

    private RegistrationStateSystem() {
        super();
        setRoot(Bennu.getInstance());
    }


    public RegistrationStateTypeInterface getInterface(RegistrationState state) {
        return interfaceMap.getOrDefault(state.getStateType().getCode(), new RegistrationStateTypeInterface() {});
    }

//    @Atomic
    public static RegistrationStateSystem getInstance() {
        if (Bennu.getInstance().getRegistrationStateSystem() == null) {
            return new RegistrationStateSystem();
        }
        return Bennu.getInstance().getRegistrationStateSystem();
    }

}
