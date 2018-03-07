package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

/**
 * Base class for page list resources.
 */
public class AbstractPageableListResource extends ResourceSupport {

    private final int pageNumber;

    private final int pageSize;

    private final int totalPages;

    private final long totalElements;

    /**
     * Creates new instance of {@link AbstractPageableListResource}.
     *
     * @param pageNumber
     *            actual page of the collection.
     * @param pageSize
     *            number of elements per pages of the collection.
     * @param totalPages
     *            total number of pages of the collection.
     * @param totalElements
     *            total number of elements of the collection.
     */
    public AbstractPageableListResource(int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    /**
     * Default constructor for a list without paging.
     */
    public AbstractPageableListResource() {
        this.pageNumber = -1;
        this.pageSize = -1;
        this.totalPages = -1;
        this.totalElements = -1;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
