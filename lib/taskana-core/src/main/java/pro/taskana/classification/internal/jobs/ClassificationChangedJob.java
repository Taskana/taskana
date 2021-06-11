package pro.taskana.classification.internal.jobs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.task.internal.TaskServiceImpl;

/**
 * This class executes a job of type {@linkplain
 * pro.taskana.common.api.ScheduledJob.Type#CLASSIFICATION_CHANGED_JOB}.
 */
public class ClassificationChangedJob extends AbstractTaskanaJob {

  public static final String CLASSIFICATION_ID = "classificationId";
  public static final String PRIORITY_CHANGED = "priorityChanged";
  public static final String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationChangedJob.class);
  private static final String TASK_IDS = "taskIds";
  private final String classificationId;
  private final boolean priorityChanged;
  private final boolean serviceLevelChanged;

  public ClassificationChangedJob(TaskanaEngine engine, ScheduledJob job) {
    super(engine, job, false);
    Map<String, String> args = job.getArguments();
    classificationId = args.get(CLASSIFICATION_ID);
    priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
    serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
  }

  @Override
  protected Type getJobType() {
    return Type.CLASSIFICATION_CHANGED_JOB;
  }

  @Override
  protected void executeJob() throws TaskanaException {
    LOGGER.info("Running ClassificationChangedJob for classification ({})", classificationId);
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngine.getTaskService();
      List<String> affectedTaskIds =
          taskService.findTasksIdsAffectedByClassificationChange(classificationId);
      if (!affectedTaskIds.isEmpty()) {
        scheduleTaskRefreshJobs(affectedTaskIds);
      }
      LOGGER.info("ClassificationChangedJob ended successfully.");
    } catch (Exception e) {
      throw new TaskanaException("Error while processing ClassificationChangedJob.", e);
    }
  }

  private void scheduleTaskRefreshJobs(List<String> affectedTaskIds) {
    int batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
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
        job.setType(ScheduledJob.Type.TASK_REFRESH_JOB);
        job.setArguments(args);
        taskanaEngine.getJobService().createJob(job);
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
        + "]";
  }
}
