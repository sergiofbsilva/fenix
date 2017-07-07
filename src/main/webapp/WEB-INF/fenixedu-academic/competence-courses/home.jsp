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
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="org.fenixedu.academic.domain.degreeStructure.CurricularStage" %>

<style>
    .table th {
        text-align: center;
    }
    .table td {
        vertical-align: middle !important;
        text-align: center;
    }
    .approved {
        color: #595;
    }
    
    .published {
        color: #569;
    }
    
    .draft {
        color: #bb5;
    }
</style>


<div class="page-header">
    <h1>
        <spring:message code="competence.courses.title" text="Aprovação de Disciplinas Competência"/>
        <small><spring:message code="label.listing" text="Listing" /></small>
    </h1>
</div>


<section>
    <form role="form" method="GET" class="form-inline">
        <div class="form-group">
            <label for="departmentUnit"><spring:message code="label.department" text="Departamento"/></label>
            <select name="departmentUnit" id="departmentUnit">
                <c:forEach var="department" items="${departmentUnits}">
                    <option value="${department.externalId}" <c:if test="${department == departmentUnit}"> selected="selected"</c:if>>
                        <c:out value="${department.nameI18n.content}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-small btn-primary"><spring:message code="label.submit"/></button>
    </form>
    <hr/>
    <c:if test="${ not empty groupMembers}">
        <div>
            <p class='mtop15 mbottom05'>
                <b class='highlight1'>
                    ${fr:message('resources/ScientificCouncilResources','groupMembers')}
                </b>
                ${fr:message('resources/ScientificCouncilResources','label.group.members.explanation')}
            </p>
            <ul>
                <c:forEach var="user" items="${groupMembers}">
                    <li><c:out value="${user.profile.fullName} (${user.username})"/> </li>
                </c:forEach>
            </ul>
                <%--<table class="table">--%>
                    <%--<thead>--%>
                        <%--<tr>--%>
                            <%--<th>Photo</th><th>Username</th><th>Name</th><th></th>--%>
                        <%--</tr>--%>
                    <%--</thead>--%>
                    <%--<tbody>--%>
                        <%--<c:forEach var="user" items="${groupMembers}">--%>
                            <%--<spring:url var="photoUrl" value="/user/photo/{username}?s=50">--%>
                                <%--<spring:param name="username" value="${user.username}"/>--%>
                            <%--</spring:url>--%>
                            <%--<tr>--%>
                                <%--<td><img src="${photoUrl}"/></td>--%>
                                <%--<td><c:out value="${user.username}"/></td>--%>
                                <%--<td><c:out value="${user.displayName}"/></td>--%>
                                <%--<td><a href="#">Remover</a></td>--%>
                            <%--</tr>--%>
                        <%--</c:forEach>--%>
                    <%--</tbody>--%>
                <%--</table>--%>
        </div>
    </c:if>
</section>

<c:if test="${not empty error}">
    <section>
        <ul class="nobullet list6">
            <li><span class="error0"><c:out value="${error}"/></span></li>
        </ul>
    </section>
</c:if>

