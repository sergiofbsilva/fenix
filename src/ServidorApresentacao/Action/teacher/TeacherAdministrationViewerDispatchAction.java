package ServidorApresentacao.Action.teacher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.validator.DynaValidatorForm;

import DataBeans.ISiteComponent;
import DataBeans.InfoAnnouncement;
import DataBeans.InfoBibliographicReference;
import DataBeans.InfoEvaluation;
import DataBeans.InfoItem;
import DataBeans.InfoSite;
import DataBeans.InfoSiteAnnouncement;
import DataBeans.InfoSiteBibliography;
import DataBeans.InfoSiteCommon;
import DataBeans.InfoSiteInstructions;
import DataBeans.InfoSiteItems;
import DataBeans.InfoSiteObjectives;
import DataBeans.InfoSiteProgram;
import DataBeans.InfoSiteRegularSections;
import DataBeans.InfoSiteRootSections;
import DataBeans.InfoSiteSection;
import DataBeans.InfoSiteSections;
import DataBeans.InfoSiteTeachers;
import DataBeans.TeacherAdministrationSiteView;
import ServidorAplicacao.GestorServicos;
import ServidorAplicacao.Servico.UserView;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.notAuthorizedServiceDeleteException;
import ServidorApresentacao.Action.base.FenixDispatchAction;
import ServidorApresentacao.Action.exceptions.ExistingActionException;
import ServidorApresentacao.Action.exceptions.FenixActionException;
import ServidorApresentacao.Action.exceptions.notAuthorizedActionDeleteException;
import ServidorApresentacao.Action.sop.utils.ServiceUtils;
import ServidorApresentacao.Action.sop.utils.SessionConstants;

/**
 * @author Fernanda Quit�rio
 *
 */
public class TeacherAdministrationViewerDispatchAction extends FenixDispatchAction {

	public ActionForward instructions(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent instructionsComponent = new InfoSiteInstructions();
		readSiteView(request, instructionsComponent, null, null, null);

		return mapping.findForward("viewSite");
	}

	// ========================  Customization Options Management  ========================

