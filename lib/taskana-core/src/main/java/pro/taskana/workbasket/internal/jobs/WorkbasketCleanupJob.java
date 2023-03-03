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
package pro.taskana.workbasket.internal.jobs;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;

/**
 * Job to cleanup completed workbaskets after a period of time if there are no pending tasks
 * associated to the workbasket.
 */
public class WorkbasketCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketCleanupJob.class);

  private final int batchSize;

  public WorkbasketCleanupJob(
      TaskanaEngine taskanaEngine, TaskanaTransactionProvider txProvider, ScheduledJob job) {
    super(taskanaEngine, txProvider, job, true);
    batchSize = taskanaEngine.getConfiguration().getJobBatchSize();
  }

  @Override
  public void execute() throws TaskanaException {
    LOGGER.info("Running job to delete all workbaskets marked for deletion");
    try {
      List<String> workbasketsMarkedForDeletion = getWorkbasketsMarkedForDeletion();
      int totalNumberOfWorkbasketDeleted =
          CollectionUtil.partitionBasedOnSize(workbasketsMarkedForDeletion, batchSize).stream()
              .mapToInt(this::deleteWorkbasketsTransactionally)
              .sum();
      LOGGER.info(
          "Job ended successfully. {} workbaskets deleted.", totalNumberOfWorkbasketDeleted);
    } catch (Exception e) {
      throw new SystemException("Error while processing WorkbasketCleanupJob.", e);
    }
  }

  @Override
  protected String getType() {
    return WorkbasketCleanupJob.class.getName();
  }

  private List<String> getWorkbasketsMarkedForDeletion() {

    return taskanaEngineImpl
        .getWorkbasketService()
        .createWorkbasketQuery()
        .markedForDeletion(true)
        .listValues(WorkbasketQueryColumnName.ID, BaseQuery.SortDirection.ASCENDING);
  }

  private int deleteWorkbasketsTransactionally(List<String> workbasketsToBeDeleted) {
    return TaskanaTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> {
          try {
            return deleteWorkbaskets(workbasketsToBeDeleted);
          } catch (Exception e) {
            LOGGER.warn("Could not delete workbaskets.", e);
            return 0;
          }
        });
  }

  private int deleteWorkbaskets(List<String> workbasketsToBeDeleted)
      throws InvalidArgumentException, MismatchedRoleException {

    BulkOperationResults<String, TaskanaException> results =
        taskanaEngineImpl.getWorkbasketService().deleteWorkbaskets(workbasketsToBeDeleted);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "{} workbasket deleted.", workbasketsToBeDeleted.size() - results.getFailedIds().size());
    }
    for (String failedId : results.getFailedIds()) {
      LOGGER.warn(
          "Workbasket with id {} could not be deleted. Reason:",
          failedId,
          results.getErrorForId(failedId));
    }
    return workbasketsToBeDeleted.size() - results.getFailedIds().size();
  }
}
