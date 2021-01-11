package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
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
    String id = createAndInsertTask(null);
    taskService.claim(id);
    taskService.completeTask(id);

    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(88);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(69);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDateWithSameParentBussiness() throws Exception {
    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(87);

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
    assertThat(totalTasksCount).isEqualTo(67);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
    String id = createAndInsertTask(null);
    taskService.claim(id);
    taskService.completeTask(id);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    Task completedCreatedTask = taskService.getTask(id);
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

    List<ScheduledJob> jobsToRun = getJobMapper().findJobsToRun();

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> taskCleanupJobs =
        jobsToRun.stream()
            .filter(scheduledJob -> scheduledJob.getType().equals(Type.TASKCLEANUPJOB))
            .collect(Collectors.toList());

    TaskCleanupJob.initializeSchedule(taskanaEngine);

    jobsToRun = getJobMapper().findJobsToRun();

    assertThat(jobsToRun).doesNotContainAnyElementsOf(taskCleanupJobs);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteCompletedTaskWithParentBusinessNull_When_RunningCleanupJob() throws Exception {
    String taskId1 = createAndInsertTask(null);
    taskService.claim(taskId1);
    taskService.completeTask(taskId1);
    String taskId2 = createAndInsertTask(null);
    taskService.claim(taskId2);

    long taskCount = taskService.createTaskQuery().idIn(taskId1, taskId2).count();
    assertThat(taskCount).isEqualTo(2);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);
    taskanaEngine.getConfiguration().setCleanupJobMinimumAge(Duration.ZERO);
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    List<TaskSummary> tasksAfterCleaning =
        taskService.createTaskQuery().idIn(taskId1, taskId2).list();
    assertThat(tasksAfterCleaning).hasSize(1);
    assertThat(tasksAfterCleaning.get(0).getId()).isEqualTo(taskId2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteCompletedTaskWithParentBusinessEmpty_When_RunningCleanupJob() throws Exception {
    String taskId1 = createAndInsertTask("");
    taskService.claim(taskId1);
    taskService.completeTask(taskId1);
    String taskId2 = createAndInsertTask("");
    taskService.claim(taskId2);

    long taskCount = taskService.createTaskQuery().idIn(taskId1, taskId2).count();
    assertThat(taskCount).isEqualTo(2);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);
    taskanaEngine.getConfiguration().setCleanupJobMinimumAge(Duration.ZERO);
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    List<TaskSummary> tasksAfterCleaning =
        taskService.createTaskQuery().idIn(taskId1, taskId2).list();
    assertThat(tasksAfterCleaning).hasSize(1);
    assertThat(tasksAfterCleaning.get(0).getId()).isEqualTo(taskId2);
  }

  private String createAndInsertTask(String parentBusinessProcessId) throws Exception {
    Task newTask = taskService.newTask("user-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setParentBusinessProcessId(parentBusinessProcessId);
    String id = taskService.createTask(newTask).getId();
    return id;
  }
}
