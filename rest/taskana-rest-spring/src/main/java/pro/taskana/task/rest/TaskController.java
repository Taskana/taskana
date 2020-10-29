package pro.taskana.task.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.rest.AbstractPagingController;
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.assembler.TaskSummaryRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Controller for all {@link Task} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskController extends AbstractPagingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

  private static final String LIKE = "%";
  private static final String STATE = "state";
  private static final String STATE_VALUE_CLAIMED = "CLAIMED";
  private static final String STATE_VALUE_COMPLETED = "COMPLETED";
  private static final String STATE_VALUE_READY = "READY";
  private static final String PRIORITY = "priority";
  private static final String NAME = "name";
  private static final String NAME_LIKE = "name-like";
  private static final String OWNER = "owner";
  private static final String OWNER_LIKE = "owner-like";
  private static final String DOMAIN = "domain";
  private static final String TASK_ID = "task-id";
  private static final String WORKBASKET_ID = "workbasket-id";
  private static final String WORKBASKET_KEY = "workbasket-key";
  private static final String CLASSIFICATION_KEY = "classification.key";
  private static final String POR_VALUE = "por.value";
  private static final String POR_TYPE = "por.type";
  private static final String POR_SYSTEM_INSTANCE = "por.instance";
  private static final String POR_SYSTEM = "por.system";
  private static final String POR_COMPANY = "por.company";
  private static final String DUE = "due";
  private static final String DUE_TO = "due-until";
  private static final String DUE_FROM = "due-from";
  private static final String PLANNED = "planned";
  private static final String PLANNED_UNTIL = "planned-until";
  private static final String PLANNED_FROM = "planned-from";
  private static final String EXTERNAL_ID = "external-id";
  private static final String WILDCARD_SEARCH_VALUE = "wildcard-search-value";
  private static final String WILDCARD_SEARCH_FIELDS = "wildcard-search-fields";

  private static final String INDEFINITE = "";

  private final TaskService taskService;
  private final TaskRepresentationModelAssembler taskRepresentationModelAssembler;
  private final TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler;

  @Autowired
  TaskController(
      TaskService taskService,
      TaskRepresentationModelAssembler taskRepresentationModelAssembler,
      TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler) {
    this.taskService = taskService;
    this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
    this.taskSummaryRepresentationModelAssembler = taskSummaryRepresentationModelAssembler;
  }

  @GetMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> getTasks(
      @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTasks(params= {})", params);
    }

    TaskQuery query = taskService.createTaskQuery();
    applyFilterParams(query, params);
    applySortingParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<TaskSummary> taskSummaries = getQueryList(query, pageMetadata);

    TaskanaPagedModel<TaskSummaryRepresentationModel> pagedModels =
        taskSummaryRepresentationModelAssembler.toPageModel(taskSummaries, pageMetadata);
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        ResponseEntity.ok(pagedModels);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasks(), returning {}", response);
    }

    return response;
  }

  @DeleteMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> deleteTasks(
      @RequestParam MultiValueMap<String, String> params)
      throws InvalidArgumentException, NotAuthorizedException {

    LOGGER.debug("Entry to deleteTasks(params= {})", params);

    TaskQuery query = taskService.createTaskQuery();
    applyFilterParams(query, params);
    validateNoInvalidParameterIsLeft(params);

    List<TaskSummary> taskSummaries = getQueryList(query, null);

    List<String> taskIdsToDelete =
        taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());

    BulkOperationResults<String, TaskanaException> result =
        taskService.deleteTasks(taskIdsToDelete);

    List<TaskSummary> successfullyDeletedTaskSummaries =
        taskSummaries.stream()
            .filter(summary -> !result.getFailedIds().contains(summary.getId()))
            .collect(Collectors.toList());

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        ResponseEntity.ok(
            taskSummaryRepresentationModelAssembler.toPageModel(successfullyDeletedTaskSummaries));

    LOGGER.debug("Exit from deleteTasks(), returning {}", response);

    return response;
  }

  @GetMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> getTask(@PathVariable String taskId)
      throws TaskNotFoundException, NotAuthorizedException {
    LOGGER.debug("Entry to getTask(taskId= {})", taskId);
    Task task = taskService.getTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTask(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> claimTask(
      @PathVariable String taskId, @RequestBody String userName)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    LOGGER.debug("Entry to claimTask(taskId= {}, userName= {})", taskId, userName);
    // TODO verify user
    taskService.claim(taskId);
    Task updatedTask = taskService.getTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from claimTask(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> selectAndClaimTask(
      @RequestParam MultiValueMap<String, String> params)
      throws InvalidOwnerException, NotAuthorizedException, InvalidArgumentException {

    LOGGER.debug("Entry to selectAndClaimTask");

    TaskQuery query = taskService.createTaskQuery();
    applyFilterParams(query, params);
    applySortingParams(query, params);

    Task selectedAndClaimedTask = taskService.selectAndClaim(query);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(selectedAndClaimedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from selectAndClaimTask(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelClaimTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {

    LOGGER.debug("Entry to cancelClaimTask(taskId= {}", taskId);

    taskService.cancelClaim(taskId);
    Task updatedTask = taskService.getTask(taskId);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from cancelClaimTask(), returning {}", result);
    }
    return result;
  }

  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> completeTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    LOGGER.debug("Entry to completeTask(taskId= {})", taskId);
    taskService.forceCompleteTask(taskId);
    Task updatedTask = taskService.getTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from completeTask(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> deleteTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    LOGGER.debug("Entry to deleteTask(taskId= {})", taskId);
    taskService.forceDeleteTask(taskId);
    ResponseEntity<TaskRepresentationModel> result = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from deleteTask(), returning {}", result);
    return result;
  }

  @PostMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> createTask(
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to createTask(params= {})", taskRepresentationModel);
    }

    Task fromResource = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
    Task createdTask = taskService.createTask(fromResource);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(taskRepresentationModelAssembler.toModel(createdTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createTask(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> transferTask(
      @PathVariable String taskId, @PathVariable String workbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    LOGGER.debug("Entry to transferTask(taskId= {}, workbasketId= {})", taskId, workbasketId);
    Task updatedTask = taskService.transfer(taskId, workbasketId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from transferTask(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> updateTask(
      @PathVariable(value = "taskId") String taskId,
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    LOGGER.debug(
        "Entry to updateTask(taskId= {}, taskResource= {})", taskId, taskRepresentationModel);
    ResponseEntity<TaskRepresentationModel> result;
    if (taskId.equals(taskRepresentationModel.getTaskId())) {
      Task task = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
      task = taskService.updateTask(task);
      result = ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskId ('%s') is not identical with the taskId of to "
                  + "object in the payload which should be updated. ID=('%s')",
              taskId, taskRepresentationModel.getTaskId()));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTask(), returning {}", result);
    }

    return result;
  }

  private void applyFilterParams(TaskQuery taskQuery, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyFilterParams(taskQuery= {}, params= {})", taskQuery, params);
    }

    checkForIllegalParamCombinations(params);

    // apply filters
    if (params.containsKey(NAME)) {
      String[] names = extractCommaSeparatedFields(params.get(NAME));
      taskQuery.nameIn(names);
      params.remove(NAME);
    }
    if (params.containsKey(NAME_LIKE)) {
      taskQuery.nameLike(LIKE + params.get(NAME_LIKE).get(0) + LIKE);
      params.remove(NAME_LIKE);
    }
    if (params.containsKey(PRIORITY)) {
      String[] prioritiesInString = extractCommaSeparatedFields(params.get(PRIORITY));
      int[] priorities = extractPriorities(prioritiesInString);
      taskQuery.priorityIn(priorities);
      params.remove(PRIORITY);
    }
    if (params.containsKey(STATE)) {

      TaskState[] states = extractStates(params);
      taskQuery.stateIn(states);
      params.remove(STATE);
    }
    if (params.containsKey(CLASSIFICATION_KEY)) {
      String[] classificationKeys = extractCommaSeparatedFields(params.get(CLASSIFICATION_KEY));
      taskQuery.classificationKeyIn(classificationKeys);
      params.remove(CLASSIFICATION_KEY);
    }
    if (params.containsKey(TASK_ID)) {
      String[] taskIds = extractCommaSeparatedFields(params.get(TASK_ID));
      taskQuery.idIn(taskIds);
      params.remove(TASK_ID);
    }
    if (params.containsKey(WORKBASKET_ID)) {
      String[] workbaskets = extractCommaSeparatedFields(params.get(WORKBASKET_ID));
      taskQuery.workbasketIdIn(workbaskets);
      params.remove(WORKBASKET_ID);
    }
    if (params.containsKey(WORKBASKET_KEY)) {
      updateTaskQueryWithWorkbasketKey(taskQuery, params);
    }
    if (params.containsKey(OWNER)) {
      String[] owners = extractCommaSeparatedFields(params.get(OWNER));
      taskQuery.ownerIn(owners);
      params.remove(OWNER);
    }
    if (params.containsKey(OWNER_LIKE)) {
      taskQuery.ownerLike(LIKE + params.get(OWNER_LIKE).get(0) + LIKE);
      params.remove(OWNER_LIKE);
    }
    if (params.containsKey(POR_COMPANY)) {
      String[] companies = extractCommaSeparatedFields(params.get(POR_COMPANY));
      taskQuery.primaryObjectReferenceCompanyIn(companies);
      params.remove(POR_COMPANY);
    }
    if (params.containsKey(POR_SYSTEM)) {
      String[] systems = extractCommaSeparatedFields(params.get(POR_SYSTEM));
      taskQuery.primaryObjectReferenceSystemIn(systems);
      params.remove(POR_SYSTEM);
    }
    if (params.containsKey(POR_SYSTEM_INSTANCE)) {
      String[] systemInstances = extractCommaSeparatedFields(params.get(POR_SYSTEM_INSTANCE));
      taskQuery.primaryObjectReferenceSystemInstanceIn(systemInstances);
      params.remove(POR_SYSTEM_INSTANCE);
    }
    if (params.containsKey(POR_TYPE)) {
      taskQuery.primaryObjectReferenceTypeLike(LIKE + params.get(POR_TYPE).get(0) + LIKE);
      params.remove(POR_TYPE);
    }
    if (params.containsKey(POR_VALUE)) {
      taskQuery.primaryObjectReferenceValueLike(LIKE + params.get(POR_VALUE).get(0) + LIKE);
      params.remove(POR_VALUE);
    }

    if (params.containsKey(PLANNED)) {
      updateTaskQueryWithPlannedOrDueTimeIntervals(taskQuery, params, PLANNED);
    }

    if (params.containsKey(DUE)) {
      updateTaskQueryWithPlannedOrDueTimeIntervals(taskQuery, params, DUE);
    }

    if (params.containsKey(PLANNED_FROM) && params.containsKey(PLANNED_UNTIL)) {
      updateTaskQueryWithPlannedOrDueTimeInterval(taskQuery, params, PLANNED_FROM, PLANNED_UNTIL);

    } else if (params.containsKey(PLANNED_FROM) && !params.containsKey(PLANNED_UNTIL)) {

      TimeInterval timeInterval = createIndefiniteTimeIntervalFromParam(params, PLANNED_FROM);
      updateTaskQueryWithIndefiniteTimeInterval(taskQuery, params, PLANNED_FROM, timeInterval);

    } else if (!params.containsKey(PLANNED_FROM) && params.containsKey(PLANNED_UNTIL)) {

      TimeInterval timeInterval = createIndefiniteTimeIntervalFromParam(params, PLANNED_UNTIL);
      updateTaskQueryWithIndefiniteTimeInterval(taskQuery, params, PLANNED_UNTIL, timeInterval);
    }

    if (params.containsKey(DUE_FROM) && params.containsKey(DUE_TO)) {
      updateTaskQueryWithPlannedOrDueTimeInterval(taskQuery, params, DUE_FROM, DUE_TO);

    } else if (params.containsKey(DUE_FROM) && !params.containsKey(DUE_TO)) {

      TimeInterval indefiniteTimeInterval = createIndefiniteTimeIntervalFromParam(params, DUE_FROM);
      updateTaskQueryWithIndefiniteTimeInterval(
          taskQuery, params, DUE_FROM, indefiniteTimeInterval);

    } else if (!params.containsKey(DUE_FROM) && params.containsKey(DUE_TO)) {

      TimeInterval timeInterval = createIndefiniteTimeIntervalFromParam(params, DUE_TO);
      updateTaskQueryWithIndefiniteTimeInterval(taskQuery, params, DUE_TO, timeInterval);
    }

    if (params.containsKey(WILDCARD_SEARCH_FIELDS) && params.containsKey(WILDCARD_SEARCH_VALUE)) {

      String[] requestedWildcardSearchFields =
          extractCommaSeparatedFields(params.get(WILDCARD_SEARCH_FIELDS));

      taskQuery.wildcardSearchFieldsIn(createWildcardSearchFields(requestedWildcardSearchFields));

      taskQuery.wildcardSearchValueLike(LIKE + params.getFirst(WILDCARD_SEARCH_VALUE) + LIKE);
      params.remove(WILDCARD_SEARCH_FIELDS);
      params.remove(WILDCARD_SEARCH_VALUE);
    }

    if (params.containsKey(EXTERNAL_ID)) {
      String[] externalIds = extractCommaSeparatedFields(params.get(EXTERNAL_ID));
      taskQuery.externalIdIn(externalIds);
      params.remove(EXTERNAL_ID);
    }

    for (TaskCustomField customField : TaskCustomField.values()) {
      List<String> customFieldParams =
          params.remove(customField.name().replace("_", "").toLowerCase());
      if (customFieldParams != null) {
        String[] customValues = extractCommaSeparatedFields(customFieldParams);
        taskQuery.customAttributeIn(customField, customValues);
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", taskQuery);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), query: {}", taskQuery);
    }
  }

  private WildcardSearchField[] createWildcardSearchFields(String[] wildcardFields) {

    return Stream.of(wildcardFields)
        .map(WildcardSearchField::fromString)
        .filter(Objects::nonNull)
        .toArray(WildcardSearchField[]::new);
  }

  private void updateTaskQueryWithWorkbasketKey(
      TaskQuery taskQuery, MultiValueMap<String, String> params) throws InvalidArgumentException {

    String[] domains = null;
    if (params.get(DOMAIN) != null) {
      domains = extractCommaSeparatedFields(params.get(DOMAIN));
    }
    if (domains == null || domains.length != 1) {
      throw new InvalidArgumentException(
          "workbasket-key requires excactly one domain as second parameter.");
    }
    String[] workbasketKeys = extractCommaSeparatedFields(params.get(WORKBASKET_KEY));
    KeyDomain[] keyDomains = new KeyDomain[workbasketKeys.length];
    for (int i = 0; i < workbasketKeys.length; i++) {
      keyDomains[i] = new KeyDomain(workbasketKeys[i], domains[0]);
    }
    taskQuery.workbasketKeyDomainIn(keyDomains);
    params.remove(WORKBASKET_KEY);
    params.remove(DOMAIN);
  }

  private void checkForIllegalParamCombinations(MultiValueMap<String, String> params) {

    if (params.containsKey(PLANNED)
        && (params.containsKey(PLANNED_FROM) || params.containsKey(PLANNED_UNTIL))) {

      throw new IllegalArgumentException(
          "It is prohibited to use the param \""
              + PLANNED
              + "\" in combination with the params \""
              + PLANNED_FROM
              + "\" and / or \""
              + PLANNED_UNTIL
              + "\"");
    }

    if (params.containsKey(DUE) && (params.containsKey(DUE_FROM) || params.containsKey(DUE_TO))) {

      throw new IllegalArgumentException(
          "It is prohibited to use the param \""
              + DUE
              + "\" in combination with the params \""
              + PLANNED_FROM
              + "\" and / or \""
              + PLANNED_UNTIL
              + "\"");
    }

    if (params.containsKey(WILDCARD_SEARCH_FIELDS) && !params.containsKey(WILDCARD_SEARCH_VALUE)
        || !params.containsKey(WILDCARD_SEARCH_FIELDS)
            && params.containsKey(WILDCARD_SEARCH_VALUE)) {

      throw new IllegalArgumentException(
          "The params "
              + WILDCARD_SEARCH_FIELDS
              + " and "
              + WILDCARD_SEARCH_VALUE
              + " must be used together!");
    }
  }

  private void updateTaskQueryWithIndefiniteTimeInterval(
      TaskQuery taskQuery,
      MultiValueMap<String, String> params,
      String param,
      TimeInterval timeInterval) {

    if (param.equals(PLANNED_FROM) || param.equals(PLANNED_UNTIL)) {
      taskQuery.plannedWithin(timeInterval);

    } else {
      taskQuery.dueWithin(timeInterval);
    }
    params.remove(param);
  }

  private TimeInterval createIndefiniteTimeIntervalFromParam(
      MultiValueMap<String, String> params, String param) {

    if (param.equals(PLANNED_FROM) || param.equals(DUE_FROM)) {

      return new TimeInterval(Instant.parse(params.get(param).get(0)), null);

    } else {

      return new TimeInterval(null, Instant.parse(params.get(param).get(0)));
    }
  }

  private void updateTaskQueryWithPlannedOrDueTimeInterval(
      TaskQuery taskQuery,
      MultiValueMap<String, String> params,
      String plannedFromOrDueFrom,
      String plannedToOrDueTo) {

    TimeInterval timeInterval =
        new TimeInterval(
            Instant.parse(params.get(plannedFromOrDueFrom).get(0)),
            Instant.parse(params.get(plannedToOrDueTo).get(0)));

    taskQuery.plannedWithin(timeInterval);

    params.remove(plannedToOrDueTo);
    params.remove(plannedFromOrDueFrom);
  }

  private void updateTaskQueryWithPlannedOrDueTimeIntervals(
      TaskQuery taskQuery, MultiValueMap<String, String> params, String plannedOrDue) {

    String[] instants = extractCommaSeparatedFields(params.get(plannedOrDue));

    TimeInterval[] timeIntervals = extractTimeIntervals(instants);

    taskQuery.plannedWithin(timeIntervals);

    params.remove(plannedOrDue);
  }

  private TimeInterval[] extractTimeIntervals(String[] instants) {

    List<TimeInterval> timeIntervalsList = new ArrayList<>();

    for (int i = 0; i < instants.length - 1; i += 2) {

      TimeInterval timeInterval = determineTimeInterval(instants, i);

      if (timeInterval != null) {

        timeIntervalsList.add(timeInterval);
      }
    }

    TimeInterval[] timeIntervalArray = new TimeInterval[timeIntervalsList.size()];

    return timeIntervalsList.toArray(timeIntervalArray);
  }

  private TimeInterval determineTimeInterval(String[] instants, int i) {

    if (!instants[i].equals(INDEFINITE) && !instants[i + 1].equals(INDEFINITE)) {

      return new TimeInterval(Instant.parse(instants[i]), Instant.parse(instants[i + 1]));

    } else if (instants[i].equals(INDEFINITE) && !instants[i + 1].equals(INDEFINITE)) {

      return new TimeInterval(null, Instant.parse(instants[i + 1]));

    } else if (!instants[i].equals(INDEFINITE) && instants[i + 1].equals(INDEFINITE)) {

      return new TimeInterval(Instant.parse(instants[i]), null);
    }

    return null;
  }

  private void applySortingParams(TaskQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(query= {}, params= {})", query, params);
    }

    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          switch (sortBy) {
            case (CLASSIFICATION_KEY):
              query.orderByClassificationKey(sortDirection);
              break;
            case (POR_TYPE):
              query.orderByPrimaryObjectReferenceType(sortDirection);
              break;
            case (POR_VALUE):
              query.orderByPrimaryObjectReferenceValue(sortDirection);
              break;
            case (STATE):
              query.orderByState(sortDirection);
              break;
            case (NAME):
              query.orderByName(sortDirection);
              break;
            case (DUE):
              query.orderByDue(sortDirection);
              break;
            case (PLANNED):
              query.orderByPlanned(sortDirection);
              break;
            case (PRIORITY):
              query.orderByPriority(sortDirection);
              break;
            default:
              throw new InvalidArgumentException("Unknown filter attribute: " + sortBy);
          }
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", query);
    }
  }

  private int[] extractPriorities(String[] prioritiesInString) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to extractPriorities(prioritiesInString= {})", (Object[]) prioritiesInString);
    }

    int[] priorities = new int[prioritiesInString.length];
    for (int i = 0; i < prioritiesInString.length; i++) {
      priorities[i] = Integer.parseInt(prioritiesInString[i]);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from extractPriorities(), returning {}", priorities);
    }

    return priorities;
  }

  private TaskState[] extractStates(MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to extractStates(params= {})", params);
    }

    List<TaskState> states = new ArrayList<>();
    for (String item : params.get(STATE)) {
      for (String state : item.split(",")) {
        switch (state) {
          case STATE_VALUE_READY:
            states.add(TaskState.READY);
            break;
          case STATE_VALUE_COMPLETED:
            states.add(TaskState.COMPLETED);
            break;
          case STATE_VALUE_CLAIMED:
            states.add(TaskState.CLAIMED);
            break;
          default:
            throw new InvalidArgumentException("Unknown status '" + state + "'");
        }
      }
    }

    LOGGER.debug("Exit from extractStates()");
    return states.toArray(new TaskState[0]);
  }
}
