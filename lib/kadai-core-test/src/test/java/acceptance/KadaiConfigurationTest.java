package acceptance;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.KadaiConfiguration;
import io.kadai.KadaiConfiguration.Builder;
import io.kadai.common.api.CustomHoliday;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.LocalTimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.configuration.KadaiProperty;
import io.kadai.common.internal.util.CheckedConsumer;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.internal.util.ReflectionUtil;
import io.kadai.testapi.extensions.TestContainerExtension;
import io.kadai.workbasket.api.WorkbasketPermission;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class KadaiConfigurationTest {

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class Functionality {
    Map<String, String> kadaiPropertyNameByFieldName =
        ReflectionUtil.retrieveAllFields(Builder.class).stream()
            .collect(
                Collectors.toMap(
                    Field::getName,
                    f -> {
                      if (f.isAnnotationPresent(KadaiProperty.class)) {
                        return f.getAnnotation(KadaiProperty.class).value();
                      }
                      return "";
                    }));

    @Test
    void should_SetDefaultValues() {
      Set<LocalTimeInterval> standardWorkingSlots =
          Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX));
      Map<DayOfWeek, Set<LocalTimeInterval>> defaultWorkingTimeSchedule =
          Map.ofEntries(
              Map.entry(DayOfWeek.MONDAY, standardWorkingSlots),
              Map.entry(DayOfWeek.TUESDAY, standardWorkingSlots),
              Map.entry(DayOfWeek.WEDNESDAY, standardWorkingSlots),
              Map.entry(DayOfWeek.THURSDAY, standardWorkingSlots),
              Map.entry(DayOfWeek.FRIDAY, standardWorkingSlots));

      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), true, "schemaName").build();

      // general configuration
      assertThat(configuration.isSecurityEnabled()).isTrue();
      assertThat(configuration.getDomains()).isEqualTo(Collections.emptyList());
      assertThat(configuration.isEnforceServiceLevel()).isTrue();
      // authentication configuration
      assertThat(configuration.getRoleMap())
          .isEqualTo(
              Arrays.stream(KadaiRole.values())
                  .collect(Collectors.toMap(Function.identity(), r -> Set.of())));
      // classification configuration
      assertThat(configuration.getClassificationTypes()).isEqualTo(Collections.emptyList());
      assertThat(configuration.getClassificationCategoriesByType())
          .isEqualTo(Collections.emptyMap());
      // working time configuration
      assertThat(configuration.getWorkingTimeSchedule()).isEqualTo(defaultWorkingTimeSchedule);
      assertThat(configuration.getWorkingTimeScheduleTimeZone())
          .isEqualTo(ZoneId.of("Europe/Berlin"));
      assertThat(configuration.getCustomHolidays()).isEqualTo(Collections.emptySet());
      assertThat(configuration.isGermanPublicHolidaysEnabled()).isTrue();
      assertThat(configuration.isGermanPublicHolidaysCorpusChristiEnabled()).isFalse();
      // history configuration
      assertThat(configuration.isDeleteHistoryEventsOnTaskDeletionEnabled()).isFalse();
      assertThat(configuration.getLogHistoryLoggerName()).isNull();
      // job configuration
      assertThat(configuration.isJobSchedulerEnabled()).isTrue();
      assertThat(configuration.getJobSchedulerInitialStartDelay()).isEqualTo(0);
      assertThat(configuration.getJobSchedulerPeriod()).isEqualTo(5);
      assertThat(configuration.getJobSchedulerPeriodTimeUnit()).isEqualTo(TimeUnit.MINUTES);
      assertThat(configuration.getMaxNumberOfJobRetries()).isEqualTo(3);
      assertThat(configuration.getJobBatchSize()).isEqualTo(100);
      assertThat(configuration.getJobFirstRun()).isEqualTo(Instant.parse("2023-01-01T00:00:00Z"));
      assertThat(configuration.getJobRunEvery()).isEqualTo(Duration.ofDays(1));
      assertThat(configuration.isTaskCleanupJobEnabled()).isTrue();
      assertThat(configuration.getTaskCleanupJobMinimumAge()).isEqualTo(Duration.ofDays(14));
      assertThat(configuration.isTaskCleanupJobAllCompletedSameParentBusiness()).isTrue();
      assertThat(configuration.isWorkbasketCleanupJobEnabled()).isTrue();
      assertThat(configuration.isSimpleHistoryCleanupJobEnabled()).isFalse();
      assertThat(configuration.getSimpleHistoryCleanupJobBatchSize()).isEqualTo(100);
      assertThat(configuration.getSimpleHistoryCleanupJobMinimumAge())
          .isEqualTo(Duration.ofDays(14));
      assertThat(configuration.isSimpleHistoryCleanupJobAllCompletedSameParentBusiness()).isTrue();
      assertThat(configuration.isTaskUpdatePriorityJobEnabled()).isFalse();
      assertThat(configuration.getTaskUpdatePriorityJobBatchSize()).isEqualTo(100);
      assertThat(configuration.getTaskUpdatePriorityJobFirstRun())
          .isEqualTo(Instant.parse("2023-01-01T00:00:00Z"));
      assertThat(configuration.getTaskUpdatePriorityJobRunEvery()).isEqualTo(Duration.ofDays(1));
      assertThat(configuration.isUserInfoRefreshJobEnabled()).isFalse();
      assertThat(configuration.getUserRefreshJobFirstRun())
          .isEqualTo(Instant.parse("2023-01-01T23:00:00Z"));
      assertThat(configuration.getUserRefreshJobRunEvery()).isEqualTo(Duration.ofDays(1));
      assertThat(configuration.getCustomJobs()).isEqualTo(Collections.emptySet());
      // user configuration
      assertThat(configuration.isAddAdditionalUserInfo()).isFalse();
      assertThat(configuration.getMinimalPermissionsToAssignDomains())
          .isEqualTo(Collections.emptySet());
    }

    @Test
    void should_PopulateEveryKadaiConfiguration_When_ImportingPropertiesFile() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), true, "KADAI")
              .initKadaiProperties("/fullKadai.properties")
              .build();

      verifyConfigurationValuesDifferFromDefaultConfiguration(configuration);
      // general configuration
      assertThat(configuration.getDomains())
          .isEqualTo(List.of("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN));
      assertThat(configuration.isEnforceServiceLevel()).isFalse();
      // authentication configuration
      assertThat(configuration.getRoleMap())
          .isEqualTo(
              Map.ofEntries(
                  Map.entry(KadaiRole.USER, Set.of("user-1", "user-2")),
                  Map.entry(KadaiRole.ADMIN, Set.of("admin-1", "admin-2")),
                  Map.entry(
                      KadaiRole.BUSINESS_ADMIN, Set.of("business_admin-1", "business_admin-2")),
                  Map.entry(KadaiRole.MONITOR, Set.of("monitor-1", "monitor-2")),
                  Map.entry(KadaiRole.TASK_ADMIN, Set.of("taskadmin-1", "taskadmin-2")),
                  Map.entry(KadaiRole.TASK_ROUTER, Set.of("taskrouter-1", "taskrouter-2"))));
      // classification configuration
      assertThat(configuration.getClassificationTypes()).isEqualTo(List.of("TASK", "DOCUMENT"));
      assertThat(configuration.getClassificationCategoriesByType())
          .isEqualTo(
              Map.ofEntries(
                  Map.entry("TASK", List.of("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS")),
                  Map.entry("DOCUMENT", List.of("EXTERNAL"))));
      // working time configuration
      //
      // assertThat(configuration.getWorkingTimeSchedule()).isEqualTo(defaultWorkingTimeSchedule);
      assertThat(configuration.getWorkingTimeScheduleTimeZone()).isEqualTo(ZoneId.of("UTC"));
      assertThat(configuration.getCustomHolidays())
          .isEqualTo(Set.of(CustomHoliday.of(31, 7), CustomHoliday.of(16, 12)));
      assertThat(configuration.isGermanPublicHolidaysEnabled()).isFalse();
      assertThat(configuration.isGermanPublicHolidaysCorpusChristiEnabled()).isTrue();
      // history configuration
      assertThat(configuration.isDeleteHistoryEventsOnTaskDeletionEnabled()).isTrue();
      assertThat(configuration.getLogHistoryLoggerName()).isEqualTo("AUDIT");
      // job configuration
      assertThat(configuration.isJobSchedulerEnabled()).isFalse();
      assertThat(configuration.getJobSchedulerInitialStartDelay()).isEqualTo(30);
      assertThat(configuration.getJobSchedulerPeriod()).isEqualTo(12);
      assertThat(configuration.getJobSchedulerPeriodTimeUnit()).isEqualTo(TimeUnit.HOURS);
      assertThat(configuration.getMaxNumberOfJobRetries()).isEqualTo(4);
      assertThat(configuration.getJobBatchSize()).isEqualTo(50);
      assertThat(configuration.getJobFirstRun()).isEqualTo(Instant.parse("2018-07-25T08:00:00Z"));
      assertThat(configuration.getJobRunEvery()).isEqualTo(Duration.ofDays(2));
      assertThat(configuration.isTaskCleanupJobEnabled()).isFalse();
      assertThat(configuration.getTaskCleanupJobMinimumAge()).isEqualTo(Duration.ofDays(15));
      assertThat(configuration.isTaskCleanupJobAllCompletedSameParentBusiness()).isFalse();
      assertThat(configuration.isWorkbasketCleanupJobEnabled()).isFalse();
      assertThat(configuration.isSimpleHistoryCleanupJobEnabled()).isTrue();
      assertThat(configuration.getSimpleHistoryCleanupJobBatchSize()).isEqualTo(50);
      assertThat(configuration.getSimpleHistoryCleanupJobMinimumAge())
          .isEqualTo(Duration.ofDays(17));
      assertThat(configuration.isSimpleHistoryCleanupJobAllCompletedSameParentBusiness()).isFalse();
      assertThat(configuration.isTaskUpdatePriorityJobEnabled()).isTrue();
      assertThat(configuration.getTaskUpdatePriorityJobBatchSize()).isEqualTo(50);
      assertThat(configuration.getTaskUpdatePriorityJobFirstRun())
          .isEqualTo(Instant.parse("2018-07-25T08:00:00Z"));
      assertThat(configuration.getTaskUpdatePriorityJobRunEvery()).isEqualTo(Duration.ofDays(3));
      assertThat(configuration.isUserInfoRefreshJobEnabled()).isTrue();
      assertThat(configuration.getUserRefreshJobFirstRun())
          .isEqualTo(Instant.parse("2018-07-25T08:00:00Z"));
      assertThat(configuration.getUserRefreshJobRunEvery()).isEqualTo(Duration.ofDays(4));
      assertThat(configuration.getCustomJobs()).isEqualTo(Set.of("A", "B", "C"));
      // user configuration
      assertThat(configuration.isAddAdditionalUserInfo()).isTrue();
      assertThat(configuration.getMinimalPermissionsToAssignDomains())
          .isEqualTo(Set.of(WorkbasketPermission.READ, WorkbasketPermission.OPEN));
      assertThat(configuration.getProperties())
          .contains(
              Map.entry("my_custom_property1", "my_custom_value1"),
              Map.entry("my_custom_property2", "my_custom_value2"));
    }

    @Test
    void should_PopulateEveryKadaiConfiguration_When_EveryBuilderFunctionIsCalled() {
      // given
      // general configuration
      DataSource expectedDataSource = TestContainerExtension.createDataSourceForH2();
      boolean expectedUseManagedTransactions = false;
      String expectedSchemaName = "KADAI";
      boolean expectedSecurityEnabled = false;
      List<String> expectedDomains = List.of("A", "B");
      boolean expectedEnforceServiceLevel = false;

      // authentication configuration
      Map<KadaiRole, Set<String>> expectedRoleMap =
          Map.ofEntries(
              Map.entry(KadaiRole.USER, Set.of("user")),
              Map.entry(KadaiRole.BUSINESS_ADMIN, Set.of("business_admin")),
              Map.entry(KadaiRole.ADMIN, Set.of("admin")),
              Map.entry(KadaiRole.MONITOR, Set.of("monitor")),
              Map.entry(KadaiRole.TASK_ADMIN, Set.of("task_admin")),
              Map.entry(KadaiRole.TASK_ROUTER, Set.of("task_router")));
      // classification configuration
      List<String> expectedClassificationTypes = List.of("TYPE_A", "TYPE_B");
      Map<String, List<String>> expectedClassificationCategories =
          Map.of("TYPE_A", List.of("CATEGORY_A"), "TYPE_B", List.of("CATEGORY_B"));
      // working time configuration
      boolean expectedUseDetailedWorkingTimeCalculation = false;
      Map<DayOfWeek, Set<LocalTimeInterval>> expectedWorkingTimeSchedule =
          Map.of(DayOfWeek.MONDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.NOON)));
      ZoneId expectedWorkingTimeScheduleTimeZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(4));
      Set<CustomHoliday> expectedCustomHolidays = Set.of(CustomHoliday.of(10, 10));
      boolean expectedGermanPublicHolidaysEnabled = false;
      boolean expectedGermanPublicHolidaysCorpusChristiEnabled = true;
      // history configuration
      boolean expectedDeleteHistoryEventsOnTaskDeletionEnabled = true;
      String expectedLogHistoryLoggerName = "LOGGER_NAME";
      // job configuration
      boolean expectedJobSchedulerEnabled = false;
      long expectedJobSchedulerInitialStartDelay = 15;
      long expectedJobSchedulerPeriod = 10;
      TimeUnit expectedJobSchedulerPeriodTimeUnit = TimeUnit.DAYS;
      int expectedNumberOfJobRetries = 500;
      int expectedJobBatchSize = 50;
      Instant expectedJobFirstJun = Instant.MIN;
      Duration expectedJobRunEvery = Duration.ofDays(2);
      Duration expectedJobLockExpirationPeriod = Duration.ofDays(2);
      boolean expectedTaskCleanupJobEnabled = false;
      Duration expectedTaskCleanupJobMinimumAge = Duration.ofDays(1);
      boolean expectedTaskCleanupJobAllCompletedSameParentBusiness = false;
      Duration expectedTaskCleanupJobLockExpirationPeriod = Duration.ofDays(2);
      boolean expectedWorkbasketCleanupJobEnabled = false;
      Duration expectedWorkbasketCleanupJobLockExpirationPeriod = Duration.ofDays(2);
      boolean expectedSimpleHistoryCleanupJobEnabled = true;
      int expectedSimpleHistoryCleanupJobBatchSize = 16;
      Duration expectedSimpleHistoryCleanupJobMinimumAge = Duration.ofHours(3);
      boolean expectedSimpleHistoryCleanupJobAllCompletedSameParentBusiness = false;
      Duration expectedSimpleHistoryCleanupJobLockExpirationPeriod = Duration.ofDays(2);
      boolean expectedTaskUpdatePriorityJobEnabled = true;
      int expectedPriorityJobBatchSize = 49;
      Instant expectedPriorityJobFirstRun = Instant.MIN.plus(1, ChronoUnit.DAYS);
      Duration expectedTaskUpdatePriorityJobRunEvery = Duration.ofMinutes(17);
      Duration expectedTaskUpdatePriorityJobLockExpirationPeriod = Duration.ofDays(2);
      boolean expectedUserInfoRefreshJobEnabled = true;
      Instant expectedUserRefreshJobFirstRun = Instant.MIN.plus(2, ChronoUnit.DAYS);
      Duration expectedUserRefreshJobRunEvery = Duration.ofDays(5);
      Duration expectedUserRefreshJobLockExpirationPeriod = Duration.ofDays(2);
      Set<String> expectedJobSchedulerCustomJobs = Set.of("Job_A", "Job_B");
      // user configuration
      boolean expectedAddAdditionalUserInfo = true;
      Set<WorkbasketPermission> expectedMinimalPermissionsToAssignDomains =
          Set.of(WorkbasketPermission.CUSTOM_2);
      // database configuration
      boolean expectedUseSpecificDb2Taskquery = false;

      // when
      KadaiConfiguration configuration =
          new Builder(
                  expectedDataSource,
                  expectedUseManagedTransactions,
                  expectedSchemaName,
                  expectedSecurityEnabled)
              // general configuration
              .domains(expectedDomains)
              .enforceServiceLevel(expectedEnforceServiceLevel)
              // authentication configuration
              .roleMap(expectedRoleMap)
              // classification configuration
              .classificationTypes(expectedClassificationTypes)
              .classificationCategoriesByType(expectedClassificationCategories)
              // working time configuration
              .useWorkingTimeCalculation(expectedUseDetailedWorkingTimeCalculation)
              .workingTimeSchedule(expectedWorkingTimeSchedule)
              .workingTimeScheduleTimeZone(expectedWorkingTimeScheduleTimeZone)
              .customHolidays(expectedCustomHolidays)
              .germanPublicHolidaysEnabled(expectedGermanPublicHolidaysEnabled)
              .germanPublicHolidaysCorpusChristiEnabled(
                  expectedGermanPublicHolidaysCorpusChristiEnabled)
              // history configuration
              .deleteHistoryEventsOnTaskDeletionEnabled(
                  expectedDeleteHistoryEventsOnTaskDeletionEnabled)
              .logHistoryLoggerName(expectedLogHistoryLoggerName)
              // job configuration
              .jobSchedulerEnabled(expectedJobSchedulerEnabled)
              .jobSchedulerInitialStartDelay(expectedJobSchedulerInitialStartDelay)
              .jobSchedulerPeriod(expectedJobSchedulerPeriod)
              .jobSchedulerPeriodTimeUnit(expectedJobSchedulerPeriodTimeUnit)
              .maxNumberOfJobRetries(expectedNumberOfJobRetries)
              .jobBatchSize(expectedJobBatchSize)
              .jobFirstRun(expectedJobFirstJun)
              .jobRunEvery(expectedJobRunEvery)
              .jobLockExpirationPeriod(expectedJobLockExpirationPeriod)
              .taskCleanupJobEnabled(expectedTaskCleanupJobEnabled)
              .taskCleanupJobMinimumAge(expectedTaskCleanupJobMinimumAge)
              .taskCleanupJobAllCompletedSameParentBusiness(
                  expectedTaskCleanupJobAllCompletedSameParentBusiness)
              .taskCleanupJobLockExpirationPeriod(expectedTaskCleanupJobLockExpirationPeriod)
              .workbasketCleanupJobEnabled(expectedWorkbasketCleanupJobEnabled)
              .workbasketCleanupJobLockExpirationPeriod(
                  expectedWorkbasketCleanupJobLockExpirationPeriod)
              .simpleHistoryCleanupJobEnabled(expectedSimpleHistoryCleanupJobEnabled)
              .simpleHistoryCleanupJobBatchSize(expectedSimpleHistoryCleanupJobBatchSize)
              .simpleHistoryCleanupJobMinimumAge(expectedSimpleHistoryCleanupJobMinimumAge)
              .simpleHistoryCleanupJobAllCompletedSameParentBusiness(
                  expectedSimpleHistoryCleanupJobAllCompletedSameParentBusiness)
              .simpleHistoryCleanupJobLockExpirationPeriod(
                  expectedSimpleHistoryCleanupJobLockExpirationPeriod)
              .taskUpdatePriorityJobEnabled(expectedTaskUpdatePriorityJobEnabled)
              .taskUpdatePriorityJobBatchSize(expectedPriorityJobBatchSize)
              .taskUpdatePriorityJobFirstRun(expectedPriorityJobFirstRun)
              .taskUpdatePriorityJobRunEvery(expectedTaskUpdatePriorityJobRunEvery)
              .taskUpdatePriorityJobLockExpirationPeriod(
                  expectedTaskUpdatePriorityJobLockExpirationPeriod)
              .userInfoRefreshJobEnabled(expectedUserInfoRefreshJobEnabled)
              .userRefreshJobFirstRun(expectedUserRefreshJobFirstRun)
              .userRefreshJobRunEvery(expectedUserRefreshJobRunEvery)
              .userRefreshJobLockExpirationPeriod(expectedUserRefreshJobLockExpirationPeriod)
              .customJobs(expectedJobSchedulerCustomJobs)
              // user configuration
              .addAdditionalUserInfo(expectedAddAdditionalUserInfo)
              .minimalPermissionsToAssignDomains(expectedMinimalPermissionsToAssignDomains)
              .useSpecificDb2Taskquery(expectedUseSpecificDb2Taskquery)
              .build();

      // then
      verifyConfigurationValuesDifferFromDefaultConfiguration(configuration);
      // general configuration
      assertThat(configuration.getDataSource()).isEqualTo(expectedDataSource);
      assertThat(configuration.isUseManagedTransactions())
          .isEqualTo(expectedUseManagedTransactions);
      assertThat(configuration.getSchemaName()).isEqualTo(expectedSchemaName);
      assertThat(configuration.isSecurityEnabled()).isEqualTo(expectedSecurityEnabled);
      assertThat(configuration.getDomains()).isEqualTo(expectedDomains);
      assertThat(configuration.isEnforceServiceLevel()).isEqualTo(expectedEnforceServiceLevel);
      // authentication configuration
      assertThat(configuration.getRoleMap()).isEqualTo(expectedRoleMap);
      // classification configuration
      assertThat(configuration.getClassificationTypes()).isEqualTo(expectedClassificationTypes);
      assertThat(configuration.getClassificationCategoriesByType())
          .isEqualTo(expectedClassificationCategories);
      // working time configuration
      assertThat(configuration.isUseWorkingTimeCalculation())
          .isEqualTo(expectedUseDetailedWorkingTimeCalculation);
      assertThat(configuration.getWorkingTimeSchedule()).isEqualTo(expectedWorkingTimeSchedule);
      assertThat(configuration.getWorkingTimeScheduleTimeZone())
          .isEqualTo(expectedWorkingTimeScheduleTimeZone);
      assertThat(configuration.getCustomHolidays()).isEqualTo(expectedCustomHolidays);
      assertThat(configuration.isGermanPublicHolidaysEnabled())
          .isEqualTo(expectedGermanPublicHolidaysEnabled);
      assertThat(configuration.isGermanPublicHolidaysCorpusChristiEnabled())
          .isEqualTo(expectedGermanPublicHolidaysCorpusChristiEnabled);
      // history configuration
      assertThat(configuration.isDeleteHistoryEventsOnTaskDeletionEnabled())
          .isEqualTo(expectedDeleteHistoryEventsOnTaskDeletionEnabled);
      assertThat(configuration.getLogHistoryLoggerName()).isEqualTo(expectedLogHistoryLoggerName);
      // job configuration
      assertThat(configuration.isJobSchedulerEnabled()).isEqualTo(expectedJobSchedulerEnabled);
      assertThat(configuration.getJobSchedulerInitialStartDelay())
          .isEqualTo(expectedJobSchedulerInitialStartDelay);
      assertThat(configuration.getJobSchedulerPeriod()).isEqualTo(expectedJobSchedulerPeriod);
      assertThat(configuration.getJobSchedulerPeriodTimeUnit())
          .isEqualTo(expectedJobSchedulerPeriodTimeUnit);
      assertThat(configuration.getMaxNumberOfJobRetries()).isEqualTo(expectedNumberOfJobRetries);
      assertThat(configuration.getJobBatchSize()).isEqualTo(expectedJobBatchSize);
      assertThat(configuration.getJobFirstRun()).isEqualTo(expectedJobFirstJun);
      assertThat(configuration.getJobRunEvery()).isEqualTo(expectedJobRunEvery);
      assertThat(configuration.isTaskCleanupJobEnabled()).isEqualTo(expectedTaskCleanupJobEnabled);
      assertThat(configuration.getTaskCleanupJobMinimumAge())
          .isEqualTo(expectedTaskCleanupJobMinimumAge);
      assertThat(configuration.isTaskCleanupJobAllCompletedSameParentBusiness())
          .isEqualTo(expectedTaskCleanupJobAllCompletedSameParentBusiness);
      assertThat(configuration.isWorkbasketCleanupJobEnabled())
          .isEqualTo(expectedWorkbasketCleanupJobEnabled);
      assertThat(configuration.isSimpleHistoryCleanupJobEnabled())
          .isEqualTo(expectedSimpleHistoryCleanupJobEnabled);
      assertThat(configuration.getSimpleHistoryCleanupJobBatchSize())
          .isEqualTo(expectedSimpleHistoryCleanupJobBatchSize);
      assertThat(configuration.getSimpleHistoryCleanupJobMinimumAge())
          .isEqualTo(expectedSimpleHistoryCleanupJobMinimumAge);
      assertThat(configuration.isSimpleHistoryCleanupJobAllCompletedSameParentBusiness())
          .isEqualTo(expectedSimpleHistoryCleanupJobAllCompletedSameParentBusiness);
      assertThat(configuration.isTaskUpdatePriorityJobEnabled())
          .isEqualTo(expectedTaskUpdatePriorityJobEnabled);
      assertThat(configuration.getTaskUpdatePriorityJobBatchSize())
          .isEqualTo(expectedPriorityJobBatchSize);
      assertThat(configuration.getTaskUpdatePriorityJobFirstRun())
          .isEqualTo(expectedPriorityJobFirstRun);
      assertThat(configuration.getTaskUpdatePriorityJobRunEvery())
          .isEqualTo(expectedTaskUpdatePriorityJobRunEvery);
      assertThat(configuration.isUserInfoRefreshJobEnabled())
          .isEqualTo(expectedUserInfoRefreshJobEnabled);
      assertThat(configuration.getUserRefreshJobFirstRun())
          .isEqualTo(expectedUserRefreshJobFirstRun);
      assertThat(configuration.getUserRefreshJobRunEvery())
          .isEqualTo(expectedUserRefreshJobRunEvery);
      assertThat(configuration.getCustomJobs()).isEqualTo(expectedJobSchedulerCustomJobs);
      // user configuration
      assertThat(configuration.isAddAdditionalUserInfo()).isEqualTo(expectedAddAdditionalUserInfo);
      assertThat(configuration.getMinimalPermissionsToAssignDomains())
          .isEqualTo(expectedMinimalPermissionsToAssignDomains);
    }

    @Test
    void should_PopulateEveryConfigurationProperty_When_UsingCopyConstructor() {
      // given
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI", false)
              // general configuration
              .domains(List.of("A", "B"))
              .enforceServiceLevel(false)
              // authentication configuration
              .roleMap(Map.of(KadaiRole.ADMIN, Set.of("admin")))
              // classification configuration
              .classificationTypes(List.of("typeA", "typeB"))
              .classificationCategoriesByType(
                  Map.of("typeA", List.of("categoryA"), "typeB", List.of("categoryB")))
              // working time configuration
              .useWorkingTimeCalculation(false)
              .workingTimeSchedule(
                  Map.of(
                      DayOfWeek.MONDAY,
                      Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.NOON))))
              .workingTimeScheduleTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(4)))
              .customHolidays(Set.of(CustomHoliday.of(10, 10)))
              .germanPublicHolidaysEnabled(false)
              .germanPublicHolidaysCorpusChristiEnabled(true)
              // history configuration
              .deleteHistoryEventsOnTaskDeletionEnabled(true)
              .logHistoryLoggerName("LOGGER_NAME")
              // job configuration
              .jobSchedulerEnabled(false)
              .jobSchedulerInitialStartDelay(15)
              .jobSchedulerPeriod(10)
              .jobSchedulerPeriodTimeUnit(TimeUnit.DAYS)
              .maxNumberOfJobRetries(500)
              .jobBatchSize(50)
              .jobFirstRun(Instant.MIN)
              .jobRunEvery(Duration.ofDays(2))
              .jobLockExpirationPeriod(Duration.ofDays(2))
              .taskCleanupJobEnabled(false)
              .taskCleanupJobMinimumAge(Duration.ofDays(1))
              .taskCleanupJobAllCompletedSameParentBusiness(false)
              .taskCleanupJobLockExpirationPeriod(Duration.ofDays(6))
              .workbasketCleanupJobEnabled(false)
              .workbasketCleanupJobLockExpirationPeriod(Duration.ofDays(7))
              .simpleHistoryCleanupJobEnabled(true)
              .simpleHistoryCleanupJobBatchSize(16)
              .simpleHistoryCleanupJobMinimumAge(Duration.ofHours(3))
              .simpleHistoryCleanupJobAllCompletedSameParentBusiness(false)
              .simpleHistoryCleanupJobLockExpirationPeriod(Duration.ofDays(9))
              .taskUpdatePriorityJobEnabled(true)
              .taskUpdatePriorityJobBatchSize(49)
              .taskUpdatePriorityJobFirstRun(Instant.MIN.plus(1, ChronoUnit.DAYS))
              .taskUpdatePriorityJobRunEvery(Duration.ofMinutes(17))
              .taskUpdatePriorityJobLockExpirationPeriod(Duration.ofDays(10))
              .userInfoRefreshJobEnabled(true)
              .userRefreshJobFirstRun(Instant.MIN.plus(2, ChronoUnit.DAYS))
              .userRefreshJobRunEvery(Duration.ofDays(5))
              .userRefreshJobLockExpirationPeriod(Duration.ofDays(8))
              .customJobs(Set.of("Job_A", "Job_B"))
              // user configuration
              .addAdditionalUserInfo(true)
              .minimalPermissionsToAssignDomains(Set.of(WorkbasketPermission.CUSTOM_2))
              // database configuration
              .useSpecificDb2Taskquery(false)
              .build();

      KadaiConfiguration copyConfiguration = new Builder(configuration).build();

      verifyConfigurationValuesDifferFromDefaultConfiguration(configuration);
      verifyConfigurationValuesDifferFromDefaultConfiguration(copyConfiguration);
      assertThat(copyConfiguration)
          .hasNoNullFieldsOrProperties()
          .isNotSameAs(configuration)
          .isEqualTo(configuration);
    }

    private void verifyConfigurationValuesDifferFromDefaultConfiguration(
        KadaiConfiguration configuration) {
      KadaiConfiguration defaultConfiguration =
          new Builder(TestContainerExtension.createDataSourceForH2(), true, "KADAI").build();
      Set<String> ignoredFields =
          Set.of(
              "dataSource",
              "useManagedTransactions",
              "securityEnabled",
              "schemaName",
              "properties");
      ReflectionUtil.retrieveAllFields(KadaiConfiguration.class).stream()
          .filter(f -> !ignoredFields.contains(f.getName()))
          .forEach(
              CheckedConsumer.wrap(
                  field -> {
                    field.setAccessible(true);
                    Object value = field.get(configuration);
                    Object defaultValue = field.get(defaultConfiguration);
                    assertThat(value)
                        .as(
                            "Field '%s' (%s) should not be a default configuration value",
                            field.getName(), kadaiPropertyNameByFieldName.get(field.getName()))
                        .isNotEqualTo(defaultValue);
                  }));
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class Structure {
    @TestFactory
    Stream<DynamicTest> should_HaveBuilderMethod_For_EachOptionalConfiguration() {
      Stream<Field> fields =
          ReflectionUtil.retrieveAllFields(Builder.class).stream()
              .filter(not(f -> Modifier.isStatic(f.getModifiers())))
              .filter(not(f -> Modifier.isFinal(f.getModifiers())))
              .filter(not(f -> "properties".equals(f.getName())));

      ThrowingConsumer<Field> testCase =
          field -> {
            // throws Exception if anything is wrong with the builder method
            Method method = Builder.class.getMethod(field.getName(), field.getType());

            assertThat(method).isNotNull();
            assertThat(method.getReturnType()).isEqualTo(Builder.class);
          };

      return DynamicTest.stream(fields, Field::getName, testCase);
    }

    @TestFactory
    Stream<DynamicTest> should_HaveKadaiPropertyAnnotation_For_EachOptionalConfiguration() {
      Stream<Field> fields =
          ReflectionUtil.retrieveAllFields(Builder.class).stream()
              .filter(not(f -> Modifier.isStatic(f.getModifiers())))
              .filter(not(f -> Modifier.isFinal(f.getModifiers())))
              .filter(not(f -> "properties".equals(f.getName())));

      ThrowingConsumer<Field> testCase =
          field -> assertThat(field.isAnnotationPresent(KadaiProperty.class)).isTrue();

      return DynamicTest.stream(fields, Field::getName, testCase);
    }

    @Test
    void should_ShareConfigurationNamesWithBuilder() {
      Set<Pair<String, ? extends Class<?>>> configurationFields =
          ReflectionUtil.retrieveAllFields(KadaiConfiguration.class).stream()
              .filter(not(f -> Modifier.isStatic(f.getModifiers())))
              .map(field -> Pair.of(field.getName(), field.getType()))
              .collect(Collectors.toSet());

      Set<Pair<String, ? extends Class<?>>> builderFields =
          ReflectionUtil.retrieveAllFields(KadaiConfiguration.Builder.class).stream()
              .filter(not(f -> Modifier.isStatic(f.getModifiers())))
              .map(field -> Pair.of(field.getName(), field.getType()))
              .collect(Collectors.toSet());

      assertThat(configurationFields)
          .as("KadaiConfiguration and its builder should have the same configuration names")
          .containsExactlyInAnyOrderElementsOf(builderFields);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class Immutability {
    Map<Class<?>, Supplier<Collection<?>>> mutableCollectionByClass =
        Map.ofEntries(
            Map.entry(List.class, ArrayList::new),
            Map.entry(Set.class, HashSet::new),
            Map.entry(Queue.class, PriorityQueue::new));

    @TestFactory
    Stream<DynamicTest> should_SaveUnmodifiableCollections() {
      // given
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              // necessary to bypass validation since Classification Type will be an empty Set
              .classificationCategoriesByType(Map.of());
      initializeWithMutableCollections(builder);
      Stream<Field> fields =
          ReflectionUtil.retrieveAllFields(KadaiConfiguration.class).stream()
              .filter(f -> Collection.class.isAssignableFrom(f.getType()));

      // when
      KadaiConfiguration configuration = builder.build();

      // then
      ThrowingConsumer<Field> testCase =
          field -> {
            field.setAccessible(true);
            Collection<?> o = (Collection<?>) field.get(configuration);

            // PLEASE do not change this to the _assertThatThrownBy_ syntax.
            // That syntax does not respect the given description and thus might confuse future
            // devs.
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .as("Field '%s' should be an unmodifiable Collection", field.getName())
                .isThrownBy(() -> o.add(null));
          };

      return DynamicTest.stream(fields, Field::getName, testCase);
    }

    @TestFactory
    Stream<DynamicTest> should_SaveUnmodifiableCollections_For_CollectionsAsMapValues() {
      // given
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              // necessary to bypass validation since Classification Categories will be an empty Map
              .classificationTypes(List.of(""));
      initializeWithMutableCollectionsAsMapValues(builder);
      Stream<Field> fields =
          ReflectionUtil.retrieveAllFields(KadaiConfiguration.class).stream()
              .filter(f -> Map.class.isAssignableFrom(f.getType()))
              .filter(
                  f -> {
                    ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                    Class<?> childClass =
                        ReflectionUtil.getRawClass(genericType.getActualTypeArguments()[1]);
                    return Collection.class.isAssignableFrom(childClass);
                  });

      // when
      KadaiConfiguration configuration = builder.build();

      // then
      ThrowingConsumer<Field> testCase =
          field -> {
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<?, Collection<?>> o = (Map<?, Collection<?>>) field.get(configuration);

            o.values()
                .forEach(
                    collection -> {
                      // PLEASE do not change this to the _assertThatThrownBy_ syntax.
                      // That syntax does not respect the given description and thus might confuse
                      // future devs.
                      assertThatExceptionOfType(UnsupportedOperationException.class)
                          .as(
                              "Nested Collection within Map '%s' "
                                  + "should be an unmodifiable Collection",
                              field.getName())
                          .isThrownBy(() -> collection.add(null));
                    });
          };

      return DynamicTest.stream(fields, Field::getName, testCase);
    }

    @TestFactory
    Stream<DynamicTest> should_SaveUnmodifiableMaps() {
      // given
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              // necessary to bypass validation since Classification Categories will be an empty Map
              .classificationTypes(List.of());
      initializeWithMutableMaps(builder);
      Stream<Field> fields =
          ReflectionUtil.retrieveAllFields(KadaiConfiguration.class).stream()
              .filter(f -> Map.class.isAssignableFrom(f.getType()));

      // when
      KadaiConfiguration configuration = builder.build();

      // then
      ThrowingConsumer<Field> testCase =
          field -> {
            field.setAccessible(true);
            Map<?, ?> o = (Map<?, ?>) field.get(configuration);

            // PLEASE do not change this to the _assertThatThrownBy_ syntax.
            // That syntax does not respect the given description and thus might confuse future
            // devs.
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .as("Field '%s' should be an unmodifiable Collection", field.getName())
                .isThrownBy(() -> o.put(null, null));
          };

      return DynamicTest.stream(fields, Field::getName, testCase);
    }

    private void initializeWithMutableCollections(Builder builder) {
      ReflectionUtil.retrieveAllFields(Builder.class).stream()
          .filter(f -> Collection.class.isAssignableFrom(f.getType()))
          .forEach(
              CheckedConsumer.wrap(
                  f -> {
                    f.setAccessible(true);
                    Supplier<Collection<?>> collectionSupplier =
                        mutableCollectionByClass.get(f.getType());
                    if (collectionSupplier == null) {
                      throw new SystemException(
                          String.format("Unknown Collection class '%s'", f.getType()));
                    }
                    f.set(builder, collectionSupplier.get());
                  }));
    }

    private void initializeWithMutableMaps(Builder builder) {
      ReflectionUtil.retrieveAllFields(Builder.class).stream()
          .filter(f -> Map.class.isAssignableFrom(f.getType()))
          .forEach(
              CheckedConsumer.wrap(
                  f -> {
                    f.setAccessible(true);
                    f.set(builder, new HashMap<>());
                  }));
    }

    private void initializeWithMutableCollectionsAsMapValues(Builder builder) {
      ReflectionUtil.retrieveAllFields(Builder.class).stream()
          .filter(f -> Map.class.isAssignableFrom(f.getType()))
          .filter(
              f -> {
                ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                Class<?> childClass =
                    ReflectionUtil.getRawClass(genericType.getActualTypeArguments()[1]);
                return Collection.class.isAssignableFrom(childClass);
              })
          .forEach(
              CheckedConsumer.wrap(
                  f -> {
                    ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                    Class<?> childClass =
                        ReflectionUtil.getRawClass(genericType.getActualTypeArguments()[1]);

                    Supplier<Collection<?>> collectionSupplier =
                        mutableCollectionByClass.get(childClass);
                    if (collectionSupplier == null) {
                      throw new SystemException(
                          String.format("Unknown Collection class '%s'", f.getType()));
                    }

                    f.setAccessible(true);
                    f.set(builder, Map.of("", collectionSupplier.get()));
                  }));
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class Validation {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void should_ThrowInvalidArgumentEx_When_JobBatchSizeIsNotPositive(int jobBatchSize) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .jobBatchSize(jobBatchSize);

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter jobBatchSize (kadai.jobs.batchSize) must be a positive integer");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void should_ThrowInvalidArgumentEx_When_MaxNumberOfJobRetriesIsNotPositive(
        int maxNumberOfJobRetries) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .jobBatchSize(1)
              .maxNumberOfJobRetries(maxNumberOfJobRetries);

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter maxNumberOfJobRetries (kadai.jobs.maxRetries)"
                  + " must be a positive integer");
    }

    @ParameterizedTest
    @ValueSource(strings = {"P-1D", "P0D"})
    void should_ThrowInvalidArgumentEx_When_JobRunEveryIsNotPositive(String cleanupJobRunEvery) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .jobRunEvery(Duration.parse(cleanupJobRunEvery));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter jobRunEvery (kadai.jobs.runEvery) must be a positive duration");
    }

    @Test
    void should_ThrowInvalidArgumentEx_When_SimpleHistoryCleanupJobMinimumAgeIsNegative() {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .simpleHistoryCleanupJobMinimumAge(Duration.ofDays(-1));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter simpleHistoryCleanupJobMinimumAge "
                  + "(kadai.jobs.cleanup.history.simple.minimumAge) must not be negative");
    }

    @Test
    void should_ThrowInvalidArgumentEx_When_TaskCleanupJobMinimumAgeIsNegative() {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .taskCleanupJobMinimumAge(Duration.ofDays(-1));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter taskCleanupJobMinimumAge (kadai.jobs.cleanup.task.minimumAge) "
                  + "must not be negative");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void should_ThrowInvalidArgumentEx_When_TaskUpdatePriorityJobBatchSizeIsNotPositive(
        int priorityJobBatchSize) {

      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .taskUpdatePriorityJobBatchSize(priorityJobBatchSize);

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter taskUpdatePriorityJobBatchSize (kadai.jobs.priority.task.batchSize)"
                  + " must be a positive integer");
    }

    @ParameterizedTest
    @ValueSource(strings = {"P-1D", "P0D"})
    void should_ThrowInvalidArgumentEx_When_TaskPriorityUpdatePriorityJobRunEveryIsNotPositive(
        String priorityJobRunEvery) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .taskUpdatePriorityJobRunEvery(Duration.parse(priorityJobRunEvery));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter taskUpdatePriorityJobRunEvery (kadai.jobs.priority.task.runEvery)"
                  + " must be a positive duration");
    }

    @ParameterizedTest
    @ValueSource(strings = {"P-1D", "P0D"})
    void should_ThrowInvalidArgumentEx_When_UserRefreshJobRunEveryIsNotPositive(
        String userRefreshJobRunEvery) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .userRefreshJobRunEvery(Duration.parse(userRefreshJobRunEvery));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter userRefreshJobRunEvery (kadai.jobs.refresh.user.runEvery)"
                  + " must be a positive duration");
    }

    @Test
    void should_ThrowInvalidArgumentEx_When_JobSchedulerInitialStartDelayIsNegative() {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .jobSchedulerInitialStartDelay(-1);

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter jobSchedulerInitialStartDelay (kadai.jobs.scheduler.initialStartDelay)"
                  + " must be a natural integer");
    }

    @Test
    void should_ThrowInvalidArgumentEx_When_ClassificationTypesDoesNotExist() {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .classificationTypes(List.of("valid"))
              .classificationCategoriesByType(Map.of("does_not_exist", List.of("a", "b")));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter classificationCategoriesByType"
                  + " (kadai.classification.categories.<KEY>) is configured incorrectly. Please"
                  + " check whether all specified Classification Types exist. Additionally, check"
                  + " whether the correct separator is used in the property"
                  + " kadai.classification.types .");
    }

    @Test
    void should_ThrowInvalidArgumentEx_When_ClassificationCategoryDoesNotExist() {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .classificationTypes(List.of("type1", "type2"))
              .classificationCategoriesByType(Map.of("type1", List.of("a", "b")));

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Some Classification Categories for parameter classificationTypes "
                  + "(kadai.classification.types) are missing. "
                  + "configured: [TYPE1, TYPE2] detected: [TYPE1]");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void should_ThrowInvalidArgumentEx_When_JobSchedulerPeriodIsNotPositive(
        int jobSchedulerPeriod) {
      KadaiConfiguration.Builder builder =
          new KadaiConfiguration.Builder(
                  TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .jobSchedulerPeriod(jobSchedulerPeriod);

      ThrowingCallable call = builder::build;

      assertThatThrownBy(call)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessageContaining(
              "Parameter jobSchedulerPeriod (kadai.jobs.scheduler.period)"
                  + " must be a positive integer");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class Adjustments {

    @Test
    void should_MakeDomainUpperCase() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .domains(List.of("a", "b"))
              .build();

      assertThat(configuration.getDomains()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void should_MakeClassificationTypesUpperCase() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .classificationTypes(List.of("a", "b"))
              .classificationCategoriesByType(
                  Map.ofEntries(Map.entry("a", List.of()), Map.entry("b", List.of())))
              .build();

      assertThat(configuration.getClassificationTypes()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void should_MakeClassificationCategoriesUpperCase() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .classificationTypes(List.of("type_a", "type_b"))
              .classificationCategoriesByType(
                  Map.ofEntries(
                      Map.entry("type_a", List.of("a", "b")),
                      Map.entry("type_b", List.of("c", "d"))))
              .build();

      assertThat(configuration.getClassificationCategoriesByType())
          .containsExactlyInAnyOrderEntriesOf(
              Map.ofEntries(
                  Map.entry("TYPE_A", List.of("A", "B")), Map.entry("TYPE_B", List.of("C", "D"))));
    }

    @Test
    void should_OrderClassificationCategoriesByTypeOrder() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .classificationTypes(List.of("type_a", "type_c", "type_b"))
              .classificationCategoriesByType(
                  Map.ofEntries(
                      Map.entry("type_a", Collections.emptyList()),
                      Map.entry("type_b", Collections.emptyList()),
                      Map.entry("type_c", Collections.emptyList())))
              .build();

      List<String> expectedKeysOrder = Arrays.asList("TYPE_A", "TYPE_C", "TYPE_B");
      List<String> actualKeysOrder =
          new ArrayList<>(configuration.getClassificationCategoriesByType().keySet());
      assertThat(actualKeysOrder).containsExactlyElementsOf(expectedKeysOrder);
    }

    @Test
    void should_FillAllKadaiRoles_When_RoleMapIsIncomplete() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .roleMap(Map.ofEntries(Map.entry(KadaiRole.ADMIN, Set.of("a", "b"))))
              .build();

      assertThat(configuration.getRoleMap())
          .containsExactlyInAnyOrderEntriesOf(
              Map.ofEntries(
                  Map.entry(KadaiRole.USER, Set.of()),
                  Map.entry(KadaiRole.BUSINESS_ADMIN, Set.of()),
                  Map.entry(KadaiRole.ADMIN, Set.of("a", "b")),
                  Map.entry(KadaiRole.MONITOR, Set.of()),
                  Map.entry(KadaiRole.TASK_ADMIN, Set.of()),
                  Map.entry(KadaiRole.TASK_ROUTER, Set.of())));
    }

    @Test
    void should_MakeAccessIdsLowerCase() {
      KadaiConfiguration configuration =
          new Builder(TestContainerExtension.createDataSourceForH2(), false, "KADAI")
              .roleMap(
                  Map.ofEntries(
                      Map.entry(KadaiRole.USER, Set.of("USER-1", "USER-2")),
                      Map.entry(
                          KadaiRole.BUSINESS_ADMIN, Set.of("BUSINESS_ADMIN-1", "BUSINESS_ADMIN-2")),
                      Map.entry(KadaiRole.ADMIN, Set.of("ADMIN-1", "ADMIN-2")),
                      Map.entry(KadaiRole.MONITOR, Set.of("MONITOR-1", "MONITOR-2")),
                      Map.entry(KadaiRole.TASK_ADMIN, Set.of("TASK_ADMIN-1", "TASK_ADMIN-2")),
                      Map.entry(KadaiRole.TASK_ROUTER, Set.of("TASK_ROUTER-1", "TASK_ROUTER-2"))))
              .build();

      assertThat(configuration.getRoleMap())
          .containsExactlyInAnyOrderEntriesOf(
              Map.ofEntries(
                  Map.entry(KadaiRole.USER, Set.of("user-1", "user-2")),
                  Map.entry(
                      KadaiRole.BUSINESS_ADMIN, Set.of("business_admin-1", "business_admin-2")),
                  Map.entry(KadaiRole.ADMIN, Set.of("admin-1", "admin-2")),
                  Map.entry(KadaiRole.MONITOR, Set.of("monitor-1", "monitor-2")),
                  Map.entry(KadaiRole.TASK_ADMIN, Set.of("task_admin-1", "task_admin-2")),
                  Map.entry(KadaiRole.TASK_ROUTER, Set.of("task_router-1", "task_router-2"))));
    }
  }
}
