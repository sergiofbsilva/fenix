<%@ page language="java" %>
<%@ page import="ServidorApresentacao.Action.sop.utils.SessionConstants" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<br />
<table width="100%">
	<tr>
		<td class="infoop">
			<bean:message key="label.teachers.explanation" />
		</td>
	</tr>
</table>
<span class="error"><html:errors/></span>	
<h2><bean:message key="title.teachers"/></h2>

<logic:present name="siteView">
<bean:define id="infoSiteTeachers" name="siteView" property="component"/>
<bean:define id="teachersList" name="infoSiteTeachers" property="infoTeachers"/>
<bean:define id="isResponsible" name="infoSiteTeachers" property="isResponsible"/>

<table>
	<tr>
		<td width="150" class="listClasses-header"><bean:message key="label.teacherNumber"/>	
		</td>
		<td width="250" class="listClasses-header"><bean:message key="label.name"/>	
		</td>
	</tr>	
	<logic:iterate id="infoTeacher" name="teachersList">
	<tr>
		<td class="listClasses"><bean:write name="infoTeacher"  property="teacherNumber"/>	
		</td>
		<td class="listClasses"><bean:write name="infoTeacher" property="infoPerson.nome" /> 
		</td>
		<logic:equal name="isResponsible" value="true">
			<bean:define id="teacherCode" name="infoTeacher" property="idInternal"/>		
			<td>
				<html:link page="<%= "/teachersManagerDA.do?method=removeTeacher&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;teacherCode=" + teacherCode %>">
					(<bean:message key="link.removeTeacher"/>)
				</html:link>
			</td>
		</logic:equal>
	</tr>
	</logic:iterate>	
</table>
<br />
<logic:equal name="isResponsible" value="true">
	<div class="gen-button">
		<html:link page="<%= "/teacherManagerDA.do?method=prepareAssociateTeacher&amp;objectCode=" + pageContext.findAttribute("objectCode") %>">
			<bean:message key="link.addTeacher"/>
		</html:link>
	</div>
</logic:equal>
</logic:present>