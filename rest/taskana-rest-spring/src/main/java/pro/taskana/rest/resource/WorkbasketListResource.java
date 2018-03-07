package pro.taskana.rest.resource;

import java.util.Collection;

/**
 * Resource for workbasket lists.
 */
public class WorkbasketListResource extends AbstractPageableListResource {

    private final Collection<WorkbasketSummaryResource> workbasketSummaries;

    /**
     * Create a paged list of workbaskets.
     *
     * @param workbasketSummaries
     *            the workbasket summaries
     * @param pageNumber
     *            the current page number
     * @param pageSize
     *            the size of the pages
     * @param totalPages
     *            the total number of pages
     * @param totalElements
     *            the total number of elements
     */
    public WorkbasketListResource(Collection<WorkbasketSummaryResource> workbasketSummaries, int pageNumber,
        int pageSize,
        int totalPages, long totalElements) {
        super(pageNumber, pageSize, totalPages, totalElements);
        this.workbasketSummaries = workbasketSummaries;
    }

    /**
     * Create a unpaged list of workbaskets.
     *
     * @param workbasketSummaries
     *            the workbasket summaries
     */
    public WorkbasketListResource(Collection<WorkbasketSummaryResource> workbasketSummaries) {
        this.workbasketSummaries = workbasketSummaries;
    }

    public Collection<WorkbasketSummaryResource> getWorkbasketSummaries() {
        return workbasketSummaries;
    }

}
