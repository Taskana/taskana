package acceptance;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

@ExtendWith(TaskanaIntegrationTestExtension.class)
public class TaskanaDependencyInjectionExtensionTest {

  @Nested
  class TaskanaEngineConfigurationInjectionTest {
    private final TaskanaEngineConfiguration taskanaEngineConfiguration;

    public TaskanaEngineConfigurationInjectionTest(
        TaskanaEngineConfiguration taskanaEngineConfiguration) {
      this.taskanaEngineConfiguration = taskanaEngineConfiguration;
    }

    @Test
    void should_InjectTaskanaEngineConfiguration_When_ConstructorContainsParameter() {
      assertThat(taskanaEngineConfiguration).isNotNull();
    }
  }

  @Nested
  class TaskanaEngineInjectionTest {
    private final TaskanaEngine taskanaEngine;
    private final TaskanaEngineImpl taskanaEngineImpl;

    TaskanaEngineInjectionTest(TaskanaEngine taskanaEngine, TaskanaEngineImpl taskanaEngineImpl) {
      this.taskanaEngine = taskanaEngine;
      this.taskanaEngineImpl = taskanaEngineImpl;
    }

    @Test
    void should_InjectTaskanaEngine_When_ConstructorContainsParameter() {
      assertThat(taskanaEngine).isNotNull();
    }

    @Test
    void should_InjectTaskanaEngineImpl_When_ConstructorContainsParameter() {
      assertThat(taskanaEngineImpl).isNotNull();
    }
  }

  @Nested
  class TaskServiceInjectionTest {
    private final TaskService taskService;
    private final TaskServiceImpl taskServiceImpl;

    TaskServiceInjectionTest(TaskService taskService, TaskServiceImpl taskServiceImpl) {
      this.taskService = taskService;
      this.taskServiceImpl = taskServiceImpl;
    }

    @Test
    void should_InjectTaskService_When_ConstructorContainsParameter() {
      assertThat(taskService).isNotNull();
    }

    @Test
    void should_InjectTaskServiceImpl_When_ConstructorContainsParameter() {
      assertThat(taskServiceImpl).isNotNull();
    }
  }

  @Nested
  class MonitorServiceInjectionTest {
    private final MonitorService monitorService;
    private final MonitorServiceImpl monitorServiceImpl;

    MonitorServiceInjectionTest(
        MonitorService monitorService, MonitorServiceImpl monitorServiceImpl) {
      this.monitorService = monitorService;
      this.monitorServiceImpl = monitorServiceImpl;
    }

    @Test
    void should_InjectMonitorService_When_ConstructorContainsParameter() {
      assertThat(monitorService).isNotNull();
    }

    @Test
    void should_InjectMonitorServiceImpl_When_ConstructorContainsParameter() {
      assertThat(monitorServiceImpl).isNotNull();
    }
  }

  @Nested
  class WorkbasketServiceInjectionTest {
    private final WorkbasketService workbasketService;
    private final WorkbasketServiceImpl workbasketServiceImpl;

    WorkbasketServiceInjectionTest(
        WorkbasketService workbasketService, WorkbasketServiceImpl workbasketServiceImpl) {
      this.workbasketService = workbasketService;
      this.workbasketServiceImpl = workbasketServiceImpl;
    }

    @Test
    void should_InjectWorkbasketService_When_ConstructorContainsParameter() {
      assertThat(workbasketService).isNotNull();
    }

    @Test
    void should_InjectWorkbasketServiceImpl_When_ConstructorContainsParameter() {
      assertThat(workbasketServiceImpl).isNotNull();
    }
  }

  @Nested
  class ClassificationServiceInjectionTest {
    private final ClassificationService classificationService;
    private final ClassificationServiceImpl classificationServiceImpl;

    ClassificationServiceInjectionTest(
        ClassificationService classificationService,
        ClassificationServiceImpl classificationServiceImpl) {
      this.classificationService = classificationService;
      this.classificationServiceImpl = classificationServiceImpl;
    }

    @Test
    void should_InjectClassificationService_When_ConstructorContainsParameter() {
      assertThat(classificationService).isNotNull();
    }

    @Test
    void should_InjectClassificationServiceImpl_When_ConstructorContainsParameter() {
      assertThat(classificationServiceImpl).isNotNull();
    }
  }

  @Nested
  class JobServiceInjectionTest {
    private final JobService jobService;
    private final JobServiceImpl jobServiceImpl;

    JobServiceInjectionTest(JobService jobService, JobServiceImpl jobServiceImpl) {
      this.jobService = jobService;
      this.jobServiceImpl = jobServiceImpl;
    }

    @Test
    void should_InjectJobService_When_ConstructorContainsParameter() {
      assertThat(jobService).isNotNull();
    }

    @Test
    void should_InjectJobServiceImpl_When_ConstructorContainsParameter() {
      assertThat(jobServiceImpl).isNotNull();
    }
  }

  @Nested
  class WorkingDaysToDaysConverterInjectionTest {
    private final WorkingDaysToDaysConverter workingDaysToDaysConverter;

    WorkingDaysToDaysConverterInjectionTest(WorkingDaysToDaysConverter workingDaysToDaysConverter) {
      this.workingDaysToDaysConverter = workingDaysToDaysConverter;
    }

    @Test
    void should_InjectWorkingDaysToDaysConverter_When_ConstructorContainsParameter() {
      assertThat(workingDaysToDaysConverter).isNotNull();
    }
  }
}
