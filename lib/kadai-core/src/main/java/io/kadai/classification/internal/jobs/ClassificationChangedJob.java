package io.kadai.classification.internal.jobs;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.util.CollectionUtil;
import io.kadai.task.internal.TaskServiceImpl;
import io.kadai.task.internal.jobs.TaskRefreshJob;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class executes a job of type {@linkplain
 * io.kadai.classification.internal.jobs.ClassificationChangedJob}.
 */
public class ClassificationChangedJob extends AbstractKadaiJob {

  public static final String CLASSIFICATION_ID = "classificationId";
  public static final String PRIORITY_CHANGED = "priorityChanged";
  public static final String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationChangedJob.class);
  private static final String TASK_IDS = "taskIds";
  private final String classificationId;
  private final boolean priorityChanged;
  private final boolean serviceLevelChanged;

  public ClassificationChangedJob(
      KadaiEngine engine, KadaiTransactionProvider txProvider, ScheduledJob job) {
    super(engine, txProvider, job, false);
    Map<String, String> args = job.getArguments();
    classificationId = args.get(CLASSIFICATION_ID);
    priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
    serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
  }

  @Override
  public void execute() throws KadaiException {
    LOGGER.info("Running ClassificationChangedJob for classification ({})", classificationId);
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) kadaiEngineImpl.getTaskService();
      List<String> affectedTaskIds =
          taskService.findTasksIdsAffectedByClassificationChange(classificationId);
      if (!affectedTaskIds.isEmpty()) {
        scheduleTaskRefreshJobs(affectedTaskIds);
      }
      LOGGER.info("ClassificationChangedJob ended successfully.");
    } catch (Exception e) {
      throw new SystemException("Error while processing ClassificationChangedJob.", e);
    }
  }

  @Override
  protected String getType() {
    return ClassificationChangedJob.class.getName();
  }

  private void scheduleTaskRefreshJobs(List<String> affectedTaskIds) {
    int batchSize = kadaiEngineImpl.getConfiguration().getJobBatchSize();
    Collection<List<String>> affectedTaskBatches =
        CollectionUtil.partitionBasedOnSize(affectedTaskIds, batchSize);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Creating {} TaskRefreshJobs out of {} affected tasks "
              + "with a maximum number of {} tasks each. ",
          affectedTaskBatches.size(),
          affectedTaskIds.size(),
          batchSize);
    }
    for (List<String> taskIdBatch : affectedTaskBatches) {
      Map<String, String> args = new HashMap<>();
      if (!taskIdBatch.isEmpty()) {
        String taskIds = String.join(",", affectedTaskIds);
        args.put(TASK_IDS, taskIds);
        args.put(PRIORITY_CHANGED, Boolean.toString(priorityChanged));
        args.put(SERVICE_LEVEL_CHANGED, Boolean.toString(serviceLevelChanged));
        ScheduledJob job = new ScheduledJob();
        job.setType(TaskRefreshJob.class.getName());
        job.setArguments(args);
        kadaiEngineImpl.getJobService().createJob(job);
      }
    }
  }

  @Override
  public String toString() {
    return "ClassificationChangedJob [classificationId="
        + classificationId
        + ", priorityChanged="
        + priorityChanged
        + ", serviceLevelChanged="
        + serviceLevelChanged
        + ", firstRun="
        + firstRun
        + ", runEvery="
        + runEvery
        + ", kadaiEngineImpl="
        + kadaiEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + "]";
  }
}
