/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.classification.internal.jobs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.task.internal.jobs.TaskRefreshJob;

/**
 * This class executes a job of type {@linkplain
 * pro.taskana.classification.internal.jobs.ClassificationChangedJob}.
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

  public ClassificationChangedJob(
      TaskanaEngine engine, TaskanaTransactionProvider txProvider, ScheduledJob job) {
    super(engine, txProvider, job, false);
    Map<String, String> args = job.getArguments();
    classificationId = args.get(CLASSIFICATION_ID);
    priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
    serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
  }

  @Override
  public void execute() throws TaskanaException {
    LOGGER.info("Running ClassificationChangedJob for classification ({})", classificationId);
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
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
    int batchSize = taskanaEngineImpl.getConfiguration().getJobBatchSize();
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
        taskanaEngineImpl.getJobService().createJob(job);
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
        + ", taskanaEngineImpl="
        + taskanaEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + "]";
  }
}
