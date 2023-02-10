package acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.util.ReflectionUtil;
import pro.taskana.testapi.extensions.TestContainerExtension;
import pro.taskana.workbasket.api.WorkbasketPermission;

class TaskanaConfigurationTest {

  @TestFactory
  Stream<DynamicTest> should_SaveUnmodifiableCollections() {
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .initTaskanaProperties()
            .build();

    Stream<Field> fields =
        ReflectionUtil.retrieveAllFields(TaskanaConfiguration.class).stream()
            .filter(f -> Collection.class.isAssignableFrom(f.getType()));

    ThrowingConsumer<Field> testCase =
        field -> {
          field.setAccessible(true);
          Collection<?> o = (Collection<?>) field.get(configuration);

          // PLEASE do not change this to the _assertThatThrownBy_ syntax.
          // That syntax does not respect the given description and thus might confuse future devs.
          assertThatExceptionOfType(UnsupportedOperationException.class)
              .as("Field '%s' should be an unmodifiable Collection", field.getName())
              .isThrownBy(() -> o.add(null));
        };

    return DynamicTest.stream(fields, Field::getName, testCase);
  }

  @TestFactory
  Stream<DynamicTest> should_SaveUnmodifiableMaps() {
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .initTaskanaProperties()
            .build();

    Stream<Field> fields =
        ReflectionUtil.retrieveAllFields(TaskanaConfiguration.class).stream()
            .filter(f -> Map.class.isAssignableFrom(f.getType()));

    ThrowingConsumer<Field> testCase =
        field -> {
          field.setAccessible(true);
          Map<?, ?> o = (Map<?, ?>) field.get(configuration);

          // PLEASE do not change this to the _assertThatThrownBy_ syntax.
          // That syntax does not respect the given description and thus might confuse future devs.
          assertThatExceptionOfType(UnsupportedOperationException.class)
              .as("Field '%s' should be an unmodifiable Collection", field.getName())
              .isThrownBy(() -> o.put(null, null));
        };

    return DynamicTest.stream(fields, Field::getName, testCase);
  }

