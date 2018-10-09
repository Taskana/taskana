package pro.taskana.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BaseQuery;
import pro.taskana.BulkOperationResults;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * Job to cleanup completed workbaskets after a period of time if there are no pending tasks associated to the workbasket.
 */
public class WorkbasketCleanupJob extends AbstractTaskanaJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

    // Parameter
    private Instant firstRun;
    private Duration runEvery;
    private int batchSize;

    public WorkbasketCleanupJob(TaskanaEngine taskanaEngine,
        TaskanaTransactionProvider<Object> txProvider, ScheduledJob job) {
        super(taskanaEngine, txProvider, job);
        firstRun = taskanaEngine.getConfiguration().getCleanupJobFirstRun();
        runEvery = taskanaEngine.getConfiguration().getCleanupJobRunEvery();
        batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
    }

    @Override
    public void run() throws TaskanaException {
        LOGGER.info("Running job to delete all workbaskets marked for deletion");
        try {
            List<String> workbasketsMarkedForDeletion = getWorkbasketsMarkedForDeletion();
            int totalNumberOfWorkbasketDeleted = 0;
            while (workbasketsMarkedForDeletion.size() > 0) {
                int upperLimit = batchSize;
                if (upperLimit > workbasketsMarkedForDeletion.size()) {
                    upperLimit = workbasketsMarkedForDeletion.size();
                }
                totalNumberOfWorkbasketDeleted += deleteWorkbasketsTransactionally(
                    workbasketsMarkedForDeletion.subList(0, upperLimit));
                workbasketsMarkedForDeletion.subList(0, upperLimit).clear();
            }
            LOGGER.info("Job ended successfully. {} workbaskets deleted.", totalNumberOfWorkbasketDeleted);
        } catch (Exception e) {
            throw new TaskanaException("Error while processing WorkbasketCleanupJob.", e);
        } finally {
            scheduleNextCleanupJob();
        }
    }

    private List<String> getWorkbasketsMarkedForDeletion() throws InvalidArgumentException {
        List<String> workbasketList = taskanaEngineImpl.getWorkbasketService()
            .createWorkbasketQuery()
            .markedForDeletion(true)
            .listValues("ID", BaseQuery.SortDirection.ASCENDING);
        workbasketList = excludeWorkbasketWithPendingTasks(workbasketList);

        return workbasketList;
    }

    private long getNumberTaskNotCompleted(String workbasketId) {
        return taskanaEngineImpl.getTaskService()
            .createTaskQuery()
            .workbasketIdIn(workbasketId)
            .stateNotIn(
                TaskState.COMPLETED)
            .count();
    }

    private List<String> excludeWorkbasketWithPendingTasks(List<String> workbasketList)
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngineImpl.getTaskService();
        ArrayList<String> workbasketDeletionList = new ArrayList<>();
        ArrayList<String> workbasketWithNonCompletedTasksList = new ArrayList<>();

        if (!workbasketList.isEmpty()) {
            Iterator<String> iterator = workbasketList.iterator();
            while (iterator.hasNext()) {
                String workbasketId = iterator.next();
                if (getNumberTaskNotCompleted(workbasketId) == 0) {
                    workbasketDeletionList.add(workbasketId);
                } else {
                    workbasketWithNonCompletedTasksList.add(workbasketId);
                }
            }
        }
        LOGGER.info("workbasket marked for deletion with non completed tasks {}.",
            LoggerUtils.listToString(workbasketWithNonCompletedTasksList));
        return workbasketDeletionList;
    }

    private int deleteWorkbasketsTransactionally(List<String> workbasketsToBeDeleted) {
        int deletedWorkbasketsCount = 0;
        if (txProvider != null) {
            Integer count = (Integer) txProvider.executeInTransaction(() -> {
                try {
                    return new Integer(deleteWorkbaskets(workbasketsToBeDeleted));
                } catch (Exception e) {
                    LOGGER.warn("Could not delete workbaskets.", e);
                    return new Integer(0);
                }
            });
            return count.intValue();
        } else {
            try {
                deletedWorkbasketsCount = deleteWorkbaskets(workbasketsToBeDeleted);
            } catch (Exception e) {
                LOGGER.warn("Could not delete workbaskets.", e);
            }
        }
        return deletedWorkbasketsCount;
    }

    private int deleteWorkbaskets(List<String> workbasketsToBeDeleted)
        throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException, WorkbasketInUseException {

        BulkOperationResults<String, TaskanaException> results = taskanaEngineImpl.getWorkbasketService()
            .deleteWorkbaskets(workbasketsToBeDeleted);
        LOGGER.debug("{} workbasket deleted.", workbasketsToBeDeleted.size() - results.getFailedIds().size());
        for (String failedId : results.getFailedIds()) {
            LOGGER.warn("Workbasket with id {} could not be deleted. Reason: {}", failedId,
                results.getErrorForId(failedId));
        }
        return workbasketsToBeDeleted.size() - results.getFailedIds().size();
    }

    private void scheduleNextCleanupJob() {
        LOGGER.debug("Entry to scheduleNextCleanupJob.");
        ScheduledJob job = new ScheduledJob();
        job.setType(ScheduledJob.Type.WORKBASKETCLEANUPJOB);
        job.setDue(getNextDueForWorkbasketCleanupJob());
        taskanaEngineImpl.getJobService().createJob(job);
        LOGGER.debug("Exit from scheduleNextCleanupJob.");
    }

    private Instant getNextDueForWorkbasketCleanupJob() {
        Instant nextRunAt = firstRun;
        while (nextRunAt.isBefore(Instant.now())) {
            nextRunAt = nextRunAt.plus(runEvery);
        }
        LOGGER.info("Scheduling next run of the WorkbasketCleanupJob for {}", nextRunAt);
        return nextRunAt;
    }

    /**
     * Initializes the WorkbasketCleanupJob schedule. <br>
     * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
     *
     * @param taskanaEngine
     */
    public static void initializeSchedule(TaskanaEngine taskanaEngine) {
        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.scheduleNextCleanupJob();
    }
}
