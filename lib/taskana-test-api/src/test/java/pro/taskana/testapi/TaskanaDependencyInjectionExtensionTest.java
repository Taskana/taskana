package pro.taskana.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.ConfigurationMapper;
import pro.taskana.common.internal.ConfigurationServiceImpl;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.security.CurrentUserContextImpl;
import pro.taskana.common.internal.workingtime.WorkingTimeCalculatorImpl;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.user.api.UserService;
import pro.taskana.user.internal.UserServiceImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

@TaskanaIntegrationTest
class TaskanaDependencyInjectionExtensionTest {

  TaskanaConfiguration taskanaConfigurationNotAnnotated;
  @TaskanaInject TaskanaConfiguration taskanaConfiguration;
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskanaEngine taskanaEngine2;
  @TaskanaInject TaskanaEngineImpl taskanaEngineImpl;
  @TaskanaInject InternalTaskanaEngine internalTaskanaEngine;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject ClassificationServiceImpl classificationServiceImpl;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject WorkbasketServiceImpl workbasketServiceImpl;
  @TaskanaInject TaskService taskService;
  @TaskanaInject TaskServiceImpl taskServiceImpl;
  @TaskanaInject MonitorService monitorService;
  @TaskanaInject MonitorServiceImpl monitorServiceImpl;
  @TaskanaInject JobService jobService;
  @TaskanaInject JobServiceImpl jobServiceImpl;
  @TaskanaInject ConfigurationService configurationService;
  @TaskanaInject ConfigurationServiceImpl configurationServiceImpl;
  @TaskanaInject WorkingTimeCalculator workingTimeCalculator;
  @TaskanaInject WorkingTimeCalculatorImpl workingTimeCalculatorImpl;
  @TaskanaInject CurrentUserContext currentUserContext;
  @TaskanaInject CurrentUserContextImpl currentUserContextImpl;
  @TaskanaInject ConfigurationMapper configurationMapper;
  @TaskanaInject UserService userService;
  @TaskanaInject UserServiceImpl userServiceImpl;

  @Test
  void should_NotInjectTaskanaEngineConfiguration_When_FieldIsNotAnnotated() {
    assertThat(taskanaConfigurationNotAnnotated).isNull();
  }

  @Test
  void should_InjectMultipleTimes_When_FieldIsDeclaredMultipleTimes() {
    assertThat(taskanaEngine).isSameAs(taskanaEngine2).isNotNull();
  }

  @Test
  void should_InjectTaskanaEngineConfiguration_When_FieldIsAnnotatedOrDeclaredAsParameter(
      TaskanaConfiguration taskanaConfiguration) {
    assertThat(taskanaConfiguration).isSameAs(this.taskanaConfiguration).isNotNull();
  }

  @Test
  void should_InjectTaskanaEngine_When_FieldIsAnnotatedOrDeclaredAsParameter(
      TaskanaEngine taskanaEngine) {
    assertThat(taskanaEngine)
        .isSameAs(this.taskanaEngine)
        .isSameAs(this.taskanaEngineImpl)
        .isNotNull();
  }

