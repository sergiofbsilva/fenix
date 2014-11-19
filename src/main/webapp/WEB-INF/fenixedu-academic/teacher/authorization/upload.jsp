<!-- Nesta interface pode carregar multiplas autorizações com um único ficheiro csv com o seguinte formato: -->

<!-- Coluna 1: username -->
<!-- Coluna 2: categoria profissional (ou equivalente) -->
<!-- Coluna 3: acrónimo do departamento -->
<!-- Coluna 4: número de horas léctivas devidas -->
<!-- Coluna 5: docente contratado ou não. Valores possíveis: "S" ou "N" -->

<!-- Os valores possíveis para os acrónimos dos departamentos são: -->

<!-- Os valores possíveis para a categoria profissional são: -->


<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="page-header">
	<h1>
		<spring:message code="teacher.authorizations.title"/>
		<small>
			<spring:message code="teacher.authorizations.upload"/>
		</small>
	</h1>
</div>

<section>
	<div class="alert alert-info" role="alert">
		<spring:message code="teacher.authorizations.upload.message.header"/>
		<ol>
			<li><spring:message code="teacher.authorizations.csv.column.1" /></li>
			<li>
				<spring:message code="teacher.authorizations.csv.column.2" />
				<br />
				<i>
					<spring:message code="teacher.authorizations.upload.message.example" arguments="${categories[0].name.content};${categories[0].code}" argumentSeparator=";"/>
				</i>
				<ul>
					<c:forEach var="category" items="${categories}">
						<li>${category.name.content} - ${category.code}</li>
					</c:forEach>
				</ul>
			</li>
			<li>
				<spring:message code="teacher.authorizations.csv.column.3" />
				<br />
				<i>
					<spring:message code="teacher.authorizations.upload.message.example" arguments="${departments[0].nameI18n.content};${departments[0].code}" argumentSeparator=";"/>
				</i>
				<ul>
					<c:forEach var="department" items="${departments}">
						<li>${department.acronym} - ${department.nameI18n.content}</li>
					</c:forEach>
				</ul>
				
			</li>
			<li><spring:message code="teacher.authorizations.csv.column.4" /></li>
			<li><spring:message code="teacher.authorizations.csv.column.5" /></li>
		</ol>
	</div>
</section>
