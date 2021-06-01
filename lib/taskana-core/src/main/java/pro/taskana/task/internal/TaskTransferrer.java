package pro.taskana.task.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.spi.history.api.events.task.TaskTransferredEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** This class is responsible for the transfer of Tasks to another Workbasket. */
final class TaskTransferrer {

  private static final String TASK_ID_LIST_NULL_OR_EMPTY = "TaskIds must not be null or empty.";
  private static final String TASK_IN_END_STATE =
      "Task '%s' is in end state and cannot be transferred.";
  private static final String TASK_NOT_FOUND = "Task '%s' was not found.";
  private static final String WORKBASKET_MARKED_FOR_DELETION =
      "Workbasket '%s' was marked for deletion.";
  private static final String WORKBASKET_WITHOUT_TRANSFER_PERMISSION =
      "Workbasket of Task '%s' got no TRANSFER permission.";
  private final InternalTaskanaEngine taskanaEngine;
  private final WorkbasketService workbasketService;
  private final TaskServiceImpl taskService;
  private final TaskMapper taskMapper;
  private final HistoryEventManager historyEventManager;

  TaskTransferrer(
      InternalTaskanaEngine taskanaEngine, TaskMapper taskMapper, TaskServiceImpl taskService) {
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.taskMapper = taskMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
    this.historyEventManager = taskanaEngine.getHistoryEventManager();
  }

