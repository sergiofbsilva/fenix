<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/datetime-1.0" prefix="date"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/collection-pager" prefix="cp" %>

<em><bean:message key="label.researchPortal" bundle="RESEARCHER_RESOURCES"/></em>
<h2><bean:message bundle="RESEARCHER_RESOURCES" key="label.search"/></h2>
<bean:define id="bean" name="bean" type="net.sourceforge.fenixedu.dataTransferObject.SearchDSpacePublicationBean"/>

	<div class="infoop2">
		<bean:message key="label.search.description" bundle="RESEARCHER_RESOURCES"/> 
	</div>

	<fr:form id="searchForm" action="/publications/search.do">
		<html:hidden property="method" value="searchPublication"/>
		<html:hidden property="addIndex" value=""/>
		<html:hidden property="removeIndex" value=""/>
		
		<fr:hasMessages for="search" type="validation">
			<p>
			<span class="error0"><bean:message key="label.requiredFieldsNotPresent"/></span>
			</p>
		</fr:hasMessages>
	
		<fr:edit id="search" name="bean" visible="false"/>


	

		<table class="tstyle5 thlight thright thmiddle">
		<tr>
			<th><bean:message key="label.type" bundle="RESEARCHER_RESOURCES"/>:</th>
			<td>
				<fr:edit id="searchPublication" name="bean" slot="searchPublications"/>
				<label for="searchPublication"><bean:message key="link.Publications" bundle="RESEARCHER_RESOURCES"/></label>
				<fr:edit id="searchPatent" name="bean" slot="searchPatents"/>
				<label for="searchPatent"><bean:message key="researcher.viewCurriculum.patentsTitle" bundle="RESEARCHER_RESOURCES"/></label>
			</td>
		</tr>
		<logic:iterate id="searchElement" indexId="index" name="bean" property="searchElements">
		<tr>
			<th>
				<logic:equal name="index" value="0">
					<bean:message key="label.searchField"/>:
				</logic:equal>
				
				<logic:notEqual name="index" value="0">
				<fr:edit id="<%="conjunctionType" + index%>" name="searchElement" slot="conjunction">
				<fr:layout>
					<fr:property name="defaultText" value=""/>
				</fr:layout>
				</fr:edit>
				</logic:notEqual>
				
			</th>
			<td>
				<fr:edit id="<%="valueField" + index%>" name="searchElement" slot="queryValue" >
					<fr:layout>
						<fr:property name="size" value="40"/>
					</fr:layout>
				</fr:edit>

				<bean:message key="label.in" bundle="APPLICATION_RESOURCES"/>
				<fr:edit id="<%= "searchTypeField" + index%>" name="searchElement" slot="searchField">
				<fr:layout>
					<fr:property name="excludedValues" value="TYPE, DATE, COURSE, INFORMATIONS"/>
					<fr:property name="sort" value="true"/>
				</fr:layout>
				</fr:edit>


 
				<logic:equal name="index" value="0">
					<div class="switchNone">
					<html:link page="<%="/publications/search.do?method=addNewSearchCriteria" + bean.getSearchElementsAsParameters() %>"><bean:message key="label.add" bundle="APPLICATION_RESOURCES"/></html:link>			
					<logic:greaterThan name="bean" property="searchElementsSize" value="1">
					 , 
					<html:link page="<%="/publications/search.do?method=removeSearchCriteria" + bean.getSearchElementsAsParameters() %>"><bean:message key="label.remove" bundle="APPLICATION_RESOURCES"/></html:link>								
					</logic:greaterThan>
					</div>
					<div class="switchInline">
					<a href="#" onclick="<%= "javascript:getElementById('searchForm').method.value='addNewSearchCriteria';getElementById('searchForm').addIndex.value='" + (index+1) + "';getElementById('searchForm').submit();" %>"><bean:message key="label.add" bundle="APPLICATION_RESOURCES"/></a>
					<logic:greaterThan name="bean" property="searchElementsSize" value="1">
					 , 
					<a href="#" onclick="<%= "javascript:getElementById('searchForm').method.value='removeSearchCriteria';getElementById('searchForm').removeIndex.value='" + index + "';getElementById('searchForm').submit();" %>"><bean:message key="label.remove" bundle="APPLICATION_RESOURCES"/></a>
					</logic:greaterThan>
					</div>
				</logic:equal>
				<logic:notEqual name="index" value="0">
					<div class="switchNone">
					<html:link page="<%="/publications/search.do?method=addNewSearchCriteria" + bean.getSearchElementsAsParameters() %>"><bean:message key="label.add" bundle="APPLICATION_RESOURCES"/></html:link> , 			
					<html:link page="<%="/publications/search.do?method=removeSearchCriteria" + bean.getSearchElementsAsParameters() + "&amp;removeIndex=" + index%>"><bean:message key="link.remove" bundle="APPLICATION_RESOURCES"/></html:link>
					</div>
					<div class="switchInline">
					<a href="#" onclick="<%= "javascript:getElementById('searchForm').method.value='addNewSearchCriteria'; getElementById('searchForm').addIndex.value='" + (index+1) +  "'; getElementById('searchForm').submit();" %>"><bean:message key="label.add" bundle="APPLICATION_RESOURCES"/></a> , 
					<a href="#" onclick="<%= "javascript:getElementById('searchForm').method.value='removeSearchCriteria'; getElementById('searchForm').removeIndex.value='" + index + "'; getElementById('searchForm').submit();"%>"><bean:message key="link.remove" bundle="APPLICATION_RESOURCES"/></a>
					</div>
				</logic:notEqual>
			</td>
		</tr>
		</logic:iterate>


		</table>
			<html:submit><bean:message key="label.search" /></html:submit>
	</fr:form>



<script type="text/javascript" language="javascript">
switchGlobal();
</script>

