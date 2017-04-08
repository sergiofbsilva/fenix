package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.conditions.RegistrationStateCondition;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.UserLoginPeriod;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RegistrationStateSystem extends RegistrationStateSystem_Base {

    private static Map<String, RegistrationStateTypeInterface> interfaceMap;

    private static final Map<Class<? extends RegistrationStateCondition>, RegistrationStateCondition> conditionsMap = new
            HashMap<>();

    @Atomic(mode = TxMode.READ)
    private static synchronized void initConditionsMap() {
        if (!conditionsMap.isEmpty()) {
            return;
        }
        
        getInstance().getRegistrationStateTypeSet().stream().flatMap(stateType -> Stream.concat(stateType.getPreConditionClasses()
                .stream(), stateType.getPostConditionClasses().stream())).distinct().forEach(conditionClass -> {
            try {
                conditionsMap.put(conditionClass, conditionClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static synchronized void initialize() {
        initInterfaceMap();
        initConditionsMap();
    }

    protected static void initInterfaceMap() {
        interfaceMap = new HashMap<>();
        interfaceMap.put(getInstance().getConcludedState().getCode(), new RegistrationStateTypeInterface() {
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
                state.getRegistration().getPerson().getUser().openLoginPeriod();
            }
        });
        interfaceMap.put("STUDYPLANCONCLUDED", new RegistrationStateTypeInterface() {
            @Override
            public void init(RegistrationState state) {
                state.getRegistration().getPerson().getUser().openLoginPeriod();
            }
        });
    }

    private RegistrationStateSystem() {
        setRoot(Bennu.getInstance());
    }


    public RegistrationStateTypeInterface getInterface(RegistrationState state) {
        return interfaceMap.getOrDefault(state.getStateType().getCode(), new RegistrationStateTypeInterface() {});
    }

    @Atomic(mode = TxMode.WRITE)
    private static synchronized void init() {
        if (Bennu.getInstance().getRegistrationStateSystem() == null) {
            new RegistrationStateSystem();
        }
    }

    public static RegistrationStateSystem getInstance() {
        if (Bennu.getInstance().getRegistrationStateSystem() == null) {
            init();
        }
        return Bennu.getInstance().getRegistrationStateSystem();
    }

    public static RegistrationStateCondition getCondition(Class<? extends RegistrationStateCondition> stateConditionClass) {
        return conditionsMap.get(stateConditionClass);
    }

}
