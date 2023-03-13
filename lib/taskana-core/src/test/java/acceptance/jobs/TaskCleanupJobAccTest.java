package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskRefreshJob;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskCleanupJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CleanCompletedTasksUntilDate() throws Exception {
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .taskCleanupJobAllCompletedSameParentBusiness(false)
            .build();
    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    TaskService taskService = taskanaEngine.getTaskService();
    String taskId = createAndInsertTask(taskService, null);
    taskService.claim(taskId);
    taskService.completeTask(taskId);

    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(100);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(81);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDateWithSameParentBusiness() throws Exception {
    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(99);

    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngine.getConfiguration())
            .taskCleanupJobAllCompletedSameParentBusiness(true)
            .build();

    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    List<TaskSummary> tasks =
        taskanaEngine
            .getTaskService()
            .createTaskQuery()
            .parentBusinessProcessIdIn("DOC_0000000000000000006")
            .list();
    List<String> ids = new ArrayList<>();
    tasks.forEach(
        item -> {
          if (item.getCompleted() == null) {
            ids.add(item.getId());
          }
        });
    taskanaEngine.getTaskService().deleteTasks(ids);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskanaEngine.getTaskService().createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(79);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
    String taskId = createAndInsertTask(taskService, null);
    taskService.claim(taskId);
    taskService.completeTask(taskId);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    Task completedCreatedTask = taskService.getTask(taskId);
    assertThat(completedCreatedTask).isNotNull();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteOldTaskCleanupJobs_When_InitializingSchedule() throws Exception {

    for (int i = 0; i < 10; i++) {
      ScheduledJob job = new ScheduledJob();
      job.setType(TaskCleanupJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(TaskRefreshJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(ClassificationChangedJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper(taskanaEngine).findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> taskCleanupJobs =
        jobsToRun.stream()
            .filter(scheduledJob -> scheduledJob.getType().equals(TaskCleanupJob.class.getName()))
            .collect(Collectors.toList());

    AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskCleanupJob.class);

    jobsToRun = getJobMapper(taskanaEngine).findJobsToRun(Instant.now());

    assertThat(jobsToRun).doesNotContainAnyElementsOf(taskCleanupJobs);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest>
      should_DeleteCompletedTaskWithParentBusinessEmptyOrNull_When_RunningCleanupJob()
          throws Exception {
    Iterator<String> iterator = Arrays.asList("", null).iterator();

    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngine.getConfiguration())
            .taskCleanupJobAllCompletedSameParentBusiness(true)
            .cleanupJobMinimumAge(Duration.ofMillis(1))
            .build();
    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingConsumer<String> test =
        parentBusinessId -> {
          String taskId1 = createAndInsertTask(taskService, parentBusinessId);
          taskService.claim(taskId1);
          taskService.completeTask(taskId1);
          String taskId2 = createAndInsertTask(taskService, parentBusinessId);
          taskService.claim(taskId2);

          job.run();
          List<TaskSummary> tasksAfterCleaning =
              taskService.createTaskQuery().idIn(taskId1, taskId2).list();

          assertThat(tasksAfterCleaning).extracting(TaskSummary::getId).containsExactly(taskId2);
        };

    return DynamicTest.stream(iterator, c -> "for parentBusinessProcessId = '" + c + "'", test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetNextScheduledJobBasedOnDueDateOfPredecessor_When_RunningTaskCleanupJob()
      throws Exception {
    JobMapper jobMapper = getJobMapper(AbstractAccTest.taskanaEngine);
    List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());
    assertThat(jobsToRun).isEmpty();

    Instant firstDue = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    ScheduledJob scheduledJob = new ScheduledJob();
    scheduledJob.setType(TaskCleanupJob.class.getName());
    scheduledJob.setDue(firstDue);

    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.createJob(scheduledJob);
    jobsToRun = jobMapper.findJobsToRun(Instant.now());

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue);

    JobRunner runner = new JobRunner(taskanaEngine);
    runner.runJobs();
    Duration runEvery = taskanaConfiguration.getCleanupJobRunEvery();
    jobsToRun = jobMapper.findJobsToRun(Instant.now().plus(runEvery));

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue.plus(runEvery));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ScheduleNextJobAccordingToFirstRun_When_PreviousJobNotExisting() throws Exception {
    Instant firstRun = Instant.now().minus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
    Duration runEvery = Duration.ofMinutes(5);
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .cleanupJobRunEvery(runEvery)
            .cleanupJobFirstRun(firstRun)
            .jobSchedulerEnabled(true)
            .jobSchedulerInitialStartDelay(1)
            .jobSchedulerPeriod(1)
            .jobSchedulerPeriodTimeUnit(TimeUnit.SECONDS)
            .jobSchedulerEnableTaskCleanupJob(true)
            .build();

    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);

    List<ScheduledJob> nextJobs =
        getJobMapper(taskanaEngine).findJobsToRun(Instant.now().plus(runEvery));
    assertThat(nextJobs).extracting(ScheduledJob::getDue).containsExactly(firstRun.plus(runEvery));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FindNoJobsToRunUntilFirstRunIsReached_When_CleanupScheduleIsInitialized()
      throws Exception {
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngine.getConfiguration())
            .cleanupJobRunEvery(Duration.ofMillis(1))
            .cleanupJobFirstRun(Instant.now().plus(5, ChronoUnit.MINUTES))
            .build();

    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
    AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskCleanupJob.class);

    List<ScheduledJob> nextJobs = getJobMapper(taskanaEngine).findJobsToRun(Instant.now());
    assertThat(nextJobs).isEmpty();
  }

  private String createAndInsertTask(TaskService taskService, String parentBusinessProcessId)
      throws Exception {
    Task newTask = taskService.newTask("user-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setParentBusinessProcessId(parentBusinessProcessId);
    return taskService.createTask(newTask).getId();
  }
}
