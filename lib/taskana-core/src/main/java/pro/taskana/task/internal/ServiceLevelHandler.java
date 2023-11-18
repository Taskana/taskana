package pro.taskana.task.internal;

import static java.util.function.Predicate.not;
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
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
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
  private final WorkingTimeCalculator workingTimeCalculator;
  private final TaskServiceImpl taskServiceImpl;

  ServiceLevelHandler(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      AttachmentMapper attachmentMapper,
      TaskServiceImpl taskServiceImpl) {
    this.taskanaEngine = taskanaEngine;
    this.taskMapper = taskMapper;
    this.attachmentMapper = attachmentMapper;
    workingTimeCalculator = taskanaEngine.getEngine().getWorkingTimeCalculator();
    this.taskServiceImpl = taskServiceImpl;
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
      List<MinimalTaskSummary> tasksWithoutManualPriority =
          tasks.stream()
              .filter(not(MinimalTaskSummary::isManualPriorityActive))
              .collect(Collectors.toList());
      updateTaskPriorityOnClassificationUpdate(
          tasksWithoutManualPriority, attachments, allInvolvedClassifications);
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
      LOGGER.debug("found attachments {}.", attachments);
    }
    List<ClassificationSummary> allInvolvedClassifications =
        findAllClassificationsReferencedByTasksAndAttachments(tasks, attachments);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("found involved classifications {}.", allInvolvedClassifications);
    }
    List<ClassificationWithServiceLevelResolved> allInvolvedClassificationsWithDuration =
        resolveDurationsInClassifications(allInvolvedClassifications);
    Map<Duration, List<String>> durationToTaskIdsMap =
        getDurationToTaskIdsMap(tasks, attachments, allInvolvedClassificationsWithDuration);
    BulkLog updateResult = updatePlannedPropertyOfAffectedTasks(planned, durationToTaskIdsMap);
    bulkLog.addAllErrors(updateResult);

    return bulkLog;
  }

  // TODO: Is it worth splitting the logic of this method into two separate methods one for
  //  creating new task the other for updating a task.
  TaskImpl updatePrioPlannedDueOfTask(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws InvalidArgumentException {
    boolean onlyPriority = false;
    if (newTaskImpl.getClassificationSummary() == null
        || newTaskImpl.getClassificationSummary().getServiceLevel() == null) {
      // TODO this should never be the case
      setPlannedDueOnMissingServiceLevel(newTaskImpl);
      onlyPriority = true;
    }

    if (isPriorityAndDurationAlreadyCorrect(newTaskImpl, oldTaskImpl)) {
      return newTaskImpl;
    }

    if (newTaskImpl.getPlanned() == null && newTaskImpl.getDue() == null) {
      // TODO bitte oldTaskImpl berücksichtigen
      newTaskImpl.setPlanned(Instant.now());
    }

    DurationPrioHolder durationPrioHolder = determineTaskPrioDuration(newTaskImpl, onlyPriority);
    if (newTaskImpl.isManualPriorityActive()) {
      newTaskImpl.setPriority(newTaskImpl.getManualPriority());
    } else {
      newTaskImpl.setPriority(durationPrioHolder.getPriority());
    }
    if (onlyPriority) {
      return newTaskImpl;
    }

    // creation of new task
    if (oldTaskImpl == null) {
      return updatePlannedDueOnCreationOfNewTask(newTaskImpl, durationPrioHolder.getDuration());
    } else {
      return updatePlannedDueOnTaskUpdate(
          newTaskImpl, oldTaskImpl, durationPrioHolder.getDuration());
    }
  }

  private DurationPrioHolder determineTaskPrioDuration(TaskImpl newTaskImpl, boolean onlyPriority) {
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

  private void setPlannedDueOnMissingServiceLevel(TaskImpl task) {
    Instant now = Instant.now();
    if (task.getDue() == null && task.getPlanned() == null) {
      task.setDue(now);
      task.setPlanned(now);
    } else if (task.getDue() == null) {
      task.setDue(task.getPlanned());
    } else {
      task.setPlanned(task.getDue());
    }
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
          referenceTask.setPriority(prio);
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
    // TODO this should allow negative Priorities just like #getFinalPrioDurationOfTask
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
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl, Duration duration)
      throws InvalidArgumentException {
    // TODO pull this one out and in updatePlannedDueOnCreationOfNewTask, too.
    if (!taskanaEngine.getEngine().getConfiguration().isEnforceServiceLevel()
        && newTaskImpl.getDue() != null
        && newTaskImpl.getPlanned() != null) {

      return newTaskImpl;
    }

    boolean forcedDueRecalculation = newTaskImpl.getDue() == null;
    boolean forcedPlannedRecalculation = newTaskImpl.getPlanned() == null;
    if (forcedDueRecalculation) {
      recalcDueBasedPlanned(newTaskImpl, duration);
    } else if (forcedPlannedRecalculation) {
      recalcPlannedBasedOnDue(newTaskImpl, oldTaskImpl, duration);
    } else if (oldTaskImpl.getDue().equals(newTaskImpl.getDue())) {
      // We know due has not changed, but the following two options may happen
      //  * no change of planned, but potentially change of an attachment or classification
      //  * planned has changed
      // -> normalize planned and recalculate due
      recalcDueBasedPlanned(newTaskImpl, duration);
    } else {
      // Due has changed and (maybe) planned has changed
      // -> normalize due and recalculate planned
      recalcPlannedBasedOnDue(newTaskImpl, oldTaskImpl, duration);
    }
    return newTaskImpl;
  }

  private void recalcPlannedBasedOnDue(
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl, Duration duration)
      throws InvalidArgumentException {
    Instant calcDue = normalizeDue(newTaskImpl.getDue());
    Instant calcPlanned = calculatePlanned(calcDue, duration);
    if (plannedHasChanged(newTaskImpl, oldTaskImpl)) {
      ensureServiceLevelIsNotViolated(newTaskImpl, duration, calcPlanned);
    }
    newTaskImpl.setPlanned(calcPlanned);
    newTaskImpl.setDue(calcDue);
  }

  private void recalcDueBasedPlanned(TaskImpl newTaskImpl, Duration duration) {
    Instant planned = normalizePlanned(newTaskImpl.getPlanned());
    newTaskImpl.setPlanned(planned);
    newTaskImpl.setDue(calculateDue(planned, duration));
  }

  private boolean plannedHasChanged(Task newTask, Task oldTask) {
    return newTask.getPlanned() != null && !oldTask.getPlanned().equals(newTask.getPlanned());
  }

  private Instant calculateDue(Instant planned, Duration duration) {
    Instant dueExclusive = workingTimeCalculator.addWorkingTime(planned, duration);
    if (taskanaEngine.getEngine().getConfiguration().isUseWorkingTimeCalculation()
        && !planned.equals(dueExclusive)) {
      // Calculation is exclusive, but we want due date to be inclusive. Hence, we subtract a
      // millisecond
      // If planned and dueExclusive are the same values, we don't want due to be before planned.
      // To compensate for that we allow a delta of one millisecond in
      // ensureServiceLevelIsNotViolated
      dueExclusive = dueExclusive.minusMillis(1);
    }
    return dueExclusive;
  }

  private Instant calculatePlanned(Instant due, Duration duration) {
    if (Duration.ZERO.equals(duration)) {
      // Since calculation happens on due, that is already inclusive we do not calculate at all
      return due;
    } else {
      // due is inclusive, but calculation happens exclusive.
      Instant normalize =
          taskanaEngine.getEngine().getConfiguration().isUseWorkingTimeCalculation()
              ? due.plusMillis(1)
              : due;
      return workingTimeCalculator.subtractWorkingTime(normalize, duration);
    }
  }

  private Instant normalizeDue(Instant due) {
    // plusMillis since due is inclusive, but calculation happens exclusive.
    // minusMillis since we calculated a due date
    // Without that some edge case fail (e.g. due is exactly the start of weekend)
    if (taskanaEngine.getEngine().getConfiguration().isUseWorkingTimeCalculation()) {
      return workingTimeCalculator
          .subtractWorkingTime(due.plusMillis(1), Duration.ZERO)
          .minusMillis(1);
    }

    return workingTimeCalculator.subtractWorkingTime(due, Duration.ZERO);
  }

  private Instant normalizePlanned(Instant instant) {
    return workingTimeCalculator.addWorkingTime(instant, Duration.ZERO);
  }

  /**
   * Ensure that planned and due of task comply with the associated service level. The 'planned'
   * timestamp was calculated by subtracting the service level duration from task.due. It may not be
   * the same as task.planned and the request may nevertheless be correct. The following Scenario
   * illustrates this: If task.planned is on a Saturday morning, and duration is 1 working day, then
   * calculating forward from planned to due will give Tuesday morning as due date, because sunday
   * is skipped. On the other hand, calculating from due (Tuesday morning) 1 day backwards will
   * result in a planned date of monday morning which differs from task.planned. Therefore, if
   * task.planned is not equal to calcPlanned, the service level is not violated and we still must
   * grant the request if the following conditions are fulfilled:
   *
   * <ul>
   *   <li>task.planned is not a working day
   *   <li>there are no working days between task.planned and calcPlanned
   * </ul>
   *
   * @param task the task for the difference between planned and due must be duration
   * @param duration the serviceLevel for the task
   * @param calcPlanned the planned timestamp that was calculated based on due and duration
   * @throws InvalidArgumentException if service level is violated.
   */
  private void ensureServiceLevelIsNotViolated(
      TaskImpl task, Duration duration, Instant calcPlanned) throws InvalidArgumentException {
    if (task.getPlanned() != null
        && !task.getPlanned().equals(calcPlanned)
        // We allow a diff of at most one millisecond, because calcPlanned is based on due date
        // which is inclusive and not exclusive. This handles standard cases and edge cases
        // (planned und due on weekends, e.g.)
        && (workingTimeCalculator
                .workingTimeBetween(task.getPlanned(), calcPlanned)
                .compareTo(Duration.ofMillis(1))
            > 0)) {
      throw new InvalidArgumentException(
          String.format(
              "Cannot update a task with given planned %s "
                  + "and due date %s not matching the service level %s.",
              task.getPlanned(), task.getDue(), duration));
    }
  }

  private TaskImpl updatePlannedDueOnCreationOfNewTask(TaskImpl newTask, Duration duration)
      throws InvalidArgumentException {
    if (!taskanaEngine.getEngine().getConfiguration().isEnforceServiceLevel()
        && newTask.getDue() != null
        && newTask.getPlanned() != null) {
      return newTask;
    }
    if (newTask.getDue() != null) {
      // due is specified: calculate back and check correctness
      Instant calcDue = normalizeDue(newTask.getDue());
      Instant calcPlanned = calculatePlanned(calcDue, duration);
      ensureServiceLevelIsNotViolated(newTask, duration, calcPlanned);
      newTask.setDue(calcDue);
      newTask.setPlanned(calcPlanned);
    } else {
      // task.due is null: calculate forward from planned
      recalcDueBasedPlanned(newTask, duration);
    }
    return newTask;
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
          InstantDurationHolder durationHolder, List<TaskDuration> taskDurationList) {
    final BulkLog bulkLog = new BulkLog();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(durationHolder.getPlanned());
    referenceTask.setModified(Instant.now());
    referenceTask.setDue(calculateDue(referenceTask.getPlanned(), durationHolder.getDuration()));
    List<String> taskIdsToUpdate =
        taskDurationList.stream().map(TaskDuration::getTaskId).collect(Collectors.toList());
    Pair<List<MinimalTaskSummary>, BulkLog> existingAndAuthorizedTasks =
        taskServiceImpl.getMinimalTaskSummaries(taskIdsToUpdate);
    bulkLog.addAllErrors(existingAndAuthorizedTasks.getRight());

    taskMapper.updateTaskDueDates(existingAndAuthorizedTasks.getLeft(), referenceTask);
    return bulkLog;
  }

  private BulkLog updatePlannedPropertyOfAffectedTasks(
      Instant planned, Map<Duration, List<String>> taskIdsByDueDuration) {
    final BulkLog bulkLog = new BulkLog();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(planned);
    referenceTask.setModified(Instant.now());

    taskIdsByDueDuration.forEach(
        (duration, taskIds) -> {
          referenceTask.setDue(calculateDue(planned, duration));
          Pair<List<MinimalTaskSummary>, BulkLog> existingAndAuthorizedTasks =
              taskServiceImpl.getMinimalTaskSummaries(taskIds);
          bulkLog.addAllErrors(existingAndAuthorizedTasks.getRight());
          taskMapper.updateTaskDueDates(existingAndAuthorizedTasks.getLeft(), referenceTask);
        });

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
    if (oldTaskImpl == null) {
      return false;
    }
    // TODO Do we need to compare Key and Id or could we simply compare ClassificationSummary only?
    final boolean isClassificationKeyChanged =
        !Objects.equals(newTaskImpl.getClassificationKey(), oldTaskImpl.getClassificationKey());
    final boolean isClassificationIdChanged =
        !Objects.equals(newTaskImpl.getClassificationId(), oldTaskImpl.getClassificationId());

    final boolean isManualPriorityChanged =
        newTaskImpl.getManualPriority() != oldTaskImpl.getManualPriority();

    return oldTaskImpl.getPlanned().equals(newTaskImpl.getPlanned())
        && oldTaskImpl.getDue().equals(newTaskImpl.getDue())
        && !isClassificationKeyChanged
        && !isClassificationIdChanged
        && !isManualPriorityChanged
        && areAttachmentsUnchanged(newTaskImpl, oldTaskImpl);
  }

  private boolean areAttachmentsUnchanged(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    Set<String> oldAttachmentIds =
        oldTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toSet());
    Set<String> newAttachmentIds =
        newTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toSet());
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

  static class BulkLog extends BulkOperationResults<String, TaskanaException> {}

  static final class DurationPrioHolder {

    private final Duration duration;
    private final int priority;

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
