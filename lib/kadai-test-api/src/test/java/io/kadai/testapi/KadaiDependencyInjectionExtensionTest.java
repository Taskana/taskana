package io.kadai.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.internal.ClassificationServiceImpl;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.JobService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.internal.ConfigurationMapper;
import io.kadai.common.internal.ConfigurationServiceImpl;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.JobServiceImpl;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.security.CurrentUserContextImpl;
import io.kadai.common.internal.workingtime.WorkingTimeCalculatorImpl;
import io.kadai.monitor.api.MonitorService;
import io.kadai.monitor.internal.MonitorServiceImpl;
import io.kadai.task.api.TaskService;
import io.kadai.task.internal.TaskServiceImpl;
import io.kadai.user.api.UserService;
import io.kadai.user.internal.UserServiceImpl;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.internal.WorkbasketServiceImpl;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class KadaiDependencyInjectionExtensionTest {

  KadaiConfiguration kadaiConfigurationNotAnnotated;
  @KadaiInject KadaiConfiguration kadaiConfiguration;
  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject KadaiEngine kadaiEngine2;
  @KadaiInject KadaiEngineImpl kadaiEngineImpl;
  @KadaiInject InternalKadaiEngine internalKadaiEngine;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject ClassificationServiceImpl classificationServiceImpl;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject WorkbasketServiceImpl workbasketServiceImpl;
  @KadaiInject TaskService taskService;
  @KadaiInject TaskServiceImpl taskServiceImpl;
  @KadaiInject MonitorService monitorService;
  @KadaiInject MonitorServiceImpl monitorServiceImpl;
  @KadaiInject JobService jobService;
  @KadaiInject JobServiceImpl jobServiceImpl;
  @KadaiInject ConfigurationService configurationService;
  @KadaiInject ConfigurationServiceImpl configurationServiceImpl;
  @KadaiInject WorkingTimeCalculator workingTimeCalculator;
  @KadaiInject WorkingTimeCalculatorImpl workingTimeCalculatorImpl;
  @KadaiInject CurrentUserContext currentUserContext;
  @KadaiInject CurrentUserContextImpl currentUserContextImpl;
  @KadaiInject ConfigurationMapper configurationMapper;
  @KadaiInject UserService userService;
  @KadaiInject UserServiceImpl userServiceImpl;

  @Test
  void should_NotInjectKadaiConfiguration_When_FieldIsNotAnnotated() {
    assertThat(kadaiConfigurationNotAnnotated).isNull();
  }

  @Test
  void should_InjectMultipleTimes_When_FieldIsDeclaredMultipleTimes() {
    assertThat(kadaiEngine).isSameAs(kadaiEngine2).isNotNull();
  }

  @Test
  void should_InjectKadaiConfiguration_When_FieldIsAnnotatedOrDeclaredAsParameter(
      KadaiConfiguration kadaiConfiguration) {
    assertThat(kadaiConfiguration).isSameAs(this.kadaiConfiguration).isNotNull();
  }

  @Test
  void should_InjectKadaiEngine_When_FieldIsAnnotatedOrDeclaredAsParameter(
      KadaiEngine kadaiEngine) {
    assertThat(kadaiEngine).isSameAs(this.kadaiEngine).isSameAs(this.kadaiEngineImpl).isNotNull();
  }

  @Test
  void should_InjectKadaiEngineImpl_When_FieldIsAnnotatedOrDeclaredAsParameter(
      KadaiEngineImpl kadaiEngineImpl) {
    assertThat(kadaiEngineImpl)
        .isSameAs(this.kadaiEngineImpl)
        .isSameAs(this.kadaiEngine)
        .isNotNull();
  }

  @Test
  void should_InjectInternalKadaiEngine_When_FieldIsAnnotatedOrDeclaredAsParameter(
      InternalKadaiEngine internalKadaiEngine) {
    assertThat(internalKadaiEngine).isSameAs(this.internalKadaiEngine).isNotNull();
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
