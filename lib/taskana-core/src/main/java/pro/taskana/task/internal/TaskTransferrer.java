package pro.taskana.task.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.spi.history.api.events.task.TaskTransferredEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** This class is responsible for the transfer of tasks. */
public class TaskTransferrer {

  private static final String WAS_NOT_FOUND2 = " was not found.";
  private static final String TASK_IN_END_STATE_WITH_ID_CANNOT_BE_TRANSFERRED =
      "Task in end state with id %s cannot be transferred.";
  private static final String TASK_WITH_ID = "Task with id ";
  private static final String WAS_MARKED_FOR_DELETION = " was marked for deletion";
  private static final String THE_WORKBASKET = "The workbasket ";
  private static final String ID_PREFIX_HISTORY_EVENT = "HEI";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskTransferrer.class);
  private InternalTaskanaEngine taskanaEngine;
  private WorkbasketService workbasketService;
  private TaskServiceImpl taskService;
  private TaskMapper taskMapper;
  private HistoryEventManager historyEventManager;

  TaskTransferrer(
      InternalTaskanaEngine taskanaEngine, TaskMapper taskMapper, TaskServiceImpl taskService) {
    super();
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.taskMapper = taskMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
    this.historyEventManager = taskanaEngine.getHistoryEventManager();
  }

  Task transfer(String taskId, String destinationWorkbasketKey, String domain)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    LOGGER.debug(
        "entry to transfer(taskId = {}, destinationWorkbasketKey = {}, domain = {})",
        taskId,
        destinationWorkbasketKey,
        domain);
    TaskImpl task = null;
    WorkbasketSummary oldWorkbasketSummary = null;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) taskService.getTask(taskId);

      if (task.getState().isEndState()) {
        throw new InvalidStateException(
            String.format(TASK_IN_END_STATE_WITH_ID_CANNOT_BE_TRANSFERRED, task.getId()));
      }

      // Save previous workbasket id before transfer it.
      oldWorkbasketSummary = task.getWorkbasketSummary();

      // transfer requires TRANSFER in source and APPEND on destination workbasket
      workbasketService.checkAuthorization(
          destinationWorkbasketKey, domain, WorkbasketPermission.APPEND);
      workbasketService.checkAuthorization(
          task.getWorkbasketSummary().getId(), WorkbasketPermission.TRANSFER);

      Workbasket destinationWorkbasket =
          workbasketService.getWorkbasket(destinationWorkbasketKey, domain);

      // reset read flag and set transferred flag
      task.setRead(false);
      task.setTransferred(true);

      // transfer task from source to destination workbasket
      if (!destinationWorkbasket.isMarkedForDeletion()) {
        task.setWorkbasketSummary(destinationWorkbasket.asSummary());
      } else {
        throw new WorkbasketNotFoundException(
            destinationWorkbasket.getId(),
            THE_WORKBASKET + destinationWorkbasket.getId() + WAS_MARKED_FOR_DELETION);
      }

      task.setModified(Instant.now());
      task.setState(TaskState.READY);
      task.setOwner(null);
      taskMapper.update(task);
      LOGGER.debug(
          "Method transfer() transferred Task '{}' to destination workbasket {}",
          taskId,
          destinationWorkbasket.getId());
      if (HistoryEventManager.isHistoryEnabled()) {
        createTaskTransferredEvent(task, oldWorkbasketSummary, destinationWorkbasket.asSummary());
      }
      return task;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from transfer(). Returning result {} ", task);
    }
  }

  Task transfer(String taskId, String destinationWorkbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    LOGGER.debug(
        "entry to transfer(taskId = {}, destinationWorkbasketId = {})",
        taskId,
        destinationWorkbasketId);
    TaskImpl task = null;
    WorkbasketSummary oldWorkbasketSummary = null;
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) taskService.getTask(taskId);

      if (task.getState().isEndState()) {
        throw new InvalidStateException(
            String.format(TASK_IN_END_STATE_WITH_ID_CANNOT_BE_TRANSFERRED, task.getId()));
      }
      oldWorkbasketSummary = task.getWorkbasketSummary();

      // transfer requires TRANSFER in source and APPEND on destination workbasket
      workbasketService.checkAuthorization(destinationWorkbasketId, WorkbasketPermission.APPEND);
      workbasketService.checkAuthorization(
          task.getWorkbasketSummary().getId(), WorkbasketPermission.TRANSFER);

      Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketId);

      // reset read flag and set transferred flag
      task.setRead(false);
      task.setTransferred(true);

      // transfer task from source to destination workbasket
      if (!destinationWorkbasket.isMarkedForDeletion()) {
        task.setWorkbasketSummary(destinationWorkbasket.asSummary());
      } else {
        throw new WorkbasketNotFoundException(
            destinationWorkbasket.getId(),
            THE_WORKBASKET + destinationWorkbasket.getId() + WAS_MARKED_FOR_DELETION);
      }

      task.setModified(Instant.now());
      task.setState(TaskState.READY);
      task.setOwner(null);
      taskMapper.update(task);
      LOGGER.debug(
          "Method transfer() transferred Task '{}' to destination workbasket {}",
          taskId,
          destinationWorkbasketId);
      if (HistoryEventManager.isHistoryEnabled()) {
        createTaskTransferredEvent(task, oldWorkbasketSummary, destinationWorkbasket.asSummary());
      }
      return task;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from transfer(). Returning result {} ", task);
    }
  }

  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey, String destinationWorkbasketDomain, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    try {
      taskanaEngine.openConnection();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "entry to transferTasks(targetWbKey = {}, domain = {}, taskIds = {})",
            destinationWorkbasketKey,
            destinationWorkbasketDomain,
            taskIds);
      }

      // Check pre-conditions with trowing Exceptions
      if (destinationWorkbasketKey == null || destinationWorkbasketDomain == null) {
        throw new InvalidArgumentException(
            "DestinationWorkbasketKey or domain can´t be used as NULL-Parameter.");
      }
      Workbasket destinationWorkbasket =
          workbasketService.getWorkbasket(destinationWorkbasketKey, destinationWorkbasketDomain);

      return transferTasks(taskIds, destinationWorkbasket);
    } finally {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "exit from transferTasks(targetWbKey = {}, targetWbDomain = {}, "
                + "destination taskIds = {})",
            destinationWorkbasketKey,
            destinationWorkbasketDomain,
            taskIds);
      }

      taskanaEngine.returnConnection();
    }
  }

  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    try {
      taskanaEngine.openConnection();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "entry to transferTasks(targetWbId = {}, taskIds = {})",
            destinationWorkbasketId,
            taskIds);
      }

      // Check pre-conditions with trowing Exceptions
      if (destinationWorkbasketId == null || destinationWorkbasketId.isEmpty()) {
        throw new InvalidArgumentException("DestinationWorkbasketId must not be null or empty.");
      }
      Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketId);

      return transferTasks(taskIds, destinationWorkbasket);
    } finally {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "exit from transferTasks(targetWbKey = {}, taskIds = {})",
            destinationWorkbasketId,
            taskIds);
      }

      taskanaEngine.returnConnection();
    }
  }

  private BulkOperationResults<String, TaskanaException> transferTasks(
      List<String> taskIdsToBeTransferred, Workbasket destinationWorkbasket)
      throws InvalidArgumentException, WorkbasketNotFoundException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to transferTasks(taskIdsToBeTransferred = {}, destinationWorkbasket = {})",
          taskIdsToBeTransferred,
          destinationWorkbasket);
    }

    workbasketService.checkAuthorization(
        destinationWorkbasket.getId(), WorkbasketPermission.APPEND);

    if (taskIdsToBeTransferred == null) {
      throw new InvalidArgumentException("TaskIds must not be null.");
    }
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    List<String> taskIds = new ArrayList<>(taskIdsToBeTransferred);
    taskService.removeNonExistingTasksFromTaskIdList(taskIds, bulkLog);

    if (taskIds.isEmpty()) {
      throw new InvalidArgumentException("TaskIds must not contain only invalid arguments.");
    }

    List<MinimalTaskSummary> taskSummaries;
    if (taskIds.isEmpty()) {
      taskSummaries = new ArrayList<>();
    } else {
      taskSummaries = taskMapper.findExistingTasks(taskIds, null);
    }
    checkIfTransferConditionsAreFulfilled(taskIds, taskSummaries, bulkLog);
    updateTasksToBeTransferred(taskIds, taskSummaries, destinationWorkbasket);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("exit from transferTasks(), returning {}", bulkLog);
    }

    return bulkLog;
  }

  private void checkIfTransferConditionsAreFulfilled(
      List<String> taskIds,
      List<MinimalTaskSummary> taskSummaries,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to checkIfTransferConditionsAreFulfilled(taskIds = {}, "
              + "taskSummaries = {}, bulkLog = {})",
          taskIds,
          taskSummaries,
          bulkLog);
    }

    Set<String> workbasketIds = new HashSet<>();
    taskSummaries.forEach(t -> workbasketIds.add(t.getWorkbasketId()));
    WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
    query.setUsedToAugmentTasks(true);
    List<WorkbasketSummary> sourceWorkbaskets;
    if (taskSummaries.isEmpty()) {
      sourceWorkbaskets = new ArrayList<>();
    } else {
      sourceWorkbaskets =
          query
              .callerHasPermission(WorkbasketPermission.TRANSFER)
              .idIn(workbasketIds.toArray(new String[0]))
              .list();
    }
    checkIfTasksMatchTransferCriteria(taskIds, taskSummaries, sourceWorkbaskets, bulkLog);
    LOGGER.debug("exit from checkIfTransferConditionsAreFulfilled()");
  }

  private void checkIfTasksMatchTransferCriteria(
      List<String> taskIds,
      List<MinimalTaskSummary> taskSummaries,
      List<WorkbasketSummary> sourceWorkbaskets,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to checkIfTasksMatchTransferCriteria(taskIds = {}, taskSummaries = {}, "
              + "sourceWorkbaskets = {}, bulkLog = {})",
          taskIds,
          taskSummaries,
          sourceWorkbaskets,
          bulkLog);
    }

    Iterator<String> taskIdIterator = taskIds.iterator();
    while (taskIdIterator.hasNext()) {
      String currentTaskId = taskIdIterator.next();
      MinimalTaskSummary taskSummary =
          taskSummaries.stream()
              .filter(t -> currentTaskId.equals(t.getTaskId()))
              .findFirst()
              .orElse(null);
      if (taskSummary == null) {
        bulkLog.addError(
            currentTaskId,
            new TaskNotFoundException(
                currentTaskId, TASK_WITH_ID + currentTaskId + WAS_NOT_FOUND2));
        taskIdIterator.remove();
      } else if (taskSummary.getTaskState().isEndState()) {
        bulkLog.addError(
            currentTaskId,
            new InvalidStateException(
                String.format(TASK_IN_END_STATE_WITH_ID_CANNOT_BE_TRANSFERRED, currentTaskId)));
        taskIdIterator.remove();
      } else if (sourceWorkbaskets.stream()
          .noneMatch(wb -> taskSummary.getWorkbasketId().equals(wb.getId()))) {
        bulkLog.addError(
            currentTaskId,
            new NotAuthorizedException(
                "The workbasket of this task got not TRANSFER permissions. TaskId=" + currentTaskId,
                CurrentUserContext.getUserid()));
        taskIdIterator.remove();
      }
    }
    LOGGER.debug("exit from checkIfTasksMatchTransferCriteria()");
  }

  private void createTaskTransferredEvent(
      Task task, WorkbasketSummary oldWorkbasketSummary, WorkbasketSummary newWorkbasketSummary) {
    historyEventManager.createEvent(
        new TaskTransferredEvent(
            IdGenerator.generateWithPrefix(ID_PREFIX_HISTORY_EVENT),
            task,
            oldWorkbasketSummary,
            newWorkbasketSummary,
            CurrentUserContext.getUserid()));
  }

  private void updateTasksToBeTransferred(
      List<String> taskIds,
      List<MinimalTaskSummary> taskSummaries,
      Workbasket destinationWorkbasket) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to updateTasksToBeTransferred(taskIds = {}, taskSummaries = {}, "
              + "destinationWorkbasket = {})",
          taskIds,
          taskSummaries,
          destinationWorkbasket.getId());
    }

    taskSummaries =
        taskSummaries.stream()
            .filter(ts -> taskIds.contains(ts.getTaskId()))
            .collect(Collectors.toList());
    if (!taskSummaries.isEmpty()) {
      Instant now = Instant.now();
      TaskSummaryImpl updateObject = new TaskSummaryImpl();
      updateObject.setRead(false);
      updateObject.setTransferred(true);
      updateObject.setWorkbasketSummary(destinationWorkbasket.asSummary());
      updateObject.setDomain(destinationWorkbasket.getDomain());
      updateObject.setModified(now);
      updateObject.setState(TaskState.READY);
      updateObject.setOwner(null);
      taskMapper.updateTransfered(taskIds, updateObject);
      if (HistoryEventManager.isHistoryEnabled()) {
        createTasksTransferredEvents(taskSummaries, updateObject);
      }
    }
    LOGGER.debug("exit from updateTasksToBeTransferred()");
  }

  private void createTasksTransferredEvents(
      List<MinimalTaskSummary> taskSummaries, TaskSummaryImpl updateObject) {
    taskSummaries.stream()
        .forEach(
            task -> {
              TaskImpl newTask = (TaskImpl) taskService.newTask(task.getWorkbasketId());
              newTask.setWorkbasketSummary(updateObject.getWorkbasketSummary());
              newTask.setRead(updateObject.isRead());
              newTask.setTransferred(updateObject.isTransferred());
              newTask.setWorkbasketSummary(updateObject.getWorkbasketSummary());
              newTask.setDomain(updateObject.getDomain());
              newTask.setModified(updateObject.getModified());
              newTask.setState(updateObject.getState());
              newTask.setOwner(updateObject.getOwner());
              createTaskTransferredEvent(
                  newTask, newTask.getWorkbasketSummary(), updateObject.getWorkbasketSummary());
            });
  }
}
