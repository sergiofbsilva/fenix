<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:url var="createUrl" value="/teacher/authorizations/categories/create"></spring:url>
<spring:url var="editUrl" value="/teacher/authorizations/categories"></spring:url>

${portal.toolkit()}

<div class="page-header">
	<h1>
		<spring:message code="teacher.categories"/>
	</h1>
</div>
<section>
	<a class="btn btn-primary" href="${createUrl}"><spring:message code="label.create"/></a>
	<table class="table">
		<thead>
			<th><spring:message code="teacher.categories.code" /></th>
			<th><spring:message code="teacher.categories.name" /></th>
			<th><spring:message code="teacher.categories.weight" /></th>
			<th></th>
		</thead>
		<tbody>
			<c:forEach var="category" items="${categories}">
				<tr>
					<td>${category.code}</td>
					<td>${category.name.content}</td>
					<td>${category.weight}</td>
					<td><a class="btn btn-default" href="${editUrl}/${category.externalId}"><spring:message code="label.edit"/></a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</section>
