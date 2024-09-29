package pro.taskana.task.internal;

import static java.util.Map.entry;

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
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.ObjectAttributeChangeDetector;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.spi.history.api.events.task.TaskReroutedEvent;
import pro.taskana.spi.history.api.events.task.TaskTransferredEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** This class is responsible for the transfer of Tasks to another Workbasket. */
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
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
    return transferSingleTask(taskId, destinationWorkbasket, null, setTransferFlag);
  }

  Task transfer(
      String taskId,
      String destinationWorkbasketKey,
      String destinationDomain,
      boolean setTransferFlag)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    return transferSingleTask(taskId, destinationWorkbasket, null, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transfer(
      List<String> taskIds, String destinationWorkbasketId, boolean setTransferFlag)
      throws WorkbasketNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, null, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transfer(
      List<String> taskIds,
      String destinationWorkbasketKey,
      String destinationDomain,
      boolean setTransferFlag)
      throws WorkbasketNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, null, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transferWithOwner(
      List<String> taskIds, String destinationWorkbasketId, String owner, boolean setTransferFlag)
      throws WorkbasketNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, owner, setTransferFlag);
  }

  BulkOperationResults<String, TaskanaException> transferWithOwner(
      List<String> taskIds,
      String destinationWorkbasketKey,
      String destinationDomain,
      String owner,
      boolean setTransferFlag)
      throws WorkbasketNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    checkDestinationWorkbasket(destinationWorkbasket);

    return transferMultipleTasks(taskIds, destinationWorkbasket, owner, setTransferFlag);
  }

  Task transferWithOwner(
      String taskId, String destinationWorkbasketId, String owner, boolean setTransferFlag)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketId).asSummary();
    return transferSingleTask(taskId, destinationWorkbasket, owner, setTransferFlag);
  }

  Task transferWithOwner(
      String taskId,
      String destinationWorkbasketKey,
      String destinationDomain,
      String owner,
      boolean setTransferFlag)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    WorkbasketSummary destinationWorkbasket =
        workbasketService.getWorkbasket(destinationWorkbasketKey, destinationDomain).asSummary();
    return transferSingleTask(taskId, destinationWorkbasket, owner, setTransferFlag);
  }

  Task rerouteTask(String taskId)
      throws NotAuthorizedOnWorkbasketException,
          TaskNotFoundException,
          WorkbasketNotFoundException,
          InvalidTaskStateException {
    TaskImpl task = (TaskImpl) taskService.getTask(taskId);
    WorkbasketSummary originWorkbasket = task.getWorkbasketSummary();
    String newWorkbasketId = taskanaEngine.getTaskRoutingManager().determineWorkbasketId(task);

    if (!originWorkbasket.getId().equals(newWorkbasketId)) {
      try {
        taskanaEngine.openConnection();
        TaskImpl oldTask = task.copy();
        oldTask.setId(task.getId());
        oldTask.setExternalId(task.getExternalId());
        WorkbasketSummary destinationWorkbasket =
            workbasketService.getWorkbasket(newWorkbasketId).asSummary();

        checkPreconditionsForTransferTask(task, destinationWorkbasket, originWorkbasket);
        applyTransferValuesForTask(task, destinationWorkbasket, true);
        taskMapper.update(task);
        if (historyEventManager.isEnabled()) {
          createReroutedEvent(
              oldTask, task, originWorkbasket.getId(), destinationWorkbasket.getId());
        }
        return task;
      } finally {
        taskanaEngine.returnConnection();
      }
    }
    return task;
  }

  BulkOperationResults<String, TaskanaException> rerouteTasks(List<String> taskIds) {

    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    List<TaskSummary> taskSummaries = filterNotExistingTaskIds(taskIds, bulkLog);

    List<String> workbasketIds =
        taskSummaries.stream()
            .map(TaskSummary::asTask)
            .map(task -> taskanaEngine.getTaskRoutingManager().determineWorkbasketId(task))
            .collect(Collectors.toList());

    filterOutTasksWhichNotNeededToBeTransferred(taskSummaries, workbasketIds);

    List<String> taskIdsToBeTransferred =
        taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());
    filterOutTasksWhichDoNotMatchRerouteCriteria(
        taskIdsToBeTransferred, taskSummaries, workbasketIds, bulkLog);
    List<WorkbasketSummary> destinationWorkbaskets =
        filterOutDestinationWbsWhichDoNotMatchTransferCriteria(
            taskSummaries, workbasketIds, bulkLog);
    try {
      taskanaEngine.openConnection();
      updateReroutableTasksWithDifferentWorkbaskets(taskSummaries, destinationWorkbaskets, true);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private Task transferSingleTask(
      String taskId, WorkbasketSummary destinationWorkbasket, String owner, boolean setTransferFlag)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    try {
      taskanaEngine.openConnection();
      TaskImpl task = (TaskImpl) taskService.getTask(taskId);
      TaskImpl oldTask = task.copy();
      oldTask.setId(task.getId());
      oldTask.setExternalId(task.getExternalId());

      WorkbasketSummary originWorkbasket = task.getWorkbasketSummary();
      checkPreconditionsForTransferTask(task, destinationWorkbasket, originWorkbasket);

      applyTransferValuesForTask(task, destinationWorkbasket, owner, setTransferFlag);
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
      String owner,
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
      updateTransferableTasks(taskSummaries, destinationWorkbasket, owner, setTransferFlag);

      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void checkPreconditionsForTransferTask(
      Task task, WorkbasketSummary destinationWorkbasket, WorkbasketSummary originWorkbasket)
      throws WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    if (task.getState().isEndState()) {
      throw new InvalidTaskStateException(
          task.getId(), task.getState(), EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    }
    workbasketService.checkAuthorization(originWorkbasket.getId(), WorkbasketPermission.TRANSFER);
    checkDestinationWorkbasket(destinationWorkbasket);
  }

  private void checkDestinationWorkbasket(WorkbasketSummary destinationWorkbasket)
      throws WorkbasketNotFoundException, NotAuthorizedOnWorkbasketException {
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

  private void filterOutTasksWhichDoNotMatchRerouteCriteria(
      List<String> taskIds,
      List<TaskSummary> taskSummaries,
      List<String> workbasketIds,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    Map<String, TaskSummary> taskIdToTaskSummary =
        taskSummaries.stream().collect(Collectors.toMap(TaskSummary::getId, Function.identity()));

    Set<String> sourceWorkbasketIds = getSourceWorkbasketIdsWithTransferPermission(taskSummaries);

    for (String taskId : new HashSet<>(taskIds)) {
      TaskSummary taskSummary = taskIdToTaskSummary.get(taskId);
      Optional<TaskanaException> error =
          checkTaskForTransferCriteria(sourceWorkbasketIds, taskId, taskSummary);
      if (error.isPresent()) {
        bulkLog.addError(taskId, error.get());
        for (int i = 0; i < taskSummaries.size(); i++) {
          if (taskSummaries.get(i).getId().equals(taskId)) {
            taskSummaries.remove(i);
            workbasketIds.remove(i);
            break;
          }
        }
      }
    }
  }

  private Optional<TaskanaException> checkTaskForTransferCriteria(
      Set<String> sourceWorkbasketIds, String taskId, TaskSummary taskSummary) {
    TaskanaException error = null;
    if (taskId == null || taskId.isEmpty()) {
      error = new TaskNotFoundException(null);
    } else if (taskSummary == null) {
      error = new TaskNotFoundException(taskId);
    } else if (taskSummary.getState().isEndState()) {
      error =
          new InvalidTaskStateException(
              taskId, taskSummary.getState(), EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    } else if (!sourceWorkbasketIds.contains(taskSummary.getWorkbasketSummary().getId())) {
      error =
          new NotAuthorizedOnWorkbasketException(
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
        query.callerHasPermissions(WorkbasketPermission.TRANSFER).idIn(workbasketIds).list();
    return sourceWorkbaskets.stream().map(WorkbasketSummary::getId).collect(Collectors.toSet());
  }

  private void updateTransferableTasks(
      List<TaskSummary> taskSummaries,
      WorkbasketSummary destinationWorkbasket,
      String owner,
      boolean setTransferFlag) {
    Map<TaskState, List<TaskSummary>> summariesByState = groupTasksByState(taskSummaries);
    for (Map.Entry<TaskState, List<TaskSummary>> entry : summariesByState.entrySet()) {
      TaskState goalState = entry.getKey();
      List<TaskSummary> taskSummariesWithSameGoalState = entry.getValue();
      if (!taskSummariesWithSameGoalState.isEmpty()) {
        TaskImpl updateObject = new TaskImpl();
        updateObject.setState(goalState);
        applyTransferValuesForTask(updateObject, destinationWorkbasket, owner, setTransferFlag);
        taskMapper.updateTransfered(
            taskSummariesWithSameGoalState.stream()
                .map(TaskSummary::getId)
                .collect(Collectors.toSet()),
            updateObject);

        if (historyEventManager.isEnabled()) {
          taskSummaries.forEach(
              oldSummary -> {
                TaskSummaryImpl newSummary = (TaskSummaryImpl) oldSummary.copy();
                newSummary.setId(oldSummary.getId());
                newSummary.setExternalId(oldSummary.getExternalId());
                applyTransferValuesForTask(
                    newSummary, destinationWorkbasket, owner, setTransferFlag);

                createTransferredEvent(
                    oldSummary,
                    newSummary,
                    oldSummary.getWorkbasketSummary().getId(),
                    newSummary.getWorkbasketSummary().getId());
              });
        }
      }
    }
  }

  private void applyTransferValuesForTask(
      TaskSummaryImpl task, WorkbasketSummary workbasket, String owner, boolean setTransferFlag) {
    task.setRead(false);
    task.setTransferred(setTransferFlag);
    task.setState(getStateAfterTransfer(task));
    task.setOwner(owner);
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

  private void createReroutedEvent(
      TaskSummary oldTask,
      TaskSummary newTask,
      String originWorkbasketId,
      String destinationWorkbasketId) {
    String details = ObjectAttributeChangeDetector.determineChangesInAttributes(oldTask, newTask);
    historyEventManager.createEvent(
        new TaskReroutedEvent(
            IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT),
            newTask,
            originWorkbasketId,
            destinationWorkbasketId,
            taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
            details));
  }

  private TaskState getStateAfterTransfer(TaskSummary taskSummary) {
    TaskState stateBeforeTransfer = taskSummary.getState();
    if (stateBeforeTransfer.equals(TaskState.CLAIMED)) {
      return TaskState.READY;
    }
    if (stateBeforeTransfer.equals(TaskState.IN_REVIEW)) {
      return TaskState.READY_FOR_REVIEW;
    } else {
      return stateBeforeTransfer;
    }
  }

  private Map<TaskState, List<TaskSummary>> groupTasksByState(List<TaskSummary> taskSummaries) {
    Map<TaskState, List<TaskSummary>> result =
        Map.ofEntries(
            entry((TaskState.READY), new ArrayList<>()),
            entry((TaskState.READY_FOR_REVIEW), new ArrayList<>()));
    for (TaskSummary taskSummary : taskSummaries) {
      List<TaskSummary> relevantSummaries = result.get(getStateAfterTransfer(taskSummary));
      relevantSummaries.add(taskSummary);
    }
    return result;
  }

  private void filterOutTasksWhichNotNeededToBeTransferred(
      List<TaskSummary> taskSummaries, List<String> workbasketIds) {
    for (int i = taskSummaries.size() - 1; i >= 0; i--) {
      TaskSummary taskSummary = taskSummaries.get(i);
      String workbasketId = workbasketIds.get(i);
      if (taskSummary.getWorkbasketSummary().getId().equals(workbasketId)) {
        taskSummaries.remove(i);
        workbasketIds.remove(i);
      }
    }
  }

  private List<WorkbasketSummary> filterOutDestinationWbsWhichDoNotMatchTransferCriteria(
      List<TaskSummary> taskSummaries,
      List<String> workbasketIds,
      BulkOperationResults<String, TaskanaException> bulkLog) {
    List<WorkbasketSummary> wbsWithAppendPerm =
        workbasketService
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.APPEND)
            .idIn(workbasketIds.toArray(new String[0]))
            .list();
    List<WorkbasketSummary> destinationWbSummaries = new ArrayList<>();
    for (int i = taskSummaries.size() - 1; i >= 0; i--) {
      String workbasketId = workbasketIds.get(i);
      WorkbasketSummary matchingSummary =
          wbsWithAppendPerm.stream()
              .filter(summary -> workbasketId.equals(summary.getId()))
              .findFirst()
              .orElse(null);
      if (matchingSummary == null) {
        bulkLog.addError(
            taskSummaries.get(i).getId(),
            new NotAuthorizedOnWorkbasketException(
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                workbasketId,
                WorkbasketPermission.APPEND));
        workbasketIds.remove(i);
        taskSummaries.remove(i);
      } else if (matchingSummary.isMarkedForDeletion()) {
        bulkLog.addError(
            taskSummaries.get(i).getId(), new WorkbasketNotFoundException(matchingSummary.getId()));
        workbasketIds.remove(i);
        taskSummaries.remove(i);
      } else {
        destinationWbSummaries.add(matchingSummary);
      }
    }
    Collections.reverse(destinationWbSummaries);
    return destinationWbSummaries;
  }

  private void updateReroutableTasksWithDifferentWorkbaskets(
      List<TaskSummary> taskSummaries,
      List<WorkbasketSummary> workbasketSummaries,
      boolean setTransferFlag) {
    List<TaskSummary> oldTaskSummaries = new ArrayList<>();
    for (TaskSummary taskSummary : taskSummaries) {
      TaskSummaryImpl copy = new TaskSummaryImpl();
      copy.setRead(taskSummary.isRead());
      copy.setTransferred(taskSummary.isTransferred());
      copy.setState(taskSummary.getState());
      copy.setOwner(taskSummary.getOwner());
      copy.setWorkbasketSummary(taskSummary.getWorkbasketSummary());
      copy.setDomain(taskSummary.getDomain());
      copy.setModified(taskSummary.getModified());
      copy.setId(taskSummary.getId());
      oldTaskSummaries.add(copy);
    }
    for (int i = 0; i < taskSummaries.size(); i++) {
      applyTransferValuesForTask(
          (TaskSummaryImpl) taskSummaries.get(i), workbasketSummaries.get(i), setTransferFlag);
    }
    Set<String> taskIds =
        taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toSet());
    taskMapper.updateTransferMultipleWorkbaskets(taskIds, taskSummaries);

    if (historyEventManager.isEnabled()) {
      for (int i = 0; i < taskSummaries.size(); i++) {
        TaskSummary oldSummary = oldTaskSummaries.get(i);
        TaskSummary newSummary = taskSummaries.get(i);

        createReroutedEvent(
            oldSummary,
            newSummary,
            oldSummary.getWorkbasketSummary().getId(),
            newSummary.getWorkbasketSummary().getId());
      }
    }
  }

  private DB getDB() {
    return DB.getDB(this.taskanaEngine.getSqlSession().getConfiguration().getDatabaseId());
  }

  private List<TaskSummary> filterNotExistingTaskIds(
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
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }

  private List<TaskSummary> getTasksToChange(List<String> taskIds) {
    return taskanaEngine
        .getEngine()
        .runAsAdmin(
            () -> taskService.createTaskQuery().idIn(taskIds.toArray(new String[0])).list());
  }
}
