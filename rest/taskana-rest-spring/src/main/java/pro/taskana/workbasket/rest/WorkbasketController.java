package pro.taskana.workbasket.rest;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.util.QueryParamsValidator;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
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
import pro.taskana.workbasket.rest.models.DistributionTargetsCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;

/** Controller for all {@link Workbasket} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class WorkbasketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketController.class);

  private final WorkbasketService workbasketService;
  private final WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler;
  private final WorkbasketSummaryRepresentationModelAssembler
      workbasketSummaryRepresentationModelAssembler;
  private final WorkbasketAccessItemRepresentationModelAssembler
      workbasketAccessItemRepresentationModelAssembler;

  @Autowired
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

  /**
   * This endpoint retrieves a list of existing Workbaskets. Filters can be applied.
   *
   * @title Get a list of all Workbaskets
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Workbaskets with the given filter, sort and paging options.
   */
  @GetMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketSummaryPagedRepresentationModel> getWorkbaskets(
      HttpServletRequest request,
      WorkbasketQueryFilterParameter filterParameter,
      WorkbasketQuerySortParameter sortParameter,
      QueryPagingParameter<WorkbasketSummary, WorkbasketQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        WorkbasketQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    filterParameter.applyToQuery(query);
    sortParameter.applyToQuery(query);

    List<WorkbasketSummary> workbasketSummaries = pagingParameter.applyToQuery(query);
    WorkbasketSummaryPagedRepresentationModel pagedModels =
        workbasketSummaryRepresentationModelAssembler.toPagedModel(
            workbasketSummaries, pagingParameter.getPageMetadata());

    return ResponseEntity.ok(pagedModels);
  }

  /**
   * This endpoint retrieves a single Workbasket.
   *
   * @title Get a single Workbasket
   * @param workbasketId the Id of the requested Workbasket
   * @return the requested Workbasket
   * @throws WorkbasketNotFoundException if the requested Workbasket is not found
   * @throws NotAuthorizedException if the current user has no permissions to access the requested
   *     Workbasket
   */
  @GetMapping(path = RestEndpoints.URL_WORKBASKET_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> getWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);

    return ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint deletes an existing Workbasket.
   *
   * <p>Returned HTTP Status codes:
   *
   * <ul>
   *   <li><b>204 NO_CONTENT</b> - Workbasket has been deleted successfully
   *   <li><b>202 ACCEPTED</b> - Workbasket still contains completed Tasks. It has been marked for
   *       deletion and will be deleted automatically as soon as all completed Tasks are deleted.
   *   <li><b>423 LOCKED</b> - Workbasket contains non-completed Tasks and cannot be deleted.
   * </ul>
   *
   * @title Delete a Workbasket
   * @param workbasketId the Id of the Workbasket which should be deleted
   * @return the deleted Workbasket
   * @throws NotAuthorizedException if the current user is not authorized to delete this Workbasket.
   * @throws InvalidArgumentException if the requested Workbasket Id is null or empty
   * @throws WorkbasketNotFoundException if the requested Workbasket is not found
   * @throws WorkbasketInUseException if the Workbasket contains tasks.
   */
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class, noRollbackFor = WorkbasketNotFoundException.class)
  public ResponseEntity<WorkbasketRepresentationModel> deleteWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketInUseException {

    boolean workbasketDeleted = workbasketService.deleteWorkbasket(workbasketId);

    if (workbasketDeleted) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Workbasket successfully deleted.");
      }
      return ResponseEntity.noContent().build();
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Workbasket was only marked for deletion and will be physically deleted later on.");
      }
      return ResponseEntity.accepted().build();
    }
  }

  /**
   * This endpoint creates a persistent Workbasket.
   *
   * @title Create a new Workbasket
   * @param workbasketRepresentationModel the Workbasket which should be created.
   * @return the created Workbasket
   * @throws InvalidWorkbasketException if some required properties of the Workbasket are not set.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketAlreadyExistException if the Workbasket exists already
   * @throws DomainNotFoundException if the domain does not exist in the configuration.
   */
  @PostMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> createWorkbasket(
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    workbasket = workbasketService.createWorkbasket(workbasket);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint updates a given Workbasket.
   *
   * @title Update a Workbasket
   * @param workbasketId the Id of the Workbasket which should be updated.
   * @param workbasketRepresentationModel the new Workbasket for the requested id.
   * @return the updated Workbasket
   * @throws InvalidWorkbasketException if the requested Id and the Id within the new Workbasket do
   *     not match.
   * @throws WorkbasketNotFoundException if the requested workbasket does not
   * @throws NotAuthorizedException if the current user is not authorized to update the Workbasket
   * @throws ConcurrencyException if an attempt is made to update the Workbasket and another user
   *     updated it already
   */
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> updateWorkbasket(
      @PathVariable(value = "workbasketId") String workbasketId,
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException,
          ConcurrencyException {
    if (!workbasketId.equals(workbasketRepresentationModel.getWorkbasketId())) {
      throw new InvalidWorkbasketException(
          "Target-WB-ID('"
              + workbasketId
              + "') is not identical with the WB-ID of to object which should be updated. ID=('"
              + workbasketRepresentationModel.getWorkbasketId()
              + "')");
    }
    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    workbasket = workbasketService.updateWorkbasket(workbasket);

    return ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint retrieves all Workbasket Access Items for a given Workbasket.
   *
   * @title Get all Workbasket Access Items
   * @param workbasketId the Id of the requested Workbasket.
   * @return the access items for the requested Workbasket.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   */
  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> getWorkbasketAccessItems(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    return ResponseEntity.ok(
        workbasketAccessItemRepresentationModelAssembler
            .toTaskanaCollectionModelForSingleWorkbasket(workbasketId, accessItems));
  }

  /**
   * This endpoint replaces all Workbasket Access Items for a given Workbasket with the provided
   * ones.
   *
   * @title Set all Workbasket Access Items
   * @param workbasketId the Id of the Workbasket whose Workbasket Access Items will be replaced
   * @param workbasketAccessItemRepModels the new Workbasket Access Items.
   * @return the new Workbasket Access Items for the requested Workbasket
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws InvalidArgumentException if the new Workbasket Access Items are not provided.
   * @throws WorkbasketNotFoundException TODO: this is never thrown.
   * @throws WorkbasketAccessItemAlreadyExistException if a duplicate Workbasket Access Item exists
   *     in the provided list.
   */
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> setWorkbasketAccessItems(
      @PathVariable(value = "workbasketId") String workbasketId,
      @RequestBody WorkbasketAccessItemCollectionRepresentationModel workbasketAccessItemRepModels)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    if (workbasketAccessItemRepModels == null) {
      throw new InvalidArgumentException("Can´t create something with NULL body-value.");
    }

    List<WorkbasketAccessItem> wbAccessItems =
        workbasketAccessItemRepModels.getContent().stream()
            .map(workbasketAccessItemRepresentationModelAssembler::toEntityModel)
            .collect(Collectors.toList());
    workbasketService.setWorkbasketAccessItems(workbasketId, wbAccessItems);
    List<WorkbasketAccessItem> updatedWbAccessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    return ResponseEntity.ok(
        workbasketAccessItemRepresentationModelAssembler
            .toTaskanaCollectionModelForSingleWorkbasket(workbasketId, updatedWbAccessItems));
  }

  /**
   * This endpoint retrieves all Distribution Targets for a requested Workbasket.
   *
   * @title Get all Distribution Targets for a Workbasket
   * @param workbasketId the Id of the Workbasket whose Distribution Targets will be retrieved
   * @return the Distribution Targets for the requested Workbasket
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   * @throws NotAuthorizedException if the current user has no read permission for the specified
   *     Workbasket
   */
  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel> getDistributionTargets(
      @PathVariable(value = "workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasketId);
    DistributionTargetsCollectionRepresentationModel distributionTargetRepModels =
        workbasketSummaryRepresentationModelAssembler.toTaskanaCollectionModel(distributionTargets);

    return ResponseEntity.ok(distributionTargetRepModels);
  }

  /**
   * This endpoint replaces all Distribution Targets for a given Workbasket with the provided ones.
   *
   * @title Set all Distribution Targets for a Workbasket
   * @param sourceWorkbasketId the source Workbasket
   * @param targetWorkbasketIds the destination Workbaskets.
   * @return the new Distribution Targets for the requested Workbasket.
   * @throws WorkbasketNotFoundException if any Workbasket was not found (either source or target)
   * @throws NotAuthorizedException if the current user doesn't have READ permission for the source
   *     Workbasket
   */
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel>
      setDistributionTargetsForWorkbasketId(
          @PathVariable(value = "workbasketId") String sourceWorkbasketId,
          @RequestBody List<String> targetWorkbasketIds)
          throws WorkbasketNotFoundException, NotAuthorizedException {
    workbasketService.setDistributionTargets(sourceWorkbasketId, targetWorkbasketIds);

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasketId);

    return ResponseEntity.ok(
        workbasketSummaryRepresentationModelAssembler.toTaskanaCollectionModel(
            distributionTargets));
  }

  /**
   * This endpoint removes all Distribution Target references for a provided Workbasket.
   *
   * @title Remove a Workbasket as Distribution Target
   * @param targetWorkbasketId the Id of the requested Workbasket.
   * @return no content
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   * @throws NotAuthorizedException if the requested user ist not ADMIN or BUSINESS_ADMIN.
   */
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> removeDistributionTargetForWorkbasketId(
      @PathVariable(value = "workbasketId") String targetWorkbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException {
    List<WorkbasketSummary> sourceWorkbaskets =
        workbasketService.getDistributionSources(targetWorkbasketId);
    for (WorkbasketSummary source : sourceWorkbaskets) {
      workbasketService.removeDistributionTarget(source.getId(), targetWorkbasketId);
    }

    return ResponseEntity.noContent().build();
  }

  public enum WorkbasketQuerySortBy implements QuerySortBy<WorkbasketQuery> {
    NAME(WorkbasketQuery::orderByName),
    KEY(WorkbasketQuery::orderByKey),
    OWNER(WorkbasketQuery::orderByOwner),
    TYPE(WorkbasketQuery::orderByType),
    DESCRIPTION(WorkbasketQuery::orderByDescription),
    CUSTOM_1((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_1, sort)),
    CUSTOM_2((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_2, sort)),
    CUSTOM_3((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_3, sort)),
    CUSTOM_4((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_4, sort)),
    DOMAIN(WorkbasketQuery::orderByDomain),
    ORG_LEVEL_1(WorkbasketQuery::orderByOrgLevel1),
    ORG_LEVEL_2(WorkbasketQuery::orderByOrgLevel2),
    ORG_LEVEL_3(WorkbasketQuery::orderByOrgLevel3),
    ORG_LEVEL_4(WorkbasketQuery::orderByOrgLevel4);

    private final BiConsumer<WorkbasketQuery, SortDirection> consumer;

    WorkbasketQuerySortBy(BiConsumer<WorkbasketQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(WorkbasketQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class WorkbasketQuerySortParameter
      extends QuerySortParameter<WorkbasketQuery, WorkbasketQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public WorkbasketQuerySortParameter(
        List<WorkbasketQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<WorkbasketQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