	public ActionForward prepareCustomizationOptions(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent customizationOptionsComponent = new InfoSite();
		readSiteView(request, customizationOptionsComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		DynaValidatorForm alternativeSiteForm = (DynaValidatorForm) form;
		alternativeSiteForm.set("siteAddress", ((InfoSite) siteView.getComponent()).getAlternativeSite());
		alternativeSiteForm.set("mail", ((InfoSite) siteView.getComponent()).getMail());
		alternativeSiteForm.set("initialStatement", ((InfoSite) siteView.getComponent()).getInitialStatement());
		alternativeSiteForm.set("introduction", ((InfoSite) siteView.getComponent()).getIntroduction());

		return mapping.findForward("editAlternativeSite");
	}
	public ActionForward editCustomizationOptions(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {
		DynaValidatorForm alternativeSiteForm = (DynaValidatorForm) form;

		HttpSession session = request.getSession();
		session.removeAttribute(SessionConstants.INFO_SECTION);

		Integer objectCode = null;
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		objectCode = new Integer(objectCodeString);

		String alternativeSite = (String) alternativeSiteForm.get("siteAddress");
		String mail = (String) alternativeSiteForm.get("mail");
		String initialStatement = (String) alternativeSiteForm.get("initialStatement");
		String introduction = (String) alternativeSiteForm.get("introduction");

		InfoSite infoSiteNew = new InfoSite();
		infoSiteNew.setAlternativeSite(alternativeSite);
		infoSiteNew.setMail(mail);
		infoSiteNew.setInitialStatement(initialStatement);
		infoSiteNew.setIntroduction(introduction);

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, infoSiteNew };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "EditCustomizationOptions", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		ISiteComponent instructionsComponent = new InfoSiteInstructions();
		readSiteView(request, instructionsComponent, null, null, null);

		session.setAttribute("alternativeSiteForm", alternativeSiteForm);

		return mapping.findForward("viewSite");
	}

	//	========================  Announcements Management  ========================

	public ActionForward showAnnouncements(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent announcementsComponent = new InfoSiteAnnouncement();
		readSiteView(request, announcementsComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		if (!((InfoSiteAnnouncement) siteView.getComponent()).getAnnouncements().isEmpty()) {
			return mapping.findForward("showAnnouncements");
		} else {
			HttpSession session = request.getSession(false);
			session.removeAttribute("insertAnnouncementForm");

			return mapping.findForward("insertAnnouncement");
		}
	}

	public ActionForward prepareCreateAnnouncement(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute("insertAnnouncementForm");

		readSiteView(request, null, null, null, null);

		return mapping.findForward("insertAnnouncement");
	}

	public ActionForward createAnnouncement(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		Integer objectCode = null;
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		objectCode = new Integer(objectCodeString);

		DynaActionForm insertAnnouncementForm = (DynaActionForm) form;
		String title = (String) insertAnnouncementForm.get("title");
		String information = (String) insertAnnouncementForm.get("information");

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, title, information };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "CreateAnnouncement", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		ISiteComponent announcementsComponent = new InfoSiteAnnouncement();
		readSiteView(request, announcementsComponent, null, null, null);

		return mapping.findForward("showAnnouncements");
	}

	public ActionForward prepareEditAnnouncement(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		//retrieve announcement
		String announcementCodeString = request.getParameter("announcementCode");
		if (announcementCodeString == null) {
			announcementCodeString = (String) request.getAttribute("announcementCode");
		}
		Integer announcementCode = new Integer(announcementCodeString);

		ISiteComponent announcementComponent = new InfoAnnouncement();
		readSiteView(request, announcementComponent, null, announcementCode, null);

		return mapping.findForward("editAnnouncement");
	}

	public ActionForward editAnnouncement(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String announcementCodeString = request.getParameter("announcementCode");
		if (announcementCodeString == null) {
			announcementCodeString = (String) request.getAttribute("announcementCode");
		}
		Integer announcementCode = new Integer(announcementCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm insertAnnouncementForm = (DynaActionForm) form;
		String newTitle = (String) insertAnnouncementForm.get("title");
		String newInformation = (String) insertAnnouncementForm.get("information");

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, announcementCode, newTitle, newInformation };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "EditAnnouncementService", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		ISiteComponent announecementsComponent = new InfoSiteAnnouncement();
		readSiteView(request, announecementsComponent, null, null, null);

		return mapping.findForward("showAnnouncements");
	}

	public ActionForward deleteAnnouncement(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String announcementCodeString = request.getParameter("announcementCode");
		if (announcementCodeString == null) {
			announcementCodeString = (String) request.getAttribute("announcementCode");
		}
		Integer announcementCode = new Integer(announcementCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		Integer objectCode = new Integer(objectCodeString);

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, announcementCode };
		GestorServicos gestor = GestorServicos.manager();

		try {
			gestor.executar(userView, "DeleteAnnouncementService", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		ISiteComponent announecementsComponent = new InfoSiteAnnouncement();
		readSiteView(request, announecementsComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		if (!((InfoSiteAnnouncement) siteView.getComponent()).getAnnouncements().isEmpty()) {
			return mapping.findForward("showAnnouncements");
		} else {
			session.removeAttribute("insertAnnouncementForm");
			return mapping.findForward("insertAnnouncement");
		}
	}

	//	========================  Objectives Management  ========================

	public ActionForward viewObjectives(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent objectivesComponent = new InfoSiteObjectives();
		readSiteView(request, objectivesComponent, null, null, null);

		return mapping.findForward("viewObjectives");
	}

	public ActionForward prepareEditObjectives(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent objectivesComponent = new InfoSiteObjectives();
		readSiteView(request, objectivesComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		DynaActionForm objectivesForm = (DynaActionForm) form;
		objectivesForm.set("generalObjectives", ((InfoSiteObjectives) siteView.getComponent()).getGeneralObjectives());
		objectivesForm.set("operacionalObjectives", ((InfoSiteObjectives) siteView.getComponent()).getOperacionalObjectives());
		objectivesForm.set("generalObjectivesEn", ((InfoSiteObjectives) siteView.getComponent()).getGeneralObjectivesEn());
		objectivesForm.set("operacionalObjectivesEn", ((InfoSiteObjectives) siteView.getComponent()).getOperacionalObjectivesEn());

		return mapping.findForward("editObjectives");
	}

	public ActionForward editObjectives(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {
		HttpSession session = request.getSession(false);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm objectivesForm = (DynaActionForm) form;
		InfoSiteObjectives infoSiteObjectivesNew = new InfoSiteObjectives();
		infoSiteObjectivesNew.setGeneralObjectives((String) objectivesForm.get("generalObjectives"));
		infoSiteObjectivesNew.setOperacionalObjectives((String) objectivesForm.get("operacionalObjectives"));
		infoSiteObjectivesNew.setGeneralObjectivesEn((String) objectivesForm.get("generalObjectivesEn"));
		infoSiteObjectivesNew.setOperacionalObjectivesEn((String) objectivesForm.get("operacionalObjectivesEn"));

		Object args[] = { objectCode, infoSiteObjectivesNew };
		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		GestorServicos serviceManager = GestorServicos.manager();
		try {
			serviceManager.executar(userView, "EditObjectives", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		return viewObjectives(mapping, form, request, response);
	}

	//	========================  Program Management  ========================

	public ActionForward viewProgram(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent programComponent = new InfoSiteProgram();
		readSiteView(request, programComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		//		if (((InfoSiteProgram) siteView.getComponent()).getProgram() != null
		//			&& ((InfoSiteProgram) siteView.getComponent()).getProgram().length() > 0){
		return mapping.findForward("viewProgram");
		//			} else{
		//				return mapping.findForward("editProgram");
		//			}
	}

	public ActionForward prepareEditProgram(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent programComponent = new InfoSiteProgram();
		readSiteView(request, programComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		DynaActionForm programForm = (DynaActionForm) form;
		programForm.set("program", ((InfoSiteProgram) siteView.getComponent()).getProgram());
		programForm.set("programEn", ((InfoSiteProgram) siteView.getComponent()).getProgramEn());

		return mapping.findForward("editProgram");
	}

	public ActionForward editProgram(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {
		HttpSession session = request.getSession(false);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm programForm = (DynaActionForm) form;
		InfoSiteProgram infoSiteProgramNew = new InfoSiteProgram();
		infoSiteProgramNew.setProgram((String) programForm.get("program"));
		infoSiteProgramNew.setProgramEn((String) programForm.get("programEn"));

		Object args[] = { objectCode, infoSiteProgramNew };
		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		GestorServicos serviceManager = GestorServicos.manager();
		try {
			serviceManager.executar(userView, "EditProgram", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		return viewProgram(mapping, form, request, response);
	}

	//	========================  Evaluation Management  ========================

	public ActionForward viewEvaluation(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		ISiteComponent evaluationComponent = new InfoEvaluation();
		readSiteView(request, evaluationComponent, null, null, null);

		return mapping.findForward("viewEvaluation");
	}

	public ActionForward prepareEditEvaluation(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent evaluationComponent = new InfoEvaluation();
		readSiteView(request, evaluationComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		DynaActionForm evaluationForm = (DynaActionForm) form;
		evaluationForm.set("evaluationElements", ((InfoEvaluation) siteView.getComponent()).getEvaluationElements());
		evaluationForm.set("evaluationElementsEn", ((InfoEvaluation) siteView.getComponent()).getEvaluationElementsEn());

		return mapping.findForward("editEvaluation");
	}

	public ActionForward editEvaluation(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {
		HttpSession session = request.getSession(false);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");

		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm evaluationForm = (DynaActionForm) form;
		InfoEvaluation infoEvaluationNew = new InfoEvaluation();
		infoEvaluationNew.setEvaluationElements((String) evaluationForm.get("evaluationElements"));
		infoEvaluationNew.setEvaluationElementsEn((String) evaluationForm.get("evaluationElementsEn"));

		Object args[] = { objectCode, infoEvaluationNew };
		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		GestorServicos serviceManager = GestorServicos.manager();
		try {
			serviceManager.executar(userView, "EditEvaluation", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		return viewEvaluation(mapping, form, request, response);
	}

	//	========================  Bibliographic References Management  ========================

	public ActionForward viewBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent bibliographyComponent = new InfoSiteBibliography();
		readSiteView(request, bibliographyComponent, null, null, null);

		TeacherAdministrationSiteView siteView = (TeacherAdministrationSiteView) request.getAttribute("siteView");

		if (((InfoSiteBibliography) siteView.getComponent()).getBibliographicReferences() != null
			&& (!((InfoSiteBibliography) siteView.getComponent()).getBibliographicReferences().isEmpty())) {
			return mapping.findForward("bibliographyManagement");
		} else {
			HttpSession session = request.getSession(false);
			session.removeAttribute("bibliographicReferenceForm");

			return mapping.findForward("insertBibliographicReference");
		}
	}

	public ActionForward prepareCreateBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute("bibliographicReferenceForm");

		readSiteView(request, null, null, null, null);

		return mapping.findForward("insertBibliographicReference");
	}

	public ActionForward createBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		Integer objectCode = null;
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		objectCode = new Integer(objectCodeString);

		DynaActionForm insertBibliographicReferenceForm = (DynaActionForm) form;

		String title = (String) insertBibliographicReferenceForm.get("title");
		String authors = (String) insertBibliographicReferenceForm.get("authors");
		String reference = (String) insertBibliographicReferenceForm.get("reference");
		String year = (String) insertBibliographicReferenceForm.get("year");
		String optionalStr = (String) insertBibliographicReferenceForm.get("optional");
		Boolean optional;

		if (optionalStr.equals(this.getResources(request).getMessage("message.optional"))) {
			optional = new Boolean(true);
		} else {
			optional = new Boolean(false);
		}

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, title, authors, reference, year, optional };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "CreateBibliographicReference", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		ISiteComponent bibliographyComponent = new InfoSiteBibliography();
		readSiteView(request, bibliographyComponent, null, null, null);

		return mapping.findForward("bibliographyManagement");
	}

	public ActionForward prepareEditBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		//retrieve bibliographic reference
		String bibliographicReferenceCodeString = request.getParameter("bibliographicReferenceCode");
		if (bibliographicReferenceCodeString == null) {
			bibliographicReferenceCodeString = (String) request.getAttribute("bibliographicReferenceCode");
		}
		Integer bibliographicReferenceCode = new Integer(bibliographicReferenceCodeString);

		ISiteComponent bibliographyComponent = new InfoBibliographicReference();
		readSiteView(request, bibliographyComponent, null, bibliographicReferenceCode, null);

		return mapping.findForward("editBibliographicReference");
	}

	public ActionForward editBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String bibliographicReferenceCodeString = request.getParameter("bibliographicReferenceCode");
		if (bibliographicReferenceCodeString == null) {
			bibliographicReferenceCodeString = (String) request.getAttribute("bibliographicReferenceCode");
		}
		Integer bibliographicReferenceCode = new Integer(bibliographicReferenceCodeString);

		Integer objectCode = null;
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		objectCode = new Integer(objectCodeString);

		DynaActionForm editBibliographicReferenceForm = (DynaActionForm) form;
		String title = (String) editBibliographicReferenceForm.get("title");
		String authors = (String) editBibliographicReferenceForm.get("authors");
		String reference = (String) editBibliographicReferenceForm.get("reference");
		String year = (String) editBibliographicReferenceForm.get("year");
		String optionalStr = (String) editBibliographicReferenceForm.get("optional");
		Boolean optional;

		if (optionalStr.equals(this.getResources(request).getMessage("message.optional"))) {
			optional = new Boolean(true);
		} else {
			optional = new Boolean(false);
		}

		Object args[] = { objectCode, bibliographicReferenceCode, title, authors, reference, year, optional };

		UserView userView = (UserView) session.getAttribute("UserView");
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "EditBibliographicReference", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		ISiteComponent bibliographyComponent = new InfoSiteBibliography();
		readSiteView(request, bibliographyComponent, null, null, null);

		return mapping.findForward("bibliographyManagement");
	}

	public ActionForward deleteBibliographicReference(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String bibliographicReferenceCodeString = request.getParameter("bibliographicReferenceCode");
		if (bibliographicReferenceCodeString == null) {
			bibliographicReferenceCodeString = (String) request.getAttribute("bibliographicReferenceCode");
		}
		Integer bibliographicReferenceCode = new Integer(bibliographicReferenceCodeString);

		Integer objectCode = null;
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		objectCode = new Integer(objectCodeString);

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, bibliographicReferenceCode };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "DeleteBibliographicReference", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		return viewBibliographicReference(mapping, form, request, response);
	}

	//	========================  Teachers Management  ========================

	public ActionForward viewTeachersByProfessorship(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = getSession(request);
		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);

		ISiteComponent teachersComponent = new InfoSiteTeachers();
		readSiteView(request, teachersComponent, null, null, userView.getUtilizador());

		return mapping.findForward("viewTeachers");
	}

	public ActionForward prepareAssociateTeacher(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {
		HttpSession session = request.getSession(false);
		session.removeAttribute("teacherForm");

		readSiteView(request, null, null, null, null);

		return mapping.findForward("associateTeacher");
	}

	public ActionForward associateTeacher(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = getSession(request);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm teacherForm = (DynaActionForm) form;
		Integer teacherNumber = new Integer((String) teacherForm.get("teacherNumber"));

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, teacherNumber };
		GestorServicos gestor = GestorServicos.manager();
		try {

			gestor.executar(userView, "AssociateTeacher", args);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		return viewTeachersByProfessorship(mapping, form, request, response);
	}

	public ActionForward removeTeacher(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = getSession(request);

		String teacherCodeString = request.getParameter("teacherCode");
		if (teacherCodeString == null) {
			teacherCodeString = (String) request.getAttribute("teacherCode");
		}
		Integer teacherCode = new Integer(teacherCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		UserView userView = (UserView) session.getAttribute("UserView");
		Object args[] = { objectCode, teacherCode };
		GestorServicos gestor = GestorServicos.manager();
		try {
			gestor.executar(userView, "DeleteTeacher", args);
		} catch (notAuthorizedServiceDeleteException ee) {
			throw new notAuthorizedActionDeleteException("error.invalidTeacherRemoval");
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}

		return viewTeachersByProfessorship(mapping, form, request, response);
	}

	//	========================  Sections Management  ========================

	public ActionForward viewSection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		ISiteComponent sectionComponent = new InfoSiteSection();
		readSiteView(request, sectionComponent, null, sectionCode, null);

		return mapping.findForward("viewSection");

	}
	public ActionForward prepareCreateRegularSection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		ISiteComponent regularSectionsComponent = new InfoSiteRegularSections();
		readSiteView(request, regularSectionsComponent, null, sectionCode, null);

		request.setAttribute("currentSectionCode", sectionCode);

		return mapping.findForward("createSection");
	}

	public ActionForward prepareCreateRootSection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent rootSectionsComponent = new InfoSiteRootSections();
		readSiteView(request, rootSectionsComponent, null, null, null);

		return mapping.findForward("createSection");
	}

	public ActionForward createSection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession();

		Integer sectionCode = null;
		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		if (sectionCodeString != null) {
			sectionCode = new Integer(sectionCodeString);
		}

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm dynaForm = (DynaValidatorForm) form;
		String sectionName = (String) dynaForm.get("name");
		Integer order = Integer.valueOf((String) dynaForm.get("sectionOrder"));

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		Object args[] = { objectCode, sectionCode, sectionName, order };
		GestorServicos manager = GestorServicos.manager();
		try {
			manager.executar(userView, "InsertSection", args);
		} catch (ExistingServiceException e) {
			throw new ExistingActionException("Uma sec��o com esse nome", e);
		} catch (FenixServiceException fenixServiceException) {
			throw new FenixActionException(fenixServiceException.getMessage());
		}
		return instructions(mapping, form, request, response);
	}

	public ActionForward prepareEditSection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		ISiteComponent sectionsComponent = new InfoSiteSections();
		readSiteView(request, sectionsComponent, null, sectionCode, null);

		return mapping.findForward("editSection");
	}

	public ActionForward editSection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession();

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm sectionForm = (DynaValidatorForm) form;
		String sectionName = (String) sectionForm.get("name");
		Integer order = (Integer) sectionForm.get("sectionOrder");
		order = new Integer(order.intValue() - 1);

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		GestorServicos manager = GestorServicos.manager();
		Object editionArgs[] = { objectCode, sectionCode, sectionName, order };
		try {
			manager.executar(userView, "EditSection", editionArgs);
		} catch (FenixServiceException fenixServiceException) {
			throw new FenixActionException(fenixServiceException.getMessage());
		}

		return instructions(mapping, form, request, response);
	}

	public ActionForward deleteSection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		Integer sectionCode = null;
		String sectionCodeString = request.getParameter("superiorSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("superiorSectionCode");
		}
		if (sectionCodeString != null) {
			sectionCode = new Integer(sectionCodeString);
		}

		String subSectionCodeString = request.getParameter("currentSectionCode");
		if (subSectionCodeString == null) {
			subSectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer subSectionCode = new Integer(subSectionCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		Object deleteSectionArguments[] = { objectCode, subSectionCode };
		GestorServicos manager = GestorServicos.manager();
		try {
			manager.executar(userView, "DeleteSection", deleteSectionArguments);
			//			if this section has a parent section
			if (sectionCode != null) {
				return viewSection(mapping, form, request, response);
			} else {
				return instructions(mapping, form, request, response);
			}

		} catch (FenixServiceException fenixServiceException) {
			throw new FenixActionException(fenixServiceException.getMessage());
		} catch (Exception e) {
			throw new FenixActionException(e.getMessage());
		}
	}

	public ActionForward prepareInsertItem(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		ISiteComponent sectionComponent = new InfoSiteSection();
		readSiteView(request, sectionComponent, null, sectionCode, null);

		return mapping.findForward("insertItem");
	}

	public ActionForward insertItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String sectionCodeString = request.getParameter("currentSectionCode");
		if (sectionCodeString == null) {
			sectionCodeString = (String) request.getAttribute("currentSectionCode");
		}
		Integer sectionCode = new Integer(sectionCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm dynaForm = (DynaValidatorForm) form;
		Integer order = new Integer((String) dynaForm.get("itemOrder"));
		String itemName = (String) dynaForm.get("name");
		String information = (String) dynaForm.get("information");
		String urgentString = (String) dynaForm.get("urgent");

		InfoItem newInfoItem = new InfoItem();
		newInfoItem.setItemOrder(order);
		newInfoItem.setName(itemName);
		newInfoItem.setInformation(information);
		newInfoItem.setUrgent(new Boolean(urgentString));

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		Object args[] = { objectCode, sectionCode, newInfoItem };
		GestorServicos manager = GestorServicos.manager();
		try {
			manager.executar(userView, "InsertItem", args);
		} catch (ExistingServiceException e) {
			throw new ExistingActionException("Um item com esse nome", e);
		} catch (FenixServiceException fenixServiceException) {
			throw new FenixActionException(fenixServiceException.getMessage());
		}

		return viewSection(mapping, form, request, response);

	}
	public ActionForward prepareEditItem(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String itemCodeString = request.getParameter("itemCode");
		if (itemCodeString == null) {
			itemCodeString = (String) request.getAttribute("itemCode");
		}
		Integer itemCode = new Integer(itemCodeString);

		ISiteComponent itemsComponent = new InfoSiteItems();
		readSiteView(request, itemsComponent, null, itemCode, null);

		return mapping.findForward("editItem");
	}

	public ActionForward editItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String itemCodeString = request.getParameter("itemCode");
		if (itemCodeString == null) {
			itemCodeString = (String) request.getAttribute("itemCode");
		}
		Integer itemCode = new Integer(itemCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		DynaActionForm itemForm = (DynaActionForm) form;
		String information = (String) itemForm.get("information");
		String name = (String) itemForm.get("name");
		Boolean urgent = new Boolean((String) itemForm.get("urgent"));
		Integer itemOrder = new Integer((String) itemForm.get("itemOrder"));
		itemOrder = new Integer(itemOrder.intValue() - 1);

		InfoItem newInfoItem = new InfoItem();
		newInfoItem.setInformation(information);
		newInfoItem.setName(name);
		newInfoItem.setItemOrder(itemOrder);
		newInfoItem.setUrgent(urgent);

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		Object editItemArgs[] = { objectCode, itemCode, newInfoItem };
		GestorServicos manager = GestorServicos.manager();
		try {
			manager.executar(userView, "EditItem", editItemArgs);
		} catch (ExistingServiceException e) {
			throw new ExistingActionException("Um item com esse nome", e);
		} catch (FenixServiceException fenixServiceException) {

			throw new FenixActionException(fenixServiceException.getMessage());
		}

		return viewSection(mapping, form, request, response);
	}

	public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws FenixActionException {

		HttpSession session = request.getSession(false);

		String itemCodeString = request.getParameter("itemCode");
		if (itemCodeString == null) {
			itemCodeString = (String) request.getAttribute("itemCode");
		}
		Integer itemCode = new Integer(itemCodeString);

		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer objectCode = new Integer(objectCodeString);

		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);
		Object deleteItemArguments[] = { objectCode, itemCode };
		GestorServicos manager = GestorServicos.manager();
		try {
			manager.executar(userView, "DeleteItem", deleteItemArguments);

		} catch (FenixServiceException fenixServiceException) {
			throw new FenixActionException(fenixServiceException.getMessage());
		}

		return viewSection(mapping, form, request, response);
	}

	private void readSiteView(
		HttpServletRequest request,
		ISiteComponent firstPageComponent,
		Integer infoExecutionCourseCode,
		Object obj1,
		Object obj2)
		throws FenixActionException {

		HttpSession session = getSession(request);
		UserView userView = (UserView) session.getAttribute(SessionConstants.U_VIEW);

		Integer objectCode = null;
		if (infoExecutionCourseCode == null) {
			String objectCodeString = request.getParameter("objectCode");
			if (objectCodeString == null) {
				objectCodeString = (String) request.getAttribute("objectCode");
			}
			objectCode = new Integer(objectCodeString);
			infoExecutionCourseCode = objectCode;
		}

		ISiteComponent commonComponent = new InfoSiteCommon();
		Object[] args = { infoExecutionCourseCode, commonComponent, firstPageComponent, objectCode, obj1, obj2 };

		try {
			TeacherAdministrationSiteView siteView =
				(TeacherAdministrationSiteView) ServiceUtils.executeService(
					userView,
					"TeacherAdministrationSiteComponentService",
					args);

			request.setAttribute("siteView", siteView);
			request.setAttribute("objectCode", ((InfoSiteCommon) siteView.getCommonComponent()).getExecutionCourse().getIdInternal());

			if (siteView.getComponent() instanceof InfoSiteSection) {
				request.setAttribute("infoSection", ((InfoSiteSection) siteView.getComponent()).getSection());
			}

		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
	}
}