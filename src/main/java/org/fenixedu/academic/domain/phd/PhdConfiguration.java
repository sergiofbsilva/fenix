package org.fenixedu.academic.domain.phd;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.FenixFramework;

public class PhdConfiguration extends PhdConfiguration_Base {

    private PhdConfiguration() {
        setBennu(Bennu.getInstance());
    }

    public static PhdConfiguration getInstance() {
        if (Bennu.getInstance().getPhdConfiguration() == null) {
            FenixFramework.atomic(() -> {
                if (Bennu.getInstance().getPhdConfiguration() == null) {
                    new PhdConfiguration();
                }
            });
        }
        return Bennu.getInstance().getPhdConfiguration();
    }

}
