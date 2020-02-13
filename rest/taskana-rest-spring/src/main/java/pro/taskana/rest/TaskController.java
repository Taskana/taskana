package pro.taskana.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.AttachmentPersistenceException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidOwnerException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskResourceAssembler;
import pro.taskana.rest.resource.TaskSummaryListResource;
import pro.taskana.rest.resource.TaskSummaryResourceAssembler;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
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
  private static final String PLANNED_TO = "planned-until";
  private static final String PLANNED_FROM = "planned-from";

  private static final String SORT_BY = "sort-by";
  private static final String SORT_DIRECTION = "order";

  private static final String INDEFINITE = "";

  private TaskService taskService;

  private TaskResourceAssembler taskResourceAssembler;

  private TaskSummaryResourceAssembler taskSummaryResourceAssembler;

  TaskController(
      TaskService taskService,
      TaskResourceAssembler taskResourceAssembler,
      TaskSummaryResourceAssembler taskSummaryResourceAssembler) {
    this.taskService = taskService;
    this.taskResourceAssembler = taskResourceAssembler;
    this.taskSummaryResourceAssembler = taskSummaryResourceAssembler;
  }

  @GetMapping(path = Mapping.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryListResource> getTasks(
      @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTasks(params= {})", params);
    }

    TaskQuery query = taskService.createTaskQuery();
    query = applyFilterParams(query, params);
    query = applySortingParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<TaskSummary> taskSummaries = getQueryList(query, pageMetadata);

    TaskSummaryListResource pagedResources =
        taskSummaryResourceAssembler.toResources(taskSummaries, pageMetadata);
    ResponseEntity<TaskSummaryListResource> response = ResponseEntity.ok(pagedResources);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasks(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_TASKS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> getTask(@PathVariable String taskId)
      throws TaskNotFoundException, NotAuthorizedException {
    LOGGER.debug("Entry to getTask(taskId= {})", taskId);
    Task task = taskService.getTask(taskId);
    ResponseEntity<TaskResource> result = ResponseEntity.ok(taskResourceAssembler.toResource(task));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTask(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = Mapping.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> claimTask(
      @PathVariable String taskId, @RequestBody String userName)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    LOGGER.debug("Entry to claimTask(taskId= {}, userName= {})", taskId, userName);
    // TODO verify user
    taskService.claim(taskId);
    Task updatedTask = taskService.getTask(taskId);
    ResponseEntity<TaskResource> result =
        ResponseEntity.ok(taskResourceAssembler.toResource(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from claimTask(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = Mapping.URL_TASKS_ID_COMPLETE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> completeTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    LOGGER.debug("Entry to completeTask(taskId= {})", taskId);
    taskService.forceCompleteTask(taskId);
    Task updatedTask = taskService.getTask(taskId);
    ResponseEntity<TaskResource> result =
        ResponseEntity.ok(taskResourceAssembler.toResource(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from completeTask(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = Mapping.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> deleteTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    LOGGER.debug("Entry to deleteTask(taskId= {})", taskId);
    taskService.forceDeleteTask(taskId);
    ResponseEntity<TaskResource> result = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from deleteTask(), returning {}", result);
    return result;
  }

  @PostMapping(path = Mapping.URL_TASKS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> createTask(@RequestBody TaskResource taskResource)
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to createTask(params= {})", taskResource);
    }

    Task fromResource = taskResourceAssembler.toModel(taskResource);
    Task createdTask = taskService.createTask(fromResource);

    ResponseEntity<TaskResource> result =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(taskResourceAssembler.toResource(createdTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createTask(), returning {}", result);
    }

    return result;
  }

  @RequestMapping(path = Mapping.URL_TASKS_ID_TRANSFER_WORKBASKETID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> transferTask(
      @PathVariable String taskId, @PathVariable String workbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    LOGGER.debug("Entry to transferTask(taskId= {}, workbasketId= {})", taskId, workbasketId);
    Task updatedTask = taskService.transfer(taskId, workbasketId);
    ResponseEntity<TaskResource> result =
        ResponseEntity.ok(taskResourceAssembler.toResource(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from transferTask(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = Mapping.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskResource> updateTask(
      @PathVariable(value = "taskId") String taskId, @RequestBody TaskResource taskResource)
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    LOGGER.debug("Entry to updateTask(taskId= {}, taskResource= {})", taskId, taskResource);
    ResponseEntity<TaskResource> result;
    if (taskId.equals(taskResource.getTaskId())) {
      Task task = taskResourceAssembler.toModel(taskResource);
      task = taskService.updateTask(task);
      result = ResponseEntity.ok(taskResourceAssembler.toResource(task));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskId ('%s') is not identical with the taskId of to "
                  + "object in the payload which should be updated. ID=('%s')",
              taskId, taskResource.getTaskId()));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTask(), returning {}", result);
    }

    return result;
  }

  private TaskQuery applyFilterParams(TaskQuery taskQuery, MultiValueMap<String, String> params)
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

    if (params.containsKey(PLANNED_FROM) && params.containsKey(PLANNED_TO)) {
      updateTaskQueryWithPlannedOrDueTimeInterval(taskQuery, params, PLANNED_FROM, PLANNED_TO);

    } else if (params.containsKey(PLANNED_FROM) && !params.containsKey(PLANNED_TO)) {

      TimeInterval timeInterval = createIndefiniteTimeIntervalFromParam(params, PLANNED_FROM);
      updateTaskQueryWithIndefiniteTimeInterval(taskQuery, params, PLANNED_FROM, timeInterval);

    } else if (!params.containsKey(PLANNED_FROM) && params.containsKey(PLANNED_TO)) {

      TimeInterval timeInterval = createIndefiniteTimeIntervalFromParam(params, PLANNED_TO);
      updateTaskQueryWithIndefiniteTimeInterval(taskQuery, params, PLANNED_TO, timeInterval);
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

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", taskQuery);
    }

    return taskQuery;
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
        && (params.containsKey(PLANNED_FROM) || params.containsKey(PLANNED_TO))) {

      throw new IllegalArgumentException(
          "It is prohibited to use the param \""
              + PLANNED
              + "\" in combination with the params \""
              + PLANNED_FROM
              + "\" and / or \""
              + PLANNED_TO
              + "\"");
    }

    if (params.containsKey(DUE) && (params.containsKey(DUE_FROM) || params.containsKey(DUE_TO))) {

      throw new IllegalArgumentException(
          "It is prohibited to use the param \""
              + DUE
              + "\" in combination with the params \""
              + PLANNED_FROM
              + "\" and / or \""
              + PLANNED_TO
              + "\"");
    }
  }

  private void updateTaskQueryWithIndefiniteTimeInterval(
      TaskQuery taskQuery,
      MultiValueMap<String, String> params,
      String param,
      TimeInterval timeInterval) {

    if (param.equals(PLANNED_FROM) || param.equals(PLANNED_TO)) {
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

  private TaskQuery applySortingParams(TaskQuery taskQuery, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(taskQuery= {}, params= {})", taskQuery, params);
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
        case (CLASSIFICATION_KEY):
          taskQuery = taskQuery.orderByClassificationKey(sortDirection);
          break;
        case (POR_TYPE):
          taskQuery = taskQuery.orderByPrimaryObjectReferenceType(sortDirection);
          break;
        case (POR_VALUE):
          taskQuery = taskQuery.orderByPrimaryObjectReferenceValue(sortDirection);
          break;
        case (STATE):
          taskQuery = taskQuery.orderByState(sortDirection);
          break;
        case (NAME):
          taskQuery = taskQuery.orderByName(sortDirection);
          break;
        case (DUE):
          taskQuery = taskQuery.orderByDue(sortDirection);
          break;
        case (PLANNED):
          taskQuery = taskQuery.orderByPlanned(sortDirection);
          break;
        case (PRIORITY):
          taskQuery = taskQuery.orderByPriority(sortDirection);
          break;
        default:
          throw new InvalidArgumentException("Unknown filter attribute: " + sortBy);
      }
    }
    params.remove(SORT_BY);
    params.remove(SORT_DIRECTION);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", taskQuery);
    }

    return taskQuery;
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