  @Test
  void should_PopulateEveryTaskanaConfiguration_When_EveryBuilderFunctionIsCalled() {
    // given
    List<String> expectedDomains = List.of("A", "B");
    Map<TaskanaRole, Set<String>> expectedRoleMap = Map.of(TaskanaRole.ADMIN, Set.of("admin"));
    List<String> expectedClassificationTypes = List.of("typeA", "typeB");
    Map<String, List<String>> expectedClassificationCategories =
        Map.of("typeA", List.of("categoryA"), "typeB", List.of("categoryB"));
    List<CustomHoliday> expectedCustomHolidays = List.of(CustomHoliday.of(10, 10));
    int expectedJobBatchSize = 50;
    int expectedNumberOfJobRetries = 500;
    Instant expectedCleanupJobFirstJun = Instant.MIN;
    Duration expectedCleanupJobRunEvery = Duration.ofDays(2);
    Duration expectedCleanupJobMinimumAge = Duration.ZERO;
    int expectedPriorityJobBatchSize = 49;
    Instant expectedPriorityJobFirstRun = Instant.MIN.plus(1, ChronoUnit.DAYS);
    Instant expectedUserRefreshJobFirstRun = Instant.MIN.plus(2, ChronoUnit.DAYS);
    Duration expectedUserRefreshJobRunEvery = Duration.ofDays(5);
    List<WorkbasketPermission> expectedMinimalPermissionsToAssignDomains =
        List.of(WorkbasketPermission.CUSTOM_2);
    long expectedJobSchedulerInitialStartDelay = 15;
    long expectedJobSchedulerPeriod = 10;
    TimeUnit expectedJobSchedulerPeriodTimeUnit = TimeUnit.DAYS;
    List<String> expectedJobSchedulerCustomJobs = List.of("Job_A", "Job_B");

    // when
    Map<DayOfWeek, Set<LocalTimeInterval>> expectedWorkingTimeSchedule =
        Map.of(DayOfWeek.MONDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.NOON)));
    TaskanaConfiguration configuration =
        new Builder(TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .domains(expectedDomains)
            .roleMap(expectedRoleMap)
            .classificationTypes(expectedClassificationTypes)
            .classificationCategoriesByTypeMap(expectedClassificationCategories)
            .allowTimestampServiceLevelMismatch(true)
            .customHolidays(expectedCustomHolidays)
            .germanPublicHolidaysEnabled(true)
            .corpusChristiEnabled(true)
            .deleteHistoryOnTaskDeletionEnabled(true)
            .jobBatchSize(expectedJobBatchSize)
            .maxNumberOfJobRetries(expectedNumberOfJobRetries)
            .cleanupJobFirstRun(expectedCleanupJobFirstJun)
            .cleanupJobRunEvery(expectedCleanupJobRunEvery)
            .cleanupJobMinimumAge(expectedCleanupJobMinimumAge)
            .taskCleanupJobAllCompletedSameParentBusiness(false)
            .priorityJobBatchSize(expectedPriorityJobBatchSize)
            .priorityJobFirstRun(expectedPriorityJobFirstRun)
            .priorityJobActive(true)
            .userRefreshJobFirstRun(expectedUserRefreshJobFirstRun)
            .userRefreshJobRunEvery(expectedUserRefreshJobRunEvery)
            .addAdditionalUserInfo(true)
            .minimalPermissionsToAssignDomains(expectedMinimalPermissionsToAssignDomains)
            .jobSchedulerEnabled(false)
            .jobSchedulerInitialStartDelay(expectedJobSchedulerInitialStartDelay)
            .jobSchedulerPeriod(expectedJobSchedulerPeriod)
            .jobSchedulerPeriodTimeUnit(expectedJobSchedulerPeriodTimeUnit)
            .jobSchedulerEnableTaskCleanupJob(false)
            .jobSchedulerEnableTaskUpdatePriorityJob(false)
            .jobSchedulerEnableWorkbasketCleanupJob(false)
            .jobSchedulerEnableUserInfoRefreshJob(false)
            .jobSchedulerEnableHistorieCleanupJob(false)
            .jobSchedulerCustomJobs(expectedJobSchedulerCustomJobs)
            .workingTimeSchedule(expectedWorkingTimeSchedule)
            .build();

    // then
    assertThat(configuration).hasNoNullFieldsOrProperties();
    assertThat(configuration.getDomains()).isEqualTo(expectedDomains);
    assertThat(configuration.getRoleMap()).isEqualTo(expectedRoleMap);
    assertThat(configuration.getClassificationTypes()).isEqualTo(expectedClassificationTypes);
    assertThat(configuration.getClassificationCategoriesByType())
        .isEqualTo(expectedClassificationCategories);
    assertThat(configuration.isAllowTimestampServiceLevelMismatch()).isTrue();
    assertThat(configuration.getCustomHolidays()).isEqualTo(expectedCustomHolidays);
    assertThat(configuration.isGermanPublicHolidaysEnabled()).isTrue();
    assertThat(configuration.isCorpusChristiEnabled()).isTrue();
    assertThat(configuration.isDeleteHistoryOnTaskDeletionEnabled()).isTrue();
    assertThat(configuration.getJobBatchSize()).isEqualTo(expectedJobBatchSize);
    assertThat(configuration.getMaxNumberOfJobRetries()).isEqualTo(expectedNumberOfJobRetries);
    assertThat(configuration.getCleanupJobFirstRun()).isEqualTo(expectedCleanupJobFirstJun);
    assertThat(configuration.getCleanupJobRunEvery()).isEqualTo(expectedCleanupJobRunEvery);
    assertThat(configuration.getCleanupJobMinimumAge()).isEqualTo(expectedCleanupJobMinimumAge);
    assertThat(configuration.isTaskCleanupJobAllCompletedSameParentBusiness()).isFalse();
    assertThat(configuration.getPriorityJobBatchSize()).isEqualTo(expectedPriorityJobBatchSize);
    assertThat(configuration.getPriorityJobFirstRun()).isEqualTo(expectedPriorityJobFirstRun);
    assertThat(configuration.isPriorityJobActive()).isTrue();
    assertThat(configuration.getUserRefreshJobFirstRun()).isEqualTo(expectedUserRefreshJobFirstRun);
    assertThat(configuration.getUserRefreshJobRunEvery()).isEqualTo(expectedUserRefreshJobRunEvery);
    assertThat(configuration.isAddAdditionalUserInfo()).isTrue();
    assertThat(configuration.getMinimalPermissionsToAssignDomains())
        .isEqualTo(expectedMinimalPermissionsToAssignDomains);
    assertThat(configuration.isJobSchedulerEnabled()).isFalse();
    assertThat(configuration.getJobSchedulerInitialStartDelay())
        .isEqualTo(expectedJobSchedulerInitialStartDelay);
    assertThat(configuration.getJobSchedulerPeriod()).isEqualTo(expectedJobSchedulerPeriod);
    assertThat(configuration.getJobSchedulerPeriodTimeUnit())
        .isEqualTo(expectedJobSchedulerPeriodTimeUnit);
    assertThat(configuration.isJobSchedulerEnableTaskCleanupJob()).isFalse();
    assertThat(configuration.isJobSchedulerEnableTaskUpdatePriorityJob()).isFalse();
    assertThat(configuration.isJobSchedulerEnableWorkbasketCleanupJob()).isFalse();
    assertThat(configuration.isJobSchedulerEnableUserInfoRefreshJob()).isFalse();
    assertThat(configuration.isJobSchedulerEnableHistorieCleanupJob()).isFalse();
    assertThat(configuration.getJobSchedulerCustomJobs()).isEqualTo(expectedJobSchedulerCustomJobs);
    assertThat(configuration.getWorkingTimeSchedule()).isEqualTo(expectedWorkingTimeSchedule);
  }

  @Test
  void should_PopulateEveryConfigurationProperty_When_UsingCopyConstructor() {
    TaskanaConfiguration configuration =
        new Builder(TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .domains(List.of("A", "B"))
            .roleMap(Map.of(TaskanaRole.ADMIN, Set.of("admin")))
            .classificationTypes(List.of("typeA", "typeB"))
            .classificationCategoriesByTypeMap(
                Map.of("typeA", List.of("categoryA"), "typeB", List.of("categoryB")))
            .allowTimestampServiceLevelMismatch(true)
            .customHolidays(List.of(CustomHoliday.of(10, 10)))
            .germanPublicHolidaysEnabled(true)
            .corpusChristiEnabled(true)
            .deleteHistoryOnTaskDeletionEnabled(true)
            .jobBatchSize(50)
            .maxNumberOfJobRetries(500)
            .cleanupJobFirstRun(Instant.MIN)
            .cleanupJobRunEvery(Duration.ofDays(2))
            .cleanupJobMinimumAge(Duration.ZERO)
            .taskCleanupJobAllCompletedSameParentBusiness(false)
            .priorityJobBatchSize(49)
            .priorityJobFirstRun(Instant.MIN.plus(1, ChronoUnit.DAYS))
            .priorityJobActive(true)
            .userRefreshJobFirstRun(Instant.MIN.plus(2, ChronoUnit.DAYS))
            .userRefreshJobRunEvery(Duration.ofDays(5))
            .addAdditionalUserInfo(true)
            .minimalPermissionsToAssignDomains(List.of(WorkbasketPermission.CUSTOM_2))
            .jobSchedulerEnabled(false)
            .jobSchedulerInitialStartDelay(10)
            .jobSchedulerPeriod(15)
            .jobSchedulerPeriodTimeUnit(TimeUnit.DAYS)
            .jobSchedulerEnableTaskCleanupJob(false)
            .jobSchedulerEnableTaskUpdatePriorityJob(false)
            .jobSchedulerEnableWorkbasketCleanupJob(false)
            .jobSchedulerEnableUserInfoRefreshJob(false)
            .jobSchedulerEnableHistorieCleanupJob(false)
            .jobSchedulerCustomJobs(List.of("Job_A", "Job_B"))
            .workingTimeSchedule(
                Map.of(
                    DayOfWeek.MONDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.NOON))))
            .build();

    TaskanaConfiguration copyConfiguration = new Builder(configuration).build();

    assertThat(copyConfiguration)
        .hasNoNullFieldsOrProperties()
        .usingRecursiveComparison()
        .isEqualTo(configuration);
  }
}
