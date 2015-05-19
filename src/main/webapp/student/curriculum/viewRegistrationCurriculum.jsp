<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.academic.domain.student.Registration"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.AverageType"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.ICurriculum"%>
<%@page import="org.fenixedu.academic.dto.student.RegistrationCurriculumBean"%>
<html:xhtml />

<h2><bean:message key="registration.curriculum" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>

<bean:define id="registrationCurriculumBean" name="registrationCurriculumBean" type="org.fenixedu.academic.dto.student.RegistrationCurriculumBean"/>
<%
	final Registration registration = ((RegistrationCurriculumBean) registrationCurriculumBean).getRegistration();
	request.setAttribute("registration", registration);

	// average
	ICurriculum curriculum = registrationCurriculumBean.getCurriculum();
	request.setAttribute("curriculum", curriculum);

	final BigDecimal sumPiCi = curriculum.getSumPiCi();
	request.setAttribute("sumPiCi", sumPiCi);

	final BigDecimal sumPi = curriculum.getSumPi();
	request.setAttribute("sumPi", sumPi);

	final BigDecimal average = curriculum.getAverage();
	request.setAttribute("weightedAverage", average);
	
	// curricular year
	final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
	request.setAttribute("currentExecutionYear", currentExecutionYear);
	curriculum = registrationCurriculumBean.getCurriculum(currentExecutionYear);
	
	final BigDecimal sumEctsCredits = curriculum.getSumEctsCredits();
	request.setAttribute("sumEctsCredits", sumEctsCredits);
		
	final Integer curricularYear = curriculum.getCurricularYear();
	request.setAttribute("curricularYear", curricularYear);
%>

<%-- Person and Student short info --%>
<p class="mvert2">
	<span class="showpersonid">
	<bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>: 
		<fr:view name="registration" property="student" schema="student.show.personAndStudentInformation.short">
			<fr:layout name="flow">
				<fr:property name="labelExcluded" value="true"/>
			</fr:layout>
		</fr:view>
	</span>
</p>

<logic:equal name="curriculum" property="empty" value="true">
	<p class="mvert15">
		<em>
			<bean:message key="no.approvements" bundle="ACADEMIC_OFFICE_RESOURCES"/>
		</em>
	</p>	
</logic:equal>

