package pro.taskana.task.internal;

import static java.util.stream.Collectors.groupingBy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.task.api.exceptions.UpdateFailedException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;

/** This class handles service level manipulations. */
class ServiceLevelHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLevelHandler.class);
  private static final String ERROR_CANNOT_INITIALIZE_DAYS_TO_WORKING_DAYS_CONVERTER =
      "Internal error. Cannot initialize DaysToWorkingDaysConverter";
  private static final Duration MAX_DURATION = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
  private final InternalTaskanaEngine taskanaEngine;
  private final TaskMapper taskMapper;
  private final AttachmentMapper attachmentMapper;
  private DaysToWorkingDaysConverter converter;

  ServiceLevelHandler(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      AttachmentMapper attachmentMapper) {
    this.taskanaEngine = taskanaEngine;
    this.taskMapper = taskMapper;
    this.attachmentMapper = attachmentMapper;
    try {
      this.converter = DaysToWorkingDaysConverter.initialize();
    } catch (InvalidArgumentException e) {
      LOGGER.error(ERROR_CANNOT_INITIALIZE_DAYS_TO_WORKING_DAYS_CONVERTER);
      throw new SystemException(
          ERROR_CANNOT_INITIALIZE_DAYS_TO_WORKING_DAYS_CONVERTER, e.getCause());
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
    List<ClassificationSummary> allInvolvedClassifications =
        findAllInvolvedClassifications(tasks, attachments);
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
    Set<ClassificationSummary> classificationsInvolved = getInvolvedClassifications(newTaskImpl);

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
    } else { // case 3: due was changed
      if (newTaskImpl.getDue() == null) {
        newTaskImpl.setDue(
            converter.addWorkingDaysToInstant(
                newTaskImpl.getPlanned(), durationPrioHolder.getDuration()));
      } else {
        Instant planned =
            (converter.subtractWorkingDaysFromInstant(
                newTaskImpl.getDue(), durationPrioHolder.getDuration()));
        if (newTaskImpl.getPlanned() != null && !planned.equals(newTaskImpl.getPlanned())) {
          throw new InvalidArgumentException(
              "Cannot update a task with given planned "
                  + "and due date not matching the service level");
        }
        newTaskImpl.setPlanned(planned);
      }
    }
    return newTaskImpl;
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

  private BulkLog updatePlannedPropertyOfAffectedTasks(
      Instant planned, Map<Duration, List<String>> durationToTaskIdsMap) {
    BulkLog bulkLog = new BulkLog();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(planned);
    referenceTask.setModified(Instant.now());
    for (Map.Entry<Duration, List<String>> entry : durationToTaskIdsMap.entrySet()) {
      List<String> taskIdsToUpdate = entry.getValue();
      long days = converter.convertWorkingDaysToDays(planned, entry.getKey().toDays());
      Instant due = planned.plus(Duration.ofDays(days));
      referenceTask.setDue(due);
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
        findAllClassificationIdsPerTask(minimalTaskSummariesAuthorizedFor, attachments);
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
      TaskDuration taskDuration = new TaskDuration(task.getTaskId(), duration);
      resultingTaskDurations.add(taskDuration);
    }
    return resultingTaskDurations.stream()
        .collect(
            groupingBy(
                TaskDuration::getDuration,
                Collectors.mapping(TaskDuration::getTaskId, Collectors.toList())));
  }

  private Duration determineMinimalDurationForATask(
      Set<String> classificationIds, Map<String, Duration> durationPerClassificationId) {
    Duration result = MAX_DURATION;
    for (String classificationId : classificationIds) {
      Duration actualDuration = durationPerClassificationId.get(classificationId);
      if (result.compareTo(actualDuration) > 0) {
        result = actualDuration;
      }
    }
    return result;
  }

  // returns a map <taskId -> Set of ClassificationIds>
  private Map<String, Set<String>> findAllClassificationIdsPerTask(
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

  private List<ClassificationSummary> findAllInvolvedClassifications(
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

  private Set<ClassificationSummary> getInvolvedClassifications(TaskImpl taskImpl) {
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

  static class ClassificationWithServiceLevelResolved {
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

  static class TaskDuration {

    private final String taskId;
    private final Duration duration;

    TaskDuration(String id, Duration serviceLevel) {
      this.taskId = id;
      this.duration = serviceLevel;
    }

    String getTaskId() {
      return taskId;
    }

    Duration getDuration() {
      return duration;
    }
  }

  static class BulkLog extends BulkOperationResults<String, TaskanaException> {
    BulkLog() {
      super();
    }
  }

  static class DurationPrioHolder {
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
}
