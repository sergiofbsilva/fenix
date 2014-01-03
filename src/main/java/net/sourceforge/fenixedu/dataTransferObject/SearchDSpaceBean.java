package net.sourceforge.fenixedu.dataTransferObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.fenix.tools.file.FilesetMetadataQuery.MetadataQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class SearchDSpaceBean implements Serializable {

    protected static final int pageSize = 15;

    List<SearchElement> searchElements;
    transient JsonArray results;

    private int page;
    private int totalItems;
    private int numberOfPages;

    public SearchDSpaceBean() {
        this.searchElements = new ArrayList<SearchElement>();
        this.results = new JsonArray();
        this.page = 1;
        this.totalItems = 0;
        this.numberOfPages = 1;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getSearchElementsSize() {
        return this.searchElements.size();
    }

    public List<SearchElement> getSearchElements() {
        return searchElements;
    }

    public void setSearchElements(List<SearchElement> searchElements) {
        this.searchElements = searchElements;
    }

    public void addSearchElement() {
        this.searchElements.add(SearchElement.emptySearchElement());
    }

    public void addSearchElement(Integer index) {
        this.searchElements.add(index, SearchElement.emptySearchElement());
    }

    public void addSearchElement(SearchElement element) {
        this.searchElements.add(element);
    }

    public void removeSearchElement(int i) {
        this.searchElements.remove(i);
    }

    public String getSearchElementsAsParameters() {
        String parameters = "";

        for (SearchElement element : getSearchElements()) {
            parameters +=
                    "&amp;criteria=" + element.getConjunction() + ":" + element.getSearchField() + ":" + element.getQueryValue();
        }
        return parameters;
    }

    protected boolean hasSearchElements() {
        for (SearchElement element : getSearchElements()) {
            if (element.queryValue.length() > 0) {
                return true;
            }
        }
        return false;
    }

    public void setResults(JsonArray results) {
        this.results = results;
    }

    public void setResults(String results) {
        JsonParser p = new JsonParser();
        this.results = (JsonArray) p.parse(results);
    }

    public String getResults() {
        return this.results.toString();
    }

    public static class SearchElement implements Serializable {

        public static enum SearchField {
            AUTHOR("author"),
            COURSE("author"), // on executionCourses the author of the file is the course
            TITLE("title"), KEYWORD("keyword"), DATE("date"), PUBLISHER("publisher"), DESCRIPTION("description"), TYPE("type"),
            INFORMATIONS("informations"), UNIT("unit"), ANY(MetadataQuery.ANY_FIELD);

            private String field;

            private SearchField(String name) {
                field = name;
            }

            public String fieldName() {
                return field;
            }

            public static List<SearchField> getSearchFieldsInResearchPublications() {
                List<SearchField> fields = new ArrayList<SearchField>();
                fields.add(SearchField.AUTHOR);
                fields.add(SearchField.TITLE);
                fields.add(SearchField.DATE);
                fields.add(SearchField.PUBLISHER);
                fields.add(SearchField.DESCRIPTION);
                fields.add(SearchField.ANY);
                return fields;
            }

        }

        public static enum ConjunctionType {
            OR, AND;

        }

        private SearchField field;
        private String queryValue;
        private ConjunctionType conjunction;

        private SearchElement() {
            super();
        }

        public SearchElement(SearchField field, String queryValue, ConjunctionType type) {
            this();
            this.field = field;
            this.queryValue = queryValue;
            this.conjunction = type;
        }

        public SearchField getSearchField() {
            return (this.field != null) ? this.field : SearchField.ANY;
        }

        public void setSearchField(SearchField field) {
            this.field = field;
        }

        public String getQueryValue() {
            return this.queryValue;
        }

        public void setQueryValue(String queryValue) {
            this.queryValue = queryValue;
        }

        public static SearchElement emptySearchElement() {
            return new SearchElement(SearchField.ANY, " ", ConjunctionType.AND);
        }

        public ConjunctionType getConjunction() {
            return conjunction;
        }

        public void setConjunction(ConjunctionType type) {
            this.conjunction = type;
        }
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

}
