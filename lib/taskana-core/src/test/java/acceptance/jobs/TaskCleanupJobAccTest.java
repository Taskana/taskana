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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.TaskCleanupJob;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskCleanupJobAccTest extends AbstractAccTest {

  TaskService taskService;

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CleanCompletedTasksUntilDate() throws Exception {
    String taskId = createAndInsertTask(null);
    taskService.claim(taskId);
    taskService.completeTask(taskId);

    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(89);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(70);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDateWithSameParentBussiness() throws Exception {
    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(88);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    List<TaskSummary> tasks =
        taskService.createTaskQuery().parentBusinessProcessIdIn("DOC_0000000000000000006").list();
    List<String> ids = new ArrayList<>();
    tasks.forEach(
        item -> {
          if (item.getCompleted() == null) {
            ids.add(item.getId());
          }
        });
    taskService.deleteTasks(ids);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(68);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
    String taskId = createAndInsertTask(null);
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
      job.setType(ScheduledJob.Type.TASKCLEANUPJOB);
      taskanaEngine.getJobService().createJob(job);
      job.setType(Type.UPDATETASKSJOB);
      taskanaEngine.getJobService().createJob(job);
      job.setType(Type.CLASSIFICATIONCHANGEDJOB);
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper().findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> taskCleanupJobs =
        jobsToRun.stream()
            .filter(scheduledJob -> scheduledJob.getType().equals(Type.TASKCLEANUPJOB))
            .collect(Collectors.toList());

    TaskCleanupJob.initializeSchedule(taskanaEngine);

    jobsToRun = getJobMapper().findJobsToRun(Instant.now());

    assertThat(jobsToRun).doesNotContainAnyElementsOf(taskCleanupJobs);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest>
      should_DeleteCompletedTaskWithParentBusinessEmptyOrNull_When_RunningCleanupJob() {
    Iterator<String> iterator = Arrays.asList("", null).iterator();

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);
    taskanaEngine.getConfiguration().setCleanupJobMinimumAge(Duration.ZERO);
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);

    ThrowingConsumer<String> test =
        parentBusinessId -> {
          String taskId1 = createAndInsertTask(parentBusinessId);
          taskService.claim(taskId1);
          taskService.completeTask(taskId1);
          String taskId2 = createAndInsertTask(parentBusinessId);
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
    JobMapper jobMapper = getJobMapper();
    List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());
    assertThat(jobsToRun).isEmpty();

    Instant firstDue = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    ScheduledJob scheduledJob = new ScheduledJob();
    scheduledJob.setType(ScheduledJob.Type.TASKCLEANUPJOB);
    scheduledJob.setDue(firstDue);

    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.createJob(scheduledJob);
    jobsToRun = jobMapper.findJobsToRun(Instant.now());

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue);

    JobRunner runner = new JobRunner(taskanaEngine);
    runner.runJobs();
    Duration runEvery = taskanaEngineConfiguration.getCleanupJobRunEvery();
    jobsToRun = jobMapper.findJobsToRun(Instant.now().plus(runEvery));

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue.plus(runEvery));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ScheduleNextJobAccordingToFirstRun_When_PreviousJobNotExisting() throws Exception {
    Instant firstRun = Instant.now().minus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
    Duration runEvery = Duration.ofMinutes(5);
    taskanaEngineConfiguration.setCleanupJobRunEvery(runEvery);
    taskanaEngineConfiguration.setCleanupJobFirstRun(firstRun);

    TaskCleanupJob.initializeSchedule(taskanaEngine);

    List<ScheduledJob> nextJobs = getJobMapper().findJobsToRun(Instant.now().plus(runEvery));
    assertThat(nextJobs).extracting(ScheduledJob::getDue).containsExactly(firstRun.plus(runEvery));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FindNoJobsToRunUntilFirstRunIsReached_When_CleanupScheduleIsInitialized()
      throws Exception {
    taskanaEngineConfiguration.setCleanupJobRunEvery(Duration.ZERO);
    taskanaEngineConfiguration.setCleanupJobFirstRun(Instant.now().plus(5, ChronoUnit.MINUTES));

    TaskCleanupJob.initializeSchedule(taskanaEngine);

    List<ScheduledJob> nextJobs = getJobMapper().findJobsToRun(Instant.now());
    assertThat(nextJobs).isEmpty();
  }

  private String createAndInsertTask(String parentBusinessProcessId) throws Exception {
    Task newTask = taskService.newTask("user-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setParentBusinessProcessId(parentBusinessProcessId);
    return taskService.createTask(newTask).getId();
  }
}
