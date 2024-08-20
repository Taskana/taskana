package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.KadaiConfiguration.Builder;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.JobMapper;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.jobs.JobScheduler;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
class JobSchedulerExecutionAccTest implements KadaiConfigurationModifier {
  @KadaiInject KadaiConfiguration kadaiConfiguration;
  @KadaiInject TaskService taskService;
  @KadaiInject JobMapper jobMapper;
  WorkbasketSummary workbasket;
  ClassificationSummary classification;
  ObjectReference primaryObjRef;

  @Override
  public Builder modify(Builder builder) {
    return builder
        .taskCleanupJobEnabled(true)
        .jobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
        .jobRunEvery(Duration.ofMillis(1))
        .taskCleanupJobMinimumAge(Duration.ofMillis(10));
  }

  @WithAccessId(user = "businessadmin")
  @BeforeEach
  void setup(WorkbasketService workbasketService, ClassificationService classificationService)
      throws Exception {
    workbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ExecuteAJobSuccessfully() throws Exception {
    Instant timeStampAnyJobIsOverdue = Instant.now().plus(10, ChronoUnit.DAYS);
    KadaiEngine kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.EXPLICIT);
    JobScheduler jobScheduler = new JobScheduler(kadaiEngine, new FakeClock());
    TaskBuilder.newTask()
        .workbasketSummary(workbasket)
        .classificationSummary(classification)
        .primaryObjRef(primaryObjRef)
        .state(TaskState.COMPLETED)
        .completed(Instant.now().minus(5, ChronoUnit.DAYS))
        .buildAndStoreAsSummary(taskService);
    final List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);

    Thread.sleep(2); // to make sure that TaskCleanupJob is overdue
    jobScheduler.start();

    List<TaskSummary> existingTasks = taskService.createTaskQuery().list();
    assertThat(existingTasks).isEmpty();
    List<ScheduledJob> jobsToRunAfter = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);
    assertThat(jobsToRunAfter).isNotEmpty().doesNotContainAnyElementsOf(jobsToRun);
  }

  public static class AlwaysFailJob extends AbstractKadaiJob {

    public AlwaysFailJob(
        KadaiEngine kadaiEngine, KadaiTransactionProvider txProvider, ScheduledJob job) {
      super(kadaiEngine, txProvider, job, true);
    }

    @Override
    protected String getType() {
      return AlwaysFailJob.class.getName();
    }

    @Override
    protected void execute() {
      throw new SystemException("I always fail. Muahhahaa!");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class AJobFails implements KadaiConfigurationModifier {

    @KadaiInject KadaiConfiguration kadaiConfiguration;
    @KadaiInject TaskService taskService;
    @KadaiInject JobMapper jobMapper;
    WorkbasketSummary workbasket;
    ClassificationSummary classification;
    ObjectReference primaryObjRef;

    @Override
    public Builder modify(Builder builder) {
      return builder
          .taskCleanupJobEnabled(false)
          .jobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
          .jobRunEvery(Duration.ofMillis(1))
          .taskCleanupJobMinimumAge(Duration.ofMillis(10))
          .customJobs(Set.of(AlwaysFailJob.class.getName(), TaskCleanupJob.class.getName()));
    }

    @WithAccessId(user = "businessadmin")
    @BeforeEach
    void setup(WorkbasketService workbasketService, ClassificationService classificationService)
        throws Exception {
      workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      classification =
          DefaultTestEntities.defaultTestClassification()
              .buildAndStoreAsSummary(classificationService);
      primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ContinueExecutingJobs_When_ASingeJobFails() throws Exception {
      Instant timeStampAnyJobIsOverdue = Instant.now().plus(10, ChronoUnit.DAYS);
      KadaiEngine kadaiEngine =
          KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.EXPLICIT);
      JobScheduler jobScheduler = new JobScheduler(kadaiEngine, new FakeClock());
      TaskBuilder.newTask()
          .workbasketSummary(workbasket)
          .classificationSummary(classification)
          .primaryObjRef(primaryObjRef)
          .state(TaskState.COMPLETED)
          .completed(Instant.now().minus(5, ChronoUnit.DAYS))
          .buildAndStoreAsSummary(taskService);
      final List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);

      Thread.sleep(2); // to make sure that TaskCleanupJob is overdue
      jobScheduler.start();

      List<TaskSummary> existingTasks = taskService.createTaskQuery().list();
      assertThat(existingTasks).isEmpty();
      List<ScheduledJob> jobsToRunAfter = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);
      assertThat(jobsToRunAfter).isNotEmpty().doesNotContainAnyElementsOf(jobsToRun);
      assertThat(jobsToRunAfter)
          .filteredOn(job -> AlwaysFailJob.class.getName().equals(job.getType()))
          .extracting(ScheduledJob::getRetryCount)
          .containsExactly(2);
    }
  }
}
