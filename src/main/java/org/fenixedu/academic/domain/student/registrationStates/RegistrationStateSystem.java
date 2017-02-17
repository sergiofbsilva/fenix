package org.fenixedu.academic.domain.student.registrationStates;

import org.fenixedu.bennu.core.domain.Bennu;
import pt.ist.fenixframework.FenixFramework;

public class RegistrationStateSystem extends RegistrationStateSystem_Base {

    private RegistrationStateSystem() {
        super();
        setRoot(Bennu.getInstance());
    }


    public static RegistrationStateSystem getInstance() {
        if (FenixFramework.getDomainRoot().getBennu() == null) {
            return new RegistrationStateSystem();
        }
        return Bennu.getInstance().getRegistrationStateSystem();
    }

}
