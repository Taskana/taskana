package pro.taskana.workbasket.rest;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel.PageMetadata;
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

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.AbstractPagingController;
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.rest.assembler.WorkbasketAccessItemRepresentationModelAssembler;
import pro.taskana.workbasket.rest.assembler.WorkbasketRepresentationModelAssembler;
import pro.taskana.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

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

  private final WorkbasketService workbasketService;

  private final WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler;

  private final WorkbasketSummaryRepresentationModelAssembler
      workbasketSummaryRepresentationModelAssembler;

  private final WorkbasketAccessItemRepresentationModelAssembler
      workbasketAccessItemRepresentationModelAssembler;

  WorkbasketController(
      WorkbasketService workbasketService,
      WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketSummaryRepresentationModelAssembler,
      WorkbasketAccessItemRepresentationModelAssembler
          workbasketAccessItemRepresentationModelAssembler) {
    this.workbasketService = workbasketService;
    this.workbasketRepresentationModelAssembler = workbasketRepresentationModelAssembler;
    this.workbasketSummaryRepresentationModelAssembler =
        workbasketSummaryRepresentationModelAssembler;

    this.workbasketAccessItemRepresentationModelAssembler =
        workbasketAccessItemRepresentationModelAssembler;
  }

  @GetMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>> getWorkbaskets(
      @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getWorkbaskets(params= {})", params);
    }

    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    applySortingParams(query, params);
    applyFilterParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<WorkbasketSummary> workbasketSummaries = getQueryList(query, pageMetadata);
    TaskanaPagedModel<WorkbasketSummaryRepresentationModel> pagedModels =
        workbasketSummaryRepresentationModelAssembler.toPageModel(
            workbasketSummaries, pageMetadata);

    ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>> response =
        ResponseEntity.ok(pagedModels);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbaskets(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = RestEndpoints.URL_WORKBASKET_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> getWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    LOGGER.debug("Entry to getWorkbasket(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketRepresentationModel> result;
    Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
    result = ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbasket(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class, noRollbackFor = WorkbasketNotFoundException.class)
  public ResponseEntity<WorkbasketRepresentationModel> deleteWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketInUseException {
    LOGGER.debug("Entry to markWorkbasketForDeletion(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketRepresentationModel> response;

    boolean workbasketDeleted = workbasketService.deleteWorkbasket(workbasketId);

    if (workbasketDeleted) {
      LOGGER.debug("Workbasket successfully deleted.");
      response = ResponseEntity.noContent().build();
    } else {
      LOGGER.debug(
          "Workbasket was only marked for deletion and will be physically deleted later on.");
      response = ResponseEntity.accepted().build();
    }

    LOGGER.debug("Exit from markWorkbasketForDeletion(), returning {}", response);
    return response;
  }

  @PostMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> createWorkbasket(
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to createWorkbasket(workbasketResource= {})", workbasketRepresentationModel);
    }

    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    workbasket = workbasketService.createWorkbasket(workbasket);
    ResponseEntity<WorkbasketRepresentationModel> response =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(workbasketRepresentationModelAssembler.toModel(workbasket));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createWorkbasket(), returning {}", response);
    }

    return response;
  }

  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> updateWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId,
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException,
          ConcurrencyException {
    LOGGER.debug("Entry to updateWorkbasket(workbasketId= {})", workbasketId);
    ResponseEntity<WorkbasketRepresentationModel> result;
    if (workbasketId.equals(workbasketRepresentationModel.getWorkbasketId())) {
      Workbasket workbasket =
          workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
      workbasket = workbasketService.updateWorkbasket(workbasket);
      result = ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
    } else {
      throw new InvalidWorkbasketException(
          "Target-WB-ID('"
              + workbasketId
              + "') is not identical with the WB-ID of to object which should be updated. ID=('"
              + workbasketRepresentationModel.getWorkbasketId()
              + "')");
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateWorkbasket(), returning {}", result);
    }

    return result;
  }

  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>>
      getWorkbasketAccessItems(@PathVariable(value = "workbasketId") String workbasketId)
          throws NotAuthorizedException, WorkbasketNotFoundException {
    LOGGER.debug("Entry to getWorkbasketAccessItems(workbasketId= {})", workbasketId);

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);
    final ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> result =
        ResponseEntity.ok(
            workbasketAccessItemRepresentationModelAssembler.toPageModelForSingleWorkbasket(
                workbasketId, accessItems, null));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbasketAccessItems(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>>
      setWorkbasketAccessItems(
          @PathVariable(value = "workbasketId") String workbasketId,
          @RequestBody List<WorkbasketAccessItemRepresentationModel> workbasketAccessResourceItems)
          throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
              WorkbasketAccessItemAlreadyExistException {
    LOGGER.debug("Entry to setWorkbasketAccessItems(workbasketId= {})", workbasketId);
    if (workbasketAccessResourceItems == null) {
      throw new InvalidArgumentException("Can´t create something with NULL body-value.");
    }

    List<WorkbasketAccessItem> wbAccessItems = new ArrayList<>();
    workbasketAccessResourceItems.forEach(
        item ->
            wbAccessItems.add(
                workbasketAccessItemRepresentationModelAssembler.toEntityModel(item)));
    workbasketService.setWorkbasketAccessItems(workbasketId, wbAccessItems);
    List<WorkbasketAccessItem> updatedWbAccessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> response =
        ResponseEntity.ok(
            workbasketAccessItemRepresentationModelAssembler.toPageModelForSingleWorkbasket(
                workbasketId, updatedWbAccessItems, null));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from setWorkbasketAccessItems(), returning {}", response);
    }

    return response;
  }

  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>>
      getDistributionTargets(@PathVariable(value = "workbasketId") String workbasketId)
          throws WorkbasketNotFoundException, NotAuthorizedException {

    LOGGER.debug("Entry to getDistributionTargets(workbasketId= {})", workbasketId);
    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasketId);
    TaskanaPagedModel<WorkbasketSummaryRepresentationModel> distributionTargetListResource =
        workbasketSummaryRepresentationModelAssembler.toDistributionTargetPageModel(
            distributionTargets, null);
    ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>> result =
        ResponseEntity.ok(distributionTargetListResource);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getDistributionTargets(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>>
      setDistributionTargetsForWorkbasketId(
          @PathVariable(value = "workbasketId") String sourceWorkbasketId,
          @RequestBody List<String> targetWorkbasketIds)
          throws WorkbasketNotFoundException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to getTasksStatusReport(workbasketId= {}, targetWorkbasketIds´= {})",
          sourceWorkbasketId,
          targetWorkbasketIds);
    }

    workbasketService.setDistributionTargets(sourceWorkbasketId, targetWorkbasketIds);

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasketId);
    ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>> response =
        ResponseEntity.ok(
            workbasketSummaryRepresentationModelAssembler.toDistributionTargetPageModel(
                distributionTargets, null));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksStatusReport(), returning {}", response);
    }

    return response;
  }

  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>>
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

    ResponseEntity<TaskanaPagedModel<WorkbasketSummaryRepresentationModel>> response =
        ResponseEntity.noContent().build();
    LOGGER.debug("Exit from removeDistributionTargetForWorkbasketId(), returning {}", response);
    return response;
  }

  private void applySortingParams(WorkbasketQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(query= {}, params={})", query, params);
    }

    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          switch (sortBy) {
            case (NAME):
              query.orderByName(sortDirection);
              break;
            case (KEY):
              query.orderByKey(sortDirection);
              break;
            case (OWNER):
              query.orderByOwner(sortDirection);
              break;
            case (TYPE):
              query.orderByType(sortDirection);
              break;
            case (DESCRIPTION):
              query.orderByDescription(sortDirection);
              break;
            default:
              throw new InvalidArgumentException("Unknown order '" + sortBy + "'");
          }
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", query);
    }
  }

  private void applyFilterParams(WorkbasketQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
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
    String type = params.getFirst(TYPE);
    if (type != null) {
      try {
        query.typeIn(WorkbasketType.valueOf(type));
      } catch (IllegalArgumentException e) {
        throw new InvalidArgumentException("Unknown Workbasket type '" + type + "'");
      }
      params.remove(TYPE);
    }
    String permissions = params.getFirst(REQUIRED_PERMISSION);
    if (permissions != null) {
      for (String authorization : permissions.split(",")) {
        try {
          query.callerHasPermission(WorkbasketPermission.valueOf(authorization.trim()));
        } catch (IllegalArgumentException e) {
          throw new InvalidArgumentException("Unknown authorization '" + authorization + "'", e);
        }
      }
      params.remove(REQUIRED_PERMISSION);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
    }
  }
}
