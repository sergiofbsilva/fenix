<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<html:xhtml/>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<bean:define id="step" value="<%=request.getParameter("step")%>" />

<div class="breadcumbs">
	<span class="<%= step.equals("1") ? "actual" : "" %>"><bean:message  key="label.phd.candidacy.academicAdminOffice.createCandidacy.searchPersonStep.breadcrumb" bundle="PHD_RESOURCES"/></span> > 
	<span class="<%= step.equals("2") ? "actual" : "" %>"><bean:message  key="label.phd.candidacy.academicAdminOffice.createCandidacy.createCandidacyStep.breadcrumb" bundle="PHD_RESOURCES"/></span>
</div>
