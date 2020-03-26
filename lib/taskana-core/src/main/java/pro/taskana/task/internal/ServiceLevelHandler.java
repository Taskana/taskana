package pro.taskana.task.internal;

import static java.util.stream.Collectors.groupingBy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.WorkingDaysToDaysConverter;
import pro.taskana.task.api.exceptions.UpdateFailedException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;

/** This class handles service level manipulations. */
class ServiceLevelHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLevelHandler.class);
  private static final Duration MAX_DURATION = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
  private final InternalTaskanaEngine taskanaEngine;
  private final TaskMapper taskMapper;
  private final AttachmentMapper attachmentMapper;
  private WorkingDaysToDaysConverter converter;

  ServiceLevelHandler(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      AttachmentMapper attachmentMapper) {
    this.taskanaEngine = taskanaEngine;
    this.taskMapper = taskMapper;
    this.attachmentMapper = attachmentMapper;
    this.converter = WorkingDaysToDaysConverter.initialize();
  }

  // use the same algorithm as setPlannedPropertyOfTasksImpl to refresh
  // priority and duration of affected tasks, just don't use a fix
  // planned date but the individual planned date of the tasks
  public void refreshPriorityAndDueDatesOfTasks(
      List<MinimalTaskSummary> tasks, boolean serviceLevelChanged, boolean priorityChanged) {

    List<AttachmentSummaryImpl> attachments = getAttachmentSummaries(tasks);
    List<ClassificationSummary> allInvolvedClassifications =
        findAllClassificationsReferencedByTasksAndAttachments(tasks, attachments);

    if (serviceLevelChanged) {
      List<ClassificationWithServiceLevelResolved> allInvolvedClassificationsWithDuration =
          resolveDurationsInClassifications(allInvolvedClassifications);

      updateTaskDueDatesOnClassificationUpdate(
          tasks, attachments, allInvolvedClassificationsWithDuration);
    }
    if (priorityChanged) {
      updateTaskPriorityOnClassificationUpdate(tasks, attachments, allInvolvedClassifications);
    }
  }

  // Algorithm:
  // - load all relevant tasks and their attachmentSummaries
  // - load all classifications referenced by these tasks / attachments
  // - calculate duration for ServiceLevel in each classification
  // - For each task iterate through all referenced classifications and find minimum ServiceLevel
  // - collect the results into a map Duration -> List of tasks
  // - for each duration in this map update due date of all associated tasks
  BulkLog setPlannedPropertyOfTasksImpl(Instant planned, List<MinimalTaskSummary> tasks) {
    BulkLog bulkLog = new BulkLog();
    List<AttachmentSummaryImpl> attachments = getAttachmentSummaries(tasks);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("found attachments {}.", LoggerUtils.listToString(attachments));
    }
    List<ClassificationSummary> allInvolvedClassifications =
        findAllClassificationsReferencedByTasksAndAttachments(tasks, attachments);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "found involved classifications {}.",
          LoggerUtils.listToString(allInvolvedClassifications));
    }
    List<ClassificationWithServiceLevelResolved> allInvolvedClassificationsWithDuration =
        resolveDurationsInClassifications(allInvolvedClassifications);
    Map<Duration, List<String>> durationToTaskIdsMap =
        getDurationToTaskIdsMap(tasks, attachments, allInvolvedClassificationsWithDuration);
    BulkLog updateResult = updatePlannedPropertyOfAffectedTasks(planned, durationToTaskIdsMap);
    bulkLog.addAllErrors(updateResult);

    return bulkLog;
  }

  TaskImpl updatePrioPlannedDueOfTask(
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl, boolean forRefreshOnClassificationUpdate)
      throws InvalidArgumentException {
    boolean onlyPriority = false;
    if (newTaskImpl.getClassificationSummary() == null
        || newTaskImpl.getClassificationSummary().getServiceLevel() == null) {
      newTaskImpl = setPlannedDueOnMissingServiceLevel(newTaskImpl);
      onlyPriority = true;
    }

    if (isPriorityAndDurationAlreadyCorrect(newTaskImpl, oldTaskImpl)) {
      return newTaskImpl;
    }

    if (newTaskImpl.getPlanned() == null && newTaskImpl.getDue() == null) {
      newTaskImpl.setPlanned(Instant.now());
    }

    DurationPrioHolder durationPrioHolder = determineTaskPrioDuration(newTaskImpl, onlyPriority);
    newTaskImpl.setPriority(durationPrioHolder.getPriority());
    if (onlyPriority) {
      return newTaskImpl;
    }
    // classification update
    if (forRefreshOnClassificationUpdate) {
      newTaskImpl.setDue(
          converter.addWorkingDaysToInstant(
              newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
      return newTaskImpl;
    }
    // creation of new task
    if (oldTaskImpl == null) {
      return updatePlannedDueOnCreationOfNewTask(newTaskImpl, durationPrioHolder);
    } else {
      return updatePlannedDueOnTaskUpdate(newTaskImpl, oldTaskImpl, durationPrioHolder);
    }
  }

  DurationPrioHolder determineTaskPrioDuration(TaskImpl newTaskImpl, boolean onlyPriority) {
    Set<ClassificationSummary> classificationsInvolved =
        getClassificationsReferencedByATask(newTaskImpl);

    List<ClassificationWithServiceLevelResolved> resolvedClassifications = new ArrayList<>();
    if (onlyPriority) {
      for (ClassificationSummary c : classificationsInvolved) {
        resolvedClassifications.add(
            new ClassificationWithServiceLevelResolved(c.getId(), MAX_DURATION, 0));
      }
    } else {
      resolvedClassifications =
          resolveDurationsInClassifications(new ArrayList<>(classificationsInvolved));
    }

    return getFinalPrioDurationOfTask(resolvedClassifications, onlyPriority);
  }

  private TaskImpl setPlannedDueOnMissingServiceLevel(TaskImpl task) {
    Instant now = Instant.now();
    if (task.getDue() == null && task.getPlanned() == null) {
      task.setDue(now);
      task.setPlanned(now);
    } else if (task.getDue() == null) {
      task.setDue(task.getPlanned());
    } else {
      task.setPlanned(task.getDue());
    }
    return task;
  }

  private void updateTaskPriorityOnClassificationUpdate(
      List<MinimalTaskSummary> existingTasks,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationSummary> allInvolvedClassifications) {

    Map<Integer, List<String>> priorityToTaskIdsMap =
        getPriorityToTasksIdsMap(existingTasks, attachments, allInvolvedClassifications);
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setModified(Instant.now());
    priorityToTaskIdsMap.forEach(
        (prio, taskIdList) -> {
          referenceTask.setPriority(prio.intValue());
          if (!taskIdList.isEmpty()) {
            taskMapper.updatePriorityOfTasks(taskIdList, referenceTask);
          }
        });
  }

  private Map<Integer, List<String>> getPriorityToTasksIdsMap(
      List<MinimalTaskSummary> existingTasks,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationSummary> allInvolvedClassifications) {

    Map<String, Integer> classificationIdToPriorityMap =
        allInvolvedClassifications.stream()
            .collect(
                Collectors.toMap(ClassificationSummary::getId, ClassificationSummary::getPriority));

    Map<String, Set<String>> taskIdToClassificationIdsMap =
        getTaskIdToClassificationsMap(existingTasks, attachments);
    List<TaskIdPriority> taskIdPriorities =
        existingTasks.stream()
            .map(
                t ->
                    new TaskIdPriority(
                        t.getTaskId(),
                        determinePriorityForATask(
                            t, classificationIdToPriorityMap, taskIdToClassificationIdsMap)))
            .collect(Collectors.toList());
    return taskIdPriorities.stream()
        .collect(
            groupingBy(
                TaskIdPriority::getPriority,
                Collectors.mapping(TaskIdPriority::getTaskId, Collectors.toList())));
  }

  private int determinePriorityForATask(
      MinimalTaskSummary minimalTaskSummary,
      Map<String, Integer> classificationIdToPriorityMap,
      Map<String, Set<String>> taskIdToClassificationIdsMap) {
    int actualPriority = 0;
    for (String classificationId :
        taskIdToClassificationIdsMap.get(minimalTaskSummary.getTaskId())) {
      int prio = classificationIdToPriorityMap.get(classificationId);
      if (prio > actualPriority) {
        actualPriority = prio;
      }
    }
    return actualPriority;
  }

  private BulkLog updateTaskDueDatesOnClassificationUpdate(
      List<MinimalTaskSummary> existingTasks,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationWithServiceLevelResolved> allInvolvedClassificationsWithDuration) {
    Map<InstantDurationHolder, List<TaskDuration>> tasksPerPlannedAndDuration =
        getTasksPerPlannedAndDuration(
            existingTasks, attachments, allInvolvedClassificationsWithDuration);
    return updateDuePropertyOfAffectedTasks(tasksPerPlannedAndDuration);
  }

  private TaskImpl updatePlannedDueOnTaskUpdate(
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl, DurationPrioHolder durationPrioHolder)
      throws InvalidArgumentException {
    if (newTaskImpl.getPlanned() == null && newTaskImpl.getDue() == null) {
      newTaskImpl.setPlanned(oldTaskImpl.getPlanned());
    }
    // case 1: no change of planned / due, but potentially change of an attachment or classification
    if (oldTaskImpl.getDue().equals(newTaskImpl.getDue())
        && oldTaskImpl.getPlanned().equals(newTaskImpl.getPlanned())) {
      newTaskImpl.setDue(
          converter.addWorkingDaysToInstant(
              newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
    } else if (oldTaskImpl.getDue().equals(newTaskImpl.getDue())
        && newTaskImpl.getPlanned() != null) {
      // case 2: planned was changed
      newTaskImpl.setDue(
          converter.addWorkingDaysToInstant(
              newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
      if (!converter.isWorkingDay(0, newTaskImpl.getPlanned())) {
        newTaskImpl.setPlanned(getFirstFollowingWorkingDay(newTaskImpl.getPlanned()));
      }
    } else {
      updatePlannedDueOnTaskUpdateWhenDueWasChanged(newTaskImpl, durationPrioHolder);
    }
    return newTaskImpl;
  }

  private void updatePlannedDueOnTaskUpdateWhenDueWasChanged(TaskImpl newTaskImpl,
      DurationPrioHolder durationPrioHolder) throws InvalidArgumentException {
    if (newTaskImpl.getDue() == null) {
      newTaskImpl.setDue(
          converter.addWorkingDaysToInstant(
              newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
      if (!converter.isWorkingDay(0, newTaskImpl.getPlanned())) {
        newTaskImpl.setPlanned(getFirstFollowingWorkingDay(newTaskImpl.getPlanned()));
      }
    } else {
      Instant planned =
          (converter.subtractWorkingDaysFromInstant(
              newTaskImpl.getDue(), durationPrioHolder.getDuration()));
      ensureServiceLevelIsNotViolated(newTaskImpl, durationPrioHolder.getDuration(), planned);
      newTaskImpl.setPlanned(planned);
      if (!converter.isWorkingDay(0, newTaskImpl.getDue())) {
        newTaskImpl.setDue(getFirstPreceedingWorkingDay(newTaskImpl.getDue()));
      }
    }
  }

  private Instant getFirstFollowingWorkingDay(Instant planned) {
    long days = 0;
    while (!converter.isWorkingDay(days, planned)) {
      days++;
    }
    return planned.plus(Duration.ofDays(days));
  }

  private Instant getFirstPreceedingWorkingDay(Instant due) {
    long days = 0;
    while (!converter.isWorkingDay(days, due)) {
      days--;
    }
    return due.minus(Duration.ofDays(Math.abs(days)));
  }

  /**
   * Ensure that planned and due of task comply with the associated service level. The 'planned'
   * timestamp was calculated by subtracting the service level duration from task.due. It may not be
   * the same as task.planned and the request may nevertheless be correct. The following Scenario
   * illustrates this: If task.planned is on a Saturday morning, and duration is 1 working day, then
   * calculating forward from planned to due will give Tuesday morning as due date, because sunday
   * is skipped. On the other hand, calculating from due (Tuesday morning) 1 day backwards will
   * result in a planned date of monday morning which differs from task.planned. Therefore, if
   * task.getPlanned is not equal to planned, the service level is not violated and we still must
   * grant the request if the following conditions are fulfilled: - planned is after task.planned -
   * task.planned is not a working day, - there is no working day between task.planned and planned.
   *
   * @param task the task for the difference between planned and due must be duration
   * @param duration the serviceLevel for the task
   * @param planned the planned Timestamp thas was calculated based on due and duration
   * @throws InvalidArgumentException if service level is violated.
   */
  private void ensureServiceLevelIsNotViolated(TaskImpl task, Duration duration, Instant planned)
      throws InvalidArgumentException {
    if (task.getPlanned() != null && !task.getPlanned().equals(planned)) {
      boolean isServiceLevelViolated = false;
      Instant taskPlanned = task.getPlanned();
      if (converter.isWorkingDay(0, taskPlanned)) {
        isServiceLevelViolated = true;
      } else if (taskPlanned.isAfter(planned)) {
        isServiceLevelViolated = true;
      } else {
        long days = Duration.between(taskPlanned, planned).toDays();
        for (long day = 0; day < days; day++) {
          if (converter.isWorkingDay(day, taskPlanned)) {
            isServiceLevelViolated = true;
            break;
          }
        }
      }
      if (isServiceLevelViolated) {
        throw new InvalidArgumentException(
            String.format(
                "Cannot update a task with given planned %s "
                    + "and due date %s not matching the service level %s.",
                task.getPlanned(), task.getDue(), duration));
      }
    }
  }

  private TaskImpl updatePlannedDueOnCreationOfNewTask(
      TaskImpl newTaskImpl, DurationPrioHolder durationPrioHolder) throws InvalidArgumentException {
    if (newTaskImpl.getDue() != null) { // due is specified: calculate back and check correctnes
      Instant planned =
          (converter.subtractWorkingDaysFromInstant(
              newTaskImpl.getDue(), durationPrioHolder.getDuration()));
      if (newTaskImpl.getPlanned() != null && !planned.equals(newTaskImpl.getPlanned())) {
        throw new InvalidArgumentException(
            "Cannot create a task with given planned "
                + "and due date not matching the service level");
      }
      newTaskImpl.setPlanned(planned);
    } else { // task.due is null: calculate forward from planned
      newTaskImpl.setDue(
          converter.addWorkingDaysToInstant(
              newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
    }
    return newTaskImpl;
  }

  private BulkLog updateDuePropertyOfAffectedTasks(
      Map<InstantDurationHolder, List<TaskDuration>> tasksPerPlannedAndDuration) {
    BulkLog bulkLog = new BulkLog();
    tasksPerPlannedAndDuration.forEach(
        (instDurHld, taskDurationList) ->
            bulkLog.addAllErrors(
                updateDuePropertyOfTasksWithIdenticalDueDate(instDurHld, taskDurationList)));
    return bulkLog;
  }

  private BulkOperationResults<String, TaskanaException>
      updateDuePropertyOfTasksWithIdenticalDueDate(
          InstantDurationHolder instDurHld, List<TaskDuration> taskDurationList) {
    BulkLog bulkLog = new BulkLog();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(instDurHld.getPlanned());
    referenceTask.setModified(Instant.now());
    referenceTask.setDue(
        converter.addWorkingDaysToInstant(referenceTask.getPlanned(), instDurHld.getDuration()));
    List<String> taskIdsToUpdate =
        taskDurationList.stream().map(TaskDuration::getTaskId).collect(Collectors.toList());
    long numTasksUpdated = taskMapper.updateTaskDueDates(taskIdsToUpdate, referenceTask);
    if (numTasksUpdated != taskIdsToUpdate.size()) {
      BulkLog checkResult =
          checkResultsOfTasksUpdateAndAddErrorsToBulkLog(
              taskIdsToUpdate, referenceTask, numTasksUpdated);
      bulkLog.addAllErrors(checkResult);
    }
    return bulkLog;
  }

  private BulkLog updatePlannedPropertyOfAffectedTasks(
      Instant planned, Map<Duration, List<String>> durationToTaskIdsMap) {
    BulkLog bulkLog = new BulkLog();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(planned);
    referenceTask.setModified(Instant.now());
    for (Map.Entry<Duration, List<String>> entry : durationToTaskIdsMap.entrySet()) {
      List<String> taskIdsToUpdate = entry.getValue();
      referenceTask.setDue(
          converter.addWorkingDaysToInstant(referenceTask.getPlanned(), entry.getKey()));
      long numTasksUpdated = taskMapper.updateTaskDueDates(taskIdsToUpdate, referenceTask);
      if (numTasksUpdated != taskIdsToUpdate.size()) {
        BulkLog checkResult =
            checkResultsOfTasksUpdateAndAddErrorsToBulkLog(
                taskIdsToUpdate, referenceTask, numTasksUpdated);
        bulkLog.addAllErrors(checkResult);
      }
    }
    return bulkLog;
  }

  private BulkLog checkResultsOfTasksUpdateAndAddErrorsToBulkLog(
      List<String> taskIdsToUpdate, TaskImpl referenceTask, long numTasksUpdated) {
    BulkLog bulkLog = new BulkLog();
    long numErrors = taskIdsToUpdate.size() - numTasksUpdated;
    long numErrorsLogged = 0;
    if (numErrors > 0) {
      List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(taskIdsToUpdate, null);
      for (MinimalTaskSummary task : taskSummaries) {
        if (referenceTask.getDue() != task.getDue()) {
          bulkLog.addError(
              task.getTaskId(),
              new UpdateFailedException(
                  String.format("Could not set Due Date of Task with Id %s. ", task.getTaskId())));
          numErrorsLogged++;
        }
      }
      long numErrorsNotLogged = numErrors - numErrorsLogged;
      if (numErrorsNotLogged != 0) {
        for (int i = 1; i <= numErrorsNotLogged; i++) {
          bulkLog.addError(
              String.format("UnknownTaskId%s", Integer.valueOf(i)),
              new UpdateFailedException("Update of unknown task failed"));
        }
      }
    }
    return bulkLog;
  }

  private Map<Duration, List<String>> getDurationToTaskIdsMap(
      List<MinimalTaskSummary> minimalTaskSummariesAuthorizedFor,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationWithServiceLevelResolved>
          allInvolvedClassificationsWithServiceLevelResolved) {

    List<TaskDuration> resultingTaskDurations = new ArrayList<>();
    // Map taskId -> Set Of involved classification Ids
    Map<String, Set<String>> taskIdToClassificationIdsMap =
        getTaskIdToClassificationsMap(minimalTaskSummariesAuthorizedFor, attachments);
    // Map classificationId -> Duration
    Map<String, Duration> classificationIdToDurationMap =
        allInvolvedClassificationsWithServiceLevelResolved.stream()
            .collect(
                Collectors.toMap(
                    ClassificationWithServiceLevelResolved::getClassificationId,
                    ClassificationWithServiceLevelResolved::getDurationFromClassification));
    for (MinimalTaskSummary task : minimalTaskSummariesAuthorizedFor) {
      Duration duration =
          determineMinimalDurationForATask(
              taskIdToClassificationIdsMap.get(task.getTaskId()), classificationIdToDurationMap);
      TaskDuration taskDuration = new TaskDuration(task.getTaskId(), duration, task.getPlanned());
      resultingTaskDurations.add(taskDuration);
    }
    return resultingTaskDurations.stream()
        .collect(
            groupingBy(
                TaskDuration::getDuration,
                Collectors.mapping(TaskDuration::getTaskId, Collectors.toList())));
  }

  private Map<InstantDurationHolder, List<TaskDuration>> getTasksPerPlannedAndDuration(
      List<MinimalTaskSummary> minimalTaskSummaries,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationWithServiceLevelResolved>
          allInvolvedClassificationsWithServiceLevelResolved) {

    Map<String, Duration> durationPerClassificationId =
        getClassificationIdToDurationMap(allInvolvedClassificationsWithServiceLevelResolved);

    List<TaskDuration> resultingTaskDurations = new ArrayList<>();
    // Map taskId -> Set Of involved classification Ids
    Map<String, Set<String>> taskIdClassificationIdsMap =
        getTaskIdToClassificationsMap(minimalTaskSummaries, attachments);

    for (MinimalTaskSummary task : minimalTaskSummaries) {
      Duration duration =
          determineMinimalDurationForATask(
              taskIdClassificationIdsMap.get(task.getTaskId()), durationPerClassificationId);

      TaskDuration taskDuration = new TaskDuration(task.getTaskId(), duration, task.getPlanned());
      resultingTaskDurations.add(taskDuration);
    }
    return resultingTaskDurations.stream().collect(groupingBy(TaskDuration::getPlannedDuration));
  }

  private Map<String, Duration> getClassificationIdToDurationMap(
      List<ClassificationWithServiceLevelResolved>
          allInvolvedClassificationsWithServiceLevelResolved) {
    // Map classificationId -> Duration
    return allInvolvedClassificationsWithServiceLevelResolved.stream()
        .collect(
            Collectors.toMap(
                ClassificationWithServiceLevelResolved::getClassificationId,
                ClassificationWithServiceLevelResolved::getDurationFromClassification));
  }

  private Duration determineMinimalDurationForATask(
      Set<String> classificationIds, Map<String, Duration> classificationIdDurationMap) {
    Duration result = MAX_DURATION;
    for (String classificationId : classificationIds) {
      Duration actualDuration = classificationIdDurationMap.get(classificationId);
      if (result.compareTo(actualDuration) > 0) {
        result = actualDuration;
      }
    }
    return result;
  }

  // returns a map <taskId -> Set of ClassificationIds>
  private Map<String, Set<String>> getTaskIdToClassificationsMap(
      List<MinimalTaskSummary> minimalTaskSummaries, List<AttachmentSummaryImpl> attachments) {
    Map<String, Set<String>> resultingTaskIdToClassificationIdsMap = new HashMap<>();
    for (MinimalTaskSummary task : minimalTaskSummaries) {
      Set<String> classificationIds =
          attachments.stream()
              .filter(a -> task.getTaskId().equals(a.getTaskId()))
              .map(AttachmentSummaryImpl::getClassificationSummary)
              .map(ClassificationSummary::getId)
              .collect(Collectors.toSet());
      classificationIds.add(task.getClassificationId());
      resultingTaskIdToClassificationIdsMap.put(task.getTaskId(), classificationIds);
    }
    return resultingTaskIdToClassificationIdsMap;
  }

  private List<ClassificationWithServiceLevelResolved> resolveDurationsInClassifications(
      List<ClassificationSummary> allInvolvedClassifications) {
    List<ClassificationWithServiceLevelResolved> result = new ArrayList<>();
    for (ClassificationSummary classification : allInvolvedClassifications) {
      Duration serviceLevel = Duration.parse(classification.getServiceLevel());
      result.add(
          new ClassificationWithServiceLevelResolved(
              classification.getId(), serviceLevel, classification.getPriority()));
    }
    return result;
  }

  private List<AttachmentSummaryImpl> getAttachmentSummaries(
      List<MinimalTaskSummary> existingTasksAuthorizedFor) {
    List<String> existingTaskIdsAuthorizedFor =
        existingTasksAuthorizedFor.stream()
            .map(MinimalTaskSummary::getTaskId)
            .collect(Collectors.toList());

    return existingTaskIdsAuthorizedFor.isEmpty()
        ? new ArrayList<>()
        : attachmentMapper.findAttachmentSummariesByTaskIds(existingTaskIdsAuthorizedFor);
  }

  private List<ClassificationSummary> findAllClassificationsReferencedByTasksAndAttachments(
      List<MinimalTaskSummary> existingTasksAuthorizedFor,
      List<AttachmentSummaryImpl> attachments) {
    Set<String> classificationIds =
        attachments.stream()
            .map(AttachmentSummaryImpl::getClassificationSummary)
            .map(ClassificationSummary::getId)
            .collect(Collectors.toSet());
    Set<String> classificationIdsFromTasks =
        existingTasksAuthorizedFor.stream()
            .map(MinimalTaskSummary::getClassificationId)
            .collect(Collectors.toSet());
    classificationIds.addAll(classificationIdsFromTasks);
    if (classificationIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      String[] idsArrayForQuery = new String[classificationIds.size()];
      idsArrayForQuery = classificationIds.toArray(idsArrayForQuery);
      return taskanaEngine
          .getEngine()
          .getClassificationService()
          .createClassificationQuery()
          .idIn(idsArrayForQuery)
          .list();
    }
  }

  private DurationPrioHolder getFinalPrioDurationOfTask(
      List<ClassificationWithServiceLevelResolved> cl, boolean onlyPriority) {
    Duration duration = MAX_DURATION;
    int priority = Integer.MIN_VALUE;
    for (ClassificationWithServiceLevelResolved classification : cl) {
      Duration actualDuration = classification.getDurationFromClassification();
      if (!onlyPriority && duration.compareTo(actualDuration) > 0) {
        duration = actualDuration;
      }
      if (classification.getPriority() > priority) {
        priority = classification.getPriority();
      }
    }
    return new DurationPrioHolder(duration, priority);
  }

  private Set<ClassificationSummary> getClassificationsReferencedByATask(TaskImpl taskImpl) {
    Set<ClassificationSummary> classifications =
        taskImpl.getAttachments() == null
            ? new HashSet<>()
            : taskImpl.getAttachments().stream()
                .map(Attachment::getClassificationSummary)
                .collect(Collectors.toSet());
    classifications.add(taskImpl.getClassificationSummary());
    return classifications;
  }

  private boolean isPriorityAndDurationAlreadyCorrect(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    if (oldTaskImpl != null) {
      final boolean isClassificationKeyChanged =
          newTaskImpl.getClassificationKey() != null
              && (oldTaskImpl.getClassificationKey() == null
                  || !newTaskImpl
                      .getClassificationKey()
                      .equals(oldTaskImpl.getClassificationKey()));

      final boolean isClassificationIdChanged =
          newTaskImpl.getClassificationId() != null
              && (oldTaskImpl.getClassificationId() == null
                  || !newTaskImpl.getClassificationId().equals(oldTaskImpl.getClassificationId()));

      return oldTaskImpl.getPlanned().equals(newTaskImpl.getPlanned())
          && oldTaskImpl.getDue().equals(newTaskImpl.getDue())
          && !isClassificationKeyChanged
          && !isClassificationIdChanged
          && areAttachmentsUnchanged(newTaskImpl, oldTaskImpl);
    } else {
      return false;
    }
  }

  private boolean areAttachmentsUnchanged(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    List<String> oldAttachmentIds =
        oldTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toList());
    List<String> newAttachmentIds =
        newTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toList());
    Set<String> oldClassificationIds =
        oldTaskImpl.getAttachments().stream()
            .map(Attachment::getClassificationSummary)
            .map(ClassificationSummary::getId)
            .collect(Collectors.toSet());
    Set<String> newClassificationIds =
        newTaskImpl.getAttachments().stream()
            .map(Attachment::getClassificationSummary)
            .map(ClassificationSummary::getId)
            .collect(Collectors.toSet());

    return oldAttachmentIds.size() == newAttachmentIds.size()
        && newAttachmentIds.containsAll(oldAttachmentIds)
        && oldClassificationIds.size() == newClassificationIds.size()
        && newClassificationIds.containsAll(oldClassificationIds);
  }

  static class BulkLog extends BulkOperationResults<String, TaskanaException> {
    BulkLog() {
      super();
    }
  }

  static final class DurationPrioHolder {
    private Duration duration;
    private int priority;

    DurationPrioHolder(Duration duration, int priority) {
      this.duration = duration;
      this.priority = priority;
    }

    Duration getDuration() {
      return duration;
    }

    int getPriority() {
      return priority;
    }
  }

  private static final class TaskDuration {

    private final String taskId;
    private final Duration duration;
    private final Instant planned;

    TaskDuration(String id, Duration serviceLevel, Instant planned) {
      this.taskId = id;
      this.duration = serviceLevel;
      this.planned = planned;
    }

    String getTaskId() {
      return taskId;
    }

    Duration getDuration() {
      return duration;
    }

    Instant getPlanned() {
      return planned;
    }

    InstantDurationHolder getPlannedDuration() {
      return new InstantDurationHolder(planned, duration);
    }
  }

  private static final class TaskIdPriority {
    private String taskId;
    private int priority;

    TaskIdPriority(String taskId, int priority) {
      this.taskId = taskId;
      this.priority = priority;
    }

    public String getTaskId() {
      return taskId;
    }

    public void setTaskId(String taskId) {
      this.taskId = taskId;
    }

    public Integer getPriority() {
      return priority;
    }

    public void setPriority(int priority) {
      this.priority = priority;
    }
  }

  private static final class InstantDurationHolder {
    private Instant planned;
    private Duration duration;

    InstantDurationHolder(Instant planned, Duration duration) {
      this.planned = planned;
      this.duration = duration;
    }

    public Instant getPlanned() {
      return planned;
    }

    public void setPlanned(Instant planned) {
      this.planned = planned;
    }

    public Duration getDuration() {
      return duration;
    }

    public void setDuration(Duration duration) {
      this.duration = duration;
    }

    @Override
    public int hashCode() {
      return Objects.hash(planned, duration);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      InstantDurationHolder other = (InstantDurationHolder) obj;
      return Objects.equals(planned, other.planned) && Objects.equals(duration, other.duration);
    }
  }

  private static final class ClassificationWithServiceLevelResolved {
    private final int priority;
    private final String classificationId;
    private final Duration duration;

    ClassificationWithServiceLevelResolved(String id, Duration serviceLevel, int priority) {
      this.classificationId = id;
      this.duration = serviceLevel;
      this.priority = priority;
    }

    String getClassificationId() {
      return classificationId;
    }

    Duration getDurationFromClassification() {
      return duration;
    }

    int getPriority() {
      return priority;
    }
  }
}