  Task transfer(String taskId, String destinationWorkbasketId, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId);
    return transferSingleTask(taskId, destinationWorkbasket, setTransferFlag);
  }

  Task transfer(
      String taskId,
      String destinationWorkbasketKey,
      String destinationDomain,
      boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain);
    return transferSingleTask(taskId, destinationWorkbasket, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transfer(
      List<String> taskIds, String destinationWorkbasketId, boolean setTransferFlag)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidArgumentException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId);
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transfer(
      List<String> taskIds,
      String destinationWorkbasketKey,
      String destinationDomain,
      boolean setTransferFlag)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidArgumentException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain);
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, setTransferFlag);
  }

  private Task transferSingleTask(
      String taskId, WorkbasketSummary destinationWorkbasket, boolean setTransferFlag)
      throws NotAuthorizedException, TaskNotFoundException, WorkbasketNotFoundException,
          InvalidStateException {
    TaskImpl task = new TaskImpl();
    try {
      taskanaEngine.openConnection();
      task = (TaskImpl) taskService.getTask(taskId);
      WorkbasketSummary originWorkbasket = task.getWorkbasketSummary();
      checkPreconditionsForTransferTask(task, destinationWorkbasket, originWorkbasket);

      modifyTaskParameters(task, destinationWorkbasket, setTransferFlag);
      taskMapper.update(task);
      if (HistoryEventManager.isHistoryEnabled()) {
        createTransferredEvent(task, originWorkbasket.getId(), destinationWorkbasket.getId());
      }

      return task;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private BulkOperationResults<String, TaskanaException> transferMultipleTasks(
      List<String> taskToBeTransferred,
      WorkbasketSummary destinationWorkbasket,
      boolean setTransferFlag)
      throws InvalidArgumentException {
    if (taskToBeTransferred == null || taskToBeTransferred.isEmpty()) {
      throw new InvalidArgumentException(TASK_ID_LIST_NULL_OR_EMPTY);
    }
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    List<String> taskIds = new ArrayList<>(taskToBeTransferred);

    try {
      taskanaEngine.openConnection();

      List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(taskIds, null);
      removeNotTransferableTasks(taskIds, taskSummaries, bulkLog);
      updateTransferableTasks(taskIds, taskSummaries, destinationWorkbasket, setTransferFlag);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void checkPreconditionsForTransferTask(
      Task task, WorkbasketSummary destinationWorkbasket, WorkbasketSummary originWorkbasket)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidStateException {
    if (task.getState().isEndState()) {
      throw new InvalidStateException(String.format(TASK_IN_END_STATE, task.getId()));
    }
    workbasketService.checkAuthorization(originWorkbasket.getId(), WorkbasketPermission.TRANSFER);
    checkDestinationWorkbasket(destinationWorkbasket);
  }

  private void checkDestinationWorkbasket(WorkbasketSummary destinationWorkbasket)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    workbasketService.checkAuthorization(
        destinationWorkbasket.getId(), WorkbasketPermission.APPEND);

    if (destinationWorkbasket.isMarkedForDeletion()) {
      throw new WorkbasketNotFoundException(
          destinationWorkbasket.getId(),
          String.format(WORKBASKET_MARKED_FOR_DELETION, destinationWorkbasket.getId()));
    }
  }

  private void removeNotTransferableTasks(
      List<String> taskIds,
      List<MinimalTaskSummary> taskSummaries,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    List<WorkbasketSummary> sourceWorkbaskets =
        getSourceWorkbasketsWithTransferPermission(taskSummaries);

    taskIds.removeIf(id -> !taskIsTransferable(id, taskSummaries, sourceWorkbaskets, bulkLog));
    taskSummaries.removeIf(task -> !taskIds.contains(task.getTaskId()));
  }

  private List<WorkbasketSummary> getSourceWorkbasketsWithTransferPermission(
      List<MinimalTaskSummary> taskSummaries) {
    Set<String> workbasketIds =
        taskSummaries.stream().map(MinimalTaskSummary::getWorkbasketId).collect(Collectors.toSet());

    WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
    query.setUsedToAugmentTasks(true);

    List<WorkbasketSummary> sourceWorkbaskets = new ArrayList<>();
    if (!workbasketIds.isEmpty()) {
      sourceWorkbaskets.addAll(
          query
              .callerHasPermission(WorkbasketPermission.TRANSFER)
              .idIn(workbasketIds.toArray(new String[0]))
              .list());
    }
    return sourceWorkbaskets;
  }

  private boolean taskIsTransferable(
      String currentTaskId,
      List<MinimalTaskSummary> taskSummaries,
      List<WorkbasketSummary> sourceWorkbaskets,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    if (currentTaskId == null || currentTaskId.isEmpty()) {
      return false;
    }
    MinimalTaskSummary currentTaskSummary =
        taskSummaries.stream()
            .filter(t -> currentTaskId.equals(t.getTaskId()))
            .findFirst()
            .orElse(null);

    if (currentTaskSummary == null) {
      bulkLog.addError(
          currentTaskId,
          new TaskNotFoundException(currentTaskId, String.format(TASK_NOT_FOUND, currentTaskId)));
      return false;
    } else if (currentTaskSummary.getTaskState().isEndState()) {
      bulkLog.addError(
          currentTaskId,
          new InvalidStateException(String.format(TASK_IN_END_STATE, currentTaskId)));
      return false;
    } else if (sourceWorkbaskets.stream()
        .noneMatch(wb -> currentTaskSummary.getWorkbasketId().equals(wb.getId()))) {
      bulkLog.addError(
          currentTaskId,
          new NotAuthorizedException(
              String.format(WORKBASKET_WITHOUT_TRANSFER_PERMISSION, currentTaskId),
              taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
      return false;
    }
    return true;
  }

  private void updateTransferableTasks(
      List<String> taskIds,
      List<MinimalTaskSummary> taskSummaries,
      WorkbasketSummary destinationWorkbasket,
      boolean setTransferFlag) {
    if (!taskIds.isEmpty()) {
      TaskImpl updateObject = new TaskImpl();
      modifyTaskParameters(updateObject, destinationWorkbasket, setTransferFlag);
      taskMapper.updateTransfered(taskIds, updateObject);

      if (HistoryEventManager.isHistoryEnabled()) {
        taskSummaries.forEach(
            task -> {
              updateObject.setId(task.getTaskId());
              createTransferredEvent(
                  updateObject,
                  task.getWorkbasketId(),
                  updateObject.getWorkbasketSummary().getId());
            });
      }
    }
  }

  private void modifyTaskParameters(
      TaskImpl task, WorkbasketSummary workbasket, boolean setTransferFlag) {
    task.setRead(false);
    task.setTransferred(setTransferFlag);
    task.setState(TaskState.READY);
    task.setOwner(null);
    task.setWorkbasketSummary(workbasket);
    task.setDomain(workbasket.getDomain());
    task.setModified(Instant.now());
  }

  private void createTransferredEvent(
      Task task, String originWorkbasketId, String destinationWorkbasketId) {
    historyEventManager.createEvent(
        new TaskTransferredEvent(
            IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
            task,
            originWorkbasketId,
            destinationWorkbasketId,
            taskanaEngine.getEngine().getCurrentUserContext().getUserid()));
  }
}
