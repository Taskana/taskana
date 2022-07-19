package pro.taskana.task.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.ObjectAttributeChangeDetector;
import pro.taskana.spi.history.api.events.task.TaskTransferredEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** The TaskTransferrer is responsible for the transfer of Tasks to another Workbasket. */
final class TaskTransferrer {

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
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
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
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    return transferSingleTask(taskId, destinationWorkbasket, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transfer(
      List<String> taskIds, String destinationWorkbasketId, boolean setTransferFlag)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidArgumentException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
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
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, setTransferFlag);
  }

  private Task transferSingleTask(
      String taskId, WorkbasketSummary destinationWorkbasket, boolean setTransferFlag)
      throws NotAuthorizedException, TaskNotFoundException, WorkbasketNotFoundException,
          InvalidStateException {
    try {
      taskanaEngine.openConnection();
      TaskImpl task = (TaskImpl) taskService.getTask(taskId);
      TaskImpl oldTask = task.copy();
      oldTask.setId(task.getId());
      oldTask.setExternalId(task.getExternalId());

      WorkbasketSummary originWorkbasket = task.getWorkbasketSummary();
      checkPreconditionsForTransferTask(task, destinationWorkbasket, originWorkbasket);

      applyTransferValuesForTask(task, destinationWorkbasket, setTransferFlag);
      taskMapper.update(task);
      if (historyEventManager.isEnabled()) {
        createTransferredEvent(
            oldTask, task, originWorkbasket.getId(), destinationWorkbasket.getId());
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
      throw new InvalidArgumentException("TaskIds must not be null or empty.");
    }
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    List<String> taskIds = new ArrayList<>(taskToBeTransferred);

    try {
      taskanaEngine.openConnection();

      List<TaskSummary> taskSummaries =
          taskanaEngine
              .getEngine()
              .runAsAdmin(
                  () -> taskService.createTaskQuery().idIn(taskIds.toArray(new String[0])).list());
      taskSummaries =
          filterOutTasksWhichDoNotMatchTransferCriteria(taskIds, taskSummaries, bulkLog);
      updateTransferableTasks(taskSummaries, destinationWorkbasket, setTransferFlag);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void checkPreconditionsForTransferTask(
      Task task, WorkbasketSummary destinationWorkbasket, WorkbasketSummary originWorkbasket)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidStateException {
    if (task.getState().isEndState()) {
      throw new InvalidTaskStateException(
          task.getId(), task.getState(), EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    }
    workbasketService.checkAuthorization(originWorkbasket.getId(), WorkbasketPermission.TRANSFER);
    checkDestinationWorkbasket(destinationWorkbasket);
  }

  private void checkDestinationWorkbasket(WorkbasketSummary destinationWorkbasket)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    workbasketService.checkAuthorization(
        destinationWorkbasket.getId(), WorkbasketPermission.APPEND);

    if (destinationWorkbasket.isMarkedForDeletion()) {
      throw new WorkbasketNotFoundException(destinationWorkbasket.getId());
    }
  }

  private List<TaskSummary> filterOutTasksWhichDoNotMatchTransferCriteria(
      List<String> taskIds,
      List<TaskSummary> taskSummaries,
      BulkOperationResults<String, TaskanaException> bulkLog) {

    Map<String, TaskSummary> taskIdToTaskSummary =
        taskSummaries.stream().collect(Collectors.toMap(TaskSummary::getId, Function.identity()));

    Set<String> workbasketIds = getSourceWorkbasketIdsWithTransferPermission(taskSummaries);

    List<TaskSummary> filteredOutTasks = new ArrayList<>(taskIds.size());

    for (String taskId : new HashSet<>(taskIds)) {
      TaskSummary taskSummary = taskIdToTaskSummary.get(taskId);
      Optional<TaskanaException> error =
          checkTaskForTransferCriteria(workbasketIds, taskId, taskSummary);
      if (error.isPresent()) {
        bulkLog.addError(taskId, error.get());
      } else {
        filteredOutTasks.add(taskSummary);
      }
    }
    return filteredOutTasks;
  }

  private Optional<TaskanaException> checkTaskForTransferCriteria(
      Set<String> sourceWorkbasketIds, String taskId, TaskSummary taskSummary) {
    TaskanaException error = null;
    if (taskId == null || taskId.isEmpty()) {
      error = new InvalidArgumentException("TaskId should not be null or empty");
    } else if (taskSummary == null) {
      error = new TaskNotFoundException(taskId);
    } else if (taskSummary.getState().isEndState()) {
      error =
          new InvalidTaskStateException(
              taskId, taskSummary.getState(), EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    } else if (!sourceWorkbasketIds.contains(taskSummary.getWorkbasketSummary().getId())) {
      error =
          new MismatchedWorkbasketPermissionException(
              taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
              taskSummary.getWorkbasketSummary().getId(),
              WorkbasketPermission.TRANSFER);
    }
    return Optional.ofNullable(error);
  }

  private Set<String> getSourceWorkbasketIdsWithTransferPermission(
      List<TaskSummary> taskSummaries) {
    if (taskSummaries.isEmpty()) {
      return Collections.emptySet();
    }

    WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
    query.setUsedToAugmentTasks(true);
    String[] workbasketIds =
        taskSummaries.stream()
            .map(TaskSummary::getWorkbasketSummary)
            .map(WorkbasketSummary::getId)
            .distinct()
            .toArray(String[]::new);

    List<WorkbasketSummary> sourceWorkbaskets =
        query.callerHasPermission(WorkbasketPermission.TRANSFER).idIn(workbasketIds).list();
    return sourceWorkbaskets.stream().map(WorkbasketSummary::getId).collect(Collectors.toSet());
  }

  private void updateTransferableTasks(
      List<TaskSummary> taskSummaries,
      WorkbasketSummary destinationWorkbasket,
      boolean setTransferFlag) {
    if (!taskSummaries.isEmpty()) {
      TaskImpl updateObject = new TaskImpl();
      applyTransferValuesForTask(updateObject, destinationWorkbasket, setTransferFlag);
      taskMapper.updateTransfered(
          taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toSet()), updateObject);

      if (historyEventManager.isEnabled()) {
        taskSummaries.forEach(
            oldSummary -> {
              TaskSummaryImpl newSummary = (TaskSummaryImpl) oldSummary.copy();
              newSummary.setId(oldSummary.getId());
              newSummary.setExternalId(oldSummary.getExternalId());
              applyTransferValuesForTask(newSummary, destinationWorkbasket, setTransferFlag);

              createTransferredEvent(
                  oldSummary,
                  newSummary,
                  oldSummary.getWorkbasketSummary().getId(),
                  newSummary.getWorkbasketSummary().getId());
            });
      }
    }
  }

  private void applyTransferValuesForTask(
      TaskSummaryImpl task, WorkbasketSummary workbasket, boolean setTransferFlag) {
    task.setRead(false);
    task.setTransferred(setTransferFlag);
    task.setState(TaskState.READY);
    task.setOwner(null);
    task.setWorkbasketSummary(workbasket);
    task.setDomain(workbasket.getDomain());
    task.setModified(Instant.now());
  }

  private void createTransferredEvent(
      TaskSummary oldTask,
      TaskSummary newTask,
      String originWorkbasketId,
      String destinationWorkbasketId) {
    String details = ObjectAttributeChangeDetector.determineChangesInAttributes(oldTask, newTask);
    historyEventManager.createEvent(
        new TaskTransferredEvent(
            IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
            newTask,
            originWorkbasketId,
            destinationWorkbasketId,
            taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
            details));
  }
}
