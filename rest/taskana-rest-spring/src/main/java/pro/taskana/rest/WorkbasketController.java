package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.DistributionTargetListResource;
import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.DistributionTargetResourceAssembler;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.WorkbasketAccessItemListResource;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketAccessItemResourceAssembler;
import pro.taskana.rest.resource.WorkbasketResource;
import pro.taskana.rest.resource.WorkbasketResourceAssembler;
import pro.taskana.rest.resource.WorkbasketSummaryListResource;
import pro.taskana.rest.resource.WorkbasketSummaryResourceAssembler;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketAccessItem;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Controller for all {@link Workbasket} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class WorkbasketController extends AbstractPagingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketController.class);

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

  private WorkbasketService workbasketService;

  private WorkbasketResourceAssembler workbasketResourceAssembler;

  private WorkbasketSummaryResourceAssembler workbasketSummaryResourceAssembler;

  private DistributionTargetResourceAssembler distributionTargetResourceAssembler;

  private WorkbasketAccessItemResourceAssembler workbasketAccessItemResourceAssembler;

  WorkbasketController(
      WorkbasketService workbasketService,
      WorkbasketResourceAssembler workbasketResourceAssembler,
      WorkbasketSummaryResourceAssembler workbasketSummaryResourceAssembler,
      DistributionTargetResourceAssembler distributionTargetResourceAssembler,
      WorkbasketAccessItemResourceAssembler workbasketAccessItemResourceAssembler) {
    this.workbasketService = workbasketService;
    this.workbasketResourceAssembler = workbasketResourceAssembler;
    this.workbasketSummaryResourceAssembler = workbasketSummaryResourceAssembler;
    this.distributionTargetResourceAssembler = distributionTargetResourceAssembler;
    this.workbasketAccessItemResourceAssembler = workbasketAccessItemResourceAssembler;
  }

  @GetMapping(path = Mapping.URL_WORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketSummaryListResource> getWorkbaskets(
      @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getWorkbaskets(params= {})", params);
    }

    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    query = applySortingParams(query, params);
    query = applyFilterParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<WorkbasketSummary> workbasketSummaries = getQueryList(query, pageMetadata);
    WorkbasketSummaryListResource pagedResources =
        workbasketSummaryResourceAssembler.toResources(workbasketSummaries, pageMetadata);

    ResponseEntity<WorkbasketSummaryListResource> response = ResponseEntity.ok(pagedResources);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbaskets(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_WORKBASKET_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketResource> getWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    LOGGER.debug("Entry to getWorkbasket(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketResource> result;
    Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
    result = ResponseEntity.ok(workbasketResourceAssembler.toResource(workbasket));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbasket(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = Mapping.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class, noRollbackFor = WorkbasketNotFoundException.class)
  public ResponseEntity<?> markWorkbasketForDeletion(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketInUseException {
    LOGGER.debug("Entry to markWorkbasketForDeletion(workbasketId= {})", workbasketId);
    // http status code accepted because workbaskets will not be deleted immediately
    ResponseEntity<?> response =
        ResponseEntity.accepted().body(workbasketService.deleteWorkbasket(workbasketId));
    LOGGER.debug("Exit from markWorkbasketForDeletion(), returning {}", response);
    return response;
  }

  @PostMapping(path = Mapping.URL_WORKBASKET)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketResource> createWorkbasket(
      @RequestBody WorkbasketResource workbasketResource)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to createWorkbasket(workbasketResource= {})", workbasketResource);
    }

    Workbasket workbasket = workbasketResourceAssembler.toModel(workbasketResource);
    workbasket = workbasketService.createWorkbasket(workbasket);
    ResponseEntity<WorkbasketResource> response =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(workbasketResourceAssembler.toResource(workbasket));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createWorkbasket(), returning {}", response);
    }

    return response;
  }

  @PutMapping(path = Mapping.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketResource> updateWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId,
      @RequestBody WorkbasketResource workbasketResource)
      throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException,
          ConcurrencyException {
    LOGGER.debug("Entry to updateWorkbasket(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketResource> result;
    if (workbasketId.equals(workbasketResource.workbasketId)) {
      Workbasket workbasket = workbasketResourceAssembler.toModel(workbasketResource);
      workbasket = workbasketService.updateWorkbasket(workbasket);
      result = ResponseEntity.ok(workbasketResourceAssembler.toResource(workbasket));
    } else {
      throw new InvalidWorkbasketException(
          "Target-WB-ID('"
              + workbasketId
              + "') is not identical with the WB-ID of to object which should be updated. ID=('"
              + workbasketResource.getId()
              + "')");
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateWorkbasket(), returning {}", result);
    }

    return result;
  }

  @GetMapping(path = Mapping.URL_WORKBASKET_ID_ACCESSITEMS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemListResource> getWorkbasketAccessItems(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    LOGGER.debug("Entry to getWorkbasketAccessItems(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketAccessItemListResource> result;

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);
    result =
        ResponseEntity.ok(
            workbasketAccessItemResourceAssembler.toResources(workbasketId, accessItems));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbasketAccessItems(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = Mapping.URL_WORKBASKET_ID_ACCESSITEMS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemListResource> setWorkbasketAccessItems(
      @PathVariable(value = "workbasketId") String workbasketId,
      @RequestBody List<WorkbasketAccessItemResource> workbasketAccessResourceItems)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    LOGGER.debug("Entry to setWorkbasketAccessItems(workbasketId= {})", workbasketId);
    if (workbasketAccessResourceItems == null) {
      throw new InvalidArgumentException("Can´t create something with NULL body-value.");
    }

    List<WorkbasketAccessItem> wbAccessItems = new ArrayList<>();
    workbasketAccessResourceItems.forEach(
        item -> wbAccessItems.add(workbasketAccessItemResourceAssembler.toModel(item)));
    workbasketService.setWorkbasketAccessItems(workbasketId, wbAccessItems);
    List<WorkbasketAccessItem> updatedWbAccessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    ResponseEntity<WorkbasketAccessItemListResource> response =
        ResponseEntity.ok(
            workbasketAccessItemResourceAssembler.toResources(workbasketId, updatedWbAccessItems));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from setWorkbasketAccessItems(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetListResource> getDistributionTargets(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {

    LOGGER.debug("Entry to getDistributionTargets(workbasketId= {})", workbasketId);
    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasketId);
    DistributionTargetListResource distributionTargetListResource =
        distributionTargetResourceAssembler.toResources(workbasketId, distributionTargets);
    ResponseEntity<DistributionTargetListResource> result =
        ResponseEntity.ok(distributionTargetListResource);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getDistributionTargets(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = Mapping.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetListResource> setDistributionTargetsForWorkbasketId(
      @PathVariable(value = "workbasketId") String sourceWorkbasketId,
      @RequestBody List<String> targetWorkbasketIds)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to getTasksStatusReport(workbasketId= {}, targetWorkbasketIds´= {})",
          sourceWorkbasketId,
          LoggerUtils.listToString(targetWorkbasketIds));
    }

    workbasketService.setDistributionTargets(sourceWorkbasketId, targetWorkbasketIds);

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasketId);
    ResponseEntity<DistributionTargetListResource> response =
        ResponseEntity.ok(
            distributionTargetResourceAssembler.toResources(
                sourceWorkbasketId, distributionTargets));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksStatusReport(), returning {}", response);
    }

    return response;
  }

  // TODO - schema inconsistent with PUT and GET
  @DeleteMapping(path = Mapping.URL_WORKBASKET_DISTRIBUTION_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Resources<DistributionTargetResource>>
      removeDistributionTargetForWorkbasketId(
          @PathVariable(value = "workbasketId") String targetWorkbasketId)
          throws WorkbasketNotFoundException, NotAuthorizedException {
    LOGGER.debug(
        "Entry to removeDistributionTargetForWorkbasketId(workbasketId= {})", targetWorkbasketId);
    List<WorkbasketSummary> sourceWorkbaskets =
        workbasketService.getDistributionSources(targetWorkbasketId);
    for (WorkbasketSummary source : sourceWorkbaskets) {
      workbasketService.removeDistributionTarget(source.getId(), targetWorkbasketId);
    }

    ResponseEntity<Resources<DistributionTargetResource>> response =
        ResponseEntity.noContent().build();
    LOGGER.debug("Exit from removeDistributionTargetForWorkbasketId(), returning {}", response);
    return response;
  }

  private WorkbasketQuery applySortingParams(
      WorkbasketQuery query, MultiValueMap<String, String> params) throws IllegalArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(query= {}, params={})", query, params);
    }

    // sorting
    String sortBy = params.getFirst(SORT_BY);
    if (sortBy != null) {
      SortDirection sortDirection;
      if (params.getFirst(SORT_DIRECTION) != null
          && "desc".equals(params.getFirst(SORT_DIRECTION))) {
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
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", query);
    }

    return query;
  }

  private WorkbasketQuery applyFilterParams(
      WorkbasketQuery query, MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyFilterParams(query= {}, params= {})", query, params);
    }

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
          throw new InvalidArgumentException(
              "Unknown Workbasket type '" + params.getFirst(TYPE) + "'");
      }
      params.remove(TYPE);
    }
    if (params.containsKey(REQUIRED_PERMISSION)) {
      for (String authorization : params.getFirst(REQUIRED_PERMISSION).split(",")) {
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
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
    }

    return query;
  }
}
