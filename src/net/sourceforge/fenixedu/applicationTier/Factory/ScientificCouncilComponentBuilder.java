/*
 * Created on 23/Jul/2003
 *
 * 
 */
package net.sourceforge.fenixedu.applicationTier.Factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.dataTransferObject.ISiteComponent;
import net.sourceforge.fenixedu.dataTransferObject.InfoCurricularCourse;
import net.sourceforge.fenixedu.dataTransferObject.InfoDegree;
import net.sourceforge.fenixedu.dataTransferObject.InfoDegreeCurricularPlan;
import net.sourceforge.fenixedu.dataTransferObject.InfoDegreeCurricularPlanWithDegree;
import net.sourceforge.fenixedu.dataTransferObject.InfoSiteBasicCurricularCourses;
import net.sourceforge.fenixedu.dataTransferObject.InfoSiteCurricularCourses;
import net.sourceforge.fenixedu.dataTransferObject.InfoSiteDegreeCurricularPlans;
import net.sourceforge.fenixedu.dataTransferObject.InfoSiteSCDegrees;
import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.degree.degreeCurricularPlan.DegreeCurricularPlanState;
import net.sourceforge.fenixedu.domain.degreeStructure.CurricularStage;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ICursoPersistente;
import net.sourceforge.fenixedu.persistenceTier.IPersistentCurricularCourse;
import net.sourceforge.fenixedu.persistenceTier.IPersistentDegreeCurricularPlan;
import net.sourceforge.fenixedu.persistenceTier.IPersistentObject;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;

/**
 * @author Jo�o Mota
 * 
 * 23/Jul/2003 fenix-head ServidorAplicacao.Factory
 * 
 */
public class ScientificCouncilComponentBuilder {

	private static ScientificCouncilComponentBuilder instance = null;

	public ScientificCouncilComponentBuilder() {
	}

	public static ScientificCouncilComponentBuilder getInstance() {
		if (instance == null) {
			instance = new ScientificCouncilComponentBuilder();
		}
		return instance;
	}

	public ISiteComponent getComponent(ISiteComponent component, Integer degreeId,
			Integer curricularYear, Integer degreeCurricularPlanId) throws FenixServiceException,
			ExcepcaoPersistencia {
		if (component instanceof InfoSiteSCDegrees) {
			return getInfoSiteSCDegrees((InfoSiteSCDegrees) component);
		} else if (component instanceof InfoSiteDegreeCurricularPlans) {
			return getInfoSiteDegreeCurricularPlans((InfoSiteDegreeCurricularPlans) component, degreeId);
		} else if (component instanceof InfoSiteCurricularCourses) {
			return getInfoSiteCurricularCourses((InfoSiteCurricularCourses) component,
					degreeCurricularPlanId);
		} else if (component instanceof InfoSiteBasicCurricularCourses) {
			return getInfoSiteBasicCurricularCourses((InfoSiteBasicCurricularCourses) component,
					degreeCurricularPlanId);
		} else {
			return null;
		}

	}

	/**
	 * @param courses
	 * @param degreeCurricularPlanId
	 * @return
	 * @throws ExcepcaoPersistencia
	 */
	private ISiteComponent getInfoSiteBasicCurricularCourses(InfoSiteBasicCurricularCourses component,
			Integer degreeCurricularPlanId) throws FenixServiceException, ExcepcaoPersistencia {

		ISuportePersistente persistentSupport = PersistenceSupportFactory.getDefaultPersistenceSupport();
		IPersistentObject persistentObject = persistentSupport.getIPersistentObject();
		DegreeCurricularPlan degreeCurricularPlan = (DegreeCurricularPlan) persistentObject.readByOID(
				DegreeCurricularPlan.class, degreeCurricularPlanId);

		if (degreeCurricularPlan == null) {
			throw new InvalidArgumentsServiceException();
		}
		IPersistentCurricularCourse persistentCurricularCourse = persistentSupport.getIPersistentCurricularCourse();

		List nonBasicCurricularCourses = persistentCurricularCourse
				.readCurricularCoursesByDegreeCurricularPlanAndBasicAttribute(degreeCurricularPlan
						.getIdInternal(), new Boolean(false));

		List basicCurricularCourses = persistentCurricularCourse
				.readCurricularCoursesByDegreeCurricularPlanAndBasicAttribute(degreeCurricularPlan
						.getIdInternal(), new Boolean(true));
		Iterator iter = nonBasicCurricularCourses.iterator();
		Iterator iter1 = basicCurricularCourses.iterator();
		List infoNonBasicCurricularCourses = new ArrayList();
		List infoBasicCurricularCourses = new ArrayList();
		while (iter.hasNext()) {
			CurricularCourse curricularCourse = (CurricularCourse) iter.next();
			InfoCurricularCourse infoCurricularCourse = InfoCurricularCourse
					.newInfoFromDomain(curricularCourse);
			infoNonBasicCurricularCourses.add(infoCurricularCourse);
		}
		while (iter1.hasNext()) {
			CurricularCourse curricularCourse = (CurricularCourse) iter1.next();
			InfoCurricularCourse infoCurricularCourse = InfoCurricularCourse
					.newInfoFromDomain(curricularCourse);
			infoBasicCurricularCourses.add(infoCurricularCourse);
		}

		component.setBasicCurricularCourses(infoBasicCurricularCourses);
		component.setNonBasicCurricularCourses(infoNonBasicCurricularCourses);
		component.setInfoDegreeCurricularPlan(InfoDegreeCurricularPlan
				.newInfoFromDomain(degreeCurricularPlan));

		return component;
	}

