package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.bennu.core.domain.Bennu;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class RegistrationStateSystem extends RegistrationStateSystem_Base {

    private RegistrationStateSystem() {
        super();
        setRoot(Bennu.getInstance());
    }


//    @Atomic
    public static RegistrationStateSystem getInstance() {
        if (Bennu.getInstance().getRegistrationStateSystem() == null) {
            return new RegistrationStateSystem();
        }
        return Bennu.getInstance().getRegistrationStateSystem();
    }

}
