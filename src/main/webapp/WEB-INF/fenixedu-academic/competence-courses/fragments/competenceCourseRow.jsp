<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true" %>

<tr class="color2">
    <td>
        <c:if test="${empty competenceCourse.code}">
            <c:out value="${competenceCourse.name}"/>
        </c:if>
        <c:if test="${not empty competenceCourse.code}">
            <c:out value='${competenceCourse.code} - ${competenceCourse.name}'/>
        </c:if>
        <span>&nbsp;<em class="${fn:toLowerCase(competenceCourse.curricularStage)}">${fr:message('resources/EnumerationResources', competenceCourse.curricularStage)}</em></span>
        <c:if test="${showOldCompetenceCourses}">
            <span class="warning0">(&nbsp;Departamento actual:&nbsp;<c:out value="${competenceCourse.departmentUnit.department.acronym}"/>&nbsp;)</span>
        </c:if>
    </td>
    <td class="aright nowrap">
        <!-- View Link -->
        <c:choose>
            <c:when test="${isScientificCouncilMember}">
                <spring:url var="showCompetenceUrl" context="" value="/scientificCouncil/competenceCourses/showCompetenceCourse.faces?action=ccm&competenceCourseID={competenceCourse}&selectedDepartmentUnitID={departmentUnit}">
                    <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                    <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
                </spring:url>
            </c:when>
            <c:when test="${isBolonhaManager}">
                <spring:url var="showCompetenceUrl" context="" value="/bolonhaManager/competenceCourses/showCompetenceCourse.faces?action=ccm&competenceCourseID={competenceCourse}">
                    <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                    <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
                </spring:url>
            </c:when>
        </c:choose>
        <a href="${fr:checksumLink(pageContext.request, showCompetenceUrl)}"><spring:message code="label.show" text="Ver"/></a>
    
        <!-- Versions Link -->
        <spring:url var="showVersionsUrl" context="" value="/bolonhaManager/competenceCourses/manageVersions.do?method=showVersions&competenceCourseID={competenceCourse}">
            <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
        </spring:url>
        
        <c:if test="${isBolonhaManager}">
            <a href="${fr:checksumLink(pageContext.request, showVersionsUrl)}"><spring:message code="label.show.versions" text="Consultar Versão"/></a>
        </c:if>
        
        <!-- Transfer and Toggle Links -->
        <c:if test="${isScientificCouncilMember}">
            <spring:url var="transferCompetenceUrl" context="" value="/scientificCouncil/competenceCourses/transferCompetenceCourse.faces?competenceCourseID={competenceCourse}&selectedDepartmentUnitID={departmentUnit}">
                <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
            </spring:url>
            <spring:url var="toggleStateUrl" value="/scientific-council/competence-courses/toggle?competenceCourse={competenceCourse}&departmentUnit={departmentUnit}">
                <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
            </spring:url>
            <a href="${fr:checksumLink(pageContext.request, transferCompetenceUrl)}"><spring:message code="label.transfer" text="Transferir"/></a>
            <c:if test="${competenceCourse.curricularStage.name == 'APPROVED'}">
                <a href="${toggleStateUrl}"><spring:message code="label.approved.toggle" text="Desaprovar"/></a>
            </c:if>
            <c:if test="${competenceCourse.curricularStage.name == 'PUBLISHED'}">
                <a href="${toggleStateUrl}"><spring:message code="label.published.toggle" text="Aprovar"/></a>
            </c:if>
        </c:if>
    </td>
</tr>
