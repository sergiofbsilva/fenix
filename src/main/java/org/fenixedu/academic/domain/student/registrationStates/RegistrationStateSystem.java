package org.fenixedu.academic.domain.student.registrationStates;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.student.registrationStates.conditions.RegistrationStateCondition;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class RegistrationStateSystem extends RegistrationStateSystem_Base {

    private static Map<String, RegistrationStateTypeInterface> interfaceMap;

    private static final Map<Class<? extends RegistrationStateCondition>, RegistrationStateCondition> conditionsMap =
            new ConcurrentHashMap<>();

    public static synchronized void initialize() {
        initInterfaceMap();
        initConditionsMap();
    }

    protected static void initInterfaceMap() {
        interfaceMap = new HashMap<>();
        interfaceMap.put("CONCLUDED", new RegistrationStateTypeInterface() {
            @Override
            public void checkRulesToDelete(RegistrationState state) {
                final Person person = AccessControl.getPerson();
                if (AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.REPEAT_CONCLUSION_PROCESS,
                        state.getRegistration().getDegree(), person.getUser())) {
                    return;
                }

                if (!state.getRegistration()
                        .getSucessfullyFinishedDocumentRequests(DocumentRequestType.DEGREE_FINALIZATION_CERTIFICATE).isEmpty()) {
                    throw new DomainException(
                            "cannot.delete.concluded.state.of.registration.with.concluded.degree.finalization.request");
                }

                if (!state.getRegistration().getSucessfullyFinishedDocumentRequests(DocumentRequestType.DIPLOMA_REQUEST)
                        .isEmpty()) {
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
        return interfaceMap.getOrDefault(state.getStateType().getCode(), new RegistrationStateTypeInterface() {
        });
    }

    public static RegistrationStateSystem getInstance() {
        if (Bennu.getInstance().getRegistrationStateSystem() == null) {
            FenixFramework.atomic(() -> {
                if (Bennu.getInstance().getRegistrationStateSystem() == null) {
                    new RegistrationStateSystem();
                }
            });
        }
        return Bennu.getInstance().getRegistrationStateSystem();
    }

    public static RegistrationStateCondition getCondition(Class<? extends RegistrationStateCondition> stateConditionClass) {
        return conditionsMap.computeIfAbsent(stateConditionClass, conditionClass -> {
            try {
                return conditionClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Atomic(mode = TxMode.READ)
    private static synchronized void initConditionsMap() {
        if (!conditionsMap.isEmpty()) {
            return;
        }

        getInstance()
                .getRegistrationStateTypeSet().stream().flatMap(stateType -> Stream
                .concat(stateType.getPreConditionClasses().stream(), stateType.getPostConditionClasses().stream()))
                .distinct().forEach(RegistrationStateSystem::getCondition);
    }

    public Optional<RegistrationStateType> findByCode(String code) {
        return getRegistrationStateTypeSet().stream().filter(rst -> code.equals(rst.getCode())).findAny();
    }
}
