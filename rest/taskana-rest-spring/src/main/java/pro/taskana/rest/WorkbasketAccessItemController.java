package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.BaseQuery;
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.WorkbasketAccessItemExtendedQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.WorkbasketAccesItemExtendedResource;
import pro.taskana.rest.resource.assembler.WorkbasketAccessItemExtendedAssembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for Workbasket access.
 */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RequestMapping(path = "/v1/workbasket-access", produces = "application/hal+json")
public class WorkbasketAccessItemController extends AbstractPagingController {

    private static final String LIKE = "%";
    private static final String WORKBASKET_KEY = "workbasket-key";
    private static final String WORKBASKET_KEY_LIKE = "workbasket-key-like";
    private static final String ACCESS_ID = "access-id";
    private static final String ACCESS_ID_LIKE = "access-id-like";
    private static final String ACCESS_IDS = "access-ids";

    private static final String SORT_BY = "sort-by";
    private static final String SORT_DIRECTION = "order";

    private static final String PAGING_PAGE = "page";
    private static final String PAGING_PAGE_SIZE = "page-size";

    @Autowired
    private WorkbasketService workbasketService;

    @GetMapping
    public ResponseEntity<PagedResources<WorkbasketAccesItemExtendedResource>> getWorkbasketAccessItems(
            @RequestParam MultiValueMap<String, String> params)
            throws NotAuthorizedException, InvalidArgumentException {

        WorkbasketAccessItemExtendedQuery query = workbasketService.createWorkbasketAccessItemExtendedQuery();
        query = getAccessIds(query, params);
        query = applyFilterParams(query, params);
        query = applySortingParams(query, params);

        PagedResources.PageMetadata pageMetadata = null;
        List<WorkbasketAccessItemExtended> workbasketAccessItemsExtended;
        String page = params.getFirst(PAGING_PAGE);
        String pageSize = params.getFirst(PAGING_PAGE_SIZE);
        params.remove(PAGING_PAGE);
        params.remove(PAGING_PAGE_SIZE);
        validateNoInvalidParameterIsLeft(params);
        if (page != null && pageSize != null) {
            // paging
            long totalElements = query.count();
            pageMetadata = initPageMetadata(pageSize, page, totalElements);
            workbasketAccessItemsExtended = query.listPage((int) pageMetadata.getNumber(),
                    (int) pageMetadata.getSize());
        } else if (page == null && pageSize == null) {
            // not paging
            workbasketAccessItemsExtended = query.list();
        } else {
            throw new InvalidArgumentException("Paging information is incomplete.");
        }

        WorkbasketAccessItemExtendedAssembler assembler = new WorkbasketAccessItemExtendedAssembler();
        PagedResources<WorkbasketAccesItemExtendedResource> pagedResources = assembler.toResources(workbasketAccessItemsExtended,
                pageMetadata);

        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }

    private WorkbasketAccessItemExtendedQuery getAccessIds(WorkbasketAccessItemExtendedQuery query,
                                                           MultiValueMap<String, String> params) throws InvalidArgumentException {
        if (params.containsKey(ACCESS_IDS)) {
            String[] accessIds = extractVerticalBarSeparatedFields(params.get(ACCESS_IDS));
            query.accessIdIn(accessIds);
            params.remove(ACCESS_IDS);
        }
        return query;
    }

    private WorkbasketAccessItemExtendedQuery applyFilterParams(WorkbasketAccessItemExtendedQuery query,
                                                                MultiValueMap<String, String> params) throws InvalidArgumentException {
        if (params.containsKey(WORKBASKET_KEY)) {
            String[] keys = extractCommaSeparatedFields(params.get(WORKBASKET_KEY));
            query.workbasketKeyIn(keys);
            params.remove(WORKBASKET_KEY);
        }
        if (params.containsKey(WORKBASKET_KEY_LIKE)) {
            query.workbasketKeyLike(LIKE + params.get(WORKBASKET_KEY_LIKE).get(0) + LIKE);
            params.remove(WORKBASKET_KEY_LIKE);
        }
        if (params.containsKey(ACCESS_ID)) {
            String[] accessId = extractCommaSeparatedFields(params.get(ACCESS_ID));
            query.accessIdIn(accessId);
            params.remove(ACCESS_ID);
        }
        if (params.containsKey(ACCESS_ID_LIKE)) {
            query.accessIdLike(LIKE + params.get(ACCESS_ID_LIKE).get(0) + LIKE);
            params.remove(ACCESS_ID_LIKE);
        }
        return query;
    }

    private WorkbasketAccessItemExtendedQuery applySortingParams(WorkbasketAccessItemExtendedQuery query, MultiValueMap<String, String> params)
            throws IllegalArgumentException {
        // sorting
        String sortBy = params.getFirst(SORT_BY);
        if (sortBy != null) {
            BaseQuery.SortDirection sortDirection;
            if (params.getFirst(SORT_DIRECTION) != null && "desc".equals(params.getFirst(SORT_DIRECTION))) {
                sortDirection = BaseQuery.SortDirection.DESCENDING;
            } else {
                sortDirection = BaseQuery.SortDirection.ASCENDING;
            }
            switch (sortBy) {
                case (WORKBASKET_KEY):
                    query = query.orderByWorkbasketKey(sortDirection);
                    break;
                case (ACCESS_ID):
                    query = query.orderByAccessId(sortDirection);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown order '" + sortBy + "'");
            }
        }
        params.remove(SORT_BY);
        params.remove(SORT_DIRECTION);
        return query;
    }

    private String[] extractVerticalBarSeparatedFields(List<String> searchFor) {
        List<String> values = new ArrayList<>();
        if (searchFor != null) {
            searchFor.forEach(item -> values.addAll(Arrays.asList(item.split("\\|"))));
        }
        return values.toArray(new String[0]);
    }

}