<section>
    <c:if test="${not empty scientificAreaUnits}">
        <p class="mtop2 mbottom05"><b><spring:message code="label.listing.options" text="Opções de listagem:"/></b></p>
        <ul>
            <spring:url context="" var="baseListingUrl" value="/scientificCouncil/competenceCourses/showAllCompetenceCourses.faces?selectedDepartmentUnitID={departmentUnit}">
                <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
            </spring:url>
            <spring:url context="" var="draftListingUrl" value="${baseListingUrl}&competenceCoursesToList=DRAFT"/>
            <spring:url context="" var="approvedListingUrl" value="${baseListingUrl}&competenceCoursesToList=APPROVED"/>
            <spring:url context="" var="publishedListingUrl" value="${baseListingUrl}&competenceCoursesToList=PUBLISHED"/>
            
            <li>
                <a target="_blank" href="${fr:checksumLink(pageContext.request, draftListingUrl)}"><c:out value="${fr:message('resources/ScientificCouncilResources', 'showDraftCompetenceCourses')} (${fr:message('resources/ScientificCouncilResources', 'newPage')})"/></a>
            </li>
            <li>
                <a target="_blank" href="${fr:checksumLink(pageContext.request, approvedListingUrl)}"><c:out value="${fr:message('resources/ScientificCouncilResources', 'showApprovedCompetenceCourses')} (${fr:message('resources/ScientificCouncilResources', 'newPage')})"/></a>
            </li>
            <li>
                <a target="_blank" href="${fr:checksumLink(pageContext.request, publishedListingUrl)}"><c:out value="${fr:message('resources/ScientificCouncilResources', 'showPublishedCompetenceCourses')} (${fr:message('resources/ScientificCouncilResources', 'newPage')})"/></a>
            </li>
            
        </ul>
        <table>
            <tbody>
            <c:forEach var="scientificAreaUnit" items="${scientificAreaUnits}">
                    <tr>
                        <td>
                            <p class="mtop2 mbottom0">
                                <strong><c:out value="${scientificAreaUnit.name}"/></strong>
                            </p>
                            <c:forEach var="competenceCourseGroupUnit" items="${scientificAreaUnit.competenceCourseGroupUnits}">
                                <ul class="list3">
                                    <table style="width: 100%">
                                        <tbody>
                                            <tr>
                                                <td>
                                                    <li class="tree_label" style="background-position: 0em 0.5em;"><c:out value="${competenceCourseGroupUnit.name}"/>
                                                    <table class="showinfo1 smallmargin mtop05" style="width: 100%;">
                                                        <tbody>
                                                            <c:forEach var="competenceCourse" items="${competenceCourseGroupUnit.competenceCourses}">
                                                                <tr class="color2">
                                                                    <td>
                                                                        <c:if test="${empty competenceCourse.code}">
                                                                            <c:out value="${competenceCourse.name}"/>
                                                                        </c:if>
                                                                        <c:if test="${not empty competenceCourse.code}">
                                                                            <c:out value='${competenceCourse.code} - ${competenceCourse.name}'/>
                                                                        </c:if>
                                                                        <span><em class="${fn:toLowerCase(competenceCourse.curricularStage)}">${fr:message('resources/EnumerationResources', competenceCourse.curricularStage)}</em></span>
                                                                    </td>
                                                                    <td class="aright nowrap">
                                                                        <spring:url var="showCompetenceUrl" context="" value="/scientificCouncil/competenceCourses/showCompetenceCourse.faces?action=ccm&competenceCourseID={competenceCourse}&selectedDepartmentUnitID={departmentUnit}">
                                                                            <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                                                                            <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
                                                                        </spring:url>
                                                                        <spring:url var="transferCompetenceUrl" context="" value="/scientificCouncil/competenceCourses/transferCompetenceCourse.faces?competenceCourseID={competenceCourse}&selectedDepartmentUnitID={departmentUnit}">
                                                                            <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                                                                            <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
                                                                        </spring:url>
                                                                        <spring:url var="toggleStateUrl" value="/scientific-council/competence-courses/toggle?competenceCourse={competenceCourse}&departmentUnit={departmentUnit}">
                                                                            <spring:param name="competenceCourse" value="${competenceCourse.externalId}"/>
                                                                            <spring:param name="departmentUnit" value="${departmentUnit.externalId}"/>
                                                                        </spring:url>
                                                                        <a href="${fr:checksumLink(pageContext.request, showCompetenceUrl)}"><spring:message code="label.show" text="Ver"/></a>
                                                                        <a href="${fr:checksumLink(pageContext.request, transferCompetenceUrl)}"><spring:message code="label.transfer" text="Transferir"/></a>
                                                                        <c:if test="${competenceCourse.curricularStage.name == 'APPROVED'}">
                                                                            <a href="${toggleStateUrl}"><spring:message code="label.approved.toggle" text="Desaprovar"/></a>
                                                                        </c:if>
                                                                        <c:if test="${competenceCourse.curricularStage.name == 'PUBLISHED'}">
                                                                            <a href="${toggleStateUrl}"><spring:message code="label.published.toggle" text="Aprovar"/></a>
                                                                        </c:if>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                    </li>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </ul>
                            </c:forEach>
                        </td>
                    </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</section>