<logic:equal name="curriculum" property="empty" value="false">

	<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="true">
		<div class="infoop2 mvert2">
			<p class="mvert05"><strong><bean:message key="final.degree.average.info" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>

			<p class="mtop1 mbottom05"><strong><bean:message key="degree.average" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>
			<p class="pleft1 mvert05"><bean:message key="degree.average.abbreviation" bundle="ACADEMIC_OFFICE_RESOURCES"/> = <b class="highlight1"><bean:write name="registrationCurriculumBean" property="finalAverage"/></b></p>
			<p class="mtop1 mbottom05"><strong><bean:message key="conclusion.date" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>
			<p class="pleft1 mvert05"><b class="highlight1"><bean:write name="registrationCurriculumBean" property="conclusionDate"/></b></p>
		</div>	
	</logic:equal>
		
	<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="false">

		<logic:equal name="registrationCurriculumBean" property="concluded" value="true">
			<div class="infoop2 mvert2">
				<p class="mvert05"><span class="error0"><strong><bean:message key="missing.final.average.info" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></span></p>
			</div>
		</logic:equal>

		<div class="infoop2 mvert2">
			<p class="mvert05"><strong><bean:message key="legal.value.info" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>
			<p class="mvert05"><strong><bean:message key="rules.info" arg0="<%=org.fenixedu.academic.domain.organizationalStructure.Unit.getInstitutionName().getContent()%>" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>
	
			<p class="mtop1 mbottom05"><strong><bean:message key="degree.average.is.current.info" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></p>
			<p class="pleft1 mvert05"><bean:message key="degree.average" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <b class="highlight1"><bean:write name="weightedAverage"/></b></p>
			<p class="pleft1 mvert05"><bean:message key="rule" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <bean:message key="average.rule" bundle="ACADEMIC_OFFICE_RESOURCES"/></p>
			<p class="pleft1 mvert05"><bean:message key="result" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <bean:message key="degree.average.abbreviation" bundle="ACADEMIC_OFFICE_RESOURCES"/> = <bean:write name="sumPiCi"/> / <bean:write name="sumPi"/> = <b class="highlight1"><bean:write name="weightedAverage"/></b></p>
	
			<p class="mtop1 mbottom05"><strong><bean:message key="curricular.year.in.begin.of.execution.year.info" bundle="ACADEMIC_OFFICE_RESOURCES"/> <bean:write name="currentExecutionYear" property="year"/></strong>.</p>
			<p class="pleft1 mvert05"><bean:message key="curricular.year" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <b class="highlight1"><bean:write name="curricularYear"/></b></p>
			<p class="pleft1 mvert05"><bean:message key="rule" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <bean:message key="curricular.year.rule" bundle="ACADEMIC_OFFICE_RESOURCES"/></p>
			<p class="pleft1 mvert05"><bean:message key="result" bundle="ACADEMIC_OFFICE_RESOURCES"/>: <bean:message key="curricular.year.abbreviation" bundle="ACADEMIC_OFFICE_RESOURCES"/> = <bean:message key="minimum" bundle="ACADEMIC_OFFICE_RESOURCES"/> (<bean:message key="int" bundle="ACADEMIC_OFFICE_RESOURCES"/> ( (<bean:write name="sumEctsCredits"/> + 24) / 60 + 1) ; <bean:write name="registrationCurriculumBean" property="curriculum.totalCurricularYears"/>) = <b class="highlight1"><bean:write name="curricularYear"/></b>;</p>
		</div>

	</logic:equal>

	<table class="tstyle4 thlight tdcenter mtop15">
		<tr>
			<th><bean:message key="label.numberAprovedCurricularCourses" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
			<th><bean:message key="label.total.ects.credits" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
			<th><bean:message key="average" bundle="STUDENT_RESOURCES"/> <bean:message key="AverageType.WEIGHTED" bundle="ENUMERATION_RESOURCES"/></th>
			<th><bean:message key="average" bundle="STUDENT_RESOURCES"/> <bean:message key="AverageType.SIMPLE" bundle="ENUMERATION_RESOURCES"/></th>
			<th><bean:message key="label.curricular.year" bundle="STUDENT_RESOURCES"/></th>
		</tr>
		<tr>
			<bean:size id="curricularEntriesCount" name="curriculum" property="curriculumEntries"/>
			<td><bean:write name="curricularEntriesCount"/></td>
			<td><bean:write name="sumEctsCredits"/></td>
			<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="false">
				<td><bean:write name="weightedAverage"/></td>
				<logic:equal name="curriculum" property="studentCurricularPlan.averageType.name" value="WEIGHTED">
					<td>-</td>
				</logic:equal>
				<logic:notEqual name="curriculum" property="studentCurricularPlan.averageType.name" value="WEIGHTED">
					<%
						curriculum.setAverageType(AverageType.SIMPLE);
						request.setAttribute("simpleAverage", curriculum.getAverage());
					%>
					<td><bean:write name="simpleAverage"/></td>
				</logic:notEqual>
				<td><bean:write name="curricularYear"/></td>
			</logic:equal>
			<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="true">
				<logic:equal name="curriculum" property="studentCurricularPlan.averageType.name" value="WEIGHTED">
					<td>
							<bean:write name="registrationCurriculumBean" property="finalAverage"/>
					</td>
					<td>-</td>
				</logic:equal>
				<logic:notEqual name="curriculum" property="studentCurricularPlan.averageType.name" value="WEIGHTED">
					<td>-</td>
					<td>
						<bean:write name="registrationCurriculumBean" property="finalAverage"/>
					</td>
				</logic:notEqual>
				<td>-</td>
			</logic:equal>			
		</tr>
	</table>

		<p>
			<fr:view name="curriculum"/>
		</p>
	
</logic:equal>
