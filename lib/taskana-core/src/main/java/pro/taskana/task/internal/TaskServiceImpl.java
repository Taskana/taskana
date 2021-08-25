package pro.taskana.task.internal;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.CheckedConsumer;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.ObjectAttributeChangeDetector;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.spi.history.api.events.task.TaskCancelledEvent;
import pro.taskana.spi.history.api.events.task.TaskClaimCancelledEvent;
import pro.taskana.spi.history.api.events.task.TaskClaimedEvent;
import pro.taskana.spi.history.api.events.task.TaskCompletedEvent;
import pro.taskana.spi.history.api.events.task.TaskCreatedEvent;
import pro.taskana.spi.history.api.events.task.TaskTerminatedEvent;
import pro.taskana.spi.history.api.events.task.TaskUpdatedEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.spi.task.internal.CreateTaskPreprocessorManager;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidCallbackStateException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.ServiceLevelHandler.BulkLog;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** This is the implementation of TaskService. */
@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public class TaskServiceImpl implements TaskService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

  private final InternalTaskanaEngine taskanaEngine;
  private final WorkbasketService workbasketService;
  private final ClassificationService classificationService;
  private final TaskMapper taskMapper;
  private final TaskTransferrer taskTransferrer;
  private final TaskCommentServiceImpl taskCommentService;
  private final ServiceLevelHandler serviceLevelHandler;
  private final AttachmentHandler attachmentHandler;
  private final AttachmentMapper attachmentMapper;
  private final HistoryEventManager historyEventManager;
  private final CreateTaskPreprocessorManager createTaskPreprocessorManager;
  private final PriorityServiceManager priorityServiceManager;

  public TaskServiceImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      TaskCommentMapper taskCommentMapper,
      AttachmentMapper attachmentMapper) {
    this.taskanaEngine = taskanaEngine;
    this.taskMapper = taskMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
    this.attachmentMapper = attachmentMapper;
    this.classificationService = taskanaEngine.getEngine().getClassificationService();
    this.historyEventManager = taskanaEngine.getHistoryEventManager();
    this.createTaskPreprocessorManager = taskanaEngine.getCreateTaskPreprocessorManager();
    this.priorityServiceManager = taskanaEngine.getPriorityServiceManager();
    this.taskTransferrer = new TaskTransferrer(taskanaEngine, taskMapper, this);
    this.taskCommentService = new TaskCommentServiceImpl(taskanaEngine, taskCommentMapper, this);
    this.serviceLevelHandler =
        new ServiceLevelHandler(taskanaEngine, taskMapper, attachmentMapper, this);
    this.attachmentHandler = new AttachmentHandler(attachmentMapper, classificationService);
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
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException {

    if (CreateTaskPreprocessorManager.isCreateTaskPreprocessorEnabled()) {
      taskToCreate = createTaskPreprocessorManager.processTaskBeforeCreation(taskToCreate);
    }

    TaskImpl task = (TaskImpl) taskToCreate;

    try {
      taskanaEngine.openConnection();

      if (task.getId() != null && !task.getId().isEmpty()) {
        throw new InvalidArgumentException("taskId must be empty when creating a task");
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Task {} cannot be found, so it can be created.", task.getId());
      }
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
        throw new WorkbasketNotFoundException(workbasket.getId());
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
      ObjectReference.validate(task.getPrimaryObjRef(), "primary ObjectReference", "Task");
      standardSettingsOnTaskCreation(task, classification);
      setCallbackStateOnTaskCreation(task);

      if (PriorityServiceManager.isPriorityServiceEnabled()) {
        Optional<Integer> newPriority = priorityServiceManager.calculatePriorityOfTask(task);
        if (newPriority.isPresent()) {
          task.setPriority(newPriority.get());
        }
      }

      try {
        this.taskMapper.insert(task);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Method createTask() created Task '{}'.", task.getId());
        }
        if (HistoryEventManager.isHistoryEnabled()) {

          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(newTask(), task);
          historyEventManager.createEvent(
              new TaskCreatedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                  task,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
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
            && (msg.contains("violation") || msg.contains("violates") || msg.contains("verletzt"))
            && msg.contains("external_id")) {
          throw new TaskAlreadyExistException(task.getExternalId());
        } else {
          throw e;
        }
      }
      return task;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Task getTask(String id) throws NotAuthorizedException, TaskNotFoundException {
    try {
      taskanaEngine.openConnection();

      TaskImpl resultTask = taskMapper.findById(id);
      if (resultTask != null) {
        WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
        query.setUsedToAugmentTasks(true);
        String workbasketId = resultTask.getWorkbasketSummary().getId();
        List<WorkbasketSummary> workbaskets = query.idIn(workbasketId).list();
        if (workbaskets.isEmpty()) {
          throw new MismatchedWorkbasketPermissionException(
              taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
              workbasketId,
              WorkbasketPermission.READ);
        } else {
          resultTask.setWorkbasketSummary(workbaskets.get(0));
        }

        List<AttachmentImpl> attachmentImpls =
            attachmentMapper.findAttachmentsByTaskId(resultTask.getId());
        if (attachmentImpls == null) {
          attachmentImpls = new ArrayList<>();
        }

        Map<String, ClassificationSummary> classificationSummariesById =
            findClassificationForTaskImplAndAttachments(resultTask, attachmentImpls);
        addClassificationSummariesToAttachments(attachmentImpls, classificationSummariesById);
        resultTask.setAttachments(new ArrayList<>(attachmentImpls));

        String classificationId = resultTask.getClassificationSummary().getId();
        ClassificationSummary classification = classificationSummariesById.get(classificationId);
        if (classification == null) {
          throw new SystemException(
              "Could not find a Classification for task " + resultTask.getId());
        }

        resultTask.setClassificationSummary(classification);
        return resultTask;
      } else {
        throw new TaskNotFoundException(id);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Task transfer(String taskId, String destinationWorkbasketId, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return taskTransferrer.transfer(taskId, destinationWorkbasketId, setTransferFlag);
  }

  @Override
  public Task transfer(String taskId, String workbasketKey, String domain, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return taskTransferrer.transfer(taskId, workbasketKey, domain, setTransferFlag);
  }

  @Override
  public Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException {
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      task.setRead(isRead);
      task.setModified(Instant.now());
      taskMapper.update(task);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Method setTaskRead() set read property of Task '{}' to {} ", task, isRead);
      }
      return task;
    } finally {
      taskanaEngine.returnConnection();
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
    TaskImpl task = new TaskImpl();
    WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
    wb.setKey(workbasketKey);
    wb.setDomain(domain);
    task.setWorkbasketSummary(wb);
    return task;
  }

  @Override
  public TaskComment newTaskComment(String taskId) {
    return taskCommentService.newTaskComment(taskId);
  }

  @Override
  public Attachment newAttachment() {
    return new AttachmentImpl();
  }

  @Override
  public Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          NotAuthorizedException, AttachmentPersistenceException, InvalidStateException,
          ClassificationNotFoundException {
    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    TaskImpl newTaskImpl = (TaskImpl) task;
    try {
      taskanaEngine.openConnection();
      TaskImpl oldTaskImpl = (TaskImpl) getTask(newTaskImpl.getId());

      checkConcurrencyAndSetModified(newTaskImpl, oldTaskImpl);

      attachmentHandler.insertAndDeleteAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
      ObjectReference.validate(newTaskImpl.getPrimaryObjRef(), "primary ObjectReference", "Task");

      standardUpdateActions(oldTaskImpl, newTaskImpl);

      if (PriorityServiceManager.isPriorityServiceEnabled()) {
        Optional<Integer> newPriority = priorityServiceManager.calculatePriorityOfTask(newTaskImpl);
        if (newPriority.isPresent()) {
          newTaskImpl.setPriority(newPriority.get());
        }
      }

      taskMapper.update(newTaskImpl);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Method updateTask() updated task '{}' for user '{}'.", task.getId(), userId);
      }

      if (HistoryEventManager.isHistoryEnabled()) {

        String changeDetails =
            ObjectAttributeChangeDetector.determineChangesInAttributes(oldTaskImpl, newTaskImpl);

        historyEventManager.createEvent(
            new TaskUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                task,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                changeDetails));
      }

    } finally {
      taskanaEngine.returnConnection();
    }
    return task;
  }

  @Override
  public BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds, boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return taskTransferrer.transfer(taskIds, destinationWorkbasketId, setTransferFlag);
  }

  @Override
  public BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey,
      String destinationWorkbasketDomain,
      List<String> taskIds,
      boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return taskTransferrer.transfer(
        taskIds, destinationWorkbasketKey, destinationWorkbasketDomain, setTransferFlag);
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
  public Task selectAndClaim(TaskQuery taskQuery)
      throws NotAuthorizedException, InvalidOwnerException {

    try {

      taskanaEngine.openConnection();

      ((TaskQueryImpl) taskQuery).selectAndClaimEquals(true);

      TaskSummary taskSummary = taskQuery.single();

      if (taskSummary == null) {
        throw new SystemException(
            "No tasks matched the specified filter and sorting options,"
                + " task query returned nothing!");
      }

      return claim(taskSummary.getId());

    } catch (InvalidStateException | TaskNotFoundException e) {
      throw new SystemException("Caught exception ", e);
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> deleteTasks(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN);

    try {
      taskanaEngine.openConnection();
      if (taskIds == null) {
        throw new InvalidArgumentException("List of TaskIds must not be null.");
      }
      taskIds = new ArrayList<>(taskIds);

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
        attachmentMapper.deleteMultipleByTaskIds(taskIds);
        taskMapper.deleteMultiple(taskIds);

        if (taskanaEngine.getEngine().isHistoryEnabled()
            && taskanaEngine
                .getEngine()
                .getConfiguration()
                .isDeleteHistoryOnTaskDeletionEnabled()) {
          historyEventManager.deleteEvents(taskIds);
        }
      }
      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
      throws InvalidArgumentException {
    return completeTasks(taskIds, false);
  }

  @Override
  public BulkOperationResults<String, TaskanaException> forceCompleteTasks(List<String> taskIds)
      throws InvalidArgumentException {
    return completeTasks(taskIds, true);
  }

  @Override
  public List<String> updateTasks(
      ObjectReference selectionCriteria, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException {

    ObjectReference.validate(selectionCriteria, "ObjectReference", "updateTasks call");
    validateCustomFields(customFieldsToUpdate);
    TaskCustomPropertySelector fieldSelector = new TaskCustomPropertySelector();
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
          LOGGER.debug("updateTasks() updated the following tasks: {} ", changedTasks);
        }

      } else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("updateTasks() found no tasks for update ");
        }
      }
      return changedTasks;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> updateTasks(
      List<String> taskIds, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException {

    validateCustomFields(customFieldsToUpdate);
    TaskCustomPropertySelector fieldSelector = new TaskCustomPropertySelector();
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
          LOGGER.debug("updateTasks() updated the following tasks: {} ", changedTasks);
        }

      } else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("updateTasks() found no tasks for update ");
        }
      }
      return changedTasks;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public TaskComment createTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException {
    return taskCommentService.createTaskComment(taskComment);
  }

  @Override
  public TaskComment updateTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException {
    return taskCommentService.updateTaskComment(taskComment);
  }

  @Override
  public void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException {
    taskCommentService.deleteTaskComment(taskCommentId);
  }

  @Override
  public TaskComment getTaskComment(String taskCommentid)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException {
    return taskCommentService.getTaskComment(taskCommentid);
  }

  @Override
  public List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException {

    return taskCommentService.getTaskComments(taskId);
  }

  @Override
  public BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state) {

    try {
      taskanaEngine.openConnection();

      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

      if (externalIds == null || externalIds.isEmpty()) {
        return bulkLog;
      }

      List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(null, externalIds);

      Iterator<String> taskIdIterator = new ArrayList<>(externalIds).iterator();
      while (taskIdIterator.hasNext()) {
        removeSingleTaskForCallbackStateByExternalId(bulkLog, taskSummaries, taskIdIterator, state);
      }
      if (!externalIds.isEmpty()) {
        taskMapper.setCallbackStateMultiple(externalIds, state);
      }
      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> setOwnerOfTasks(
      String owner, List<String> taskIds) {

    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    if (taskIds == null || taskIds.isEmpty()) {
      return bulkLog;
    }

    try {
      taskanaEngine.openConnection();
      Pair<List<MinimalTaskSummary>, BulkLog> existingAndAuthorizedTasks =
          getMinimalTaskSummaries(taskIds);
      bulkLog.addAllErrors(existingAndAuthorizedTasks.getRight());
      Pair<List<String>, BulkLog> taskIdsToUpdate =
          filterOutTasksWhichAreNotReady(existingAndAuthorizedTasks.getLeft());
      bulkLog.addAllErrors(taskIdsToUpdate.getRight());

      if (!taskIdsToUpdate.getLeft().isEmpty()) {
        taskMapper.setOwnerOfTasks(owner, taskIdsToUpdate.getLeft(), Instant.now());
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Received the Request to set owner on {} tasks, actually modified tasks = {}"
                + ", could not set owner on {} tasks.",
            taskIds.size(),
            taskIdsToUpdate.getLeft().size(),
            bulkLog.getFailedIds().size());
      }

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasks(
      Instant planned, List<String> argTaskIds) {

    BulkLog bulkLog = new BulkLog();
    if (argTaskIds == null || argTaskIds.isEmpty()) {
      return bulkLog;
    }
    try {
      taskanaEngine.openConnection();
      Pair<List<MinimalTaskSummary>, BulkLog> resultsPair = getMinimalTaskSummaries(argTaskIds);
      List<MinimalTaskSummary> tasksToModify = resultsPair.getLeft();
      bulkLog.addAllErrors(resultsPair.getRight());
      BulkLog errorsFromProcessing =
          serviceLevelHandler.setPlannedPropertyOfTasksImpl(planned, tasksToModify);
      bulkLog.addAllErrors(errorsFromProcessing);
      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Task cancelTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

    Task cancelledTask;

    try {
      taskanaEngine.openConnection();
      cancelledTask = terminateCancelCommonActions(taskId, TaskState.CANCELLED);

      if (HistoryEventManager.isHistoryEnabled()) {
        historyEventManager.createEvent(
            new TaskCancelledEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                cancelledTask,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
    }

    return cancelledTask;
  }

  @Override
  public Task terminateTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN);

    Task terminatedTask;

    try {
      taskanaEngine.openConnection();
      terminatedTask = terminateCancelCommonActions(taskId, TaskState.TERMINATED);

      if (HistoryEventManager.isHistoryEnabled()) {
        historyEventManager.createEvent(
            new TaskTerminatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                terminatedTask,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      }

    } finally {
      taskanaEngine.returnConnection();
    }
    return terminatedTask;
  }

  public List<String> findTasksIdsAffectedByClassificationChange(String classificationId) {
    // tasks directly affected
    List<TaskSummary> tasksAffectedDirectly =
        createTaskQuery()
            .classificationIdIn(classificationId)
            .stateIn(TaskState.READY, TaskState.CLAIMED)
            .list();

    // tasks indirectly affected via attachments
    List<Pair<String, Instant>> affectedPairs =
        tasksAffectedDirectly.stream()
            .map(t -> Pair.of(t.getId(), t.getPlanned()))
            .collect(Collectors.toList());
    // tasks indirectly affected via attachments
    List<Pair<String, Instant>> taskIdsAndPlannedFromAttachments =
        attachmentMapper.findTaskIdsAndPlannedAffectedByClassificationChange(classificationId);

    List<String> taskIdsFromAttachments =
        taskIdsAndPlannedFromAttachments.stream().map(Pair::getLeft).collect(Collectors.toList());
    List<Pair<String, Instant>> filteredTaskIdsAndPlannedFromAttachments =
        taskIdsFromAttachments.isEmpty()
            ? new ArrayList<>()
            : taskMapper.filterTaskIdsForReadyAndClaimed(taskIdsFromAttachments);
    affectedPairs.addAll(filteredTaskIdsAndPlannedFromAttachments);
    //  sort all affected tasks according to the planned instant
    List<String> affectedTaskIds =
        affectedPairs.stream()
            .sorted(Comparator.comparing(Pair::getRight))
            .distinct()
            .map(Pair::getLeft)
            .collect(Collectors.toList());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "the following tasks are affected by the update of classification {} : {}",
          classificationId,
          affectedTaskIds);
    }
    return affectedTaskIds;
  }

  public void refreshPriorityAndDueDatesOfTasksOnClassificationUpdate(
      List<String> taskIds, boolean serviceLevelChanged, boolean priorityChanged) {
    Pair<List<MinimalTaskSummary>, BulkLog> resultsPair = getMinimalTaskSummaries(taskIds);
    List<MinimalTaskSummary> tasks = resultsPair.getLeft();
    try {
      taskanaEngine.openConnection();
      Set<String> adminAccessIds =
          taskanaEngine.getEngine().getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
      if (adminAccessIds.contains(taskanaEngine.getEngine().getCurrentUserContext().getUserid())) {
        serviceLevelHandler.refreshPriorityAndDueDatesOfTasks(
            tasks, serviceLevelChanged, priorityChanged);
      } else {
        taskanaEngine
            .getEngine()
            .runAsAdmin(
                () -> {
                  serviceLevelHandler.refreshPriorityAndDueDatesOfTasks(
                      tasks, serviceLevelChanged, priorityChanged);
                  return null;
                });
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  Pair<List<MinimalTaskSummary>, BulkLog> getMinimalTaskSummaries(Collection<String> argTaskIds) {
    BulkLog bulkLog = new BulkLog();
    // remove duplicates
    Set<String> taskIds = new HashSet<>(argTaskIds);
    // get existing tasks
    List<MinimalTaskSummary> minimalTaskSummaries = taskMapper.findExistingTasks(taskIds, null);
    bulkLog.addAllErrors(addExceptionsForNonExistingTasksToBulkLog(taskIds, minimalTaskSummaries));
    Pair<List<MinimalTaskSummary>, BulkLog> filteredPair =
        filterTasksAuthorizedForAndLogErrorsForNotAuthorized(minimalTaskSummaries);
    bulkLog.addAllErrors(filteredPair.getRight());
    return Pair.of(filteredPair.getLeft(), bulkLog);
  }

  Pair<List<MinimalTaskSummary>, BulkLog> filterTasksAuthorizedForAndLogErrorsForNotAuthorized(
      List<MinimalTaskSummary> existingTasks) {
    BulkLog bulkLog = new BulkLog();
    // check authorization only for non-admin or task-admin users
    if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN)) {
      return Pair.of(existingTasks, bulkLog);
    } else {
      List<String> accessIds = taskanaEngine.getEngine().getCurrentUserContext().getAccessIds();
      List<Pair<String, String>> taskAndWorkbasketIdsNotAuthorizedFor =
          taskMapper.getTaskAndWorkbasketIdsNotAuthorizedFor(existingTasks, accessIds);
      String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();

      for (Pair<String, String> taskAndWorkbasketIds : taskAndWorkbasketIdsNotAuthorizedFor) {
        bulkLog.addError(
            taskAndWorkbasketIds.getLeft(),
            new MismatchedWorkbasketPermissionException(
                userId, taskAndWorkbasketIds.getRight(), WorkbasketPermission.READ));
      }

      Set<String> taskIdsToRemove =
          taskAndWorkbasketIdsNotAuthorizedFor.stream()
              .map(Pair::getLeft)
              .collect(Collectors.toSet());
      List<MinimalTaskSummary> tasksAuthorizedFor =
          existingTasks.stream()
              .filter(t -> !taskIdsToRemove.contains(t.getTaskId()))
              .collect(Collectors.toList());
      return Pair.of(tasksAuthorizedFor, bulkLog);
    }
  }

  BulkLog addExceptionsForNonExistingTasksToBulkLog(
      Collection<String> requestTaskIds, List<MinimalTaskSummary> existingMinimalTaskSummaries) {
    BulkLog bulkLog = new BulkLog();
    Set<String> existingTaskIds =
        existingMinimalTaskSummaries.stream()
            .map(MinimalTaskSummary::getTaskId)
            .collect(Collectors.toSet());
    requestTaskIds.stream()
        .filter(taskId -> !existingTaskIds.contains(taskId))
        .forEach(taskId -> bulkLog.addError(taskId, new TaskNotFoundException(taskId)));
    return bulkLog;
  }

  List<TaskSummary> augmentTaskSummariesByContainedSummariesWithPartitioning(
      List<TaskSummaryImpl> taskSummaries) {
    // splitting Augmentation into steps of maximal 32000 tasks
    // reason: DB2 has a maximum for parameters in a query
    return CollectionUtil.partitionBasedOnSize(taskSummaries, 32000).stream()
        .map(this::augmentTaskSummariesByContainedSummariesWithoutPartitioning)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private Pair<List<String>, BulkLog> filterOutTasksWhichAreNotReady(
      Collection<MinimalTaskSummary> minimalTaskSummaries) {
    List<String> filteredTasks = new ArrayList<>(minimalTaskSummaries.size());
    BulkLog bulkLog = new BulkLog();

    for (MinimalTaskSummary taskSummary : minimalTaskSummaries) {
      if (taskSummary.getTaskState() != TaskState.READY) {
        bulkLog.addError(
            taskSummary.getTaskId(),
            new InvalidTaskStateException(
                taskSummary.getTaskId(), taskSummary.getTaskState(), TaskState.READY));
      } else {
        filteredTasks.add(taskSummary.getTaskId());
      }
    }
    return Pair.of(filteredTasks, bulkLog);
  }

  private List<TaskSummaryImpl> augmentTaskSummariesByContainedSummariesWithoutPartitioning(
      List<TaskSummaryImpl> taskSummaries) {
    Set<String> taskIds =
        taskSummaries.stream().map(TaskSummaryImpl::getId).collect(Collectors.toSet());

    if (taskIds.isEmpty()) {
      taskIds = null;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "augmentTaskSummariesByContainedSummariesWithoutPartitioning() with sublist {} "
              + "about to query for attachmentSummaries ",
          taskSummaries);
    }

    List<AttachmentSummaryImpl> attachmentSummaries =
        attachmentMapper.findAttachmentSummariesByTaskIds(taskIds);
    Map<String, ClassificationSummary> classificationSummariesById =
        findClassificationsForTasksAndAttachments(taskSummaries, attachmentSummaries);
    Map<String, WorkbasketSummary> workbasketSummariesById = findWorkbasketsForTasks(taskSummaries);

    addClassificationSummariesToAttachments(attachmentSummaries, classificationSummariesById);
    addClassificationSummariesToTaskSummaries(taskSummaries, classificationSummariesById);
    addWorkbasketSummariesToTaskSummaries(taskSummaries, workbasketSummariesById);
    addAttachmentSummariesToTaskSummaries(taskSummaries, attachmentSummaries);

    return taskSummaries;
  }

  private BulkOperationResults<String, TaskanaException> completeTasks(
      List<String> taskIds, boolean forced) throws InvalidArgumentException {
    try {
      taskanaEngine.openConnection();
      if (taskIds == null) {
        throw new InvalidArgumentException("TaskIds can't be used as NULL-Parameter.");
      }
      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

      Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
      Stream<TaskSummaryImpl> filteredSummaries =
          filterNotExistingTaskIds(taskIds, bulkLog)
              .filter(task -> task.getState() != TaskState.COMPLETED)
              .filter(
                  addErrorToBulkLog(TaskServiceImpl::checkIfTaskIsTerminatedOrCancelled, bulkLog));
      if (!forced) {
        filteredSummaries =
            filteredSummaries.filter(
                addErrorToBulkLog(this::checkPreconditionsForCompleteTask, bulkLog));
      } else {
        String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
        filteredSummaries =
            filteredSummaries.filter(
                addErrorToBulkLog(
                    summary -> {
                      if (taskIsNotClaimed(summary)) {
                        checkPreconditionsForClaimTask(summary, true);
                        claimActionsOnTask(summary, userId, now);
                      }
                    },
                    bulkLog));
      }

      updateTasksToBeCompleted(filteredSummaries, now);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private Stream<TaskSummaryImpl> filterNotExistingTaskIds(
      List<String> taskIds, BulkOperationResults<String, TaskanaException> bulkLog) {

    Map<String, TaskSummaryImpl> taskSummaryMap =
        getTasksToChange(taskIds).stream()
            .collect(Collectors.toMap(TaskSummary::getId, TaskSummaryImpl.class::cast));
    return taskIds.stream()
        .map(id -> Pair.of(id, taskSummaryMap.get(id)))
        .filter(
            pair -> {
              if (pair.getRight() != null) {
                return true;
              }
              String taskId = pair.getLeft();
              bulkLog.addError(taskId, new TaskNotFoundException(taskId));
              return false;
            })
        .map(Pair::getRight);
  }

  private static Predicate<TaskSummaryImpl> addErrorToBulkLog(
      CheckedConsumer<TaskSummaryImpl, TaskanaException> checkedConsumer,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    return summary -> {
      try {
        checkedConsumer.accept(summary);
        return true;
      } catch (TaskanaException e) {
        bulkLog.addError(summary.getId(), e);
        return false;
      }
    };
  }

  private void checkConcurrencyAndSetModified(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws ConcurrencyException {
    // TODO: not safe to rely only on different timestamps.
    // With fast execution below 1ms there will be no concurrencyException
    if (oldTaskImpl.getModified() != null
            && !oldTaskImpl.getModified().equals(newTaskImpl.getModified())
        || oldTaskImpl.getClaimed() != null
            && !oldTaskImpl.getClaimed().equals(newTaskImpl.getClaimed())
        || oldTaskImpl.getState() != null
            && !oldTaskImpl.getState().equals(newTaskImpl.getState())) {
      throw new ConcurrencyException(newTaskImpl.getId());
    }
    newTaskImpl.setModified(Instant.now());
  }

  private TaskImpl terminateCancelCommonActions(String taskId, TaskState targetState)
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    if (taskId == null || taskId.isEmpty()) {
      throw new TaskNotFoundException(taskId);
    }
    TaskImpl task = (TaskImpl) getTask(taskId);
    TaskState state = task.getState();
    if (state.isEndState()) {
      throw new InvalidTaskStateException(taskId, state, TaskState.READY);
    }

    Instant now = Instant.now();
    task.setModified(now);
    task.setCompleted(now);
    task.setState(targetState);
    taskMapper.update(task);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Task '{}' cancelled by user '{}'.",
          taskId,
          taskanaEngine.getEngine().getCurrentUserContext().getUserid());
    }
    return task;
  }

  private Task claim(String taskId, boolean forceClaim)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      Instant now = Instant.now();

      checkPreconditionsForClaimTask(task, forceClaim);
      claimActionsOnTask(task, userId, now);
      taskMapper.update(task);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Task '{}' claimed by user '{}'.", taskId, userId);
      }
      if (HistoryEventManager.isHistoryEnabled()) {
        historyEventManager.createEvent(
            new TaskClaimedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                task,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
    }
    return task;
  }

  private static void claimActionsOnTask(TaskSummaryImpl task, String userId, Instant now) {
    task.setOwner(userId);
    task.setModified(now);
    task.setClaimed(now);
    task.setRead(true);
    task.setState(TaskState.CLAIMED);
  }

  private static void completeActionsOnTask(TaskSummaryImpl task, String userId, Instant now) {
    task.setCompleted(now);
    task.setModified(now);
    task.setState(TaskState.COMPLETED);
    task.setOwner(userId);
  }

  private void checkPreconditionsForClaimTask(TaskSummary task, boolean forced)
      throws InvalidStateException, InvalidOwnerException {
    TaskState state = task.getState();
    if (state.isEndState()) {
      throw new InvalidTaskStateException(
          task.getId(), task.getState(), EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    }

    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    if (!forced && state == TaskState.CLAIMED && !task.getOwner().equals(userId)) {
      throw new InvalidOwnerException(userId, task.getId());
    }
  }

  private static boolean taskIsNotClaimed(TaskSummary task) {
    return task.getClaimed() == null || task.getState() != TaskState.CLAIMED;
  }

  private static void checkIfTaskIsTerminatedOrCancelled(TaskSummary task)
      throws InvalidStateException {
    if (task.getState().in(TaskState.CANCELLED, TaskState.TERMINATED)) {
      throw new InvalidTaskStateException(
          task.getId(),
          task.getState(),
          EnumUtil.allValuesExceptFor(TaskState.CANCELLED, TaskState.TERMINATED));
    }
  }

  private void checkPreconditionsForCompleteTask(TaskSummary task)
      throws InvalidStateException, InvalidOwnerException {
    if (taskIsNotClaimed(task)) {
      throw new InvalidTaskStateException(task.getId(), task.getState(), TaskState.CLAIMED);
    } else if (!taskanaEngine
            .getEngine()
            .getCurrentUserContext()
            .getAccessIds()
            .contains(task.getOwner())
        && !taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)) {
      throw new InvalidOwnerException(
          taskanaEngine.getEngine().getCurrentUserContext().getUserid(), task.getId());
    }
  }

  private Task cancelClaim(String taskId, boolean forceUnclaim)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);
      TaskState state = task.getState();
      if (state.isEndState()) {
        throw new InvalidTaskStateException(
            taskId, state, EnumUtil.allValuesExceptFor(TaskState.END_STATES));
      }
      if (state == TaskState.CLAIMED && !forceUnclaim && !userId.equals(task.getOwner())) {
        throw new InvalidOwnerException(userId, taskId);
      }
      Instant now = Instant.now();
      task.setOwner(null);
      task.setModified(now);
      task.setClaimed(null);
      task.setRead(true);
      task.setState(TaskState.READY);
      taskMapper.update(task);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Task '{}' unclaimed by user '{}'.", taskId, userId);
      }
      if (HistoryEventManager.isHistoryEnabled()) {
        historyEventManager.createEvent(
            new TaskClaimCancelledEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                task,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
    }
    return task;
  }

  private Task completeTask(String taskId, boolean isForced)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) this.getTask(taskId);

      if (task.getState() == TaskState.COMPLETED) {
        return task;
      }

      checkIfTaskIsTerminatedOrCancelled(task);

      if (!isForced) {
        checkPreconditionsForCompleteTask(task);
      } else if (taskIsNotClaimed(task)) {
        task = (TaskImpl) this.forceClaim(taskId);
      }

      Instant now = Instant.now();
      completeActionsOnTask(task, userId, now);
      taskMapper.update(task);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Task '{}' completed by user '{}'.", taskId, userId);
      }
      if (HistoryEventManager.isHistoryEnabled()) {
        historyEventManager.createEvent(
            new TaskCompletedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                task,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      }
    } finally {
      taskanaEngine.returnConnection();
    }
    return task;
  }

  private void deleteTask(String taskId, boolean forceDelete)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN);
    TaskImpl task;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) getTask(taskId);

      if (!(task.getState().isEndState()) && !forceDelete) {
        throw new InvalidTaskStateException(taskId, task.getState(), TaskState.END_STATES);
      }
      if ((!task.getState().in(TaskState.TERMINATED, TaskState.CANCELLED))
          && CallbackState.CALLBACK_PROCESSING_REQUIRED.equals(task.getCallbackState())) {
        throw new InvalidCallbackStateException(
            taskId,
            task.getCallbackState(),
            EnumUtil.allValuesExceptFor(CallbackState.CALLBACK_PROCESSING_REQUIRED));
      }

      attachmentMapper.deleteMultipleByTaskIds(Collections.singletonList(taskId));
      taskMapper.delete(taskId);

      if (taskanaEngine.getEngine().isHistoryEnabled()
          && taskanaEngine.getEngine().getConfiguration().isDeleteHistoryOnTaskDeletionEnabled()) {
        historyEventManager.deleteEvents(Collections.singletonList(taskId));
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Task {} deleted.", taskId);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void removeSingleTaskForTaskDeletionById(
      BulkOperationResults<String, TaskanaException> bulkLog,
      List<MinimalTaskSummary> taskSummaries,
      Iterator<String> taskIdIterator) {
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
        bulkLog.addError(currentTaskId, new TaskNotFoundException(currentTaskId));
        taskIdIterator.remove();
      } else if (!(foundSummary.getTaskState().isEndState())) {
        bulkLog.addError(
            currentTaskId,
            new InvalidTaskStateException(
                currentTaskId, foundSummary.getTaskState(), TaskState.END_STATES));
        taskIdIterator.remove();
      } else {
        if ((!foundSummary.getTaskState().in(TaskState.CANCELLED, TaskState.TERMINATED))
            && CallbackState.CALLBACK_PROCESSING_REQUIRED.equals(foundSummary.getCallbackState())) {
          bulkLog.addError(
              currentTaskId,
              new InvalidCallbackStateException(
                  currentTaskId,
                  foundSummary.getCallbackState(),
                  EnumUtil.allValuesExceptFor(CallbackState.CALLBACK_PROCESSING_REQUIRED)));
          taskIdIterator.remove();
        }
      }
    }
  }

  private void removeSingleTaskForCallbackStateByExternalId(
      BulkOperationResults<String, TaskanaException> bulkLog,
      List<MinimalTaskSummary> taskSummaries,
      Iterator<String> externalIdIterator,
      CallbackState desiredCallbackState) {
    String currentExternalId = externalIdIterator.next();
    if (currentExternalId == null || currentExternalId.equals("")) {
      bulkLog.addError(
          "", new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
      externalIdIterator.remove();
    } else {
      Optional<MinimalTaskSummary> foundSummary =
          taskSummaries.stream()
              .filter(taskSummary -> currentExternalId.equals(taskSummary.getExternalId()))
              .findFirst();
      if (foundSummary.isPresent()) {
        Optional<TaskanaException> invalidStateException =
            desiredCallbackStateCanBeSetForFoundSummary(foundSummary.get(), desiredCallbackState);
        if (invalidStateException.isPresent()) {
          bulkLog.addError(currentExternalId, invalidStateException.get());
          externalIdIterator.remove();
        }
      } else {
        bulkLog.addError(currentExternalId, new TaskNotFoundException(currentExternalId));
        externalIdIterator.remove();
      }
    }
  }

  private Optional<TaskanaException> desiredCallbackStateCanBeSetForFoundSummary(
      MinimalTaskSummary foundSummary, CallbackState desiredCallbackState) {

    CallbackState currentTaskCallbackState = foundSummary.getCallbackState();
    TaskState currentTaskState = foundSummary.getTaskState();

    switch (desiredCallbackState) {
      case CALLBACK_PROCESSING_COMPLETED:
        if (!currentTaskState.isEndState()) {
          return Optional.of(
              new InvalidTaskStateException(
                  foundSummary.getTaskId(), foundSummary.getTaskState(), TaskState.END_STATES));
        }
        break;
      case CLAIMED:
        if (!currentTaskState.equals(TaskState.CLAIMED)) {
          return Optional.of(
              new InvalidTaskStateException(
                  foundSummary.getTaskId(), foundSummary.getTaskState(), TaskState.CLAIMED));
        }
        if (!currentTaskCallbackState.equals(CallbackState.CALLBACK_PROCESSING_REQUIRED)) {
          return Optional.of(
              new InvalidCallbackStateException(
                  foundSummary.getTaskId(),
                  currentTaskCallbackState,
                  CallbackState.CALLBACK_PROCESSING_REQUIRED));
        }
        break;
      case CALLBACK_PROCESSING_REQUIRED:
        if (currentTaskCallbackState.equals(CallbackState.CALLBACK_PROCESSING_COMPLETED)) {
          return Optional.of(
              new InvalidCallbackStateException(
                  foundSummary.getTaskId(),
                  currentTaskCallbackState,
                  EnumUtil.allValuesExceptFor(CallbackState.CALLBACK_PROCESSING_COMPLETED)));
        }
        break;
      default:
        return Optional.of(
            new InvalidArgumentException(
                String.format(
                    "desired callbackState has to be in '%s'",
                    Arrays.toString(
                        new CallbackState[] {
                          CallbackState.CALLBACK_PROCESSING_COMPLETED,
                          CallbackState.CLAIMED,
                          CallbackState.CALLBACK_PROCESSING_REQUIRED
                        }))));
    }
    return Optional.empty();
  }

  private void standardSettingsOnTaskCreation(TaskImpl task, Classification classification)
      throws InvalidArgumentException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    final Instant now = Instant.now();
    task.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK));
    if (task.getExternalId() == null) {
      task.setExternalId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_EXT_TASK));
    }
    task.setState(TaskState.READY);
    task.setCreated(now);
    task.setModified(now);
    task.setRead(false);
    task.setTransferred(false);

    String creator = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    if (taskanaEngine.getEngine().getConfiguration().isSecurityEnabled() && creator == null) {
      throw new SystemException(
          "TaskanaSecurity is enabled, but the current UserId is NULL while creating a Task.");
    }
    task.setCreator(creator);

    // if no business process id is provided, a unique id is created.
    if (task.getBusinessProcessId() == null) {
      task.setBusinessProcessId(
          IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_BUSINESS_PROCESS));
    }
    // null in case of manual tasks
    if (task.getPlanned() == null && (classification == null || task.getDue() == null)) {
      task.setPlanned(now);
    }
    if (task.getName() == null && classification != null) {
      task.setName(classification.getName());
    }
    if (task.getDescription() == null && classification != null) {
      task.setDescription(classification.getDescription());
    }
    setDefaultTaskReceivedDateFromAttachments(task);

    attachmentHandler.insertNewAttachmentsOnTaskCreation(task);
    // This has to be called after the AttachmentHandler because the AttachmentHandler fetches
    // the Classifications of the Attachments.
    // This is necessary to guarantee that the following calculation is correct.
    serviceLevelHandler.updatePrioPlannedDueOfTask(task, null, false);
  }

  private void setDefaultTaskReceivedDateFromAttachments(TaskImpl task) {
    if (task.getReceived() == null) {
      task.getAttachments().stream()
          .map(AttachmentSummary::getReceived)
          .filter(Objects::nonNull)
          .min(Instant::compareTo)
          .ifPresent(task::setReceived);
    }
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
          LOGGER.warn(
              "Attempted to determine callback state from {} and caught exception", value, e);
          throw new InvalidArgumentException(
              String.format("Attempted to set callback state for task %s.", task.getId()), e);
        }
      }
    }
  }

  private void updateTasksToBeCompleted(Stream<TaskSummaryImpl> taskSummaries, Instant now) {

    List<String> taskIds = new ArrayList<>();
    List<String> updateClaimedTaskIds = new ArrayList<>();
    List<TaskSummary> taskSummaryList =
        taskSummaries
            .peek(
                summary ->
                    completeActionsOnTask(
                        summary,
                        taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                        now))
            .peek(summary -> taskIds.add(summary.getId()))
            .peek(
                summary -> {
                  if (summary.getClaimed().equals(now)) {
                    updateClaimedTaskIds.add(summary.getId());
                  }
                })
            .collect(Collectors.toList());
    TaskSummary claimedReference =
        taskSummaryList.stream()
            .filter(summary -> updateClaimedTaskIds.contains(summary.getId()))
            .findFirst()
            .orElse(null);

    if (!taskSummaryList.isEmpty()) {
      taskMapper.updateCompleted(taskIds, taskSummaryList.get(0));
      if (!updateClaimedTaskIds.isEmpty()) {
        taskMapper.updateClaimed(updateClaimedTaskIds, claimedReference);
      }
      if (HistoryEventManager.isHistoryEnabled()) {
        createTasksCompletedEvents(taskSummaryList);
      }
    }
  }

  private Map<String, WorkbasketSummary> findWorkbasketsForTasks(
      List<? extends TaskSummary> taskSummaries) {
    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return Collections.emptyMap();
    }

    Set<String> workbasketIds =
        taskSummaries.stream()
            .map(TaskSummary::getWorkbasketSummary)
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());

    return queryWorkbasketsForTasks(workbasketIds).stream()
        .collect(Collectors.toMap(WorkbasketSummary::getId, Function.identity()));
  }

  private Map<String, ClassificationSummary> findClassificationsForTasksAndAttachments(
      List<? extends TaskSummary> taskSummaries,
      List<? extends AttachmentSummaryImpl> attachmentSummaries) {
    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return Collections.emptyMap();
    }

    Set<String> classificationIds =
        Stream.concat(
                taskSummaries.stream().map(TaskSummary::getClassificationSummary),
                attachmentSummaries.stream().map(AttachmentSummary::getClassificationSummary))
            .map(ClassificationSummary::getId)
            .collect(Collectors.toSet());

    return queryClassificationsForTasksAndAttachments(classificationIds).stream()
        .collect(Collectors.toMap(ClassificationSummary::getId, Function.identity()));
  }

  private Map<String, ClassificationSummary> findClassificationForTaskImplAndAttachments(
      TaskImpl task, List<AttachmentImpl> attachmentImpls) {
    return findClassificationsForTasksAndAttachments(
        Collections.singletonList(task), attachmentImpls);
  }

  private List<ClassificationSummary> queryClassificationsForTasksAndAttachments(
      Set<String> classificationIds) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "queryClassificationsForTasksAndAttachments() about to query classifications and exit");
    }
    return this.classificationService
        .createClassificationQuery()
        .idIn(classificationIds.toArray(new String[0]))
        .list();
  }

  private List<WorkbasketSummary> queryWorkbasketsForTasks(Set<String> workbasketIds) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("queryWorkbasketsForTasks() about to query workbaskets and exit");
    }
    // perform classification query
    return this.workbasketService
        .createWorkbasketQuery()
        .idIn(workbasketIds.toArray(new String[0]))
        .list();
  }

  private void addClassificationSummariesToTaskSummaries(
      List<TaskSummaryImpl> tasks, Map<String, ClassificationSummary> classificationSummaryById) {

    if (tasks == null || tasks.isEmpty()) {
      return;
    }

    for (TaskSummaryImpl task : tasks) {
      String classificationId = task.getClassificationSummary().getId();
      ClassificationSummary classificationSummary = classificationSummaryById.get(classificationId);
      if (classificationSummary == null) {
        throw new SystemException(
            "Did not find a Classification for task (Id="
                + task.getId()
                + ",Classification="
                + task.getClassificationSummary().getId()
                + ")");
      }
      task.setClassificationSummary(classificationSummary);
    }
  }

  private void addWorkbasketSummariesToTaskSummaries(
      List<TaskSummaryImpl> tasks, Map<String, WorkbasketSummary> workbasketSummaryById) {
    if (tasks == null || tasks.isEmpty()) {
      return;
    }

    for (TaskSummaryImpl task : tasks) {
      String workbasketId = task.getWorkbasketSummary().getId();
      WorkbasketSummary workbasketSummary = workbasketSummaryById.get(workbasketId);
      if (workbasketSummary == null) {
        throw new SystemException(
            "Did not find a Workbasket for task (Id="
                + task.getId()
                + ",Workbasket="
                + task.getWorkbasketSummary().getId()
                + ")");
      }
      task.setWorkbasketSummary(workbasketSummary);
    }
  }

  private void addAttachmentSummariesToTaskSummaries(
      List<TaskSummaryImpl> taskSummaries, List<AttachmentSummaryImpl> attachmentSummaries) {

    if (taskSummaries == null || taskSummaries.isEmpty()) {
      return;
    }

    Map<String, TaskSummaryImpl> taskSummariesById =
        taskSummaries.stream()
            .collect(
                Collectors.toMap(
                    TaskSummary::getId,
                    Function.identity(),
                    // Currently, we still have a bug (TSK-1204), where the TaskQuery#list function
                    // returns the same task multiple times when that task has more than one
                    // attachment...Therefore, this MergeFunction is necessary.
                    (a, b) -> b));

    for (AttachmentSummaryImpl attachmentSummary : attachmentSummaries) {
      String taskId = attachmentSummary.getTaskId();
      TaskSummaryImpl taskSummary = taskSummariesById.get(taskId);
      if (taskSummary != null) {
        taskSummary.addAttachmentSummary(attachmentSummary);
      }
    }
  }

  private void addClassificationSummariesToAttachments(
      List<? extends AttachmentSummaryImpl> attachments,
      Map<String, ClassificationSummary> classificationSummariesById) {

    if (attachments == null || attachments.isEmpty()) {
      return;
    }

    for (AttachmentSummaryImpl attachment : attachments) {
      String classificationId = attachment.getClassificationSummary().getId();
      ClassificationSummary classificationSummary =
          classificationSummariesById.get(classificationId);

      if (classificationSummary == null) {
        throw new SystemException("Could not find a Classification for attachment " + attachment);
      }
      attachment.setClassificationSummary(classificationSummary);
    }
  }

  private TaskImpl initUpdatedTask(
      Map<TaskCustomField, String> customFieldsToUpdate, TaskCustomPropertySelector fieldSelector) {

    TaskImpl newTask = new TaskImpl();
    newTask.setModified(Instant.now());

    for (Map.Entry<TaskCustomField, String> entry : customFieldsToUpdate.entrySet()) {
      TaskCustomField key = entry.getKey();
      fieldSelector.setCustomProperty(key, true);
      newTask.setCustomAttribute(key, entry.getValue());
    }
    return newTask;
  }

  private void validateCustomFields(Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException {

    if (customFieldsToUpdate == null || customFieldsToUpdate.isEmpty()) {
      throw new InvalidArgumentException(
          "The customFieldsToUpdate argument to updateTasks must not be empty.");
    }
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

  private void standardUpdateActions(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
      throws InvalidArgumentException, InvalidStateException, ClassificationNotFoundException {

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

    if (newTaskImpl.getClassificationSummary() == null) {
      newTaskImpl.setClassificationSummary(oldTaskImpl.getClassificationSummary());
    }

    setDefaultTaskReceivedDateFromAttachments(newTaskImpl);

    updateClassificationSummary(newTaskImpl, oldTaskImpl);

    TaskImpl newTaskImpl1 =
        serviceLevelHandler.updatePrioPlannedDueOfTask(newTaskImpl, oldTaskImpl, false);

    // if no business process id is provided, use the id of the old task.
    if (newTaskImpl1.getBusinessProcessId() == null) {
      newTaskImpl1.setBusinessProcessId(oldTaskImpl.getBusinessProcessId());
    }

    // owner can only be changed if task is in state ready
    boolean isOwnerChanged = !Objects.equals(newTaskImpl1.getOwner(), oldTaskImpl.getOwner());
    if (isOwnerChanged && oldTaskImpl.getState() != TaskState.READY) {
      throw new InvalidTaskStateException(
          oldTaskImpl.getId(), oldTaskImpl.getState(), TaskState.READY);
    }
  }

  private void updateClassificationSummary(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws ClassificationNotFoundException {
    ClassificationSummary oldClassificationSummary = oldTaskImpl.getClassificationSummary();
    ClassificationSummary newClassificationSummary = newTaskImpl.getClassificationSummary();
    if (newClassificationSummary == null) {
      newClassificationSummary = oldClassificationSummary;
    }
    if (!oldClassificationSummary.getKey().equals(newClassificationSummary.getKey())) {
      Classification newClassification =
          this.classificationService.getClassification(
              newClassificationSummary.getKey(), newTaskImpl.getWorkbasketSummary().getDomain());
      newClassificationSummary = newClassification.asSummary();
      newTaskImpl.setClassificationSummary(newClassificationSummary);
    }
  }

  private void createTasksCompletedEvents(List<? extends TaskSummary> taskSummaries) {
    taskSummaries.forEach(
        task ->
            historyEventManager.createEvent(
                new TaskCompletedEvent(
                    IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
                    task,
                    taskanaEngine.getEngine().getCurrentUserContext().getUserid())));
  }
}