  @Test
  void should_InjectTaskanaEngineImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      TaskanaEngineImpl taskanaEngineImpl) {
    assertThat(taskanaEngineImpl)
        .isSameAs(this.taskanaEngineImpl)
        .isSameAs(this.taskanaEngine)
        .isNotNull();
  }

  @Test
  void should_InjectInternalTaskanaEngine_When_FieldIsAnnotatedOrDeclaredAsParameter(
      InternalTaskanaEngine internalTaskanaEngine) {
    assertThat(internalTaskanaEngine).isSameAs(this.internalTaskanaEngine).isNotNull();
  }

  @Test
  void should_InjectClassificationService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      ClassificationService classificationService) {
    assertThat(classificationService)
        .isSameAs(this.classificationService)
        .isSameAs(this.classificationServiceImpl)
        .isNotNull();
  }

  @Test
  void should_InjectClassificationServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      ClassificationServiceImpl classificationServiceImpl) {
    assertThat(classificationServiceImpl)
        .isSameAs(this.classificationServiceImpl)
        .isSameAs(this.classificationService)
        .isNotNull();
  }

  @Test
  void should_InjectWorkbasketService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      WorkbasketService workbasketService) {
    assertThat(workbasketService)
        .isSameAs(this.workbasketService)
        .isSameAs(this.workbasketServiceImpl)
        .isNotNull();
  }

  @Test
  void should_InjectWorkbasketServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      WorkbasketServiceImpl workbasketServiceImpl) {
    assertThat(workbasketServiceImpl)
        .isSameAs(this.workbasketServiceImpl)
        .isSameAs(this.workbasketService)
        .isNotNull();
  }

  @Test
  void should_InjectTaskService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      TaskService taskService) {
    assertThat(taskService).isSameAs(this.taskService).isSameAs(this.taskServiceImpl).isNotNull();
  }

  @Test
  void should_InjectTaskServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      TaskServiceImpl taskServiceImpl) {
    assertThat(taskServiceImpl)
        .isSameAs(this.taskServiceImpl)
        .isSameAs(this.taskService)
        .isNotNull();
  }

  @Test
  void should_InjectMonitorService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      MonitorService monitorService) {
    assertThat(monitorService)
        .isSameAs(this.monitorService)
        .isSameAs(this.monitorServiceImpl)
        .isNotNull();
  }

  @Test
  void should_InjectMonitorServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      MonitorServiceImpl monitorServiceImpl) {
    assertThat(monitorServiceImpl)
        .isSameAs(this.monitorServiceImpl)
        .isSameAs(this.monitorService)
        .isNotNull();
  }

  @Test
  void should_InjectJobService_When_FieldIsAnnotatedOrDeclaredAsParameter(JobService jobService) {
    assertThat(jobService).isSameAs(this.jobService).isSameAs(jobServiceImpl).isNotNull();
  }

  @Test
  void should_InjectJobServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      JobServiceImpl jobServiceImpl) {
    assertThat(jobServiceImpl).isSameAs(this.jobServiceImpl).isSameAs(this.jobService).isNotNull();
  }

  @Test
  void should_InjectConfigurationService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      ConfigurationService configurationService) {
    assertThat(configurationService)
        .isSameAs(this.configurationService)
        .isSameAs(this.configurationServiceImpl)
        .isNotNull();
  }

  @Test
  void should_InjectConfigurationServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      ConfigurationServiceImpl configurationServiceImpl) {
    assertThat(configurationServiceImpl)
        .isSameAs(this.configurationServiceImpl)
        .isSameAs(this.configurationService)
        .isNotNull();
  }

  @Test
  void should_InjectWorkingTimeCalculator_When_FieldIsAnnotatedOrDeclaredAsParameter(
      WorkingTimeCalculator workingTimeCalculator) {
    assertThat(workingTimeCalculator)
        .isSameAs(this.workingTimeCalculator)
        .isSameAs(this.workingTimeCalculatorImpl)
        .isNotNull();
  }

  @Test
  void should_InjectWorkingTimeCalculatorImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      WorkingTimeCalculatorImpl workingTimeCalculatorImpl) {
    assertThat(workingTimeCalculatorImpl)
        .isSameAs(this.workingTimeCalculatorImpl)
        .isSameAs(this.workingTimeCalculator)
        .isNotNull();
  }

  @Test
  void should_InjectCurrentUserContext_When_FieldIsAnnotatedOrDeclaredAsParameter(
      CurrentUserContext currentUserContext) {
    assertThat(currentUserContext)
        .isSameAs(this.currentUserContext)
        .isSameAs(this.currentUserContextImpl)
        .isNotNull();
  }

  @Test
  void should_InjectCurrentUserContextImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      CurrentUserContextImpl currentUserContextImpl) {
    assertThat(currentUserContextImpl)
        .isSameAs(this.currentUserContextImpl)
        .isSameAs(this.currentUserContext)
        .isNotNull();
  }

  @Test
  void should_InjectConfigurationMapper_When_FieldIsAnnotatedOrDeclaredAsParameter(
      ConfigurationMapper configurationMapper) {
    assertThat(configurationMapper).isSameAs(this.configurationMapper).isNotNull();
  }

  @Test
  void should_InjectUserService_When_FieldIsAnnotatedOrDeclaredAsParameter(
      UserService userService) {
    assertThat(userService).isSameAs(this.userService).isSameAs(this.userServiceImpl).isNotNull();
  }

  @Test
  void should_InjectUserServiceImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      UserServiceImpl userServiceImpl) {
    assertThat(userServiceImpl)
        .isSameAs(this.userServiceImpl)
        .isSameAs(this.userService)
        .isNotNull();
  }
}
