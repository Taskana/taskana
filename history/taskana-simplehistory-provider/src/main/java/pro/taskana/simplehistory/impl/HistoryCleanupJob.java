package pro.taskana.simplehistory.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
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
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

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

  TaskanaHistoryEngineImpl taskanaHistoryEngine =
      TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineImpl.getConfiguration());

  private Instant firstRun;
  private Duration runEvery;
  private Duration minimumAge;
  private int batchSize;
  private boolean allCompletedSameParentBusiness;

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

      List<HistoryEventImpl> historyEventsToClean =
          simpleHistoryService
              .createHistoryQuery()
              .createdWithin(new TimeInterval(null, createdBefore))
              .eventTypeIn("TASK_COMPLETED")
              .list();

      if (allCompletedSameParentBusiness) {
        historyEventsToClean =
            filterSameParentBusinessHistoryEventsQualifiedToClean(
                simpleHistoryService, historyEventsToClean);
      }

      int totalNumberOfHistoryEventsDeleted = 0;
      while (!historyEventsToClean.isEmpty()) {
        int upperLimit = batchSize;
        if (upperLimit > historyEventsToClean.size()) {
          upperLimit = historyEventsToClean.size();
        }
        totalNumberOfHistoryEventsDeleted +=
            deleteHistoryEventsTransactionally(historyEventsToClean.subList(0, upperLimit));
        historyEventsToClean.subList(0, upperLimit).clear();
      }
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
    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.scheduleNextCleanupJob();
  }

  private List<HistoryEventImpl> filterSameParentBusinessHistoryEventsQualifiedToClean(
      SimpleHistoryServiceImpl simpleHistoryService, List<HistoryEventImpl> historyEventsToClean) {

    Map<String, Long> eventsToCleanForParentBusinessCount = new HashMap<>();

    historyEventsToClean.forEach(
        event ->
            eventsToCleanForParentBusinessCount.merge(
                event.getParentBusinessProcessId(), 1L, Long::sum));

    Predicate<HistoryEventImpl> noCompletedEventsUnderMinimumAgeExistInSameParentBusiness =
        event ->
            simpleHistoryService
                    .createHistoryQuery()
                    .parentBusinessProcessIdIn(event.getParentBusinessProcessId())
                    .eventTypeIn("TASK_COMPLETED")
                    .count()
                == eventsToCleanForParentBusinessCount.get(event.getParentBusinessProcessId());

    Predicate<HistoryEventImpl> allTasksCleanedSameParentBusiness =
        e ->
            taskanaEngineImpl
                    .getTaskService()
                    .createTaskQuery()
                    .parentBusinessProcessIdIn(e.getParentBusinessProcessId())
                    .count()
                == 0;

    return historyEventsToClean.stream()
        .filter(
            noCompletedEventsUnderMinimumAgeExistInSameParentBusiness.and(
                allTasksCleanedSameParentBusiness))
        .collect(Collectors.toList());
  }

  private int deleteHistoryEventsTransactionally(List<HistoryEventImpl> historyEventsToBeDeleted) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteHistoryEventsTransactionally(historyEventsToBeDeleted = {})",
          historyEventsToBeDeleted);
    }

    int deletedEventsCount = 0;
    if (txProvider != null) {
      int count =
          (Integer)
              txProvider.executeInTransaction(
                  () -> {
                    try {
                      return deleteEvents(historyEventsToBeDeleted);
                    } catch (Exception e) {
                      LOGGER.warn("Could not delete history events.", e);
                      return 0;
                    }
                  });
      LOGGER.debug("exit from deleteHistoryEventsTransactionally(), returning {}", count);
      return count;
    } else {
      try {
        deletedEventsCount = deleteEvents(historyEventsToBeDeleted);
      } catch (Exception e) {
        LOGGER.warn("Could not delete history events.", e);
      }
    }
    LOGGER.debug(
        "exit from deleteHistoryEventsTransactionally()(), returning {}", deletedEventsCount);
    return deletedEventsCount;
  }

  private int deleteEvents(List<HistoryEventImpl> historyEventsToBeDeleted)
      throws InvalidArgumentException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteEvents(historyEventsToBeDeleted = {})", historyEventsToBeDeleted);
    }

    List<String> taskIdsOfEventsToBeDeleted =
        historyEventsToBeDeleted.stream()
            .map(HistoryEventImpl::getTaskId)
            .collect(Collectors.toList());

    SimpleHistoryServiceImpl simpleHistoryService =
        (SimpleHistoryServiceImpl) taskanaHistoryEngine.getTaskanaHistoryService();

    String[] taskIdsArray = new String[taskIdsOfEventsToBeDeleted.size()];
    int deletedTasksCount =
        (int)
            simpleHistoryService
                .createHistoryQuery()
                .taskIdIn(taskIdsOfEventsToBeDeleted.toArray(taskIdsArray))
                .count();

    simpleHistoryService.deleteHistoryEventsByTaskIds(taskIdsOfEventsToBeDeleted);

    LOGGER.debug("{} events deleted.", deletedTasksCount);

    LOGGER.debug("exit from deleteEvents(), returning {}", taskIdsOfEventsToBeDeleted.size());
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
        props.load(new FileInputStream(propertiesFile));
        LOGGER.debug("taskana properties were loaded from file {}.", propertiesFile);
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
