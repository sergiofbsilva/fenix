package net.sourceforge.fenixedu.presentationTier.Action.research.result.publication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.SearchDSpaceBean;
import net.sourceforge.fenixedu.dataTransferObject.SearchDSpacePublicationBean;
import net.sourceforge.fenixedu.presentationTier.Action.SearchDSpaceGeneralAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.annotations.Tile;

@Mapping(module = "researcher", path = "/publications/search", scope = "session", parameter = "method")
@Forwards(
        value = { @Forward(name = "SearchPublication", path = "/researcher/result/publications/searchPublication.jsp",
                tileProperties = @Tile(
                        title = "private.operator.personnelmanagement.managementfaculty.teacherevaluation.publications")) })
public class SearchPublicationsAction extends SearchDSpaceGeneralAction {

    public ActionForward addNewSearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {
        return super.addNewSearchCriteria(mapping, form, request, response, "SearchPublication");
    }

    public ActionForward removeSearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {
        return super.removeSearchCriteria(mapping, form, request, response, "SearchPublication");
    }

    public ActionForward prepareSearchPublication(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        return super.prepareSearch(mapping, form, request, response, "SearchPublication");
    }

    public ActionForward moveIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        return super.moveIndex(mapping, form, request, response, "SearchPublication");

    }

    public ActionForward searchPublication(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        return super.searchContent(mapping, form, request, response, "SearchPublication");
    }

    @Override
    protected SearchDSpaceBean createNewBean() {
        return new SearchDSpacePublicationBean();
    }

    @Override
    protected SearchDSpaceBean reconstructBeanFromRequest(HttpServletRequest request) {
        SearchDSpacePublicationBean bean = (SearchDSpacePublicationBean) super.reconstructBeanFromRequest(request);
        String searchPublications = request.getParameter("searchPublications");
        String searchPatents = request.getParameter("searchPatents");

        bean.setSearchPublications(Boolean.valueOf(searchPublications));
        bean.setSearchPatents(Boolean.valueOf(searchPatents));
        return bean;
    }

}
