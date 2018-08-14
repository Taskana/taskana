package pro.taskana.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.*;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * Job to cleanup completed tasks after a period of time.
 */
public class TaskCleanupJob extends AbstractTaskanaJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

    private static BaseQuery.SortDirection asc = BaseQuery.SortDirection.ASCENDING;

    // Parameter
    private Instant firstRun;
    private Duration runEvery;
    private Duration minimumAge;
    private int batchSize;
    private boolean allCompletedSameParentBusiness;

    public TaskCleanupJob(TaskanaEngine taskanaEngine, TaskanaTransactionProvider<Object> txProvider,
        ScheduledJob scheduledJob) {
        super(taskanaEngine, txProvider, scheduledJob);
        firstRun = taskanaEngine.getConfiguration().getTaskCleanupJobFirstRun();
        runEvery = taskanaEngine.getConfiguration().getTaskCleanupJobRunEvery();
        minimumAge = taskanaEngine.getConfiguration().getTaskCleanupJobMinimumAge();
        batchSize = taskanaEngine.getConfiguration().getMaxNumberOfTaskUpdatesPerTransaction();
        allCompletedSameParentBusiness = taskanaEngine.getConfiguration().isTaskCleanupJobAllCompletedSameParentBusiness();
    }

    @Override
    public void run() throws TaskanaException {
        Instant completedBefore = Instant.now().minus(minimumAge);
        LOGGER.info("Running job to delete all tasks completed before ({})", completedBefore.toString());
        try {
            List<TaskSummary> tasksCompletedBefore = getTasksCompletedBefore(completedBefore);
            int totalNumberOfTasksCompleted = 0;
            while (tasksCompletedBefore.size() > 0) {
                int upperLimit = batchSize;
                if (upperLimit > tasksCompletedBefore.size()) {
                    upperLimit = tasksCompletedBefore.size();
                }
                totalNumberOfTasksCompleted += deleteTasksTransactionally(tasksCompletedBefore.subList(0, upperLimit));
                tasksCompletedBefore.subList(0, upperLimit).clear();
            }
            scheduleNextCleanupJob();
            LOGGER.info("Job ended successfully. {} tasks deleted.", totalNumberOfTasksCompleted);
        } catch (Exception e) {
            throw new TaskanaException("Error while processing TaskCleanupJob.", e);
        }
    }

    private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {
        List<TaskSummary> taskList = taskanaEngineImpl.getTaskService()
                .createTaskQuery()
                .completedWithin(new TimeInterval(null, untilDate))
                .orderByBusinessProcessId(asc)
                .list();

        if (allCompletedSameParentBusiness) {
            Map<String, Long> numberParentTasksShouldHave = new HashMap<>();
            Map<String, Long> countParentTask = new HashMap<>();
            for (TaskSummary task : taskList) {
                numberParentTasksShouldHave.put(task.getParentBusinessProcessId(), taskanaEngineImpl.getTaskService().createTaskQuery().parentBusinessProcessIdIn(task.getParentBusinessProcessId()).count());
                countParentTask.merge(task.getParentBusinessProcessId(), 1L, Long::sum);
            }

            List<String> idsList = new ArrayList<>();
            numberParentTasksShouldHave.forEach((k, v) -> {
                if (v.compareTo(countParentTask.get(k)) == 0) {
                    idsList.add(k);
                }
            });

            if (idsList.isEmpty()) {
                return new ArrayList<>();
            }

            String[] ids = new String[idsList.size()];
            ids = idsList.toArray(ids);
            taskList = taskanaEngineImpl.getTaskService()
                    .createTaskQuery()
                    .parentBusinessProcessIdIn(ids)
                    .list();
        }

        return taskList;
    }

    private int deleteTasksTransactionally(List<TaskSummary> tasksToBeDeleted) {
        int deletedTaskCount = 0;
        if (txProvider != null) {
            Integer count = (Integer) txProvider.executeInTransaction(() -> {
                try {
                    return new Integer(deleteTasks(tasksToBeDeleted));
                } catch (Exception e) {
                    LOGGER.warn("Could not delete tasks.", e);
                    return new Integer(0);
                }
            });
            return count.intValue();
        } else {
            try {
                deletedTaskCount = deleteTasks(tasksToBeDeleted);
            } catch (Exception e) {
                LOGGER.warn("Could not delete tasks.", e);
            }
        }
        return deletedTaskCount;
    }

    private int deleteTasks(List<TaskSummary> tasksToBeDeleted) throws InvalidArgumentException {
        List<String> tasksIdsToBeDeleted = tasksToBeDeleted.stream()
            .map(task -> task.getTaskId())
            .collect(Collectors.toList());
        BulkOperationResults<String, TaskanaException> results = taskanaEngineImpl.getTaskService()
            .deleteTasks(tasksIdsToBeDeleted);
        LOGGER.debug("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
        for (String failedId : results.getFailedIds()) {
            LOGGER.warn("Task with id {} could not be deleted. Reason: {}", failedId, results.getErrorForId(failedId));
        }
        return tasksIdsToBeDeleted.size() - results.getFailedIds().size();
    }

    public void scheduleNextCleanupJob() {
        LOGGER.debug("Entry to scheduleNextCleanupJob.");
        ScheduledJob job = new ScheduledJob();
        job.setType(ScheduledJob.Type.TASKCLEANUPJOB);
        job.setDue(getNextDueForTaskCleanupJob());
        taskanaEngineImpl.getJobService().createJob(job);
        LOGGER.debug("Exit from scheduleNextCleanupJob.");
    }

    private Instant getNextDueForTaskCleanupJob() {
        Instant nextRunAt = firstRun;
        while (nextRunAt.isBefore(Instant.now())) {
            nextRunAt = nextRunAt.plus(runEvery);
        }
        LOGGER.info("Scheduling next run of the TaskCleanupJob for {}", nextRunAt);
        return nextRunAt;
    }

    /**
     * Initializes the TaskCleanupJob schedule. <br>
     * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
     *
     * @param taskanaEngine
     */
    public static void initializeSchedule(TaskanaEngine taskanaEngine) {
        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
        job.scheduleNextCleanupJob();
    }

}
