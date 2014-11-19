<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%-- ${org.joda.time.format.DateTimeFormat.patternForStyle("M", )} --%>
${org.fenixedu.commons.i18n.I18N.getLocale()}
<div class="page-header">
	<h1>
		<spring:message code="teacher.authorizations.title" />
		<small><spring:message code="teacher.authorizations.title.search" /></small>
	</h1>
</div>
<section>
	<table class="table">
		<thead>
			<th><spring:message code="teacher.authorizations.username" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.displayname" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.revokeTime" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.contracted" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.department" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.period" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.category" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.lessonHours" ></spring:message></th>
			<th><spring:message code="teacher.authorizations.authorized" ></spring:message></th>
		</thead>
		<tbody>
			<c:forEach var="auth" items="${authorizations}">
				<c:set var="user" value="${auth.teacher.person.user}"/>
				<tr>
					<td>${user.username}</td>
					<td>${user.name}</td>
					<td>
						${auth.revokeTime.toString("dd/MM/yyyy")}
					</td>
					<td>
						<c:choose>
							<c:when test="${auth.contracted}">
								<spring:message code="teacher.authorizations.contracted.yes"></spring:message>
							</c:when>
							<c:otherwise>
								<spring:message code="teacher.authorizations.contracted.no"></spring:message>
							</c:otherwise>
						</c:choose>
					</td>
					<td>${auth.department.nameI18n.content}</td>
					<td>${auth.executionSemester.qualifiedName}</td>
					<td>${auth.teacherCategory.name.content}</td>
					<td>${auth.lessonHours}</td>
					<td>${auth.authorizer.name} (${auth.authorizer.username})</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</section>