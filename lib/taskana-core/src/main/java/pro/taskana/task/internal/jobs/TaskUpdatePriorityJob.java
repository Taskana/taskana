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
package pro.taskana.task.internal.jobs;

import static pro.taskana.common.internal.util.CollectionUtil.partitionBasedOnSize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;

/** Job to recalculate the priority of each task that is not in an endstate. */
public class TaskUpdatePriorityJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskUpdatePriorityJob.class);

  private final int batchSize;
  private final boolean isJobActive;

  public TaskUpdatePriorityJob(TaskanaEngine taskanaEngine) {
    this(taskanaEngine, null, null);
  }

  public TaskUpdatePriorityJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob, true);
    batchSize = taskanaEngine.getConfiguration().getPriorityJobBatchSize();
    isJobActive = taskanaEngine.getConfiguration().isPriorityJobActive();
    runEvery = taskanaEngine.getConfiguration().getPriorityJobRunEvery();
    firstRun = taskanaEngine.getConfiguration().getPriorityJobFirstRun();
  }

  @Override
  public void execute() {
    if (!isJobActive()) {
      LOGGER.debug("Job to update task priority is not active.");
      return;
    }
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngineImpl);
    LOGGER.info("Running job to calculate all non finished task priorities");
    try {
      partitionBasedOnSize(worker.getAllRelevantTaskIds(), getBatchSize())
          .forEach(worker::executeBatch);
      LOGGER.info("Job to update priority of tasks has finished.");
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskUpdatePriorityJob.", e);
    }
  }

  public boolean isJobActive() {
    return isJobActive;
  }

  public int getBatchSize() {
    return batchSize;
  }

  @Override
  protected String getType() {
    return TaskUpdatePriorityJob.class.getName();
  }

  @Override
  public String toString() {
    return "TaskUpdatePriorityJob [firstRun="
        + firstRun
        + ", runEvery="
        + runEvery
        + ", taskanaEngineImpl="
        + taskanaEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + ", batchSize="
        + batchSize
        + "]";
  }
}
