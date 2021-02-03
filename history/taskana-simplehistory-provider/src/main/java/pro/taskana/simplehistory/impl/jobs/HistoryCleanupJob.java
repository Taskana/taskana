package pro.taskana.simplehistory.impl.jobs;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;

public class HistoryCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCleanupJob.class);

  private static final String TASKANA_PROPERTIES = "/taskana.properties";

  private static final String TASKANA_JOB_HISTORY_BATCH_SIZE = "taskana.jobs.history.batchSize";

  private static final String TASKANA_JOB_HISTORY_CLEANUP_RUN_EVERY =
      "taskana.jobs.history.cleanup.runEvery";

  private static final String TASKANA_JOB_HISTORY_CLEANUP_FIRST_RUN =
      "taskana.jobs.history.cleanup.firstRunAt";

  private static final String TASKANA_JOB_HISTORY_CLEANUP_MINIMUM_AGE =
      "taskana.jobs.history.cleanup.minimumAge";

  private final boolean allCompletedSameParentBusiness;

  TaskanaHistoryEngineImpl taskanaHistoryEngine =
      TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineImpl);

  private Instant firstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration runEvery = Duration.parse("P1D");
  private Duration minimumAge = Duration.parse("P14D");
  private int batchSize = 100;

  public HistoryCleanupJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider<Object> txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob);
    allCompletedSameParentBusiness =
        taskanaEngine.getConfiguration().isTaskCleanupJobAllCompletedSameParentBusiness();
    Properties props = readPropertiesFromFile(TASKANA_PROPERTIES);
    initJobParameters(props);
  }

  @Override
  public void run() throws TaskanaException {

    Instant createdBefore = Instant.now().minus(minimumAge);

    LOGGER.info("Running job to delete all history events created before ({})", createdBefore);

    try {
      SimpleHistoryServiceImpl simpleHistoryService =
          (SimpleHistoryServiceImpl) taskanaHistoryEngine.getTaskanaHistoryService();

      List<TaskHistoryEvent> historyEventCandidatesToClean =
          simpleHistoryService
              .createTaskHistoryQuery()
              .createdWithin(new TimeInterval(null, createdBefore))
              .eventTypeIn(
                  TaskHistoryEventType.COMPLETED.getName(),
                  TaskHistoryEventType.CANCELLED.getName(),
                  TaskHistoryEventType.TERMINATED.getName())
              .list();

      Set<String> taskIdsToDeleteHistoryEventsFor;
      if (allCompletedSameParentBusiness) {
        taskIdsToDeleteHistoryEventsFor =
            historyEventCandidatesToClean.stream()
                .filter(
                    event ->
                        event.getParentBusinessProcessId() == null
                            || event.getParentBusinessProcessId().isEmpty())
                .map(TaskHistoryEvent::getTaskId)
                .collect(Collectors.toSet());
        historyEventCandidatesToClean.removeIf(
            event -> taskIdsToDeleteHistoryEventsFor.contains(event.getTaskId()));

        if (!historyEventCandidatesToClean.isEmpty()) {
          String[] parentBusinessProcessIds =
              historyEventCandidatesToClean.stream()
                  .map(TaskHistoryEvent::getParentBusinessProcessId)
                  .distinct()
                  .toArray(String[]::new);

          historyEventCandidatesToClean.addAll(
              simpleHistoryService
                  .createTaskHistoryQuery()
                  .parentBusinessProcessIdIn(parentBusinessProcessIds)
                  .eventTypeIn(TaskHistoryEventType.CREATED.getName())
                  .list());

          taskIdsToDeleteHistoryEventsFor.addAll(
              filterSameParentBusinessHistoryEventsQualifiedToClean(historyEventCandidatesToClean));
        }
      } else {
        taskIdsToDeleteHistoryEventsFor =
            historyEventCandidatesToClean.stream()
                .map(TaskHistoryEvent::getTaskId)
                .collect(Collectors.toSet());
      }

      int totalNumberOfHistoryEventsDeleted =
          CollectionUtil.partitionBasedOnSize(taskIdsToDeleteHistoryEventsFor, batchSize).stream()
              .mapToInt(this::deleteHistoryEventsTransactionally)
              .sum();

      LOGGER.info(
          "Job ended successfully. {} history events deleted.", totalNumberOfHistoryEventsDeleted);
    } catch (Exception e) {
      throw new TaskanaException("Error while processing HistoryCleanupJob.", e);
    } finally {
      scheduleNextCleanupJob();
    }
  }

  /**
   * Initializes the HistoryCleanupJob schedule. <br>
   * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the TASKANA engine.
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine) {
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.deleteJobs(Type.HISTORYCLEANUPJOB);
    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.scheduleNextCleanupJob();
  }

  private List<String> filterSameParentBusinessHistoryEventsQualifiedToClean(
      List<TaskHistoryEvent> historyEventCandidatesToClean) {

    Map<String, Map<String, List<String>>> historyEventsGroupedByParentBusinessProcessIdAndType =
        historyEventCandidatesToClean.stream()
            .collect(
                groupingBy(
                    TaskHistoryEvent::getParentBusinessProcessId,
                    groupingBy(
                        TaskHistoryEvent::getEventType,
                        mapping(TaskHistoryEvent::getTaskId, toList()))));

    List<String> taskIdsToDeleteHistoryEventsFor = new ArrayList<>();

    historyEventsGroupedByParentBusinessProcessIdAndType
        .entrySet()
        .forEach(
            idsOfTasksInSameParentBusinessProcessGroupedByType -> {
              if (idsOfTasksInSameParentBusinessProcessGroupedByType
                      .getValue()
                      .get(TaskHistoryEventType.CREATED.getName())
                      .size()
                  == idsOfTasksInSameParentBusinessProcessGroupedByType
                      .getValue()
                      .entrySet()
                      .stream()
                      .filter(
                          entry -> !entry.getKey().equals(TaskHistoryEventType.CREATED.getName()))
                      .mapToInt(stringListEntry -> stringListEntry.getValue().size())
                      .sum()) {

                taskIdsToDeleteHistoryEventsFor.addAll(
                    idsOfTasksInSameParentBusinessProcessGroupedByType
                        .getValue()
                        .get(TaskHistoryEventType.CREATED.getName()));
              }
            });

    return taskIdsToDeleteHistoryEventsFor;
  }

  private int deleteHistoryEventsTransactionally(List<String> taskIdsToDeleteHistoryEventsFor) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteHistoryEventsTransactionally(taskIdsToDeleteHistoryEventsFor = {})",
          taskIdsToDeleteHistoryEventsFor);
    }

    int deletedEventsCount = 0;
    if (txProvider != null) {
      int count =
          (Integer)
              txProvider.executeInTransaction(
                  () -> {
                    try {
                      return deleteEvents(taskIdsToDeleteHistoryEventsFor);
                    } catch (Exception e) {
                      LOGGER.warn("Could not delete history events.", e);
                      return 0;
                    }
                  });
      LOGGER.debug("exit from deleteHistoryEventsTransactionally(), returning {}", count);
      return count;
    } else {
      try {
        deletedEventsCount = deleteEvents(taskIdsToDeleteHistoryEventsFor);
      } catch (Exception e) {
        LOGGER.warn("Could not delete history events.", e);
      }
    }
    LOGGER.debug(
        "exit from deleteHistoryEventsTransactionally()(), returning {}", deletedEventsCount);
    return deletedEventsCount;
  }

  private int deleteEvents(List<String> taskIdsToDeleteHistoryEventsFor)
      throws InvalidArgumentException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteEvents(taskIdsToDeleteHistoryEventsFor = {})",
          taskIdsToDeleteHistoryEventsFor);
    }

    SimpleHistoryServiceImpl simpleHistoryService =
        (SimpleHistoryServiceImpl) taskanaHistoryEngine.getTaskanaHistoryService();

    String[] taskIdsArray = new String[taskIdsToDeleteHistoryEventsFor.size()];
    int deletedTasksCount =
        (int)
            simpleHistoryService
                .createTaskHistoryQuery()
                .taskIdIn(taskIdsToDeleteHistoryEventsFor.toArray(taskIdsArray))
                .count();

    simpleHistoryService.deleteHistoryEventsByTaskIds(taskIdsToDeleteHistoryEventsFor);

    LOGGER.debug("{} events deleted.", deletedTasksCount);

    LOGGER.debug("exit from deleteEvents(), returning {}", taskIdsToDeleteHistoryEventsFor.size());
    return deletedTasksCount;
  }

  private void scheduleNextCleanupJob() {
    LOGGER.debug("Entry to scheduleNextCleanupJob.");
    ScheduledJob job = new ScheduledJob();
    job.setType(Type.HISTORYCLEANUPJOB);
    job.setDue(getNextDueForHistoryCleanupJob());
    taskanaEngineImpl.getJobService().createJob(job);
    LOGGER.debug("Exit from scheduleNextCleanupJob.");
  }

  private Instant getNextDueForHistoryCleanupJob() {
    Instant nextRunAt = firstRun;
    while (nextRunAt.isBefore(Instant.now())) {
      nextRunAt = nextRunAt.plus(runEvery);
    }
    LOGGER.info("Scheduling next run of the HistoryCleanupJob for {}", nextRunAt);
    return nextRunAt;
  }

  private void initJobParameters(Properties props) {

    String jobBatchSizeProperty = props.getProperty(TASKANA_JOB_HISTORY_BATCH_SIZE);
    if (jobBatchSizeProperty != null && !jobBatchSizeProperty.isEmpty()) {
      try {
        batchSize = Integer.parseInt(jobBatchSizeProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse jobBatchSizeProperty ({}). Using default. Exception: {} ",
            jobBatchSizeProperty,
            e.getMessage());
      }
    }

    String historyCleanupJobFirstRunProperty =
        props.getProperty(TASKANA_JOB_HISTORY_CLEANUP_FIRST_RUN);
    if (historyCleanupJobFirstRunProperty != null && !historyCleanupJobFirstRunProperty.isEmpty()) {
      try {
        firstRun = Instant.parse(historyCleanupJobFirstRunProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse historyCleanupJobFirstRunProperty ({}). Using default."
                + " Exception: {} ",
            historyCleanupJobFirstRunProperty,
            e.getMessage());
      }
    }

    String historyCleanupJobRunEveryProperty =
        props.getProperty(TASKANA_JOB_HISTORY_CLEANUP_RUN_EVERY);
    if (historyCleanupJobRunEveryProperty != null && !historyCleanupJobRunEveryProperty.isEmpty()) {
      try {
        runEvery = Duration.parse(historyCleanupJobRunEveryProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse historyCleanupJobRunEveryProperty ({}). Using default. Exception: {} ",
            historyCleanupJobRunEveryProperty,
            e.getMessage());
      }
    }

    String historyEventCleanupJobMinimumAgeProperty =
        props.getProperty(TASKANA_JOB_HISTORY_CLEANUP_MINIMUM_AGE);
    if (historyEventCleanupJobMinimumAgeProperty != null
        && !historyEventCleanupJobMinimumAgeProperty.isEmpty()) {
      try {
        minimumAge = Duration.parse(historyEventCleanupJobMinimumAgeProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse historyEventCleanupJobMinimumAgeProperty ({}). Using default."
                + " Exception: {} ",
            historyEventCleanupJobMinimumAgeProperty,
            e.getMessage());
      }
    }

    LOGGER.debug("Configured number of history events per transaction: {}", batchSize);
    LOGGER.debug("HistoryCleanupJob configuration: first run at {}", firstRun);
    LOGGER.debug("HistoryCleanupJob configuration: runs every {}", runEvery);
    LOGGER.debug(
        "HistoryCleanupJob configuration: minimum age of history events to be cleanup up is {}",
        minimumAge);
  }

  private Properties readPropertiesFromFile(String propertiesFile) {
    Properties props = new Properties();
    boolean loadFromClasspath = loadFromClasspath(propertiesFile);
    try {
      if (loadFromClasspath) {
        InputStream inputStream =
            TaskanaEngineConfiguration.class.getResourceAsStream(propertiesFile);
        if (inputStream == null) {
          LOGGER.error("taskana properties file {} was not found on classpath.", propertiesFile);
        } else {
          props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
          LOGGER.debug(
              "taskana properties were loaded from file {} from classpath.", propertiesFile);
        }
      } else {
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
          props.load(fileInputStream);
          LOGGER.debug("taskana properties were loaded from file {}.", propertiesFile);
        }
      }
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing properties file {}.", propertiesFile);
      throw new SystemException(
          "internal System error when processing properties file " + propertiesFile, e.getCause());
    }
    return props;
  }

  private boolean loadFromClasspath(String propertiesFile) {
    boolean loadFromClasspath = true;
    File f = new File(propertiesFile);
    if (f.exists() && !f.isDirectory()) {
      loadFromClasspath = false;
    }
    return loadFromClasspath;
  }
}
