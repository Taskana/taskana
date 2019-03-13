package pro.taskana.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.ldap.LdapClient;
import pro.taskana.rest.resource.WorkbasketAccessItemResourceAssembler;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Controller for Workbasket access.
 */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RequestMapping(path = "/v1/workbasket-access-items", produces = "application/hal+json")
public class WorkbasketAccessItemController extends AbstractPagingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketAccessItemController.class);

    private static final String LIKE = "%";
    private static final String WORKBASKET_KEY = "workbasket-key";
    private static final String WORKBASKET_KEY_LIKE = "workbasket-key-like";
    private static final String ACCESS_ID = "access-id";
    private static final String ACCESS_ID_LIKE = "access-id-like";
    private static final String ACCESS_IDS = "access-ids";

    private static final String SORT_BY = "sort-by";
    private static final String SORT_DIRECTION = "order";

    @Autowired
    LdapClient ldapClient;

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketAccessItemResourceAssembler workbasketAccessItemResourceAssembler;

    /**
     * This GET method return all workbasketAccessItems that correspond the given data.
     *
     * @param params filter, order and access ids.
     * @return all WorkbasketAccesItemResource.
     * @throws NotAuthorizedException   if the user is not authorized.
     * @throws InvalidArgumentException if some argument is invalid.
     */
    @GetMapping
    public ResponseEntity<PagedResources<WorkbasketAccessItemResource>> getWorkbasketAccessItems(
        @RequestParam MultiValueMap<String, String> params)
        throws NotAuthorizedException, InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entry to getWorkbasketAccessItems(params= {})", params);
        }

        WorkbasketAccessItemQuery query = workbasketService.createWorkbasketAccessItemQuery();
        query = getAccessIds(query, params);
        query = applyFilterParams(query, params);
        query = applySortingParams(query, params);

        PagedResources.PageMetadata pageMetadata = getPageMetadata(params, query);
        List<WorkbasketAccessItem> workbasketAccessItems = (List<WorkbasketAccessItem>) getQueryList(query,
            pageMetadata);

        PagedResources pagedResources = workbasketAccessItemResourceAssembler.toResources(
            workbasketAccessItems,
            pageMetadata
        );

        ResponseEntity<PagedResources<WorkbasketAccessItemResource>> response = new ResponseEntity<>(pagedResources, HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getWorkbasketAccessItems(), returning {}", response);
        }

        return response;
    }

    /**
     * This DELETE method delete all workbasketAccessItems that correspond the given accessId.
     *
     * @param accessId which need remove his workbasketAccessItems.
     * @return ResponseEntity if the user is not authorized.
     * @throws NotAuthorizedException if the user is not authorized.
     * @throws InvalidArgumentException if some argument is invalid.
     */
    @DeleteMapping
    public ResponseEntity<Void> removeWorkbasketAccessItems(
        @RequestParam("access-id") String accessId)
        throws NotAuthorizedException, InvalidArgumentException {
        LOGGER.debug("Entry to removeWorkbasketAccessItems(access-id= {})", accessId);
        if (!ldapClient.isGroup(accessId)) {
            List<WorkbasketAccessItem> workbasketAccessItemList = workbasketService.createWorkbasketAccessItemQuery()
                .accessIdIn(accessId)
                .list();

            if (workbasketAccessItemList != null && !workbasketAccessItemList.isEmpty()) {
                workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);
            }
        } else {
            throw new InvalidArgumentException(
                accessId + " corresponding to a group, not a user. You just can remove access items for a user");
        }

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        LOGGER.debug("Exit from removeWorkbasketAccessItems(), returning {}", response);
        return response;
    }

    private WorkbasketAccessItemQuery getAccessIds(WorkbasketAccessItemQuery query,
        MultiValueMap<String, String> params) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entry to getAccessIds(query= {}, params= {})", params);
        }

        if (params.containsKey(ACCESS_IDS)) {
            String[] accessIds = extractVerticalBarSeparatedFields(params.get(ACCESS_IDS));
            query.accessIdIn(accessIds);
            params.remove(ACCESS_IDS);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getAccessIds(), returning {}", query);
        }

        return query;
    }

    private WorkbasketAccessItemQuery applyFilterParams(WorkbasketAccessItemQuery query,
        MultiValueMap<String, String> params) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entry to applyFilterParams(query= {}, params= {})", params);
        }

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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
        }

        return query;
    }

    private WorkbasketAccessItemQuery applySortingParams(WorkbasketAccessItemQuery query,
        MultiValueMap<String, String> params)
        throws IllegalArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Entry to applySortingParams(query= {}, params= {})", params);
        }

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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from applySortingParams(), returning {}", query);
        }

        return query;
    }

    private String[] extractVerticalBarSeparatedFields(List<String> searchFor) {
        List<String> values = new ArrayList<>();
        if (searchFor != null) {
            searchFor.forEach(item -> Collections.addAll(values, item.split("\\|")));
        }
        return values.toArray(new String[0]);
    }

}