	/**
	 * @param courses
	 * @param degreeCurricularPlanId
	 * @return
	 * @throws ExcepcaoPersistencia
	 */
	private ISiteComponent getInfoSiteCurricularCourses(InfoSiteCurricularCourses component,
			Integer degreeCurricularPlanId) throws FenixServiceException, ExcepcaoPersistencia {

		ISuportePersistente persistentSupport = PersistenceSupportFactory.getDefaultPersistenceSupport();
		IPersistentObject persistentObject = persistentSupport.getIPersistentObject();
		DegreeCurricularPlan degreeCurricularPlan = (DegreeCurricularPlan) persistentObject.readByOID(
				DegreeCurricularPlan.class, degreeCurricularPlanId);

		if (degreeCurricularPlan == null) {
			throw new InvalidArgumentsServiceException();
		}
		IPersistentCurricularCourse persistentCurricularCourse = persistentSupport.getIPersistentCurricularCourse();

		String name = degreeCurricularPlan.getName();
		String degreeName = degreeCurricularPlan.getDegree().getNome();
		String degreeSigla = degreeCurricularPlan.getDegree().getSigla();

		List curricularCourses = persistentCurricularCourse.readCurricularCoursesByDegreeCurricularPlan(
				name, degreeName, degreeSigla);
		Iterator iter = curricularCourses.iterator();
		List infoCurricularCourses = new ArrayList();
		while (iter.hasNext()) {
			CurricularCourse curricularCourse = (CurricularCourse) iter.next();
			InfoCurricularCourse infoCurricularCourse = InfoCurricularCourse
					.newInfoFromDomain(curricularCourse);
			infoCurricularCourses.add(infoCurricularCourse);
		}
		component.setCurricularCourses(infoCurricularCourses);

		return component;
	}

	/**
	 * @param courses
	 * @param degreeId
	 * @param curricularYear
	 * @return
	 * @throws ExcepcaoPersistencia
	 */
	private ISiteComponent getInfoSiteDegreeCurricularPlans(InfoSiteDegreeCurricularPlans component,
			Integer degreeId) throws FenixServiceException, ExcepcaoPersistencia {

		ISuportePersistente persistentSupport = PersistenceSupportFactory.getDefaultPersistenceSupport();
		IPersistentObject persistentObject = persistentSupport.getIPersistentObject();
		Degree degree = (Degree) persistentObject.readByOID(Degree.class, degreeId);
		if (degree == null) {
			throw new InvalidArgumentsServiceException();
		}
		IPersistentDegreeCurricularPlan persistentDegreeCurricularPlan = persistentSupport
				.getIPersistentDegreeCurricularPlan();
		List degreeCurricularPlans = persistentDegreeCurricularPlan.readByDegreeAndState(degree
				.getIdInternal(), DegreeCurricularPlanState.ACTIVE, CurricularStage.OLD);
		Iterator iter = degreeCurricularPlans.iterator();
		List infoDegreeCurricularPlans = new ArrayList();
		while (iter.hasNext()) {
			DegreeCurricularPlan degreeCurricularPlan = (DegreeCurricularPlan) iter.next();
			InfoDegreeCurricularPlan infoDegreeCurricularPlan = InfoDegreeCurricularPlanWithDegree
					.newInfoFromDomain(degreeCurricularPlan);
			infoDegreeCurricularPlans.add(infoDegreeCurricularPlan);
		}
		component.setDegreeCurricularPlans(infoDegreeCurricularPlans);

		return component;
	}

	/**
	 * @param component
	 * @return
	 * @throws ExcepcaoPersistencia 
	 */
	private ISiteComponent getInfoSiteSCDegrees(InfoSiteSCDegrees component)
			throws FenixServiceException, ExcepcaoPersistencia {

		ISuportePersistente persistentSupport = PersistenceSupportFactory.getDefaultPersistenceSupport();
		ICursoPersistente persistentDegree = persistentSupport.getICursoPersistente();
		List degrees = persistentDegree.readAllFromOldDegreeStructure();
		Iterator degreeIterator = degrees.iterator();
		List infoDegrees = new ArrayList();
		while (degreeIterator.hasNext()) {
			Degree degree = (Degree) degreeIterator.next();
			InfoDegree infoDegree = InfoDegree.newInfoFromDomain(degree);
			infoDegrees.add(infoDegree);
		}

		component.setDegrees(infoDegrees);

		return component;
	}

}