package pro.taskana.task.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.CustomPropertySelector;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.spi.history.api.events.task.ClaimCancelledEvent;
import pro.taskana.spi.history.api.events.task.ClaimedEvent;
import pro.taskana.spi.history.api.events.task.CompletedEvent;
import pro.taskana.spi.history.api.events.task.CreatedEvent;
import pro.taskana.spi.history.internal.HistoryEventProducer;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** This is the implementation of TaskService. */
@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public class TaskServiceImpl implements TaskService {

  private static final String IS_ALREADY_CLAIMED_BY = " is already claimed by ";
  private static final String IS_ALREADY_COMPLETED = " is already completed.";
  private static final String TASK_WITH_ID_IS_NOT_READY = "Task with id %s is not in state ready.";
  private static final String WAS_NOT_FOUND2 = " was not found.";
  private static final String WAS_NOT_FOUND = " was not found";
  private static final String TASK_WITH_ID = "Task with id ";
  private static final String WAS_MARKED_FOR_DELETION = " was marked for deletion";
  private static final String THE_WORKBASKET = "The workbasket ";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
  private static final String ID_PREFIX_ATTACHMENT = "TAI";
  private static final String ID_PREFIX_TASK = "TKI";
  private static final String ID_PREFIX_EXT_TASK_ID = "ETI";
  private static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";
  private static final String MUST_NOT_BE_EMPTY = " must not be empty";
  private static final Duration MAX_DURATION = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
  private static final Set<String> ALLOWED_KEYS =
      IntStream.rangeClosed(1, 16).mapToObj(String::valueOf).collect(Collectors.toSet());
  private DaysToWorkingDaysConverter converter;
  private InternalTaskanaEngine taskanaEngine;
  private WorkbasketService workbasketService;
  private ClassificationService classificationService;
  private TaskMapper taskMapper;
  private AttachmentMapper attachmentMapper;
  private HistoryEventProducer historyEventProducer;
  private TaskTransferrer taskTransferrer;

  public TaskServiceImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      AttachmentMapper attachmentMapper) {
    super();
    try {
      this.converter = DaysToWorkingDaysConverter.initialize();
    } catch (InvalidArgumentException e) {
      throw new SystemException(
          "Internal error. Cannot initialize DaysToWorkingDaysConverter", e.getCause());
    }
    this.taskanaEngine = taskanaEngine;
    this.taskMapper = taskMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
    this.attachmentMapper = attachmentMapper;
    this.classificationService = taskanaEngine.getEngine().getClassificationService();
    this.historyEventProducer = taskanaEngine.getHistoryEventProducer();
    this.taskTransferrer = new TaskTransferrer(taskanaEngine, taskMapper, this);
  }

  @Override
  public Task claim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    return claim(taskId, false);
  }

  @Override
  public Task forceClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    return claim(taskId, true);
  }

  @Override
  public Task cancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    return this.cancelClaim(taskId, false);
  }

  @Override
  public Task forceCancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    return this.cancelClaim(taskId, true);
  }

  @Override
  public Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    return completeTask(taskId, false);
  }

  @Override
  public Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    return completeTask(taskId, true);
  }

  @Override
  public Task createTask(Task taskToCreate)
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException {
    LOGGER.debug("entry to createTask(task = {})", taskToCreate);
    TaskImpl task = (TaskImpl) taskToCreate;
    try {
      taskanaEngine.openConnection();

      if (task.getId() != null && !task.getId().equals("")) {
        throw new TaskAlreadyExistException(task.getId());
      }

      LOGGER.debug("Task {} cannot be found, so it can be created.", task.getId());
      Workbasket workbasket;

      if (task.getWorkbasketSummary().getId() != null) {
        workbasket = workbasketService.getWorkbasket(task.getWorkbasketSummary().getId());
      } else if (task.getWorkbasketKey() != null) {
        workbasket = workbasketService.getWorkbasket(task.getWorkbasketKey(), task.getDomain());
      } else {
        String workbasketId = taskanaEngine.getTaskRoutingManager().determineWorkbasketId(task);
        if (workbasketId != null) {
          workbasket = workbasketService.getWorkbasket(workbasketId);
          task.setWorkbasketSummary(workbasket.asSummary());
        } else {
          throw new InvalidArgumentException("Cannot create a task outside a workbasket");
        }
      }

      if (workbasket.isMarkedForDeletion()) {
        throw new WorkbasketNotFoundException(
            workbasket.getId(), THE_WORKBASKET + workbasket.getId() + WAS_MARKED_FOR_DELETION);
      }

      task.setWorkbasketSummary(workbasket.asSummary());
      task.setDomain(workbasket.getDomain());

      workbasketService.checkAuthorization(
          task.getWorkbasketSummary().getId(), WorkbasketPermission.APPEND);

      // we do use the key and not the ID to make sure that we use the classification from the right
      // domain.
      // otherwise we would have to check the classification and its domain for validity.
      String classificationKey = task.getClassificationKey();
      if (classificationKey == null || classificationKey.length() == 0) {
        throw new InvalidArgumentException("classificationKey of task must not be empty");
      }

      Classification classification =
          this.classificationService.getClassification(classificationKey, workbasket.getDomain());
      task.setClassificationSummary(classification.asSummary());
      validateObjectReference(task.getPrimaryObjRef(), "primary ObjectReference", "Task");
      PrioDurationHolder prioDurationFromAttachments = handleAttachments(task);
      standardSettings(task, classification, prioDurationFromAttachments);
      setCallbackStateOnTaskCreation(task);
      try {
        this.taskMapper.insert(task);
        LOGGER.debug("Method createTask() created Task '{}'.", task.getId());
        if (HistoryEventProducer.isHistoryEnabled()) {
          historyEventProducer.createEvent(new CreatedEvent(task, CurrentUserContext.getUserid()));
        }
      } catch (PersistenceException e) {
        // Error messages:
        // Postgres: ERROR: duplicate key value violates unique constraint "uc_external_id"
        // DB/2: ### Error updating database.  Cause:
        // com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: DB2 SQL Error: SQLCODE=-803,
        // SQLSTATE=23505, SQLERRMC=2;TASKANA.TASK, DRIVER=4.22.29
        //       ### The error may involve pro.taskana.mappings.TaskMapper.insert-Inline
        //       ### The error occurred while setting parameters
        //       ### SQL: INSERT INTO TASK(ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED,
        // PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE,
        // CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID,
        // WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER,
        // POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED,
        // CALLBACK_INFO, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5,
        // CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11,  CUSTOM_12,  CUSTOM_13,
        // CUSTOM_14,  CUSTOM_15,  CUSTOM_16 ) VALUES(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
        // ?,  ?)
        //       ### Cause: com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: DB2 SQL
        // Error: SQLCODE=-803, SQLSTATE=23505, SQLERRMC=2;TASKANA.TASK, DRIVER=4.22.29
        // H2:   ### Error updating database.  Cause: org.h2.jdbc.JdbcSQLException: Unique index or
        // primary key violation: "UC_EXTERNAL_ID_INDEX_2 ON TASKANA.TASK(EXTERNAL_ID) ...
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : null;
        if (msg != null
            && (msg.contains("violation") || msg.contains("violates"))
            && msg.contains("external_id")) {
          throw new TaskAlreadyExistException(
              "Task with external id " + task.getExternalId() + " already exists");
        } else {
          throw e;
        }
      }
      return task;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from createTask(task = {})", task);
    }
  }

  @Override
  public Task getTask(String id) throws TaskNotFoundException, NotAuthorizedException {
    LOGGER.debug("entry to getTaskById(id = {})", id);
    TaskImpl resultTask = null;
    try {
      taskanaEngine.openConnection();

      resultTask = taskMapper.findById(id);
      if (resultTask != null) {
        WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
        query.setUsedToAugmentTasks(true);
        String workbasketId = resultTask.getWorkbasketSummary().getId();
        List<WorkbasketSummary> workbaskets = query.idIn(workbasketId).list();
        if (workbaskets.isEmpty()) {
          String currentUser = CurrentUserContext.getUserid();
          throw new NotAuthorizedException(
              "The current user "
                  + currentUser
                  + " has no read permission for workbasket "
                  + workbasketId,
              CurrentUserContext.getUserid());
        } else {
          resultTask.setWorkbasketSummary(workbaskets.get(0));
        }

        List<AttachmentImpl> attachmentImpls =
            attachmentMapper.findAttachmentsByTaskId(resultTask.getId());
        if (attachmentImpls == null) {
          attachmentImpls = new ArrayList<>();
        }

        List<ClassificationSummary> classifications;
        classifications = findClassificationForTaskImplAndAttachments(resultTask, attachmentImpls);
        List<Attachment> attachments =
            addClassificationSummariesToAttachments(attachmentImpls, classifications);
        resultTask.setAttachments(attachments);

        String classificationId = resultTask.getClassificationSummary().getId();
        ClassificationSummary classification =
            classifications.stream()
                .filter(c -> c.getId().equals(classificationId))
                .findFirst()
                .orElse(null);
        if (classification == null) {
          throw new SystemException(
              "Could not find a Classification for task " + resultTask.getId());
        }

        resultTask.setClassificationSummary(classification);
        return resultTask;
      } else {
        throw new TaskNotFoundException(id, TASK_WITH_ID + id + WAS_NOT_FOUND);
      }
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from getTaskById(). Returning result {} ", resultTask);
    }
  }

  @Override
  public Task transfer(String taskId, String destinationWorkbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return taskTransferrer.transfer(taskId, destinationWorkbasketId);
  }

  @Override
  public Task transfer(String taskId, String workbasketKey, String domain)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return taskTransferrer.transfer(taskId, workbasketKey, domain);
  }

  @Override
  public Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException {
    LOGGER.debug("entry to setTaskRead(taskId = {}, isRead = {})", taskId, isRead);
    TaskImpl task = null;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      task.setRead(isRead);
      task.setModified(Instant.now());
      taskMapper.update(task);
      LOGGER.debug("Method setTaskRead() set read property of Task '{}' to {} ", task, isRead);
      return task;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from setTaskRead(taskId, isRead). Returning result {} ", task);
    }
  }

  @Override
  public TaskQuery createTaskQuery() {
    return new TaskQueryImpl(taskanaEngine);
  }

  @Override
  public Task newTask() {
    return newTask(null);
  }

  @Override
  public Task newTask(String workbasketId) {
    TaskImpl task = new TaskImpl();
    WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
    wb.setId(workbasketId);
    task.setWorkbasketSummary(wb);
    task.setCallbackState(CallbackState.NONE);
    return task;
  }

  @Override
  public Task newTask(String workbasketKey, String domain) {
    LOGGER.debug("entry to newTask(workbasketKey = {}, domain = {})", workbasketKey, domain);
    TaskImpl task = new TaskImpl();
    WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
    wb.setKey(workbasketKey);
    wb.setDomain(domain);
    task.setWorkbasketSummary(wb);
    LOGGER.debug("exit from newTask(), returning {}", task);
    return task;
  }

  @Override
  public Attachment newAttachment() {
    return new AttachmentImpl();
  }

  @Override
  public Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    String userId = CurrentUserContext.getUserid();
    LOGGER.debug("entry to updateTask(task = {}, userId = {})", task, userId);
    TaskImpl newTaskImpl = (TaskImpl) task;
    TaskImpl oldTaskImpl;
    try {
      taskanaEngine.openConnection();
      oldTaskImpl = (TaskImpl) getTask(newTaskImpl.getId());
      PrioDurationHolder prioDurationFromAttachments =
          handleAttachmentsOnTaskUpdate(oldTaskImpl, newTaskImpl);
      standardUpdateActions(oldTaskImpl, newTaskImpl, prioDurationFromAttachments);

      taskMapper.update(newTaskImpl);
      LOGGER.debug("Method updateTask() updated task '{}' for user '{}'.", task.getId(), userId);

    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from claim()");
    }
    return task;
  }

  @Override
  public BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return taskTransferrer.transferTasks(destinationWorkbasketId, taskIds);
  }

  @Override
  public BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey, String destinationWorkbasketDomain, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return taskTransferrer.transferTasks(
        destinationWorkbasketKey, destinationWorkbasketDomain, taskIds);
  }

  @Override
  public void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    deleteTask(taskId, false);
  }

  @Override
  public void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    deleteTask(taskId, true);
  }

  @Override
  public BulkOperationResults<String, TaskanaException> deleteTasks(List<String> taskIds)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to deleteTasks(tasks = {})", LoggerUtils.listToString(taskIds));
    }
    try {
      taskanaEngine.openConnection();
      if (taskIds == null) {
        throw new InvalidArgumentException("List of TaskIds must not be null.");
      }

      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

      if (taskIds.isEmpty()) {
        return bulkLog;
      }

      List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(taskIds, null);

      Iterator<String> taskIdIterator = taskIds.iterator();
      while (taskIdIterator.hasNext()) {
        removeSingleTaskForTaskDeletionById(bulkLog, taskSummaries, taskIdIterator);
      }
      if (!taskIds.isEmpty()) {
        taskMapper.deleteMultiple(taskIds);
      }
      return bulkLog;
    } finally {
      LOGGER.debug("exit from deleteTasks()");
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> completeTasks(
      List<String> taskIdsToBeCompleted) throws InvalidArgumentException {
    try {
      LOGGER.debug("entry to completeTasks(taskIds = {})", taskIdsToBeCompleted);
      taskanaEngine.openConnection();

      if (taskIdsToBeCompleted == null || taskIdsToBeCompleted.isEmpty()) {
        throw new InvalidArgumentException("TaskIds can´t be used as NULL-Parameter.");
      }

      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
      List<String> taskIds = new ArrayList<>(taskIdsToBeCompleted);
      removeNonExistingTasksFromTaskIdList(taskIds, bulkLog);

      List<TaskSummary> taskSummaries =
          this.createTaskQuery().idIn(taskIds.toArray(new String[0])).list();

      checkIfTasksMatchCompleteCriteria(taskIds, taskSummaries, bulkLog);

      updateTasksToBeCompleted(taskIds, taskSummaries);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from to completeTasks(taskIds = {})", taskIdsToBeCompleted);
    }
  }

  @Override
  public List<String> updateTasks(
      ObjectReference selectionCriteria, Map<String, String> customFieldsToUpdate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to updateTasks(selectionCriteria = {}, customFieldsToUpdate = {})",
          selectionCriteria,
          customFieldsToUpdate);
    }
    validateObjectReference(selectionCriteria, "ObjectReference", "updateTasks call");
    validateCustomFields(customFieldsToUpdate);
    CustomPropertySelector fieldSelector = new CustomPropertySelector();
    TaskImpl updated = initUpdatedTask(customFieldsToUpdate, fieldSelector);

    try {
      taskanaEngine.openConnection();

      // use query in order to find only those tasks that are visible to the current user
      List<TaskSummary> taskSummaries = getTasksToChange(selectionCriteria);

      List<String> changedTasks = new ArrayList<>();
      if (!taskSummaries.isEmpty()) {
        changedTasks = taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());
        taskMapper.updateTasks(changedTasks, updated, fieldSelector);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "updateTasks() updated the following tasks: {} ",
              LoggerUtils.listToString(changedTasks));
        }

      } else {
        LOGGER.debug("updateTasks() found no tasks for update ");
      }
      return changedTasks;
    } finally {
      LOGGER.debug("exit from updateTasks().");
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> updateTasks(List<String> taskIds, Map<String, String> customFieldsToUpdate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to updateTasks(taskIds = {}, customFieldsToUpdate = {})",
          taskIds,
          customFieldsToUpdate);
    }

    validateCustomFields(customFieldsToUpdate);
    CustomPropertySelector fieldSelector = new CustomPropertySelector();
    TaskImpl updatedTask = initUpdatedTask(customFieldsToUpdate, fieldSelector);

    try {
      taskanaEngine.openConnection();

      // use query in order to find only those tasks that are visible to the current user
      List<TaskSummary> taskSummaries = getTasksToChange(taskIds);

      List<String> changedTasks = new ArrayList<>();
      if (!taskSummaries.isEmpty()) {
        changedTasks = taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());
        taskMapper.updateTasks(changedTasks, updatedTask, fieldSelector);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "updateTasks() updated the following tasks: {} ",
              LoggerUtils.listToString(changedTasks));
        }

      } else {
        LOGGER.debug("updateTasks() found no tasks for update ");
      }
      return changedTasks;
    } finally {
      LOGGER.debug("exit from updateTasks().");
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to setCallbackStateForTasks(externalIds = {})",
          LoggerUtils.listToString(externalIds));
    }
    try {
      taskanaEngine.openConnection();

      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

      if (externalIds == null || externalIds.isEmpty()) {
        return bulkLog;
      }

      List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(null, externalIds);

      Iterator<String> taskIdIterator = externalIds.iterator();
      while (taskIdIterator.hasNext()) {
        removeSingleTaskForCallbackStateByExternalId(bulkLog, taskSummaries, taskIdIterator, state);
      }
      if (!externalIds.isEmpty()) {
        taskMapper.setCallbackStateMultiple(externalIds, state);
      }
      return bulkLog;
    } finally {
      LOGGER.debug("exit from setCallbckStateForTasks()");
      taskanaEngine.returnConnection();
    }
  }

  public Set<String> findTasksIdsAffectedByClassificationChange(String classificationId) {
    LOGGER.debug(
        "entry to findTasksIdsAffectedByClassificationChange(classificationId = {})",
        classificationId);
    // tasks directly affected
    List<TaskSummary> tasks =
        createTaskQuery()
            .classificationIdIn(classificationId)
            .stateIn(TaskState.READY, TaskState.CLAIMED)
            .list();

    // tasks indirectly affected via attachments
    List<String> taskIdsFromAttachments =
        attachmentMapper.findTaskIdsAffectedByClassificationChange(classificationId);

    List<String> filteredTaskIdsFromAttachments =
        taskIdsFromAttachments.isEmpty()
            ? new ArrayList<>()
            : taskMapper.filterTaskIdsForNotCompleted(taskIdsFromAttachments);

    Set<String> affectedTaskIds = new HashSet<>(filteredTaskIdsFromAttachments);
    for (TaskSummary task : tasks) {
      affectedTaskIds.add(task.getId());
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "the following tasks are affected by the update of classification {} : {}",
          classificationId,
          LoggerUtils.setToString(affectedTaskIds));
    }
    LOGGER.debug("exit from findTasksIdsAffectedByClassificationChange(). ");
    return affectedTaskIds;
  }

  public void refreshPriorityAndDueDate(String taskId) throws ClassificationNotFoundException {
    LOGGER.debug("entry to refreshPriorityAndDueDate(taskId = {})", taskId);
    TaskImpl task;
    BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
    try {
      taskanaEngine.openConnection();
      if (taskId == null || taskId.isEmpty()) {
        return;
      }

      task = taskMapper.findById(taskId);

      List<AttachmentImpl> attachmentImpls = attachmentMapper.findAttachmentsByTaskId(task.getId());
      if (attachmentImpls == null) {
        attachmentImpls = new ArrayList<>();
      }
      List<Attachment> attachments = augmentAttachmentsByClassification(attachmentImpls, bulkLog);
      task.setAttachments(attachments);

      Classification classification =
          classificationService.getClassification(task.getClassificationSummary().getId());
      task.setClassificationSummary(classification.asSummary());
      PrioDurationHolder prioDurationFromAttachments =
          handleAttachmentsOnClassificationUpdate(task);

      updatePrioDueDateOnClassificationUpdate(task, prioDurationFromAttachments);

      task.setModified(Instant.now());
      taskMapper.update(task);
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from refreshPriorityAndDueDate(). ");
    }
  }

  void removeNonExistingTasksFromTaskIdList(
      List<String> taskIds, BulkOperationResults<String, TaskanaException> bulkLog) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to removeNonExistingTasksFromTaskIdList(targetWbId = {}, taskIds = {})",
          taskIds,
          bulkLog);
    }

    Iterator<String> taskIdIterator = taskIds.iterator();
    while (taskIdIterator.hasNext()) {
      String currentTaskId = taskIdIterator.next();
      if (currentTaskId == null || currentTaskId.equals("")) {
        bulkLog.addError(
            "", new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
        taskIdIterator.remove();
      }
    }
    LOGGER.debug("exit from removeNonExistingTasksFromTaskIdList()");
  }

  Duration calculateDuration(
      PrioDurationHolder prioDurationFromAttachments,
      ClassificationSummary newClassificationSummary) {
    if (newClassificationSummary.getServiceLevel() == null) {
      return null;
    }
    Duration minDuration = prioDurationFromAttachments.getLeft();
    Duration durationFromClassification =
        Duration.parse(newClassificationSummary.getServiceLevel());
    if (minDuration != null) {
      if (minDuration.compareTo(durationFromClassification) > 0) {
        minDuration = durationFromClassification;
      }
    } else {
      minDuration = durationFromClassification;
    }
    return minDuration;
  }

  List<TaskSummary> augmentTaskSummariesByContainedSummaries(List<TaskSummaryImpl> taskSummaries) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to augmentTaskSummariesByContainedSummaries(taskSummaries= {})",
          LoggerUtils.listToString(taskSummaries));
    }

    List<TaskSummary> result = new ArrayList<>();
    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return result;
    }

    String[] taskIdArray =
        taskSummaries.stream().map(TaskSummaryImpl::getId).distinct().toArray(String[]::new);

    LOGGER.debug(
        "augmentTaskSummariesByContainedSummaries() about to query for attachmentSummaries ");
    List<AttachmentSummaryImpl> attachmentSummaries =
        attachmentMapper.findAttachmentSummariesByTaskIds(taskIdArray);

    List<ClassificationSummary> classifications =
        findClassificationsForTasksAndAttachments(taskSummaries, attachmentSummaries);

    addClassificationSummariesToTaskSummaries(taskSummaries, classifications);
    addWorkbasketSummariesToTaskSummaries(taskSummaries);
    addAttachmentSummariesToTaskSummaries(taskSummaries, attachmentSummaries, classifications);
    result.addAll(taskSummaries);
    LOGGER.debug("exit from to augmentTaskSummariesByContainedSummaries()");
    return result;
  }

  private Task claim(String taskId, boolean forceClaim)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    String userId = CurrentUserContext.getUserid();
    LOGGER.debug(
        "entry to claim(id = {}, userId = {}, forceClaim = {})", taskId, userId, forceClaim);
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      TaskState state = task.getState();
      if (state == TaskState.COMPLETED) {
        throw new InvalidStateException(TASK_WITH_ID + taskId + IS_ALREADY_COMPLETED);
      }
      if (state == TaskState.CLAIMED && !forceClaim && !task.getOwner().equals(userId)) {
        throw new InvalidOwnerException(
            TASK_WITH_ID + taskId + IS_ALREADY_CLAIMED_BY + task.getOwner() + ".");
      }
      Instant now = Instant.now();
      task.setOwner(userId);
      task.setModified(now);
      task.setClaimed(now);
      task.setRead(true);
      task.setState(TaskState.CLAIMED);
      taskMapper.update(task);
      LOGGER.debug("Task '{}' claimed by user '{}'.", taskId, userId);
      if (HistoryEventProducer.isHistoryEnabled()) {
        historyEventProducer.createEvent(new ClaimedEvent(task, CurrentUserContext.getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from claim()");
    }
    return task;
  }

  private Task cancelClaim(String taskId, boolean forceUnclaim)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    String userId = CurrentUserContext.getUserid();
    LOGGER.debug(
        "entry to cancelClaim(taskId = {}), userId = {}, forceUnclaim = {})",
        taskId,
        userId,
        forceUnclaim);
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      TaskState state = task.getState();
      if (state == TaskState.COMPLETED) {
        throw new InvalidStateException(TASK_WITH_ID + taskId + IS_ALREADY_COMPLETED);
      }
      if (state == TaskState.CLAIMED && !forceUnclaim && !userId.equals(task.getOwner())) {
        throw new InvalidOwnerException(
            TASK_WITH_ID + taskId + IS_ALREADY_CLAIMED_BY + task.getOwner() + ".");
      }
      Instant now = Instant.now();
      task.setOwner(null);
      task.setModified(now);
      task.setClaimed(null);
      task.setRead(true);
      task.setState(TaskState.READY);
      taskMapper.update(task);
      LOGGER.debug("Task '{}' unclaimed by user '{}'.", taskId, userId);
      if (HistoryEventProducer.isHistoryEnabled()) {
        historyEventProducer.createEvent(
            new ClaimCancelledEvent(task, CurrentUserContext.getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from cancelClaim()");
    }
    return task;
  }

  private Task completeTask(String taskId, boolean isForced)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    String userId = CurrentUserContext.getUserid();
    LOGGER.debug(
        "entry to completeTask(id = {}, userId = {}, isForced = {})", taskId, userId, isForced);
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) this.getTask(taskId);

      if (task.getState() == TaskState.COMPLETED) {
        return task;
      }

      // check pre-conditions for non-forced invocation
      if (!isForced) {
        if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
          throw new InvalidStateException(TASK_WITH_ID + taskId + " has to be claimed before.");
        } else if (!CurrentUserContext.getAccessIds().contains(task.getOwner())) {
          throw new InvalidOwnerException(
              "Owner of task "
                  + taskId
                  + " is "
                  + task.getOwner()
                  + ", but current User is "
                  + userId);
        }
      } else {
        // CLAIM-forced, if task was not already claimed before.
        if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
          task = (TaskImpl) this.forceClaim(taskId);
        }
      }
      Instant now = Instant.now();
      task.setCompleted(now);
      task.setModified(now);
      task.setState(TaskState.COMPLETED);
      task.setOwner(userId);
      taskMapper.update(task);
      LOGGER.debug("Task '{}' completed by user '{}'.", taskId, userId);
      if (HistoryEventProducer.isHistoryEnabled()) {
        historyEventProducer.createEvent(new CompletedEvent(task, CurrentUserContext.getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from completeTask()");
    }
    return task;
  }

  private void deleteTask(String taskId, boolean forceDelete)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    LOGGER.debug("entry to deleteTask(taskId = {} , forceDelete = {})", taskId, forceDelete);
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN);
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);

      if (!TaskState.COMPLETED.equals(task.getState()) && !forceDelete) {
        throw new InvalidStateException(
            "Cannot delete Task " + taskId + " because it is not completed.");
      }
      if (CallbackState.CALLBACK_PROCESSING_REQUIRED.equals(task.getCallbackState())) {
        throw new InvalidStateException(
            "Task " + taskId + " cannot be deleted because its callback is not yet processed");
      }

      taskMapper.delete(taskId);
      LOGGER.debug("Task {} deleted.", taskId);
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from deleteTask().");
    }
  }

  private void removeSingleTaskForTaskDeletionById(
      BulkOperationResults<String, TaskanaException> bulkLog,
      List<MinimalTaskSummary> taskSummaries,
      Iterator<String> taskIdIterator) {
    LOGGER.debug("entry to removeSingleTask()");
    String currentTaskId = taskIdIterator.next();
    if (currentTaskId == null || currentTaskId.equals("")) {
      bulkLog.addError(
          "", new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
      taskIdIterator.remove();
    } else {
      MinimalTaskSummary foundSummary =
          taskSummaries.stream()
              .filter(taskSummary -> currentTaskId.equals(taskSummary.getTaskId()))
              .findFirst()
              .orElse(null);
      if (foundSummary == null) {
        bulkLog.addError(
            currentTaskId,
            new TaskNotFoundException(
                currentTaskId, TASK_WITH_ID + currentTaskId + WAS_NOT_FOUND2));
        taskIdIterator.remove();
      } else if (!TaskState.COMPLETED.equals(foundSummary.getTaskState())) {
        bulkLog.addError(currentTaskId, new InvalidStateException(currentTaskId));
        taskIdIterator.remove();
      } else {
        if (CallbackState.CALLBACK_PROCESSING_REQUIRED.equals(foundSummary.getCallbackState())) {
          bulkLog.addError(
              currentTaskId,
              new InvalidStateException(
                  "Task " + currentTaskId + " cannot be deleted before callback is processed"));
          taskIdIterator.remove();
        }
      }
    }
    LOGGER.debug("exit from removeSingleTask()");
  }

  private void removeSingleTaskForCallbackStateByExternalId(
      BulkOperationResults<String, TaskanaException> bulkLog,
      List<MinimalTaskSummary> taskSummaries,
      Iterator<String> externalIdIterator,
      CallbackState desiredCallbackState) {
    LOGGER.debug("entry to removeSingleTask()");
    String currentExternalId = externalIdIterator.next();
    if (currentExternalId == null || currentExternalId.equals("")) {
      bulkLog.addError(
          "", new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
      externalIdIterator.remove();
    } else {
      MinimalTaskSummary foundSummary =
          taskSummaries.stream()
              .filter(taskSummary -> currentExternalId.equals(taskSummary.getExternalId()))
              .findFirst()
              .orElse(null);
      if (foundSummary == null) {
        bulkLog.addError(
            currentExternalId,
            new TaskNotFoundException(
                currentExternalId, TASK_WITH_ID + currentExternalId + WAS_NOT_FOUND2));
        externalIdIterator.remove();
      } else if (!desiredCallbackStateCanBeSetForFoundSummary(foundSummary, desiredCallbackState)) {
        bulkLog.addError(currentExternalId, new InvalidStateException(currentExternalId));
        externalIdIterator.remove();
      }
    }
    LOGGER.debug("exit from removeSingleTask()");
  }

  private boolean desiredCallbackStateCanBeSetForFoundSummary(
      MinimalTaskSummary foundSummary, CallbackState desiredCallbackState) {

    CallbackState currentTaskCallbackState = foundSummary.getCallbackState();
    TaskState currentTaskState = foundSummary.getTaskState();

    switch (desiredCallbackState) {
      case CALLBACK_PROCESSING_COMPLETED:
        return currentTaskState.equals(TaskState.COMPLETED);

      case CLAIMED:
        if (!currentTaskState.equals(TaskState.CLAIMED)) {
          return false;
        } else {
          return currentTaskCallbackState.equals(CallbackState.CALLBACK_PROCESSING_REQUIRED);
        }

      case CALLBACK_PROCESSING_REQUIRED:
        return !currentTaskCallbackState.equals(CallbackState.CALLBACK_PROCESSING_COMPLETED);

      default:
        return false;
    }
  }

  private void standardSettings(
      TaskImpl task, Classification classification, PrioDurationHolder prioDurationFromAttachments)
      throws InvalidArgumentException {
    LOGGER.debug("entry to standardSettings()");
    final Instant now = Instant.now();
    task.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK));
    if (task.getExternalId() == null) {
      task.setExternalId(IdGenerator.generateWithPrefix(ID_PREFIX_EXT_TASK_ID));
    }
    task.setState(TaskState.READY);
    task.setCreated(now);
    task.setModified(now);
    task.setRead(false);
    task.setTransferred(false);

    String creator = CurrentUserContext.getUserid();
    if (taskanaEngine.getEngine().getConfiguration().isSecurityEnabled() && creator == null) {
      throw new SystemException(
          "TaskanaSecurity is enabled, but the current UserId is NULL while creating a Task.");
    }
    task.setCreator(creator);

    // if no business process id is provided, a unique id is created.
    if (task.getBusinessProcessId() == null) {
      task.setBusinessProcessId(IdGenerator.generateWithPrefix(ID_PREFIX_BUSINESS_PROCESS));
    }

    // null in case of manual tasks
    if (classification == null) {
      if (task.getPlanned() == null) {
        task.setPlanned(now);
      }
    } else {
      // do some Classification specific stuff (servicelevel).
      // get duration in days from planned to due
      PrioDurationHolder finalPrioDuration =
          getNewPrioDuration(
              prioDurationFromAttachments,
              classification.getPriority(),
              classification.getServiceLevel());
      Duration finalDuration = finalPrioDuration.getLeft();
      if (finalDuration != null && !MAX_DURATION.equals(finalDuration)) {
        // if we have a due date we need to go x days backwards,
        // else we take the planned date (or now as fallback) and add x Days
        if (task.getDue() != null) {
          long days = converter.convertWorkingDaysToDays(task.getDue(), -finalDuration.toDays());
          // days < 0 -> so we ne need to add, not substract
          Instant planned = task.getDue().plus(Duration.ofDays(days));
          if (task.getPlanned() != null && !task.getPlanned().equals(planned)) {
            throw new InvalidArgumentException(
                "Cannot create a task with given planned "
                    + "and due date not matching the service level");
          }
          task.setPlanned(planned);
        } else {
          task.setPlanned(task.getPlanned() == null ? now : task.getPlanned());
          long days = converter.convertWorkingDaysToDays(task.getPlanned(), finalDuration.toDays());
          Instant due = task.getPlanned().plus(Duration.ofDays(days));
          task.setDue(due);
        }
      }
      task.setPriority(finalPrioDuration.getRight());
    }

    if (task.getName() == null && classification != null) {
      task.setName(classification.getName());
    }

    if (task.getDescription() == null && classification != null) {
      task.setDescription(classification.getDescription());
    }

    // insert Attachments if needed
    List<Attachment> attachments = task.getAttachments();
    if (attachments != null) {
      for (Attachment attachment : attachments) {
        AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
        attachmentImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
        attachmentImpl.setTaskId(task.getId());
        attachmentImpl.setCreated(now);
        attachmentImpl.setModified(now);
        attachmentMapper.insert(attachmentImpl);
      }
    }
    LOGGER.debug("exit from standardSettings()");
  }

  private void setCallbackStateOnTaskCreation(TaskImpl task) throws InvalidArgumentException {
    Map<String, String> callbackInfo = task.getCallbackInfo();
    if (callbackInfo != null && callbackInfo.containsKey(Task.CALLBACK_STATE)) {
      String value = callbackInfo.get(Task.CALLBACK_STATE);
      if (value != null && !value.isEmpty()) {
        try {
          CallbackState state = CallbackState.valueOf(value);
          task.setCallbackState(state);
        } catch (Exception e) {
          LOGGER.warn("Attempted to determine callback state from {} and caught {}", value, e);
          throw new InvalidArgumentException(
              "Attempted to set callback state for task " + task.getId(), e);
        }
      }
    }
  }

  private void checkIfTasksMatchCompleteCriteria(
      List<String> taskIds,
      List<TaskSummary> taskSummaries,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to checkIfTasksMatchCompleteCriteria(taskIds = {}, "
              + "taskSummaries = {}, bulkLog = {})",
          LoggerUtils.listToString(taskIds),
          LoggerUtils.listToString(taskSummaries),
          bulkLog);
    }

    Instant now = Instant.now();
    Iterator<String> taskIdIterator = taskIds.iterator();
    while (taskIdIterator.hasNext()) {
      String currentTaskId = taskIdIterator.next();
      TaskSummaryImpl taskSummary =
          (TaskSummaryImpl)
              taskSummaries.stream()
                  .filter(ts -> currentTaskId.equals(ts.getId()))
                  .findFirst()
                  .orElse(null);
      if (taskSummary == null) {
        bulkLog.addError(
            currentTaskId,
            new TaskNotFoundException(
                currentTaskId, "task with id " + currentTaskId + WAS_NOT_FOUND2));
        taskIdIterator.remove();
      } else if (taskSummary.getClaimed() == null || taskSummary.getState() != TaskState.CLAIMED) {
        bulkLog.addError(currentTaskId, new InvalidStateException(currentTaskId));
        taskIdIterator.remove();
      } else if (!CurrentUserContext.getAccessIds().contains(taskSummary.getOwner())) {
        bulkLog.addError(
            currentTaskId,
            new InvalidOwnerException(
                "TaskOwner is"
                    + taskSummary.getOwner()
                    + ", but current User is "
                    + CurrentUserContext.getUserid()));
        taskIdIterator.remove();
      } else {
        taskSummary.setCompleted(now);
        taskSummary.setModified(now);
        taskSummary.setState(TaskState.COMPLETED);
      }
    }
    LOGGER.debug("exit from checkIfTasksMatchCompleteCriteria()");
  }

  private void updateTasksToBeCompleted(List<String> taskIds, List<TaskSummary> taskSummaries) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to updateTasksToBeCompleted(taskIds = {}, taskSummaries = {})",
          LoggerUtils.listToString(taskIds),
          LoggerUtils.listToString(taskSummaries));
    }

    if (!taskIds.isEmpty() && !taskSummaries.isEmpty()) {
      taskMapper.updateCompleted(taskIds, (TaskSummaryImpl) taskSummaries.get(0));
      if (HistoryEventProducer.isHistoryEnabled()) {
        createTasksCompletedEvents(taskSummaries);
      }
    }
    LOGGER.debug("exit from updateTasksToBeCompleted()");
  }

  private void addClassificationSummariesToTaskSummaries(
      List<TaskSummaryImpl> tasks, List<ClassificationSummary> classifications) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to addClassificationSummariesToTaskSummaries(tasks = {}, classifications = {})",
          LoggerUtils.listToString(tasks),
          LoggerUtils.listToString(classifications));
    }

    if (tasks == null || tasks.isEmpty()) {
      LOGGER.debug("exit from addClassificationSummariesToTaskSummaries()");
      return;
    }
    // assign query results to appropriate tasks.
    for (TaskSummaryImpl task : tasks) {
      String classificationId = task.getClassificationSummary().getId();
      ClassificationSummary classificationSummary =
          classifications.stream()
              .filter(c -> c.getId().equals(classificationId))
              .findFirst()
              .orElse(null);
      if (classificationSummary == null) {
        throw new SystemException(
            "Did not find a Classification for task (Id="
                + task.getId()
                + ",classification="
                + task.getClassificationSummary().getId()
                + ")");
      }
      // set the classification on the task object
      task.setClassificationSummary(classificationSummary);
    }
    LOGGER.debug("exit from addClassificationSummariesToTaskSummaries()");
  }

  private List<ClassificationSummary> findClassificationsForTasksAndAttachments(
      List<TaskSummaryImpl> taskSummaries, List<AttachmentSummaryImpl> attachmentSummaries) {
    LOGGER.debug("entry to findClassificationsForTasksAndAttachments()");
    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return new ArrayList<>();
    }

    Set<String> classificationIdSet =
        taskSummaries.stream()
            .map(t -> t.getClassificationSummary().getId())
            .collect(Collectors.toSet());

    if (attachmentSummaries != null && !attachmentSummaries.isEmpty()) {
      for (AttachmentSummaryImpl att : attachmentSummaries) {
        classificationIdSet.add(att.getClassificationSummary().getId());
      }
    }
    LOGGER.debug("exit from findClassificationsForTasksAndAttachments()");
    return queryClassificationsForTasksAndAttachments(classificationIdSet);
  }

  private List<ClassificationSummary> findClassificationForTaskImplAndAttachments(
      TaskImpl task, List<AttachmentImpl> attachmentImpls) {
    LOGGER.debug("entry to transferBulk()");
    Set<String> classificationIdSet =
        new HashSet<>(Collections.singletonList(task.getClassificationSummary().getId()));
    if (attachmentImpls != null && !attachmentImpls.isEmpty()) {
      for (AttachmentImpl att : attachmentImpls) {
        classificationIdSet.add(att.getClassificationSummary().getId());
      }
    }
    LOGGER.debug("exit from findClassificationForTaskImplAndAttachments()");
    return queryClassificationsForTasksAndAttachments(classificationIdSet);
  }

  private List<ClassificationSummary> queryClassificationsForTasksAndAttachments(
      Set<String> classificationIdSet) {

    String[] classificationIdArray = classificationIdSet.toArray(new String[0]);

    LOGGER.debug(
        "getClassificationsForTasksAndAttachments() about to query classifications and exit");
    // perform classification query
    return this.classificationService
        .createClassificationQuery()
        .idIn(classificationIdArray)
        .list();
  }

  private void addWorkbasketSummariesToTaskSummaries(List<TaskSummaryImpl> taskSummaries) {
    LOGGER.debug("entry to addWorkbasketSummariesToTaskSummaries()");
    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return;
    }
    // calculate parameters for workbasket query: workbasket keys
    String[] workbasketIdArray =
        taskSummaries.stream()
            .map(t -> t.getWorkbasketSummary().getId())
            .distinct()
            .toArray(String[]::new);
    LOGGER.debug("addWorkbasketSummariesToTaskSummaries() about to query workbaskets");
    WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
    query.setUsedToAugmentTasks(true);

    List<WorkbasketSummary> workbaskets = query.idIn(workbasketIdArray).list();
    Iterator<TaskSummaryImpl> taskIterator = taskSummaries.iterator();
    while (taskIterator.hasNext()) {
      TaskSummaryImpl task = taskIterator.next();
      String workbasketId = task.getWorkbasketSummaryImpl().getId();

      WorkbasketSummary workbasketSummary =
          workbaskets.stream()
              .filter(x -> workbasketId != null && workbasketId.equals(x.getId()))
              .findFirst()
              .orElse(null);
      if (workbasketSummary == null) {
        LOGGER.warn("Could not find a Workbasket for task {}.", task.getId());
        taskIterator.remove();
        continue;
      }

      task.setWorkbasketSummary(workbasketSummary);
    }
    LOGGER.debug("exit from addWorkbasketSummariesToTaskSummaries()");
  }

  private void addAttachmentSummariesToTaskSummaries(
      List<TaskSummaryImpl> taskSummaries,
      List<AttachmentSummaryImpl> attachmentSummaries,
      List<ClassificationSummary> classifications) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to addAttachmentSummariesToTaskSummaries(taskSummaries = {}, "
              + "attachmentSummaries = {}, classifications = {})",
          LoggerUtils.listToString(taskSummaries),
          LoggerUtils.listToString(attachmentSummaries),
          LoggerUtils.listToString(classifications));
    }

    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return;
    }

    // augment attachment summaries by classification summaries
    // Note:
    // the mapper sets for each Attachment summary the property classificationSummary.key from the
    // CLASSIFICATION_KEY property in the DB
    addClassificationSummariesToAttachmentSummaries(
        attachmentSummaries, taskSummaries, classifications);
    // assign attachment summaries to task summaries
    for (TaskSummaryImpl task : taskSummaries) {
      for (AttachmentSummaryImpl attachment : attachmentSummaries) {
        if (attachment.getTaskId() != null && attachment.getTaskId().equals(task.getId())) {
          task.addAttachmentSummary(attachment);
        }
      }
    }

    LOGGER.debug("exit from addAttachmentSummariesToTaskSummaries()");
  }

  private void addClassificationSummariesToAttachmentSummaries(
      List<AttachmentSummaryImpl> attachmentSummaries,
      List<TaskSummaryImpl> taskSummaries,
      List<ClassificationSummary> classifications) {
    LOGGER.debug("entry to addClassificationSummariesToAttachmentSummaries()");
    // prereq: in each attachmentSummary, the classificationSummary.key property is set.
    if (attachmentSummaries == null
        || attachmentSummaries.isEmpty()
        || taskSummaries == null
        || taskSummaries.isEmpty()) {
      LOGGER.debug("exit from addClassificationSummariesToAttachmentSummaries()");
      return;
    }
    // iterate over all attachment summaries an add the appropriate classification summary to each
    for (AttachmentSummaryImpl att : attachmentSummaries) {
      String classificationId = att.getClassificationSummary().getId();
      ClassificationSummary classificationSummary =
          classifications.stream()
              .filter(x -> classificationId != null && classificationId.equals(x.getId()))
              .findFirst()
              .orElse(null);
      if (classificationSummary == null) {
        throw new SystemException("Could not find a Classification for attachment " + att);
      }
      att.setClassificationSummary(classificationSummary);
    }
    LOGGER.debug("exit from addClassificationSummariesToAttachmentSummaries()");
  }

  private List<Attachment> addClassificationSummariesToAttachments(
      List<AttachmentImpl> attachmentImpls, List<ClassificationSummary> classifications) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to addClassificationSummariesToAttachments(targetWbId = {}, taskIds = {})",
          LoggerUtils.listToString(attachmentImpls),
          LoggerUtils.listToString(classifications));
    }

    if (attachmentImpls == null || attachmentImpls.isEmpty()) {
      LOGGER.debug("exit from addClassificationSummariesToAttachments()");
      return new ArrayList<>();
    }

    List<Attachment> result = new ArrayList<>();
    for (AttachmentImpl att : attachmentImpls) {
      // find the associated task to use the correct domain
      ClassificationSummary classificationSummary =
          classifications.stream()
              .filter(c -> c != null && c.getId().equals(att.getClassificationSummary().getId()))
              .findFirst()
              .orElse(null);

      if (classificationSummary == null) {
        throw new SystemException("Could not find a Classification for attachment " + att);
      }
      att.setClassificationSummary(classificationSummary);
      result.add(att);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("exit from addClassificationSummariesToAttachments(), returning {}", result);
    }

    return result;
  }

  private TaskImpl initUpdatedTask(
      Map<String, String> customFieldsToUpdate, CustomPropertySelector fieldSelector)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to initUpdatedTask(customFieldsToUpdate = {}, fieldSelector = {})",
          LoggerUtils.mapToString(customFieldsToUpdate),
          fieldSelector);
    }

    TaskImpl newTask = new TaskImpl();
    newTask.setModified(Instant.now());

    for (Map.Entry<String, String> entry : customFieldsToUpdate.entrySet()) {
      String key = entry.getKey();
      fieldSelector.setCustomProperty(key, true);
      newTask.setCustomAttribute(key, entry.getValue());
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("exit from initUpdatedTask(), returning {}", newTask);
    }

    return newTask;
  }

  private void validateCustomFields(Map<String, String> customFieldsToUpdate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to validateCustomFields(customFieldsToUpdate = {})",
          LoggerUtils.mapToString(customFieldsToUpdate));
    }

    if (customFieldsToUpdate == null || customFieldsToUpdate.isEmpty()) {
      throw new InvalidArgumentException(
          "The customFieldsToUpdate argument to updateTasks must not be empty.");
    }

    for (Map.Entry<String, String> entry : customFieldsToUpdate.entrySet()) {
      String key = entry.getKey();
      if (!ALLOWED_KEYS.contains(key)) {
        throw new InvalidArgumentException(
            "The customFieldsToUpdate argument to updateTasks contains invalid key " + key);
      }
    }
    LOGGER.debug("exit from validateCustomFields()");
  }

  private List<TaskSummary> getTasksToChange(List<String> taskIds) {
    return createTaskQuery().idIn(taskIds.toArray(new String[0])).list();
  }

  private List<TaskSummary> getTasksToChange(ObjectReference selectionCriteria) {
    return createTaskQuery()
        .primaryObjectReferenceCompanyIn(selectionCriteria.getCompany())
        .primaryObjectReferenceSystemIn(selectionCriteria.getSystem())
        .primaryObjectReferenceSystemInstanceIn(selectionCriteria.getSystemInstance())
        .primaryObjectReferenceTypeIn(selectionCriteria.getType())
        .primaryObjectReferenceValueIn(selectionCriteria.getValue())
        .list();
  }

  private void validateObjectReference(ObjectReference objRef, String objRefType, String objName)
      throws InvalidArgumentException {
    LOGGER.debug("entry to validateObjectReference()");
    // check that all values in the ObjectReference are set correctly
    if (objRef == null) {
      throw new InvalidArgumentException(objRefType + " of " + objName + " must not be null");
    } else if (objRef.getCompany() == null || objRef.getCompany().length() == 0) {
      throw new InvalidArgumentException(
          "Company of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
    } else if (objRef.getSystem() == null || objRef.getSystem().length() == 0) {
      throw new InvalidArgumentException(
          "System of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
    } else if (objRef.getSystemInstance() == null || objRef.getSystemInstance().length() == 0) {
      throw new InvalidArgumentException(
          "SystemInstance of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
    } else if (objRef.getType() == null || objRef.getType().length() == 0) {
      throw new InvalidArgumentException(
          "Type of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
    } else if (objRef.getValue() == null || objRef.getValue().length() == 0) {
      throw new InvalidArgumentException(
          "Value of" + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
    }
    LOGGER.debug("exit from validateObjectReference()");
  }

  private PrioDurationHolder handleAttachments(TaskImpl task) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to handleAttachments(task = {})", task);
    }

    List<Attachment> attachments = task.getAttachments();
    if (attachments == null || attachments.isEmpty()) {
      return new PrioDurationHolder(null, Integer.MIN_VALUE);
    }

    PrioDurationHolder actualPrioDuration = new PrioDurationHolder(MAX_DURATION, Integer.MIN_VALUE);

    Iterator<Attachment> i = attachments.iterator();
    while (i.hasNext()) {
      Attachment attachment = i.next();
      if (attachment == null) {
        i.remove();
      } else {
        actualPrioDuration = handleNonNullAttachment(actualPrioDuration, attachment);
      }
    }
    if (MAX_DURATION.equals(actualPrioDuration.getLeft())) {
      actualPrioDuration = new PrioDurationHolder(null, actualPrioDuration.getRight());
    }

    LOGGER.debug("exit from handleAttachments(), returning {}", actualPrioDuration);
    return actualPrioDuration;
  }

  private PrioDurationHolder handleNonNullAttachment(
      PrioDurationHolder actualPrioDuration, Attachment attachment)
      throws InvalidArgumentException {
    ObjectReference objRef = attachment.getObjectReference();
    validateObjectReference(objRef, "ObjectReference", "Attachment");
    if (attachment.getClassificationSummary() == null) {
      throw new InvalidArgumentException(
          "Classification of attachment " + attachment + " must not be null");
    } else {
      ClassificationSummary classificationSummary = attachment.getClassificationSummary();
      if (classificationSummary != null) {
        actualPrioDuration =
            getNewPrioDuration(
                actualPrioDuration,
                classificationSummary.getPriority(),
                classificationSummary.getServiceLevel());
      }
    }
    return actualPrioDuration;
  }

  private void standardUpdateActions(
      TaskImpl oldTaskImpl, TaskImpl newTaskImpl, PrioDurationHolder prioDurationFromAttachments)
      throws InvalidArgumentException, ConcurrencyException, ClassificationNotFoundException,
          InvalidStateException {
    validateObjectReference(newTaskImpl.getPrimaryObjRef(), "primary ObjectReference", "Task");
    // TODO: not safe to rely only on different timestamps.
    // With fast execution below 1ms there will be no concurrencyException
    if (oldTaskImpl.getModified() != null
            && !oldTaskImpl.getModified().equals(newTaskImpl.getModified())
        || oldTaskImpl.getClaimed() != null
            && !oldTaskImpl.getClaimed().equals(newTaskImpl.getClaimed())
        || oldTaskImpl.getState() != null
            && !oldTaskImpl.getState().equals(newTaskImpl.getState())) {
      throw new ConcurrencyException("The task has already been updated by another user");
    }

    if (oldTaskImpl.getExternalId() == null
        || !(oldTaskImpl.getExternalId().equals(newTaskImpl.getExternalId()))) {
      throw new InvalidArgumentException(
          "A task's external Id cannot be changed via update of the task");
    }

    String newWorkbasketKey = newTaskImpl.getWorkbasketKey();
    if (newWorkbasketKey != null && !newWorkbasketKey.equals(oldTaskImpl.getWorkbasketKey())) {
      throw new InvalidArgumentException(
          "A task's Workbasket cannot be changed via update of the task");
    }

    if (newTaskImpl.getPlanned() == null) {
      newTaskImpl.setPlanned(oldTaskImpl.getPlanned());
    }

    // if no business process id is provided, use the id of the old task.
    if (newTaskImpl.getBusinessProcessId() == null) {
      newTaskImpl.setBusinessProcessId(oldTaskImpl.getBusinessProcessId());
    }

    // owner can only be changed if task is in state ready
    boolean isOwnerChanged = !Objects.equals(newTaskImpl.getOwner(), oldTaskImpl.getOwner());
    if (isOwnerChanged && oldTaskImpl.getState() != TaskState.READY) {
      throw new InvalidStateException(
          String.format(TASK_WITH_ID_IS_NOT_READY, oldTaskImpl.getId()));
    }

    updateClassificationRelatedProperties(oldTaskImpl, newTaskImpl, prioDurationFromAttachments);

    newTaskImpl.setModified(Instant.now());
  }

  private void updateClassificationRelatedProperties(
      TaskImpl oldTaskImpl, TaskImpl newTaskImpl, PrioDurationHolder prioDurationFromAttachments)
      throws ClassificationNotFoundException {
    LOGGER.debug("entry to updateClassificationRelatedProperties()");
    // insert Classification specifications if Classification is given.
    ClassificationSummary oldClassificationSummary = oldTaskImpl.getClassificationSummary();
    ClassificationSummary newClassificationSummary = newTaskImpl.getClassificationSummary();
    if (newClassificationSummary == null) {
      newClassificationSummary = oldClassificationSummary;
    }

    if (newClassificationSummary
        == null) { // newClassification is null -> take prio and duration from attachments
      updateTaskPrioDurationFromAttachments(newTaskImpl, prioDurationFromAttachments);
    } else {
      updateTaskPrioDurationFromClassification(
          newTaskImpl,
          prioDurationFromAttachments,
          oldClassificationSummary,
          newClassificationSummary);
    }

    LOGGER.debug("exit from updateClassificationRelatedProperties()");
  }

  private void updateTaskPrioDurationFromClassification(
      TaskImpl newTaskImpl,
      PrioDurationHolder prioDurationFromAttachments,
      ClassificationSummary oldClassificationSummary,
      ClassificationSummary newClassificationSummary)
      throws ClassificationNotFoundException {
    LOGGER.debug("entry to updateTaskPrioDurationFromClassification()");
    Classification newClassification = null;
    if (!oldClassificationSummary.getKey().equals(newClassificationSummary.getKey())) {
      newClassification =
          this.classificationService.getClassification(
              newClassificationSummary.getKey(), newTaskImpl.getWorkbasketSummary().getDomain());
      newClassificationSummary = newClassification.asSummary();
      newTaskImpl.setClassificationSummary(newClassificationSummary);
    }

    Duration minDuration = calculateDuration(prioDurationFromAttachments, newClassificationSummary);
    if (minDuration != null) {

      long days =
          converter.convertWorkingDaysToDays(newTaskImpl.getPlanned(), minDuration.toDays());
      Instant due = newTaskImpl.getPlanned().plus(Duration.ofDays(days));

      newTaskImpl.setDue(due);
    }

    if (newTaskImpl.getName() == null) {
      newTaskImpl.setName(newClassificationSummary.getName());
    }

    if (newTaskImpl.getDescription() == null && newClassification != null) {
      newTaskImpl.setDescription(newClassification.getDescription());
    }

    int newPriority =
        Math.max(newClassificationSummary.getPriority(), prioDurationFromAttachments.getRight());
    newTaskImpl.setPriority(newPriority);
    LOGGER.debug("exit from updateTaskPrioDurationFromClassification()");
  }

  private PrioDurationHolder handleAttachmentsOnTaskUpdate(
      TaskImpl oldTaskImpl, TaskImpl newTaskImpl) throws AttachmentPersistenceException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to handleAttachmentsOnTaskUpdate(oldTaskImpl = {}, newTaskImpl = {})",
          oldTaskImpl,
          newTaskImpl);
    }

    PrioDurationHolder prioDuration = new PrioDurationHolder(MAX_DURATION, Integer.MIN_VALUE);

    // Iterator for removing invalid current values directly. OldAttachments can be ignored.
    Iterator<Attachment> i = newTaskImpl.getAttachments().iterator();
    while (i.hasNext()) {
      Attachment attachment = i.next();
      if (attachment != null) {
        prioDuration =
            handlePrioDurationOfOneAttachmentOnTaskUpdate(
                oldTaskImpl, newTaskImpl, prioDuration, attachment);
      } else {
        i.remove();
      }
    }

    // DELETE, when an Attachment was only represented before
    deleteAttachmentOnTaskUpdate(oldTaskImpl, newTaskImpl);
    if (MAX_DURATION.equals(prioDuration.getLeft())) {
      prioDuration = new PrioDurationHolder(null, prioDuration.getRight());
    }

    LOGGER.debug("exit from handleAttachmentsOnTaskUpdate()");
    return prioDuration;
  }

  private void deleteAttachmentOnTaskUpdate(TaskImpl oldTaskImpl, TaskImpl newTaskImpl) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteAttachmentOnTaskUpdate(oldTaskImpl = {}, newTaskImpl = {})",
          oldTaskImpl,
          newTaskImpl);
    }

    for (Attachment oldAttachment : oldTaskImpl.getAttachments()) {
      if (oldAttachment != null) {
        boolean isRepresented = false;
        for (Attachment newAttachment : newTaskImpl.getAttachments()) {
          if (newAttachment != null && oldAttachment.getId().equals(newAttachment.getId())) {
            isRepresented = true;
            break;
          }
        }
        if (!isRepresented) {
          attachmentMapper.deleteAttachment(oldAttachment.getId());
          LOGGER.debug(
              "TaskService.updateTask() for TaskId={} DELETED an Attachment={}.",
              newTaskImpl.getId(),
              oldAttachment);
        }
      }
    }
    LOGGER.debug("exit from deleteAttachmentOnTaskUpdate()");
  }

  private PrioDurationHolder handlePrioDurationOfOneAttachmentOnTaskUpdate(
      TaskImpl oldTaskImpl,
      TaskImpl newTaskImpl,
      PrioDurationHolder prioDuration,
      Attachment attachment)
      throws AttachmentPersistenceException {
    LOGGER.debug("entry to handlePrioDurationOfOneAttachmentOnTaskUpdate()");
    boolean wasAlreadyPresent = false;
    if (attachment.getId() != null) {
      for (Attachment oldAttachment : oldTaskImpl.getAttachments()) {
        if (oldAttachment != null && attachment.getId().equals(oldAttachment.getId())) {
          wasAlreadyPresent = true;
          if (!attachment.equals(oldAttachment)) {
            prioDuration =
                handlePrioDurationOfOneNewAttachmentOnTaskUpdate(
                    newTaskImpl, prioDuration, attachment);
            break;
          }
        }
      }
    }

    // ADD, when ID not set or not found in elements
    if (!wasAlreadyPresent) {
      prioDuration = handleNewAttachmentOnTaskUpdate(newTaskImpl, prioDuration, attachment);
    }

    LOGGER.debug(
        "exit from handlePrioDurationOfOneAttachmentOnTaskUpdate(), returning {}", prioDuration);
    return prioDuration;
  }

  private PrioDurationHolder handlePrioDurationOfOneNewAttachmentOnTaskUpdate(
      TaskImpl newTaskImpl, PrioDurationHolder prioDuration, Attachment attachment) {
    LOGGER.debug("entry to handlePrioDurationOfOneNewAttachmentOnTaskUpdate()");
    AttachmentImpl temp = (AttachmentImpl) attachment;

    ClassificationSummary classification = attachment.getClassificationSummary();
    if (classification != null) {
      prioDuration =
          getNewPrioDuration(
              prioDuration, classification.getPriority(), classification.getServiceLevel());
    }

    temp.setModified(Instant.now());
    attachmentMapper.update(temp);
    LOGGER.debug(
        "TaskService.updateTask() for TaskId={} UPDATED an Attachment={}.",
        newTaskImpl.getId(),
        attachment);
    LOGGER.debug(
        "exit from handlePrioDurationOfOneNewAttachmentOnTaskUpdate(), returning {}", prioDuration);
    return prioDuration;
  }

  private PrioDurationHolder handleNewAttachmentOnTaskUpdate(
      TaskImpl newTaskImpl, PrioDurationHolder prioDuration, Attachment attachment)
      throws AttachmentPersistenceException {
    LOGGER.debug("entry to handleNewAttachmentOnTaskUpdate()");
    AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
    initAttachment(attachmentImpl, newTaskImpl);
    ClassificationSummary classification = attachment.getClassificationSummary();
    if (classification != null) {
      prioDuration =
          getNewPrioDuration(
              prioDuration, classification.getPriority(), classification.getServiceLevel());
    }

    try {
      attachmentMapper.insert(attachmentImpl);
      LOGGER.debug(
          "TaskService.updateTask() for TaskId={} INSERTED an Attachment={}.",
          newTaskImpl.getId(),
          attachmentImpl);
    } catch (PersistenceException e) {
      throw new AttachmentPersistenceException(
          "Cannot insert the Attachement "
              + attachmentImpl.getId()
              + " for Task "
              + newTaskImpl.getId()
              + " because it already exists.",
          e.getCause());
    }
    LOGGER.debug("exit from handleNewAttachmentOnTaskUpdate(), returning {}", prioDuration);
    return prioDuration;
  }

  private PrioDurationHolder handleAttachmentsOnClassificationUpdate(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to handleAttachmentsOnClassificationUpdate(task = {})", task);
    }

    PrioDurationHolder prioDuration = new PrioDurationHolder(MAX_DURATION, Integer.MIN_VALUE);

    // Iterator for removing invalid current values directly. OldAttachments can be ignored.
    for (Attachment attachment : task.getAttachments()) {
      if (attachment != null) {
        ClassificationSummary classification = attachment.getClassificationSummary();
        if (classification != null) {
          prioDuration =
              getNewPrioDuration(
                  prioDuration, classification.getPriority(), classification.getServiceLevel());
        }
      }
    }
    if (MAX_DURATION.equals(prioDuration.getLeft())) {
      prioDuration = new PrioDurationHolder(null, prioDuration.getRight());
    }

    LOGGER.debug("exit from handleAttachmentsOnClassificationUpdate(), returning {}", prioDuration);
    return prioDuration;
  }

  private PrioDurationHolder getNewPrioDuration(
      PrioDurationHolder prioDurationHolder,
      int prioFromClassification,
      String serviceLevelFromClassification) {
    LOGGER.debug(
        "entry to getNewPrioDuration(prioDurationHolder = {}, prioFromClassification = {}, "
            + "serviceLevelFromClassification = {})",
        prioDurationHolder,
        prioFromClassification,
        serviceLevelFromClassification);
    Duration minDuration = prioDurationHolder.getLeft();
    int maxPrio = prioDurationHolder.getRight();

    if (serviceLevelFromClassification != null) {
      Duration currentDuration = Duration.parse(serviceLevelFromClassification);
      if (prioDurationHolder.getLeft() != null) {
        if (prioDurationHolder.getLeft().compareTo(currentDuration) > 0) {
          minDuration = currentDuration;
        }
      } else {
        minDuration = currentDuration;
      }
    }
    if (prioFromClassification > maxPrio) {
      maxPrio = prioFromClassification;
    }

    PrioDurationHolder pair = new PrioDurationHolder(minDuration, maxPrio);
    LOGGER.debug("exit from getNewPrioDuration(), returning {}", pair);
    return pair;
  }

  private void initAttachment(AttachmentImpl attachment, Task newTask) {
    LOGGER.debug("entry to initAttachment()");
    if (attachment.getId() == null) {
      attachment.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
    }
    if (attachment.getCreated() == null) {
      attachment.setCreated(Instant.now());
    }
    if (attachment.getModified() == null) {
      attachment.setModified(attachment.getCreated());
    }
    if (attachment.getTaskId() == null) {
      attachment.setTaskId(newTask.getId());
    }
    LOGGER.debug("exit from initAttachment()");
  }

  private void updatePrioDueDateOnClassificationUpdate(
      TaskImpl task, PrioDurationHolder prioDurationFromAttachments) {
    LOGGER.debug("entry to updatePrioDueDateOnClassificationUpdate()");
    ClassificationSummary classificationSummary = task.getClassificationSummary();

    if (classificationSummary
        == null) { // classification is null -> take prio and duration from attachments
      updateTaskPrioDurationFromAttachments(task, prioDurationFromAttachments);
    } else {
      updateTaskPrioDurationFromClassificationAndAttachments(
          task, prioDurationFromAttachments, classificationSummary);
    }

    LOGGER.debug("exit from updatePrioDueDateOnClassificationUpdate()");
  }

  private void updateTaskPrioDurationFromClassificationAndAttachments(
      TaskImpl task,
      PrioDurationHolder prioDurationFromAttachments,
      ClassificationSummary classificationSummary) {
    LOGGER.debug("entry to updateTaskPrioDurationFromClassificationAndAttachments()");

    Duration minDuration = calculateDuration(prioDurationFromAttachments, classificationSummary);
    if (minDuration != null) {
      long days = converter.convertWorkingDaysToDays(task.getPlanned(), minDuration.toDays());
      Instant due = task.getPlanned().plus(Duration.ofDays(days));

      task.setDue(due);
    }

    int newPriority =
        Math.max(classificationSummary.getPriority(), prioDurationFromAttachments.getRight());
    task.setPriority(newPriority);
    LOGGER.debug("exit from updateTaskPrioDurationFromClassificationAndAttachments()");
  }

  private void updateTaskPrioDurationFromAttachments(
      TaskImpl task, PrioDurationHolder prioDurationFromAttachments) {
    LOGGER.debug("entry to updateTaskPrioDurationFromAttachments()");
    if (prioDurationFromAttachments.getLeft() != null) {
      long days =
          converter.convertWorkingDaysToDays(
              task.getPlanned(), prioDurationFromAttachments.getLeft().toDays());
      Instant due = task.getPlanned().plus(Duration.ofDays(days));
      task.setDue(due);
    }
    if (prioDurationFromAttachments.getRight() > Integer.MIN_VALUE) {
      task.setPriority(prioDurationFromAttachments.getRight());
    }
    LOGGER.debug("exit from updateTaskPrioDurationFromAttachments()");
  }

  private List<Attachment> augmentAttachmentsByClassification(
      List<AttachmentImpl> attachmentImpls, BulkOperationResults<String, Exception> bulkLog) {
    LOGGER.debug("entry to augmentAttachmentsByClassification()");
    List<Attachment> result = new ArrayList<>();
    if (attachmentImpls == null || attachmentImpls.isEmpty()) {
      return result;
    }
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .idIn(
                attachmentImpls.stream()
                    .map(t -> t.getClassificationSummary().getId())
                    .distinct()
                    .toArray(String[]::new))
            .list();
    for (AttachmentImpl att : attachmentImpls) {
      ClassificationSummary classificationSummary =
          classifications.stream()
              .filter(cl -> cl.getId().equals(att.getClassificationSummary().getId()))
              .findFirst()
              .orElse(null);
      if (classificationSummary == null) {
        String id = att.getClassificationSummary().getId();
        bulkLog.addError(
            att.getClassificationSummary().getId(),
            new ClassificationNotFoundException(
                id,
                String.format(
                    "When processing task updates due to change "
                        + "of classification, the classification with id %s%s",
                    id, WAS_NOT_FOUND2)));
      } else {
        att.setClassificationSummary(classificationSummary);
        result.add(att);
      }
    }

    LOGGER.debug("exit from augmentAttachmentsByClassification()");
    return result;
  }

  private void createTasksCompletedEvents(List<TaskSummary> taskSummaries) {
    taskSummaries.forEach(
        task ->
            historyEventProducer.createEvent(
                new CompletedEvent(task, CurrentUserContext.getUserid())));
  }

  private static class PrioDurationHolder extends Pair<Duration, Integer> {

    public PrioDurationHolder(Duration left, Integer right) {
      super(left, right);
    }
  }
}
