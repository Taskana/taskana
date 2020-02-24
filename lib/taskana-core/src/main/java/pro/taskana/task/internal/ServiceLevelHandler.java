package pro.taskana.task.internal;

import static java.util.stream.Collectors.groupingBy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.exceptions.UpdateFailedException;
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

  public ServiceLevelHandler(
      InternalTaskanaEngine taskanaEngine,
      TaskMapper taskMapper,
      AttachmentMapper attachmentMapper) {
    super();
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
  public BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasksImpl(
      Instant planned, List<String> argTaskIds) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    if (argTaskIds == null || argTaskIds.isEmpty()) {
      return bulkLog;
    }
    Pair<List<MinimalTaskSummary>, BulkOperationResults<String, TaskanaException>> resultsPair =
        filterTasksForExistenceAndAuthorization(argTaskIds);
    List<MinimalTaskSummary> existingTasksAuthorizedFor = resultsPair.getLeft();
    bulkLog.addAllErrors(resultsPair.getRight());

    List<AttachmentSummaryImpl> attachments = getAttachmentSummaries(existingTasksAuthorizedFor);
    List<ClassificationSummary> allInvolvedClassifications =
        findAllInvolvedClassifications(existingTasksAuthorizedFor, attachments);
    List<ClassificationWithServiceLevelResolved> allInvolvedClassificationsWithDuration =
        resolveDurationsInClassifications(allInvolvedClassifications);
    Map<Duration, List<TaskDuration>> tasksPerDuration =
        getTasksPerDuration(
            existingTasksAuthorizedFor, attachments, allInvolvedClassificationsWithDuration);
    bulkLog.addAllErrors(updateAffectedTasks(planned, tasksPerDuration));

    return bulkLog;
  }

  BulkOperationResults<String, TaskanaException> addExceptionsForNonExistingTasks(
      List<String> requestTaskIds, List<MinimalTaskSummary> existingMinimalTaskSummaries) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    List<String> nonExistingTaskIds = new ArrayList<>(requestTaskIds);
    List<String> existingTaskIds =
        existingMinimalTaskSummaries.stream()
            .map(MinimalTaskSummary::getTaskId)
            .collect(Collectors.toList());
    nonExistingTaskIds.removeAll(existingTaskIds);
    for (String taskId : nonExistingTaskIds) {
      bulkLog.addError(taskId, new TaskNotFoundException(taskId, "Task was not found"));
    }
    return bulkLog;
  }

  private BulkOperationResults<String, TaskanaException> updateAffectedTasks(
      Instant planned, Map<Duration, List<TaskDuration>> tasksPerDuration) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    TaskImpl referenceTask = new TaskImpl();
    referenceTask.setPlanned(planned);
    for (Map.Entry<Duration, List<TaskDuration>> entry : tasksPerDuration.entrySet()) {
      List<String> taskIdsToUpdate =
          entry.getValue().stream().map(TaskDuration::getTaskId).collect(Collectors.toList());
      long days = converter.convertWorkingDaysToDays(planned, entry.getKey().toDays());
      Instant due = planned.plus(Duration.ofDays(days));
      referenceTask.setDue(due);
      referenceTask.setModified(Instant.now());
      long numTasksUpdated = taskMapper.updateTaskDueDates(taskIdsToUpdate, referenceTask);
      if (numTasksUpdated != taskIdsToUpdate.size()) {
        bulkLog.addAllErrors(
            checkResultsOfTasksUpdateAndAddErrorsToBulkLog(
                taskIdsToUpdate, referenceTask, numTasksUpdated));
      }
    }
    return bulkLog;
  }

  private Pair<List<MinimalTaskSummary>, BulkOperationResults<String, TaskanaException>>
      filterTasksForExistenceAndAuthorization(List<String> argTaskIds) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    // remove duplicates
    List<String> taskIds = argTaskIds.stream().distinct().collect(Collectors.toList());
    // get existing tasks
    List<MinimalTaskSummary> minimalTaskSummaries = taskMapper.findExistingTasks(taskIds, null);
    bulkLog.addAllErrors(addExceptionsForNonExistingTasks(taskIds, minimalTaskSummaries));
    Pair<List<MinimalTaskSummary>, BulkOperationResults<String, TaskanaException>> filteredPair =
        filterTasksAuthorizedForAndLogErrorsForNotAuthorized(minimalTaskSummaries);
    bulkLog.addAllErrors(filteredPair.getRight());
    return new Pair<>(filteredPair.getLeft(), bulkLog);
  }

  private BulkOperationResults<String, TaskanaException>
      checkResultsOfTasksUpdateAndAddErrorsToBulkLog(
          List<String> taskIdsToUpdate, TaskImpl referenceTask, long numTasksUpdated) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
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
              String.format("UnknownTaskId%s", i),
              new UpdateFailedException("Update of unknown task failed"));
        }
      }
    }
    return bulkLog;
  }

  private Map<Duration, List<TaskDuration>> getTasksPerDuration(
      List<MinimalTaskSummary> minimalTaskSummariesAuthorizedFor,
      List<AttachmentSummaryImpl> attachments,
      List<ClassificationWithServiceLevelResolved>
          allInvolvedClassificationsWithServiceLevelResolved) {
    List<TaskDuration> resultingTaskDurations = new ArrayList<>();
    // Map taskId -> Set Of involved classification Ids
    Map<String, Set<String>> classificationIdsPerTaskId =
        findAllClassificationIdsPerTask(minimalTaskSummariesAuthorizedFor, attachments);
    // Map classificationId -> Duration
    Map<String, Duration> durationPerClassificationId =
        allInvolvedClassificationsWithServiceLevelResolved.stream()
            .collect(
                Collectors.toMap(
                    ClassificationWithServiceLevelResolved::getClassificationId,
                    ClassificationWithServiceLevelResolved::getDurationFromClassification));
    for (MinimalTaskSummary task : minimalTaskSummariesAuthorizedFor) {
      Duration duration =
          determineMinimalDurationForTasks(
              classificationIdsPerTaskId.get(task.getTaskId()), durationPerClassificationId);
      TaskDuration taskDuration = new TaskDuration(task.getTaskId(), duration);
      resultingTaskDurations.add(taskDuration);
    }
    return resultingTaskDurations.stream().collect(groupingBy(TaskDuration::getDuration));
  }

  private Duration determineMinimalDurationForTasks(
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
    Map<String, Set<String>> result = new HashMap<>();
    for (MinimalTaskSummary task : minimalTaskSummaries) {
      Set<String> classificationIds =
          attachments.stream()
              .filter(a -> task.getTaskId().equals(a.getTaskId()))
              .map(AttachmentSummaryImpl::getClassificationSummary)
              .map(ClassificationSummary::getId)
              .collect(Collectors.toSet());
      classificationIds.add(task.getClassificationId());
      result.put(task.getTaskId(), classificationIds);
    }
    return result;
  }

  private List<ClassificationWithServiceLevelResolved> resolveDurationsInClassifications(
      List<ClassificationSummary> allInvolvedClassifications) {
    List<ClassificationWithServiceLevelResolved> result = new ArrayList<>();
    for (ClassificationSummary classification : allInvolvedClassifications) {
      Duration serviceLevel = Duration.parse(classification.getServiceLevel());
      result.add(new ClassificationWithServiceLevelResolved(classification.getId(), serviceLevel));
    }
    return result;
  }

  private List<AttachmentSummaryImpl> getAttachmentSummaries(
      List<MinimalTaskSummary> existingTasksAuthorizedFor) {
    List<String> existingTaskIdsAuthorizedFor =
        existingTasksAuthorizedFor.stream()
            .map(MinimalTaskSummary::getTaskId)
            .collect(Collectors.toList());

    String[] taskIdsAuthorizedForArray = new String[existingTaskIdsAuthorizedFor.size()];
    taskIdsAuthorizedForArray = existingTaskIdsAuthorizedFor.toArray(taskIdsAuthorizedForArray);

    if (existingTaskIdsAuthorizedFor.isEmpty()) {
      return new ArrayList<>();
    } else {
      return attachmentMapper.findAttachmentSummariesByTaskIds(taskIdsAuthorizedForArray);
    }
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

  private Pair<List<MinimalTaskSummary>, BulkOperationResults<String, TaskanaException>>
      filterTasksAuthorizedForAndLogErrorsForNotAuthorized(List<MinimalTaskSummary> existingTasks) {
    BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
    // check authorization only for non-admin users
    if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)) {
      return new Pair<>(existingTasks, bulkLog);
    } else {
      List<String> taskIds =
          existingTasks.stream().map(MinimalTaskSummary::getTaskId).collect(Collectors.toList());
      List<String> accessIds = CurrentUserContext.getAccessIds();
      List<String> taskIdsNotAuthorizedFor =
          taskMapper.filterTaskIdsNotAuthorizedFor(taskIds, accessIds);
      String userId = CurrentUserContext.getUserid();
      for (String taskId : taskIdsNotAuthorizedFor) {
        bulkLog.addError(
            taskId,
            new NotAuthorizedException(
                String.format("User %s is not authorized for task %s ", userId, taskId), userId));
      }
      taskIds.removeAll(taskIdsNotAuthorizedFor);
      List<MinimalTaskSummary> tasksAuthorizedFor =
          existingTasks.stream()
              .filter(t -> taskIds.contains(t.getTaskId()))
              .collect(Collectors.toList());
      return new Pair<>(tasksAuthorizedFor, bulkLog);
    }
  }

  static class ClassificationWithServiceLevelResolved {
    private String classificationId;
    private Duration duration;

    ClassificationWithServiceLevelResolved(String id, Duration serviceLevel) {
      this.classificationId = id;
      this.duration = serviceLevel;
    }

    String getClassificationId() {
      return classificationId;
    }

    Duration getDurationFromClassification() {
      return duration;
    }
  }

  static class TaskDuration {

    private String taskId;
    private Duration duration;

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
}
