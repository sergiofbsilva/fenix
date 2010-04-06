package net.sourceforge.fenixedu.domain.accessControl;

import net.sourceforge.fenixedu.domain.ExecutionDegree;
import net.sourceforge.fenixedu.domain.degree.DegreeType;

public class ActiveCoordinatorIntegradedMasterDegreeGroup extends ActiveCoordinatorGroup {

    private static final long serialVersionUID = -1670838873686375271L;

    @Override
    protected boolean matches(final ExecutionDegree executionDegree) {
	final DegreeType degreeType = executionDegree.getDegreeType();
	return degreeType == DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE;
    }

}