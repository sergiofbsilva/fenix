package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.thesis;

import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoMasterDegreeProofVersion;
import net.sourceforge.fenixedu.dataTransferObject.InfoStudentCurricularPlan;
import net.sourceforge.fenixedu.domain.MasterDegreeProofVersion;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * 
 * @author - Shezad Anavarali (sana@mega.ist.utl.pt) - Nadir Tarmahomed
 *         (naat@mega.ist.utl.pt)
 * 
 */
public class ReadNonActivesMasterDegreeProofVersionsByStudentCurricularPlan extends Service {

    public List run(InfoStudentCurricularPlan infoStudentCurricularPlan) throws FenixServiceException,
            ExcepcaoPersistencia {
        List masterDegreeProofVersions = persistentSupport.getIPersistentMasterDegreeProofVersion()
                .readNotActiveByStudentCurricularPlan(infoStudentCurricularPlan.getIdInternal());

        CollectionUtils.transform(masterDegreeProofVersions, new Transformer() {

            public Object transform(Object arg0) {
                MasterDegreeProofVersion masterDegreeProofVersion = (MasterDegreeProofVersion) arg0;
                return InfoMasterDegreeProofVersion.newInfoFromDomain(masterDegreeProofVersion);
            }

        });

        return masterDegreeProofVersions;
    }
}