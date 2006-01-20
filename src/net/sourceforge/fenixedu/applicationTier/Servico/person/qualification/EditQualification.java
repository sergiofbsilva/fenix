/*
 * Created on 11/Ago/2005 - 11:04:49
 * 
 */

package net.sourceforge.fenixedu.applicationTier.Servico.person.qualification;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.person.InfoQualification;
import net.sourceforge.fenixedu.domain.Country;
import net.sourceforge.fenixedu.domain.DomainFactory;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.Qualification;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentObject;

/**
 * @author Jo�o Fialho & Rita Ferreira
 *
 */
public class EditQualification extends Service {

    public void run(Integer qualificationId, InfoQualification infoQualification) throws FenixServiceException, ExcepcaoPersistencia {
		IPersistentObject po = persistentSupport.getIPersistentObject();
		
		Qualification qualification = (Qualification) po.readByOID(Qualification.class, qualificationId);
		//If it doesn't exist in the database, a new one has to be created
		Country country = (Country) po.readByOID(Country.class, infoQualification.getInfoCountry().getIdInternal());
		if(qualification == null) {
			Person person = (Person) po.readByOID(Person.class, infoQualification.getInfoPerson().getIdInternal());
			qualification = DomainFactory.makeQualification(person, country, infoQualification);
		
		} else {
			qualification.edit(infoQualification, country);
		}
		
		
    }

}
