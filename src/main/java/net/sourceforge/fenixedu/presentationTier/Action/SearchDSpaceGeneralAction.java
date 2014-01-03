package net.sourceforge.fenixedu.presentationTier.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.SearchDSpaceBean;
import net.sourceforge.fenixedu.dataTransferObject.SearchDSpaceBean.SearchElement;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.gson.JsonArray;

public abstract class SearchDSpaceGeneralAction extends FenixDispatchAction {

    public class FileSearchResult {

        private JsonArray results;
        private int pageSize;
        private int start;
        private int totalElements;

        public FileSearchResult(JsonArray results) {
            this(results, 0, 25, results.size());
        }

        public FileSearchResult(JsonArray results, int start, int pageSize, Integer totalElements) {
            this.results = results;
            this.start = start;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
        }

        public String getSearchResults() {
            return results.toString();
        }

        public Boolean hasMoreElements() {
            return (start + pageSize) < totalElements;
        }

        public Integer getTotalElements() {
            return totalElements;
        }

        public Integer getPageSize() {
            return pageSize;
        }

    }

    protected ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, String forwardTo) throws FenixServiceException {

        SearchDSpaceBean bean = createNewBean();
        bean.addSearchElement();
        request.setAttribute("bean", bean);
        return mapping.findForward(forwardTo);
    }

    protected ActionForward addNewSearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, String forwardTo) throws FenixServiceException {

        SearchDSpaceBean bean = getBean(request);
        String addIndex = request.getParameter("addIndex");
        if (addIndex == null) {
            bean.addSearchElement();
        } else {
            bean.addSearchElement(Integer.valueOf(addIndex));
        }
        request.setAttribute("bean", bean);
        request.setAttribute("pageNumber", bean.getPage());
        request.setAttribute("numberOfPages", bean.getNumberOfPages());
        return mapping.findForward(forwardTo);
    }

    protected ActionForward removeSearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, String forwardTo) throws FenixServiceException {

        SearchDSpaceBean bean = getBean(request);
        String removeIndex = request.getParameter("removeIndex");
        bean.removeSearchElement(Integer.valueOf(removeIndex));

        request.setAttribute("bean", bean);
        request.setAttribute("pageNumber", bean.getPage());
        request.setAttribute("numberOfPages", bean.getNumberOfPages());
        return mapping.findForward(forwardTo);
    }

    protected SearchDSpaceBean getBean(HttpServletRequest request) {
        SearchDSpaceBean bean;
        IViewState viewState = RenderUtils.getViewState("search");
        if (viewState == null) {
            bean = reconstructBeanFromRequest(request);
        } else {
            bean = (SearchDSpaceBean) viewState.getMetaObject().getObject();
            RenderUtils.invalidateViewState();
        }
        return bean;
    }

    protected ActionForward moveIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, String forwardTo) throws FenixServiceException {

        SearchDSpaceBean bean = reconstructBeanFromRequest(request);

        FileSearchResult searchResults = getSearchResults(request, bean);
        bean.setResults(searchResults.getSearchResults());

        request.setAttribute("bean", bean);
        return mapping.findForward(forwardTo);

    }

    protected ActionForward searchContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, String forwardTo) throws FenixServiceException {
        IViewState viewState = RenderUtils.getViewState("search");
        if (viewState == null) {
            return prepareSearch(mapping, form, request, response, forwardTo);
        }
        SearchDSpaceBean bean = (SearchDSpaceBean) viewState.getMetaObject().getObject();
        FileSearchResult searchResults = getSearchResults(request, bean);
        bean.setResults(searchResults.getSearchResults());

        request.setAttribute("bean", bean);
        return mapping.findForward(forwardTo);
    }

    protected FileSearchResult getSearchResults(HttpServletRequest request, SearchDSpaceBean bean) {
        String index = request.getParameter("pageNumber");
        Integer start = (index == null) ? 1 : Integer.valueOf(index);
        Integer searchOffset = (start - 1) * bean.getPageSize();
        //TODO: invoke sotis instead of dspace
        FileSearchResult searchResults = new FileSearchResult(new JsonArray());
        request.setAttribute("pageNumber", start);
        bean.setPage(start);
        bean.setTotalItems(searchResults.getTotalElements());
        int numberOfPages = searchResults.getTotalElements() / searchResults.getPageSize();
        numberOfPages += (searchResults.getTotalElements() % searchResults.getPageSize() != 0) ? 1 : 0;
        request.setAttribute("numberOfPages", numberOfPages);
        bean.setNumberOfPages(numberOfPages);
        return searchResults;
    }

    protected SearchDSpaceBean createNewBean() {
        return new SearchDSpaceBean();
    }

    protected SearchDSpaceBean reconstructBeanFromRequest(HttpServletRequest request) {
        SearchDSpaceBean bean = createNewBean();

        String values[] = request.getParameterValues("criteria");
        for (String value : values) {
            String fields[] = value.split(":");
            bean.addSearchElement(new SearchElement(SearchElement.SearchField.valueOf(fields[1]),
                    ((fields.length == 2) ? "" : fields[2]), SearchElement.ConjunctionType.valueOf(fields[0])));
        }

        return bean;
    }

}