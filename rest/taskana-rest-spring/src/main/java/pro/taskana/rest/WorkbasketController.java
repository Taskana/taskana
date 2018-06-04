package pro.taskana.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;
import pro.taskana.rest.resource.assembler.DistributionTargetListAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketAccessItemListAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketAccessItemAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketSummaryResourcesAssembler;

/**
 * Controller for all {@link Workbasket} related endpoints.
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(path = "/v1/workbaskets", produces = "application/hal+json")
public class WorkbasketController extends AbstractPagingController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String NAME_LIKE = "name-like";
    private static final String KEY = "key";
    private static final String KEY_LIKE = "key-like";
    private static final String OWNER = "owner";
    private static final String OWNER_LIKE = "owner-like";
    private static final String DESCRIPTION_LIKE = "description-like";
    private static final String DOMAIN = "domain";
    private static final String REQUIRED_PERMISSION = "required-permission";
    private static final String TYPE = "type";
    private static final String DESCRIPTION = "description";

    private static final String SORT_BY = "sort-by";
    private static final String SORT_DIRECTION = "order";

    private static final String PAGING_PAGE = "page";
    private static final String PAGING_PAGE_SIZE = "page-size";

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketAssembler workbasketAssembler;

    @Autowired
    private DistributionTargetListAssembler distributionTargetListAssembler;

    @Autowired
    private WorkbasketAccessItemListAssembler accessItemListAssembler;

    @Autowired
    private WorkbasketAccessItemAssembler workbasketAccessItemAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<PagedResources<WorkbasketSummaryResource>> getWorkbaskets(
        @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {

        WorkbasketQuery query = workbasketService.createWorkbasketQuery();
        query = applySortingParams(query, params);
        query = applyFilterParams(query, params);

        PageMetadata pageMetadata = null;
        List<WorkbasketSummary> workbasketSummaries = null;
        String page = params.getFirst(PAGING_PAGE);
        String pageSize = params.getFirst(PAGING_PAGE_SIZE);
        params.remove(PAGING_PAGE);
        params.remove(PAGING_PAGE_SIZE);
        validateNoInvalidParameterIsLeft(params);
        if (page != null && pageSize != null) {
            // paging
            long totalElements = query.count();
            pageMetadata = initPageMetadata(pageSize, page, totalElements);
            workbasketSummaries = query.listPage((int) pageMetadata.getNumber(),
                (int) pageMetadata.getSize());
        } else if (page == null && pageSize == null) {
            // not paging
            workbasketSummaries = query.list();
        } else {
            throw new InvalidArgumentException("Paging information is incomplete.");
        }

        WorkbasketSummaryResourcesAssembler assembler = new WorkbasketSummaryResourcesAssembler();
        PagedResources<WorkbasketSummaryResource> pagedResources = assembler.toResources(workbasketSummaries,
            pageMetadata);

        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }

    @GetMapping(path = "/{workbasketId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<WorkbasketResource> getWorkbasket(@PathVariable(value = "workbasketId") String workbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        ResponseEntity<WorkbasketResource> result;
        Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
        result = new ResponseEntity<>(workbasketAssembler.toResource(workbasket), HttpStatus.OK);
        return result;
    }

    @DeleteMapping(path = "/{workbasketId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteWorkbasket(@PathVariable(value = "workbasketId") String workbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        ResponseEntity<?> result = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        workbasketService.deleteWorkbasket(workbasketId);
        return result;
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<WorkbasketResource> createWorkbasket(@RequestBody WorkbasketResource workbasketResource)
        throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
        WorkbasketNotFoundException, DomainNotFoundException {
        Workbasket workbasket = workbasketAssembler.toModel(workbasketResource);
        workbasket = workbasketService.createWorkbasket(workbasket);
        return new ResponseEntity<>(workbasketAssembler.toResource(workbasket), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{workbasketId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<WorkbasketResource> updateWorkbasket(
        @PathVariable(value = "workbasketId") String workbasketId,
        @RequestBody WorkbasketResource workbasketResource)
        throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException {
        ResponseEntity<WorkbasketResource> result;
        if (workbasketId.equals(workbasketResource.workbasketId)) {
            Workbasket workbasket = workbasketAssembler.toModel(workbasketResource);
            workbasket = workbasketService.updateWorkbasket(workbasket);
            result = ResponseEntity.ok(workbasketAssembler.toResource(workbasket));
        } else {
            throw new InvalidWorkbasketException(
                "Target-WB-ID('" + workbasketId
                    + "') is not identical with the WB-ID of to object which should be updated. ID=('"
                    + workbasketResource.getId() + "')");
        }

        return result;
    }

    @GetMapping(path = "/{workbasketId}/workbasketAccessItems")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Resources<WorkbasketAccessItemResource>> getWorkbasketAccessItems(
        @PathVariable(value = "workbasketId") String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException {

        ResponseEntity<Resources<WorkbasketAccessItemResource>> result;

        List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAccessItems(workbasketId);
        Resources<WorkbasketAccessItemResource> accessItemListResource = accessItemListAssembler
            .toResource(workbasketId, accessItems);
        result = new ResponseEntity<>(accessItemListResource, HttpStatus.OK);
        return result;
    }

    @PutMapping(value = "/{workbasketId}/workbasketAccessItems")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Resources<WorkbasketAccessItemResource>> setWorkbasketAccessItems(
        @PathVariable(value = "workbasketId") String workbasketId,
        @RequestBody List<WorkbasketAccessItemResource> workbasketAccessResourceItems)
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
        if (workbasketAccessResourceItems == null) {
            throw new InvalidArgumentException("Can´t create something with NULL body-value.");
        }

        List<WorkbasketAccessItem> wbAccessItems = new ArrayList<>();
        workbasketAccessResourceItems.forEach(item -> wbAccessItems.add(workbasketAccessItemAssembler.toModel(item)));
        workbasketService.setWorkbasketAccessItems(workbasketId, wbAccessItems);

        List<WorkbasketAccessItem> updatedWbAccessItems = workbasketService.getWorkbasketAccessItems(workbasketId);
        Resources<WorkbasketAccessItemResource> accessItemListResource = accessItemListAssembler
            .toResource(workbasketId, updatedWbAccessItems);

        return new ResponseEntity<>(accessItemListResource, HttpStatus.OK);
    }

    @GetMapping(path = "/{workbasketId}/distribution-targets")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Resources<DistributionTargetResource>> getDistributionTargets(
        @PathVariable(value = "workbasketId") String workbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException {

        ResponseEntity<Resources<DistributionTargetResource>> result;
        List<WorkbasketSummary> distributionTargets = workbasketService.getDistributionTargets(workbasketId);
        Resources<DistributionTargetResource> distributionTargetListResource = distributionTargetListAssembler
            .toResource(workbasketId, distributionTargets);
        result = new ResponseEntity<>(distributionTargetListResource, HttpStatus.OK);
        return result;
    }

    @PutMapping(path = "/{workbasketId}/distribution-targets")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Resources<DistributionTargetResource>> setDistributionTargetsForWorkbasketId(
        @PathVariable(value = "workbasketId") String sourceWorkbasketId,
        @RequestBody List<String> targetWorkbasketIds) throws WorkbasketNotFoundException, NotAuthorizedException {
        workbasketService.setDistributionTargets(sourceWorkbasketId, targetWorkbasketIds);

        List<WorkbasketSummary> distributionTargets = workbasketService.getDistributionTargets(sourceWorkbasketId);
        Resources<DistributionTargetResource> distributionTargetListResource = distributionTargetListAssembler
            .toResource(sourceWorkbasketId, distributionTargets);

        return new ResponseEntity<>(distributionTargetListResource, HttpStatus.OK);
    }

    @DeleteMapping(path = "/distribution-targets/{workbasketId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Resources<DistributionTargetResource>> removeDistributionTargetForWorkbasketId(
        @PathVariable(value = "workbasketId") String targetWorkbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        List<WorkbasketSummary> sourceWorkbaskets = workbasketService.getDistributionSources(targetWorkbasketId);
        for (WorkbasketSummary source : sourceWorkbaskets) {
            workbasketService.removeDistributionTarget(source.getId(), targetWorkbasketId);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private WorkbasketQuery applySortingParams(WorkbasketQuery query, MultiValueMap<String, String> params)
        throws IllegalArgumentException {
        // sorting
        String sortBy = params.getFirst(SORT_BY);
        if (sortBy != null) {
            SortDirection sortDirection;
            if (params.getFirst(SORT_DIRECTION) != null && "desc".equals(params.getFirst(SORT_DIRECTION))) {
                sortDirection = SortDirection.DESCENDING;
            } else {
                sortDirection = SortDirection.ASCENDING;
            }
            switch (sortBy) {
                case (NAME):
                    query = query.orderByName(sortDirection);
                    break;
                case (KEY):
                    query = query.orderByKey(sortDirection);
                    break;
                case (OWNER):
                    query = query.orderByOwner(sortDirection);
                    break;
                case (TYPE):
                    query = query.orderByType(sortDirection);
                    break;
                case (DESCRIPTION):
                    query = query.orderByDescription(sortDirection);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown order '" + sortBy + "'");
            }
        }
        params.remove(SORT_BY);
        params.remove(SORT_DIRECTION);
        return query;
    }

    private WorkbasketQuery applyFilterParams(WorkbasketQuery query,
        MultiValueMap<String, String> params) throws InvalidArgumentException {
        if (params.containsKey(NAME)) {
            String[] names = extractCommaSeparatedFields(params.get(NAME));
            query.nameIn(names);
            params.remove(NAME);
        }
        if (params.containsKey(NAME_LIKE)) {
            query.nameLike(LIKE + params.get(NAME_LIKE).get(0) + LIKE);
            params.remove(NAME_LIKE);
        }
        if (params.containsKey(KEY)) {
            String[] names = extractCommaSeparatedFields(params.get(KEY));
            query.keyIn(names);
            params.remove(KEY);
        }
        if (params.containsKey(KEY_LIKE)) {
            query.keyLike(LIKE + params.get(KEY_LIKE).get(0) + LIKE);
            params.remove(KEY_LIKE);
        }
        if (params.containsKey(OWNER)) {
            String[] names = extractCommaSeparatedFields(params.get(OWNER));
            query.ownerIn(names);
            params.remove(OWNER);
        }
        if (params.containsKey(OWNER_LIKE)) {
            query.ownerLike(LIKE + params.get(OWNER_LIKE).get(0) + LIKE);
            params.remove(OWNER_LIKE);
        }
        if (params.containsKey(DESCRIPTION_LIKE)) {
            query.descriptionLike(LIKE + params.get(DESCRIPTION_LIKE).get(0) + LIKE);
            params.remove(DESCRIPTION_LIKE);
        }
        if (params.containsKey(DOMAIN)) {
            query.domainIn(extractCommaSeparatedFields(params.get(DOMAIN)));
            params.remove(DOMAIN);
        }
        if (params.containsKey(TYPE)) {
            switch (params.getFirst(TYPE)) {
                case "PERSONAL":
                    query.typeIn(WorkbasketType.PERSONAL);
                    break;
                case "GROUP":
                    query.typeIn(WorkbasketType.GROUP);
                    break;
                case "CLEARANCE":
                    query.typeIn(WorkbasketType.CLEARANCE);
                    break;
                case "TOPIC":
                    query.typeIn(WorkbasketType.TOPIC);
                    break;
                default:
                    throw new InvalidArgumentException("Unknown Workbasket type '" + params.getFirst(TYPE) + "'");
            }
            params.remove(TYPE);
        }
        if (params.containsKey(REQUIRED_PERMISSION)) {
            for (String authorization : Arrays.asList(params.getFirst(REQUIRED_PERMISSION).split(","))) {
                switch (authorization.trim()) {
                    case "READ":
                        query.callerHasPermission(WorkbasketPermission.READ);
                        break;
                    case "OPEN":
                        query.callerHasPermission(WorkbasketPermission.OPEN);
                        break;
                    case "APPEND":
                        query.callerHasPermission(WorkbasketPermission.APPEND);
                        break;
                    case "TRANSFER":
                        query.callerHasPermission(WorkbasketPermission.TRANSFER);
                        break;
                    case "DISTRIBUTE":
                        query.callerHasPermission(WorkbasketPermission.DISTRIBUTE);
                        break;
                    case "CUSTOM_1":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_1);
                        break;
                    case "CUSTOM_2":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_2);
                        break;
                    case "CUSTOM_3":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_3);
                        break;
                    case "CUSTOM_4":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_4);
                        break;
                    case "CUSTOM_5":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_5);
                        break;
                    case "CUSTOM_6":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_6);
                        break;
                    case "CUSTOM_7":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_7);
                        break;
                    case "CUSTOM_8":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_8);
                        break;
                    case "CUSTOM_9":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_9);
                        break;
                    case "CUSTOM_10":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_10);
                        break;
                    case "CUSTOM_11":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_11);
                        break;
                    case "CUSTOM_12":
                        query.callerHasPermission(WorkbasketPermission.CUSTOM_12);
                        break;
                    default:
                        throw new InvalidArgumentException("Unknown authorization '" + authorization + "'");
                }
            }
            params.remove(REQUIRED_PERMISSION);
        }
        return query;
    }

}
