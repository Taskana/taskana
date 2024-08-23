package io.kadai;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;

import io.kadai.common.api.CustomHoliday;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.LocalTimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.configuration.KadaiProperty;
import io.kadai.common.internal.configuration.parser.PropertyParser;
import io.kadai.common.internal.util.FileLoaderUtil;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.internal.util.ReflectionUtil;
import io.kadai.workbasket.api.WorkbasketPermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This central class creates the KadaiEngine and holds all the information about DB and Security.
 * <br>
 * Security is enabled by default. <br>
 * All members are immutable, also Lists and Maps and Sets.
 */
public class KadaiConfiguration {

  // region general configuration
  private final DataSource dataSource;
  private final boolean useManagedTransactions;
  private final String schemaName;
  private final boolean securityEnabled;

  private final List<String> domains;
  private final boolean enforceServiceLevel;
  // endregion

  // region authentication configuration
  private final Map<KadaiRole, Set<String>> roleMap;
  // endregion

  // region classification configuration
  private final List<String> classificationTypes;

  private final Map<String, List<String>> classificationCategoriesByType;
  // endregion

  // region working time configuration
  private final boolean useWorkingTimeCalculation;
  private final Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule;
  private final ZoneId workingTimeScheduleTimeZone;
  private final Set<CustomHoliday> customHolidays;
  private final boolean germanPublicHolidaysEnabled;
  private final boolean germanPublicHolidaysCorpusChristiEnabled;
  // endregion

  // region history configuration
  private final boolean deleteHistoryEventsOnTaskDeletionEnabled;
  private final String logHistoryLoggerName;
  // endregion

  // region job configuration
  private final boolean jobSchedulerEnabled;
  private final long jobSchedulerInitialStartDelay;
  private final long jobSchedulerPeriod;
  private final TimeUnit jobSchedulerPeriodTimeUnit;
  private final int maxNumberOfJobRetries;
  private final int jobBatchSize;
  private final Instant jobFirstRun;
  private final Duration jobRunEvery;
  private final Duration jobLockExpirationPeriod;
  private final boolean taskCleanupJobEnabled;
  private final Duration taskCleanupJobMinimumAge;
  private final boolean taskCleanupJobAllCompletedSameParentBusiness;
  private final Duration taskCleanupJobLockExpirationPeriod;

  private final boolean workbasketCleanupJobEnabled;

  private final Duration workbasketCleanupJobLockExpirationPeriod;
  private final boolean simpleHistoryCleanupJobEnabled;
  private final int simpleHistoryCleanupJobBatchSize;
  private final Duration simpleHistoryCleanupJobMinimumAge;
  private final boolean simpleHistoryCleanupJobAllCompletedSameParentBusiness;
  private final Duration simpleHistoryCleanupJobLockExpirationPeriod;
  private final boolean taskUpdatePriorityJobEnabled;
  private final int taskUpdatePriorityJobBatchSize;
  private final Instant taskUpdatePriorityJobFirstRun;
  private final Duration taskUpdatePriorityJobRunEvery;
  private final Duration taskUpdatePriorityJobLockExpirationPeriod;

  private final boolean userInfoRefreshJobEnabled;
  private final Instant userRefreshJobFirstRun;
  private final Duration userRefreshJobRunEvery;
  private final Duration userRefreshJobLockExpirationPeriod;

  private final Set<String> customJobs;
  // endregion

  // region user configuration
  private final boolean addAdditionalUserInfo;
  private final Set<WorkbasketPermission> minimalPermissionsToAssignDomains;
  // endregion

  // region database configuration
  private final boolean useSpecificDb2Taskquery;
  // endregion

  // region custom configuration
  private final Map<String, String> properties;

  // endregion

  private KadaiConfiguration(Builder builder) {
    // general configuration
    this.dataSource = builder.dataSource;
    this.useManagedTransactions = builder.useManagedTransactions;
    this.schemaName = builder.schemaName;
    this.securityEnabled = builder.securityEnabled;
    this.domains = Collections.unmodifiableList(builder.domains);
    this.enforceServiceLevel = builder.enforceServiceLevel;
    // authentication configuration
    this.roleMap =
        builder.roleMap.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));
    // classification configuration
    this.classificationTypes = Collections.unmodifiableList(builder.classificationTypes);
    this.classificationCategoriesByType =
        Collections.unmodifiableMap(
            builder.classificationCategoriesByType.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Collections.unmodifiableList(e.getValue()),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new)));
    // working time configuration
    this.useWorkingTimeCalculation = builder.useWorkingTimeCalculation;
    this.workingTimeSchedule =
        builder.workingTimeSchedule.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));
    this.workingTimeScheduleTimeZone = builder.workingTimeScheduleTimeZone;
    this.customHolidays = Collections.unmodifiableSet(builder.customHolidays);
    this.germanPublicHolidaysEnabled = builder.germanPublicHolidaysEnabled;
    this.germanPublicHolidaysCorpusChristiEnabled =
        builder.germanPublicHolidaysCorpusChristiEnabled;
    // history configuration
    this.deleteHistoryEventsOnTaskDeletionEnabled =
        builder.deleteHistoryEventsOnTaskDeletionEnabled;
    this.logHistoryLoggerName = builder.logHistoryLoggerName;
    // job configuration
    this.jobSchedulerEnabled = builder.jobSchedulerEnabled;
    this.jobSchedulerInitialStartDelay = builder.jobSchedulerInitialStartDelay;
    this.jobSchedulerPeriod = builder.jobSchedulerPeriod;
    this.jobSchedulerPeriodTimeUnit = builder.jobSchedulerPeriodTimeUnit;
    this.maxNumberOfJobRetries = builder.maxNumberOfJobRetries;
    this.jobBatchSize = builder.jobBatchSize;
    this.jobFirstRun = builder.jobFirstRun;
    this.jobRunEvery = builder.jobRunEvery;
    this.jobLockExpirationPeriod = builder.jobLockExpirationPeriod;
    this.taskCleanupJobEnabled = builder.taskCleanupJobEnabled;
    this.taskCleanupJobMinimumAge = builder.taskCleanupJobMinimumAge;
    this.taskCleanupJobAllCompletedSameParentBusiness =
        builder.taskCleanupJobAllCompletedSameParentBusiness;
    this.taskCleanupJobLockExpirationPeriod = builder.taskCleanupJobLockExpirationPeriod;
    this.workbasketCleanupJobEnabled = builder.workbasketCleanupJobEnabled;
    this.workbasketCleanupJobLockExpirationPeriod =
        builder.workbasketCleanupJobLockExpirationPeriod;
    this.simpleHistoryCleanupJobEnabled = builder.simpleHistoryCleanupJobEnabled;
    this.simpleHistoryCleanupJobBatchSize = builder.simpleHistoryCleanupJobBatchSize;
    this.simpleHistoryCleanupJobMinimumAge = builder.simpleHistoryCleanupJobMinimumAge;
    this.simpleHistoryCleanupJobAllCompletedSameParentBusiness =
        builder.simpleHistoryCleanupJobAllCompletedSameParentBusiness;
    this.simpleHistoryCleanupJobLockExpirationPeriod =
        builder.simpleHistoryCleanupJobLockExpirationPeriod;
    this.taskUpdatePriorityJobEnabled = builder.taskUpdatePriorityJobEnabled;
    this.taskUpdatePriorityJobBatchSize = builder.taskUpdatePriorityJobBatchSize;
    this.taskUpdatePriorityJobFirstRun = builder.taskUpdatePriorityJobFirstRun;
    this.taskUpdatePriorityJobRunEvery = builder.taskUpdatePriorityJobRunEvery;
    this.taskUpdatePriorityJobLockExpirationPeriod =
        builder.taskUpdatePriorityJobLockExpirationPeriod;
    this.userInfoRefreshJobEnabled = builder.userInfoRefreshJobEnabled;
    this.userRefreshJobFirstRun = builder.userRefreshJobFirstRun;
    this.userRefreshJobRunEvery = builder.userRefreshJobRunEvery;
    this.userRefreshJobLockExpirationPeriod = builder.userRefreshJobLockExpirationPeriod;
    this.customJobs = Collections.unmodifiableSet(builder.customJobs);
    // user configuration
    this.addAdditionalUserInfo = builder.addAdditionalUserInfo;
    this.minimalPermissionsToAssignDomains =
        Collections.unmodifiableSet(builder.minimalPermissionsToAssignDomains);
    // database configuration
    this.useSpecificDb2Taskquery = builder.useSpecificDb2Taskquery;
    // custom configuration
    this.properties = Map.copyOf(builder.properties);
  }

  /**
   * Helper method to determine whether all access ids (user id and group ids) should be used in
   * lower case.
   *
   * @return true if all access ids should be used in lower case, false otherwise
   */
  public static boolean shouldUseLowerCaseForAccessIds() {
    return true;
  }

  public List<String> getAllClassificationCategories() {
    return this.classificationCategoriesByType.values().stream()
        .flatMap(Collection::stream)
        .toList();
  }

  public List<String> getClassificationCategoriesByType(String type) {
    return classificationCategoriesByType.getOrDefault(type, Collections.emptyList());
  }

  public Map<String, List<String>> getClassificationCategoriesByType() {
    return this.classificationCategoriesByType;
  }

  // region getters

  public DataSource getDataSource() {
    return dataSource;
  }

  public boolean isUseManagedTransactions() {
    return useManagedTransactions;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public boolean isSecurityEnabled() {
    return securityEnabled;
  }

  public List<String> getDomains() {
    return domains;
  }

  public boolean isEnforceServiceLevel() {
    return enforceServiceLevel;
  }

  public Map<KadaiRole, Set<String>> getRoleMap() {
    return roleMap;
  }

  public List<String> getClassificationTypes() {
    return classificationTypes;
  }

  public boolean isUseWorkingTimeCalculation() {
    return useWorkingTimeCalculation;
  }

  public Map<DayOfWeek, Set<LocalTimeInterval>> getWorkingTimeSchedule() {
    return workingTimeSchedule;
  }

  public ZoneId getWorkingTimeScheduleTimeZone() {
    return workingTimeScheduleTimeZone;
  }

  public Set<CustomHoliday> getCustomHolidays() {
    return customHolidays;
  }

  public boolean isGermanPublicHolidaysEnabled() {
    return germanPublicHolidaysEnabled;
  }

  public boolean isGermanPublicHolidaysCorpusChristiEnabled() {
    return germanPublicHolidaysCorpusChristiEnabled;
  }

  public boolean isDeleteHistoryEventsOnTaskDeletionEnabled() {
    return deleteHistoryEventsOnTaskDeletionEnabled;
  }

  public String getLogHistoryLoggerName() {
    return logHistoryLoggerName;
  }

  public boolean isJobSchedulerEnabled() {
    return jobSchedulerEnabled;
  }

  public long getJobSchedulerInitialStartDelay() {
    return jobSchedulerInitialStartDelay;
  }

  public long getJobSchedulerPeriod() {
    return jobSchedulerPeriod;
  }

  public TimeUnit getJobSchedulerPeriodTimeUnit() {
    return jobSchedulerPeriodTimeUnit;
  }

  public int getMaxNumberOfJobRetries() {
    return maxNumberOfJobRetries;
  }

  public int getJobBatchSize() {
    return jobBatchSize;
  }

  public Instant getJobFirstRun() {
    return jobFirstRun;
  }

  public Duration getJobRunEvery() {
    return jobRunEvery;
  }

  public Duration getJobLockExpirationPeriod() {
    return jobLockExpirationPeriod;
  }

  public boolean isTaskCleanupJobEnabled() {
    return taskCleanupJobEnabled;
  }

  public Duration getTaskCleanupJobMinimumAge() {
    return taskCleanupJobMinimumAge;
  }

  public boolean isTaskCleanupJobAllCompletedSameParentBusiness() {
    return taskCleanupJobAllCompletedSameParentBusiness;
  }

  public Duration getTaskCleanupJobLockExpirationPeriod() {
    return taskCleanupJobLockExpirationPeriod;
  }

  public boolean isWorkbasketCleanupJobEnabled() {
    return workbasketCleanupJobEnabled;
  }

  public Duration getWorkbasketCleanupJobLockExpirationPeriod() {
    return workbasketCleanupJobLockExpirationPeriod;
  }

  public boolean isSimpleHistoryCleanupJobEnabled() {
    return simpleHistoryCleanupJobEnabled;
  }

  public int getSimpleHistoryCleanupJobBatchSize() {
    return simpleHistoryCleanupJobBatchSize;
  }

  public Duration getSimpleHistoryCleanupJobMinimumAge() {
    return simpleHistoryCleanupJobMinimumAge;
  }

  public boolean isSimpleHistoryCleanupJobAllCompletedSameParentBusiness() {
    return simpleHistoryCleanupJobAllCompletedSameParentBusiness;
  }

  public Duration getSimpleHistoryCleanupJobLockExpirationPeriod() {
    return simpleHistoryCleanupJobLockExpirationPeriod;
  }

  public boolean isTaskUpdatePriorityJobEnabled() {
    return taskUpdatePriorityJobEnabled;
  }

  public int getTaskUpdatePriorityJobBatchSize() {
    return taskUpdatePriorityJobBatchSize;
  }

  public Instant getTaskUpdatePriorityJobFirstRun() {
    return taskUpdatePriorityJobFirstRun;
  }

  public Duration getTaskUpdatePriorityJobRunEvery() {
    return taskUpdatePriorityJobRunEvery;
  }

  public Duration getTaskUpdatePriorityJobLockExpirationPeriod() {
    return taskUpdatePriorityJobLockExpirationPeriod;
  }

  public boolean isUserInfoRefreshJobEnabled() {
    return userInfoRefreshJobEnabled;
  }

  public Instant getUserRefreshJobFirstRun() {
    return userRefreshJobFirstRun;
  }

  public Duration getUserRefreshJobRunEvery() {
    return userRefreshJobRunEvery;
  }

  public Duration getUserRefreshJobLockExpirationPeriod() {
    return userRefreshJobLockExpirationPeriod;
  }

  public Set<String> getCustomJobs() {
    return customJobs;
  }

  public boolean isAddAdditionalUserInfo() {
    return addAdditionalUserInfo;
  }

  public Set<WorkbasketPermission> getMinimalPermissionsToAssignDomains() {
    return minimalPermissionsToAssignDomains;
  }

  public boolean isUseSpecificDb2Taskquery() {
    return useSpecificDb2Taskquery;
  }

  /**
   * return all properties loaded from kadai properties file. Per Design the normal Properties are
   * not immutable, so we return here an ImmutableMap, because we don't want direct changes in the
   * configuration.
   *
   * @return all properties loaded from kadai properties file
   */
  public Map<String, String> getProperties() {
    return properties;
  }

  // endregion

  // region hashCode, equals + toString

  @Override
  public int hashCode() {
    return Objects.hash(
        dataSource,
        useManagedTransactions,
        schemaName,
        securityEnabled,
        domains,
        enforceServiceLevel,
        roleMap,
        classificationTypes,
        classificationCategoriesByType,
        useWorkingTimeCalculation,
        workingTimeSchedule,
        workingTimeScheduleTimeZone,
        customHolidays,
        germanPublicHolidaysEnabled,
        germanPublicHolidaysCorpusChristiEnabled,
        deleteHistoryEventsOnTaskDeletionEnabled,
        logHistoryLoggerName,
        jobSchedulerEnabled,
        jobSchedulerInitialStartDelay,
        jobSchedulerPeriod,
        jobSchedulerPeriodTimeUnit,
        maxNumberOfJobRetries,
        jobBatchSize,
        jobFirstRun,
        jobRunEvery,
        jobLockExpirationPeriod,
        taskCleanupJobEnabled,
        taskCleanupJobMinimumAge,
        taskCleanupJobAllCompletedSameParentBusiness,
        taskCleanupJobLockExpirationPeriod,
        workbasketCleanupJobEnabled,
        workbasketCleanupJobLockExpirationPeriod,
        simpleHistoryCleanupJobEnabled,
        simpleHistoryCleanupJobBatchSize,
        simpleHistoryCleanupJobMinimumAge,
        simpleHistoryCleanupJobAllCompletedSameParentBusiness,
        simpleHistoryCleanupJobLockExpirationPeriod,
        taskUpdatePriorityJobEnabled,
        taskUpdatePriorityJobBatchSize,
        taskUpdatePriorityJobFirstRun,
        taskUpdatePriorityJobRunEvery,
        taskUpdatePriorityJobLockExpirationPeriod,
        userInfoRefreshJobEnabled,
        userRefreshJobFirstRun,
        userRefreshJobRunEvery,
        userRefreshJobLockExpirationPeriod,
        customJobs,
        addAdditionalUserInfo,
        minimalPermissionsToAssignDomains,
        useSpecificDb2Taskquery,
        properties);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof KadaiConfiguration)) {
      return false;
    }
    KadaiConfiguration other = (KadaiConfiguration) obj;
    return useManagedTransactions == other.useManagedTransactions
        && securityEnabled == other.securityEnabled
        && enforceServiceLevel == other.enforceServiceLevel
        && useWorkingTimeCalculation == other.useWorkingTimeCalculation
        && germanPublicHolidaysEnabled == other.germanPublicHolidaysEnabled
        && germanPublicHolidaysCorpusChristiEnabled
            == other.germanPublicHolidaysCorpusChristiEnabled
        && deleteHistoryEventsOnTaskDeletionEnabled
            == other.deleteHistoryEventsOnTaskDeletionEnabled
        && jobSchedulerEnabled == other.jobSchedulerEnabled
        && jobSchedulerInitialStartDelay == other.jobSchedulerInitialStartDelay
        && jobSchedulerPeriod == other.jobSchedulerPeriod
        && maxNumberOfJobRetries == other.maxNumberOfJobRetries
        && jobBatchSize == other.jobBatchSize
        && taskCleanupJobEnabled == other.taskCleanupJobEnabled
        && taskCleanupJobAllCompletedSameParentBusiness
            == other.taskCleanupJobAllCompletedSameParentBusiness
        && workbasketCleanupJobEnabled == other.workbasketCleanupJobEnabled
        && simpleHistoryCleanupJobEnabled == other.simpleHistoryCleanupJobEnabled
        && simpleHistoryCleanupJobBatchSize == other.simpleHistoryCleanupJobBatchSize
        && simpleHistoryCleanupJobAllCompletedSameParentBusiness
            == other.simpleHistoryCleanupJobAllCompletedSameParentBusiness
        && taskUpdatePriorityJobEnabled == other.taskUpdatePriorityJobEnabled
        && taskUpdatePriorityJobBatchSize == other.taskUpdatePriorityJobBatchSize
        && userInfoRefreshJobEnabled == other.userInfoRefreshJobEnabled
        && addAdditionalUserInfo == other.addAdditionalUserInfo
        && useSpecificDb2Taskquery == other.useSpecificDb2Taskquery
        && Objects.equals(dataSource, other.dataSource)
        && Objects.equals(schemaName, other.schemaName)
        && Objects.equals(domains, other.domains)
        && Objects.equals(roleMap, other.roleMap)
        && Objects.equals(classificationTypes, other.classificationTypes)
        && Objects.equals(classificationCategoriesByType, other.classificationCategoriesByType)
        && Objects.equals(workingTimeSchedule, other.workingTimeSchedule)
        && Objects.equals(workingTimeScheduleTimeZone, other.workingTimeScheduleTimeZone)
        && Objects.equals(customHolidays, other.customHolidays)
        && Objects.equals(logHistoryLoggerName, other.logHistoryLoggerName)
        && jobSchedulerPeriodTimeUnit == other.jobSchedulerPeriodTimeUnit
        && Objects.equals(jobFirstRun, other.jobFirstRun)
        && Objects.equals(jobRunEvery, other.jobRunEvery)
        && Objects.equals(jobLockExpirationPeriod, other.jobLockExpirationPeriod)
        && Objects.equals(taskCleanupJobMinimumAge, other.taskCleanupJobMinimumAge)
        && Objects.equals(
            taskCleanupJobLockExpirationPeriod, other.taskCleanupJobLockExpirationPeriod)
        && Objects.equals(
            workbasketCleanupJobLockExpirationPeriod,
            other.workbasketCleanupJobLockExpirationPeriod)
        && Objects.equals(
            simpleHistoryCleanupJobMinimumAge, other.simpleHistoryCleanupJobMinimumAge)
        && Objects.equals(
            simpleHistoryCleanupJobLockExpirationPeriod,
            other.simpleHistoryCleanupJobLockExpirationPeriod)
        && Objects.equals(taskUpdatePriorityJobFirstRun, other.taskUpdatePriorityJobFirstRun)
        && Objects.equals(taskUpdatePriorityJobRunEvery, other.taskUpdatePriorityJobRunEvery)
        && Objects.equals(
            taskUpdatePriorityJobLockExpirationPeriod,
            other.taskUpdatePriorityJobLockExpirationPeriod)
        && Objects.equals(userRefreshJobFirstRun, other.userRefreshJobFirstRun)
        && Objects.equals(userRefreshJobRunEvery, other.userRefreshJobRunEvery)
        && Objects.equals(
            userRefreshJobLockExpirationPeriod, other.userRefreshJobLockExpirationPeriod)
        && Objects.equals(customJobs, other.customJobs)
        && Objects.equals(
            minimalPermissionsToAssignDomains, other.minimalPermissionsToAssignDomains)
        && Objects.equals(properties, other.properties);
  }

  @Override
  public String toString() {
    return "KadaiConfiguration{"
        + "dataSource="
        + dataSource
        + ", useManagedTransactions="
        + useManagedTransactions
        + ", schemaName='"
        + schemaName
        + '\''
        + ", securityEnabled="
        + securityEnabled
        + ", domains="
        + domains
        + ", enforceServiceLevel="
        + enforceServiceLevel
        + ", roleMap="
        + roleMap
        + ", classificationTypes="
        + classificationTypes
        + ", classificationCategoriesByType="
        + classificationCategoriesByType
        + ", useWorkingTimeCalculation="
        + useWorkingTimeCalculation
        + ", workingTimeSchedule="
        + workingTimeSchedule
        + ", workingTimeScheduleTimeZone="
        + workingTimeScheduleTimeZone
        + ", customHolidays="
        + customHolidays
        + ", germanPublicHolidaysEnabled="
        + germanPublicHolidaysEnabled
        + ", germanPublicHolidaysCorpusChristiEnabled="
        + germanPublicHolidaysCorpusChristiEnabled
        + ", deleteHistoryEventsOnTaskDeletionEnabled="
        + deleteHistoryEventsOnTaskDeletionEnabled
        + ", logHistoryLoggerName='"
        + logHistoryLoggerName
        + '\''
        + ", jobSchedulerEnabled="
        + jobSchedulerEnabled
        + ", jobSchedulerInitialStartDelay="
        + jobSchedulerInitialStartDelay
        + ", jobSchedulerPeriod="
        + jobSchedulerPeriod
        + ", jobSchedulerPeriodTimeUnit="
        + jobSchedulerPeriodTimeUnit
        + ", maxNumberOfJobRetries="
        + maxNumberOfJobRetries
        + ", jobBatchSize="
        + jobBatchSize
        + ", jobFirstRun="
        + jobFirstRun
        + ", jobRunEvery="
        + jobRunEvery
        + ", jobLockExpirationPeriod="
        + jobLockExpirationPeriod
        + ", taskCleanupJobEnabled="
        + taskCleanupJobEnabled
        + ", taskCleanupJobMinimumAge="
        + taskCleanupJobMinimumAge
        + ", taskCleanupJobAllCompletedSameParentBusiness="
        + taskCleanupJobAllCompletedSameParentBusiness
        + ", taskCleanupJobLockExpirationPeriod="
        + taskCleanupJobLockExpirationPeriod
        + ", workbasketCleanupJobEnabled="
        + workbasketCleanupJobEnabled
        + ", workbasketCleanupJobLockExpirationPeriod="
        + workbasketCleanupJobLockExpirationPeriod
        + ", simpleHistoryCleanupJobEnabled="
        + simpleHistoryCleanupJobEnabled
        + ", simpleHistoryCleanupJobBatchSize="
        + simpleHistoryCleanupJobBatchSize
        + ", simpleHistoryCleanupJobMinimumAge="
        + simpleHistoryCleanupJobMinimumAge
        + ", simpleHistoryCleanupJobAllCompletedSameParentBusiness="
        + simpleHistoryCleanupJobAllCompletedSameParentBusiness
        + ", simpleHistoryCleanupJobLockExpirationPeriod="
        + simpleHistoryCleanupJobLockExpirationPeriod
        + ", taskUpdatePriorityJobEnabled="
        + taskUpdatePriorityJobEnabled
        + ", taskUpdatePriorityJobBatchSize="
        + taskUpdatePriorityJobBatchSize
        + ", taskUpdatePriorityJobFirstRun="
        + taskUpdatePriorityJobFirstRun
        + ", taskUpdatePriorityJobRunEvery="
        + taskUpdatePriorityJobRunEvery
        + ", taskUpdatePriorityJobLockExpirationPeriod="
        + taskUpdatePriorityJobLockExpirationPeriod
        + ", userInfoRefreshJobEnabled="
        + userInfoRefreshJobEnabled
        + ", userRefreshJobFirstRun="
        + userRefreshJobFirstRun
        + ", userRefreshJobRunEvery="
        + userRefreshJobRunEvery
        + ", userRefreshJobLockExpirationPeriod="
        + userRefreshJobLockExpirationPeriod
        + ", customJobs="
        + customJobs
        + ", addAdditionalUserInfo="
        + addAdditionalUserInfo
        + ", minimalPermissionsToAssignDomains="
        + minimalPermissionsToAssignDomains
        + ", useSpecificDb2Taskquery="
        + useSpecificDb2Taskquery
        + ", properties="
        + properties
        + '}';
  }

  // endregion

  public static class Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);
    private static final String DEFAULT_KADAI_PROPERTIES = "/kadai.properties";
    private static final String DEFAULT_KADAI_PROPERTY_SEPARATOR = "|";

    // region general configuration
    private final DataSource dataSource;
    private final boolean useManagedTransactions;
    private final String schemaName;
    private final boolean securityEnabled;

    @KadaiProperty("kadai.domains")
    private List<String> domains = new ArrayList<>();

    @KadaiProperty("kadai.servicelevel.validation.enforce")
    private boolean enforceServiceLevel = true;

    // endregion

    // region authentication configuration
    @KadaiProperty("kadai.roles")
    private Map<KadaiRole, Set<String>> roleMap = new EnumMap<>(KadaiRole.class);

    // endregion

    // region classification configuration
    @KadaiProperty("kadai.classification.types")
    private List<String> classificationTypes = new ArrayList<>();

    @KadaiProperty("kadai.classification.categories")
    private Map<String, List<String>> classificationCategoriesByType = new HashMap<>();

    // endregion

    // region working time configuration

    @KadaiProperty("kadai.workingTime.useWorkingTimeCalculation")
    private boolean useWorkingTimeCalculation = true;

    @KadaiProperty("kadai.workingTime.schedule")
    private Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule =
        initDefaultWorkingTimeSchedule();

    @KadaiProperty("kadai.workingTime.timezone")
    private ZoneId workingTimeScheduleTimeZone = ZoneId.of("Europe/Berlin");

    @KadaiProperty("kadai.workingTime.holidays.custom")
    private Set<CustomHoliday> customHolidays = new HashSet<>();

    @KadaiProperty("kadai.workingTime.holidays.german.enabled")
    private boolean germanPublicHolidaysEnabled = true;

    @KadaiProperty("kadai.workingTime.holidays.german.corpus-christi.enabled")
    private boolean germanPublicHolidaysCorpusChristiEnabled = false;

    // endregion

    // region history configuration
    @KadaiProperty("kadai.history.simple.deleteOnTaskDeletion.enabled")
    private boolean deleteHistoryEventsOnTaskDeletionEnabled = false;

    @KadaiProperty("kadai.history.logger.name")
    private String logHistoryLoggerName = null; // default value will be set in the logger class.

    // endregion

    // region job configuration
    @KadaiProperty("kadai.jobs.scheduler.enabled")
    private boolean jobSchedulerEnabled = true;

    @KadaiProperty("kadai.jobs.scheduler.initialStartDelay")
    private long jobSchedulerInitialStartDelay = 0;

    @KadaiProperty("kadai.jobs.scheduler.period")
    private long jobSchedulerPeriod = 5;

    @KadaiProperty("kadai.jobs.scheduler.periodTimeUnit")
    private TimeUnit jobSchedulerPeriodTimeUnit = TimeUnit.MINUTES;

    @KadaiProperty("kadai.jobs.maxRetries")
    private int maxNumberOfJobRetries = 3;

    @KadaiProperty("kadai.jobs.batchSize")
    private int jobBatchSize = 100;

    @KadaiProperty("kadai.jobs.firstRunAt")
    private Instant jobFirstRun = Instant.parse("2023-01-01T00:00:00Z");

    @KadaiProperty("kadai.jobs.runEvery")
    private Duration jobRunEvery = Duration.ofDays(1);

    @KadaiProperty("kadai.jobs.lockExpirationPeriod")
    private Duration jobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.cleanup.task.enable")
    private boolean taskCleanupJobEnabled = true;

    @KadaiProperty("kadai.jobs.cleanup.task.minimumAge")
    private Duration taskCleanupJobMinimumAge = Duration.ofDays(14);

    @KadaiProperty("kadai.jobs.cleanup.task.allCompletedSameParentBusiness")
    private boolean taskCleanupJobAllCompletedSameParentBusiness = true;

    @KadaiProperty("kadai.jobs.cleanup.task.lockExpirationPeriod")
    private Duration taskCleanupJobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.cleanup.workbasket.enable")
    private boolean workbasketCleanupJobEnabled = true;

    @KadaiProperty("kadai.jobs.cleanup.workbasket.lockExpirationPeriod")
    private Duration workbasketCleanupJobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.cleanup.history.simple.enable")
    private boolean simpleHistoryCleanupJobEnabled = false;

    @KadaiProperty("kadai.jobs.cleanup.history.simple.batchSize")
    private int simpleHistoryCleanupJobBatchSize = 100;

    @KadaiProperty("kadai.jobs.cleanup.history.simple.minimumAge")
    private Duration simpleHistoryCleanupJobMinimumAge = Duration.ofDays(14);

    @KadaiProperty("kadai.jobs.cleanup.history.simple.allCompletedSameParentBusiness")
    private boolean simpleHistoryCleanupJobAllCompletedSameParentBusiness = true;

    @KadaiProperty("kadai.jobs.cleanup.history.simple.lockExpirationPeriod")
    private Duration simpleHistoryCleanupJobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.priority.task.enable")
    private boolean taskUpdatePriorityJobEnabled = false;

    @KadaiProperty("kadai.jobs.priority.task.batchSize")
    private int taskUpdatePriorityJobBatchSize = 100;

    @KadaiProperty("kadai.jobs.priority.task.firstRunAt")
    private Instant taskUpdatePriorityJobFirstRun = Instant.parse("2023-01-01T00:00:00Z");

    @KadaiProperty("kadai.jobs.priority.task.runEvery")
    private Duration taskUpdatePriorityJobRunEvery = Duration.ofDays(1);

    @KadaiProperty("kadai.jobs.priority.task.lockExpirationPeriod")
    private Duration taskUpdatePriorityJobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.refresh.user.enable")
    private boolean userInfoRefreshJobEnabled = false;

    @KadaiProperty("kadai.jobs.refresh.user.firstRunAt")
    private Instant userRefreshJobFirstRun = Instant.parse("2023-01-01T23:00:00Z");

    @KadaiProperty("kadai.jobs.refresh.user.runEvery")
    private Duration userRefreshJobRunEvery = Duration.ofDays(1);

    @KadaiProperty("kadai.jobs.refresh.user.lockExpirationPeriod")
    private Duration userRefreshJobLockExpirationPeriod = Duration.ofMinutes(30);

    @KadaiProperty("kadai.jobs.customJobs")
    private Set<String> customJobs = new HashSet<>();

    // endregion

    // region user configuration
    @KadaiProperty("kadai.user.addAdditionalUserInfo")
    private boolean addAdditionalUserInfo = false;

    @KadaiProperty("kadai.user.minimalPermissionsToAssignDomains")
    private Set<WorkbasketPermission> minimalPermissionsToAssignDomains = new HashSet<>();

    // endregion

    // region database configuration
    @KadaiProperty("kadai.feature.useSpecificDb2Taskquery")
    private boolean useSpecificDb2Taskquery = true;

    // endregion

    // region custom configuration
    private Map<String, String> properties = Collections.emptyMap();

    // endregion

    public Builder(DataSource dataSource, boolean useManagedTransactions, String schemaName) {
      this(dataSource, useManagedTransactions, schemaName, true);
    }

    public Builder(
        DataSource dataSource,
        boolean useManagedTransactions,
        String schemaName,
        boolean securityEnabled) {
      this.useManagedTransactions = useManagedTransactions;
      this.securityEnabled = securityEnabled;
      this.dataSource = Objects.requireNonNull(dataSource);
      this.schemaName = initSchemaName(schemaName);
    }

    public Builder(KadaiConfiguration conf) {
      this(
          conf,
          conf.dataSource,
          conf.useManagedTransactions,
          conf.schemaName,
          conf.securityEnabled);
    }

    public Builder(KadaiConfiguration conf, DataSource dataSource) {
      this(conf, dataSource, conf.useManagedTransactions, conf.schemaName, conf.securityEnabled);
    }

    public Builder(KadaiConfiguration conf, DataSource dataSource, boolean useManagedTransactions) {
      this(conf, dataSource, useManagedTransactions, conf.schemaName, conf.securityEnabled);
    }

    public Builder(
        KadaiConfiguration conf,
        DataSource dataSource,
        boolean useManagedTransactions,
        String schemaName) {
      this(conf, dataSource, useManagedTransactions, schemaName, conf.securityEnabled);
    }

    public Builder(
        KadaiConfiguration conf,
        DataSource dataSource,
        boolean useManagedTransactions,
        String schemaName,
        boolean securityEnabled) {
      // general configuration
      this.dataSource = dataSource;
      this.useManagedTransactions = useManagedTransactions;
      this.schemaName = initSchemaName(schemaName);
      this.securityEnabled = securityEnabled;
      this.domains = conf.domains;
      this.enforceServiceLevel = conf.enforceServiceLevel;
      // authentication configuration
      this.roleMap = conf.roleMap;
      // classification configuration
      this.classificationTypes = conf.classificationTypes;
      this.classificationCategoriesByType = conf.classificationCategoriesByType;
      // working time configuration
      this.useWorkingTimeCalculation = conf.useWorkingTimeCalculation;
      this.workingTimeSchedule = conf.workingTimeSchedule;
      this.workingTimeScheduleTimeZone = conf.workingTimeScheduleTimeZone;
      this.customHolidays = conf.customHolidays;
      this.germanPublicHolidaysEnabled = conf.germanPublicHolidaysEnabled;
      this.germanPublicHolidaysCorpusChristiEnabled = conf.germanPublicHolidaysCorpusChristiEnabled;
      // holiday configuration
      this.deleteHistoryEventsOnTaskDeletionEnabled = conf.deleteHistoryEventsOnTaskDeletionEnabled;
      this.logHistoryLoggerName = conf.logHistoryLoggerName;
      // job configuration
      this.jobSchedulerEnabled = conf.jobSchedulerEnabled;
      this.jobSchedulerInitialStartDelay = conf.jobSchedulerInitialStartDelay;
      this.jobSchedulerPeriod = conf.jobSchedulerPeriod;
      this.jobSchedulerPeriodTimeUnit = conf.jobSchedulerPeriodTimeUnit;
      this.maxNumberOfJobRetries = conf.maxNumberOfJobRetries;
      this.jobBatchSize = conf.jobBatchSize;
      this.jobFirstRun = conf.jobFirstRun;
      this.jobRunEvery = conf.jobRunEvery;
      this.jobLockExpirationPeriod = conf.jobLockExpirationPeriod;
      this.taskCleanupJobEnabled = conf.taskCleanupJobEnabled;
      this.taskCleanupJobMinimumAge = conf.taskCleanupJobMinimumAge;
      this.taskCleanupJobAllCompletedSameParentBusiness =
          conf.taskCleanupJobAllCompletedSameParentBusiness;
      this.taskCleanupJobLockExpirationPeriod = conf.taskCleanupJobLockExpirationPeriod;
      this.workbasketCleanupJobEnabled = conf.workbasketCleanupJobEnabled;
      this.workbasketCleanupJobLockExpirationPeriod = conf.workbasketCleanupJobLockExpirationPeriod;
      this.simpleHistoryCleanupJobEnabled = conf.simpleHistoryCleanupJobEnabled;
      this.simpleHistoryCleanupJobBatchSize = conf.simpleHistoryCleanupJobBatchSize;
      this.simpleHistoryCleanupJobMinimumAge = conf.simpleHistoryCleanupJobMinimumAge;
      this.simpleHistoryCleanupJobAllCompletedSameParentBusiness =
          conf.simpleHistoryCleanupJobAllCompletedSameParentBusiness;
      this.simpleHistoryCleanupJobLockExpirationPeriod =
          conf.simpleHistoryCleanupJobLockExpirationPeriod;
      this.taskUpdatePriorityJobEnabled = conf.taskUpdatePriorityJobEnabled;
      this.taskUpdatePriorityJobBatchSize = conf.taskUpdatePriorityJobBatchSize;
      this.taskUpdatePriorityJobFirstRun = conf.taskUpdatePriorityJobFirstRun;
      this.taskUpdatePriorityJobRunEvery = conf.taskUpdatePriorityJobRunEvery;
      this.taskUpdatePriorityJobLockExpirationPeriod =
          conf.taskUpdatePriorityJobLockExpirationPeriod;
      this.userInfoRefreshJobEnabled = conf.userInfoRefreshJobEnabled;
      this.userRefreshJobFirstRun = conf.userRefreshJobFirstRun;
      this.userRefreshJobRunEvery = conf.userRefreshJobRunEvery;
      this.userRefreshJobLockExpirationPeriod = conf.userRefreshJobLockExpirationPeriod;
      this.customJobs = conf.customJobs;
      // user configuration
      this.addAdditionalUserInfo = conf.addAdditionalUserInfo;
      this.minimalPermissionsToAssignDomains = conf.minimalPermissionsToAssignDomains;
      // database configuration
      this.useSpecificDb2Taskquery = conf.useSpecificDb2Taskquery;
      // custom configuration
      this.properties = conf.properties;
    }

    /**
     * Configure the {@linkplain KadaiConfiguration} with the default {@linkplain
     * #DEFAULT_KADAI_PROPERTIES property file location} and {@linkplain
     * #DEFAULT_KADAI_PROPERTY_SEPARATOR property separator}.
     *
     * @see #initKadaiProperties(String, String)
     */
    @SuppressWarnings({"unused", "checkstyle:JavadocMethod"})
    public Builder initKadaiProperties() {
      return initKadaiProperties(DEFAULT_KADAI_PROPERTIES, DEFAULT_KADAI_PROPERTY_SEPARATOR);
    }

    /**
     * Configure the {@linkplain KadaiConfiguration} with the default {@linkplain
     * #DEFAULT_KADAI_PROPERTY_SEPARATOR property separator}.
     *
     * @see #initKadaiProperties(String, String)
     */
    @SuppressWarnings({"unused", "checkstyle:JavadocMethod"})
    public Builder initKadaiProperties(String propertiesFile) {
      return initKadaiProperties(propertiesFile, DEFAULT_KADAI_PROPERTY_SEPARATOR);
    }

    /**
     * Configure the {@linkplain KadaiConfiguration} using a property file from the classpath of
     * {@linkplain KadaiConfiguration KadaiConfigurations} or the system.
     *
     * <p>Please check this builders instance fields for the {@linkplain KadaiProperty} for property
     * naming.
     *
     * @param propertiesFile path to the properties file.
     * @param separator if a property is a collection type, this separator determines which sequence
     *     delimits each individual value.
     * @return the builder
     * @throws SystemException if propertiesFile or separator is null or empty
     */
    public Builder initKadaiProperties(String propertiesFile, String separator) {
      if (propertiesFile == null || propertiesFile.isEmpty() || propertiesFile.isBlank()) {
        throw new SystemException("property file can't be null or empty");
      }
      if (separator == null || separator.isEmpty() || separator.isBlank()) {
        throw new SystemException("separator file can't be null or empty");
      }

      LOGGER.debug(
          "Reading kadai configuration from {} with separator {}", propertiesFile, separator);
      properties = loadProperties(propertiesFile);
      configureAnnotatedFields(separator, properties);
      addMasterDomain();
      return this;
    }

    // region builder methods

    // region general configuration

    public Builder domains(List<String> domains) {
      this.domains = domains;
      return this;
    }

    public Builder enforceServiceLevel(boolean enforceServiceLevel) {
      this.enforceServiceLevel = enforceServiceLevel;
      return this;
    }

    // endregion

    // region authentication configuration

    public Builder roleMap(Map<KadaiRole, Set<String>> roleMap) {
      this.roleMap = roleMap;
      return this;
    }

    // endregion

    // region classification configuration

    public Builder classificationTypes(List<String> classificationTypes) {
      this.classificationTypes = classificationTypes;
      return this;
    }

    public Builder classificationCategoriesByType(
        Map<String, List<String>> classificationCategoriesByType) {
      this.classificationCategoriesByType = classificationCategoriesByType;
      return this;
    }

    // endregion

    // region working time configuration

    public Builder useWorkingTimeCalculation(boolean useWorkingTimeCalculation) {
      this.useWorkingTimeCalculation = useWorkingTimeCalculation;
      return this;
    }

    public Builder workingTimeSchedule(Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule) {
      this.workingTimeSchedule = workingTimeSchedule;
      return this;
    }

    public Builder workingTimeScheduleTimeZone(ZoneId workingTimeScheduleTimeZone) {
      this.workingTimeScheduleTimeZone = workingTimeScheduleTimeZone;
      return this;
    }

    public Builder customHolidays(Set<CustomHoliday> customHolidays) {
      this.customHolidays = customHolidays;
      return this;
    }

    public Builder germanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
      this.germanPublicHolidaysEnabled = germanPublicHolidaysEnabled;
      return this;
    }

    public Builder germanPublicHolidaysCorpusChristiEnabled(
        boolean germanPublicHolidaysCorpusChristiEnabled) {
      this.germanPublicHolidaysCorpusChristiEnabled = germanPublicHolidaysCorpusChristiEnabled;
      return this;
    }

    // endregion

    // region history configuration

    public Builder deleteHistoryEventsOnTaskDeletionEnabled(
        boolean deleteHistoryEventsOnTaskDeletionEnabled) {
      this.deleteHistoryEventsOnTaskDeletionEnabled = deleteHistoryEventsOnTaskDeletionEnabled;
      return this;
    }

    public Builder logHistoryLoggerName(String loggerName) {
      this.logHistoryLoggerName = loggerName;
      return this;
    }

    // endregion

    // region job configuration

    public Builder jobSchedulerEnabled(boolean jobSchedulerEnabled) {
      this.jobSchedulerEnabled = jobSchedulerEnabled;
      return this;
    }

    public Builder jobSchedulerInitialStartDelay(long jobSchedulerInitialStartDelay) {
      this.jobSchedulerInitialStartDelay = jobSchedulerInitialStartDelay;
      return this;
    }

    public Builder jobSchedulerPeriod(long jobSchedulerPeriod) {
      this.jobSchedulerPeriod = jobSchedulerPeriod;
      return this;
    }

    public Builder jobSchedulerPeriodTimeUnit(TimeUnit jobSchedulerPeriodTimeUnit) {
      this.jobSchedulerPeriodTimeUnit = jobSchedulerPeriodTimeUnit;
      return this;
    }

    public Builder maxNumberOfJobRetries(int maxNumberOfJobRetries) {
      this.maxNumberOfJobRetries = maxNumberOfJobRetries;
      return this;
    }

    public Builder jobBatchSize(int jobBatchSize) {
      this.jobBatchSize = jobBatchSize;
      return this;
    }

    public Builder jobFirstRun(Instant jobFirstRun) {
      this.jobFirstRun = jobFirstRun;
      return this;
    }

    public Builder jobRunEvery(Duration jobRunEvery) {
      this.jobRunEvery = jobRunEvery;
      return this;
    }

    public Builder taskCleanupJobEnabled(boolean taskCleanupJobEnabled) {
      this.taskCleanupJobEnabled = taskCleanupJobEnabled;
      return this;
    }

    public Builder jobLockExpirationPeriod(Duration jobLockExpirationPeriod) {
      this.jobLockExpirationPeriod = jobLockExpirationPeriod;
      return this;
    }

    public Builder taskCleanupJobMinimumAge(Duration taskCleanupJobMinimumAge) {
      this.taskCleanupJobMinimumAge = taskCleanupJobMinimumAge;
      return this;
    }

    public Builder taskCleanupJobAllCompletedSameParentBusiness(
        boolean taskCleanupJobAllCompletedSameParentBusiness) {
      this.taskCleanupJobAllCompletedSameParentBusiness =
          taskCleanupJobAllCompletedSameParentBusiness;
      return this;
    }

    public Builder taskCleanupJobLockExpirationPeriod(Duration taskCleanupJobLockExpirationPeriod) {
      this.taskCleanupJobLockExpirationPeriod = taskCleanupJobLockExpirationPeriod;
      return this;
    }

    public Builder workbasketCleanupJobEnabled(boolean workbasketCleanupJobEnabled) {
      this.workbasketCleanupJobEnabled = workbasketCleanupJobEnabled;
      return this;
    }

    public Builder workbasketCleanupJobLockExpirationPeriod(
        Duration workbasketCleanupJobLockExpirationPeriod) {
      this.workbasketCleanupJobLockExpirationPeriod = workbasketCleanupJobLockExpirationPeriod;
      return this;
    }

    public Builder simpleHistoryCleanupJobEnabled(boolean simpleHistoryCleanupJobEnabled) {
      this.simpleHistoryCleanupJobEnabled = simpleHistoryCleanupJobEnabled;
      return this;
    }

    public Builder simpleHistoryCleanupJobBatchSize(int simpleHistoryCleanupJobBatchSize) {
      this.simpleHistoryCleanupJobBatchSize = simpleHistoryCleanupJobBatchSize;
      return this;
    }

    public Builder simpleHistoryCleanupJobMinimumAge(Duration simpleHistoryCleanupJobMinimumAge) {
      this.simpleHistoryCleanupJobMinimumAge = simpleHistoryCleanupJobMinimumAge;
      return this;
    }

    public Builder simpleHistoryCleanupJobAllCompletedSameParentBusiness(
        boolean simpleHistoryCleanupJobAllCompletedSameParentBusiness) {
      this.simpleHistoryCleanupJobAllCompletedSameParentBusiness =
          simpleHistoryCleanupJobAllCompletedSameParentBusiness;
      return this;
    }

    public Builder simpleHistoryCleanupJobLockExpirationPeriod(
        Duration simpleHistoryCleanupJobLockExpirationPeriod) {
      this.simpleHistoryCleanupJobLockExpirationPeriod =
          simpleHistoryCleanupJobLockExpirationPeriod;
      return this;
    }

    public Builder taskUpdatePriorityJobEnabled(boolean taskUpdatePriorityJobEnabled) {
      this.taskUpdatePriorityJobEnabled = taskUpdatePriorityJobEnabled;
      return this;
    }

    public Builder taskUpdatePriorityJobBatchSize(int priorityJobBatchSize) {
      this.taskUpdatePriorityJobBatchSize = priorityJobBatchSize;
      return this;
    }

    public Builder taskUpdatePriorityJobFirstRun(Instant taskUpdatePriorityJobFirstRun) {
      this.taskUpdatePriorityJobFirstRun = taskUpdatePriorityJobFirstRun;
      return this;
    }

    public Builder taskUpdatePriorityJobRunEvery(Duration taskUpdatePriorityJobRunEvery) {
      this.taskUpdatePriorityJobRunEvery = taskUpdatePriorityJobRunEvery;
      return this;
    }

    public Builder taskUpdatePriorityJobLockExpirationPeriod(
        Duration taskUpdatePriorityJobLockExpirationPeriod) {
      this.taskUpdatePriorityJobLockExpirationPeriod = taskUpdatePriorityJobLockExpirationPeriod;
      return this;
    }

    public Builder userInfoRefreshJobEnabled(boolean userInfoRefreshJobEnabled) {
      this.userInfoRefreshJobEnabled = userInfoRefreshJobEnabled;
      return this;
    }

    public Builder userRefreshJobFirstRun(Instant userRefreshJobFirstRun) {
      this.userRefreshJobFirstRun = userRefreshJobFirstRun;
      return this;
    }

    public Builder userRefreshJobRunEvery(Duration userRefreshJobRunEvery) {
      this.userRefreshJobRunEvery = userRefreshJobRunEvery;
      return this;
    }

    public Builder userRefreshJobLockExpirationPeriod(Duration userRefreshJobLockExpirationPeriod) {
      this.userRefreshJobLockExpirationPeriod = userRefreshJobLockExpirationPeriod;
      return this;
    }

    public Builder customJobs(Set<String> customJobs) {
      this.customJobs = customJobs;
      return this;
    }

    // endregion

    // region user configuration

    public Builder addAdditionalUserInfo(boolean addAdditionalUserInfo) {
      this.addAdditionalUserInfo = addAdditionalUserInfo;
      return this;
    }

    public Builder minimalPermissionsToAssignDomains(
        Set<WorkbasketPermission> minimalPermissionsToAssignDomains) {
      this.minimalPermissionsToAssignDomains = minimalPermissionsToAssignDomains;
      return this;
    }

    // endregion
    // region database configuration
    public Builder useSpecificDb2Taskquery(boolean useSpecificDb2Taskquery) {
      this.useSpecificDb2Taskquery = useSpecificDb2Taskquery;
      return this;
    }

    public KadaiConfiguration build() {
      adjustConfiguration();
      validateConfiguration();
      return new KadaiConfiguration(this);
    }

    // endregion

    private void addMasterDomain() {
      // Master Domain is treat as empty string
      // it must be always added to the configuration
      // add the master domain always at the end of the list
      if (!this.domains.contains(MASTER_DOMAIN)) {
        this.domains.add(MASTER_DOMAIN);
      }
    }

    private void configureAnnotatedFields(String separator, Map<String, String> props) {
      final List<Field> fields = ReflectionUtil.retrieveAllFields(getClass());
      for (Field field : fields) {
        Optional.ofNullable(field.getAnnotation(KadaiProperty.class))
            .flatMap(
                kadaiProperty ->
                    PropertyParser.getPropertyParser(field.getType())
                        .parse(props, separator, field, kadaiProperty))
            .ifPresent(value -> setFieldValue(field, value));
      }
    }

    private void setFieldValue(Field field, Object value) {
      try {
        field.set(this, value);
      } catch (IllegalAccessException | IllegalArgumentException e) {
        throw new SystemException(
            String.format("Property value '%s' is invalid for field '%s'", value, field.getName()),
            e);
      }
    }

    private void adjustConfiguration() {
      domains = domains.stream().map(String::toUpperCase).toList();
      classificationTypes = classificationTypes.stream().map(String::toUpperCase).toList();
      classificationCategoriesByType =
          classificationCategoriesByType.entrySet().stream()
              .map(
                  e ->
                      Map.entry(
                          e.getKey().toUpperCase(),
                          e.getValue().stream().map(String::toUpperCase).toList()))
              .sorted(Comparator.comparingInt(e -> classificationTypes.indexOf(e.getKey())))
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      Map.Entry::getValue,
                      (oldValue, newValue) -> oldValue,
                      LinkedHashMap::new));
      roleMap =
          Arrays.stream(KadaiRole.values())
              .map(role -> Pair.of(role, roleMap.getOrDefault(role, Set.of())))
              .map(
                  pair -> {
                    if (KadaiConfiguration.shouldUseLowerCaseForAccessIds()) {
                      return Pair.of(
                          pair.getLeft(),
                          pair.getRight().stream()
                              .map(String::toLowerCase)
                              .collect(Collectors.toSet()));
                    }
                    return pair;
                  })
              .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private void validateConfiguration() {
      if (jobBatchSize <= 0) {
        throw new InvalidArgumentException(
            "Parameter jobBatchSize (kadai.jobs.batchSize) must be a positive integer");
      }
      if (maxNumberOfJobRetries <= 0) {
        throw new InvalidArgumentException(
            "Parameter maxNumberOfJobRetries (kadai.jobs.maxRetries)"
                + " must be a positive integer");
      }
      if (jobRunEvery == null || jobRunEvery.isNegative() || jobRunEvery.isZero()) {
        throw new InvalidArgumentException(
            "Parameter jobRunEvery (kadai.jobs.runEvery) must be a positive duration");
      }
      if (simpleHistoryCleanupJobMinimumAge == null
          || simpleHistoryCleanupJobMinimumAge.isNegative()) {
        throw new InvalidArgumentException(
            "Parameter simpleHistoryCleanupJobMinimumAge "
                + "(kadai.jobs.cleanup.history.simple.minimumAge) must not be negative");
      }
      if (taskCleanupJobMinimumAge == null || taskCleanupJobMinimumAge.isNegative()) {
        throw new InvalidArgumentException(
            "Parameter taskCleanupJobMinimumAge "
                + "(kadai.jobs.cleanup.task.minimumAge) must not be negative");
      }
      if (taskUpdatePriorityJobBatchSize <= 0) {
        throw new InvalidArgumentException(
            "Parameter taskUpdatePriorityJobBatchSize (kadai.jobs.priority.task.batchSize)"
                + " must be a positive integer");
      }
      if (taskUpdatePriorityJobRunEvery == null
          || taskUpdatePriorityJobRunEvery.isNegative()
          || taskUpdatePriorityJobRunEvery.isZero()) {
        throw new InvalidArgumentException(
            "Parameter taskUpdatePriorityJobRunEvery (kadai.jobs.priority.task.runEvery)"
                + " must be a positive duration");
      }
      if (userRefreshJobRunEvery == null
          || userRefreshJobRunEvery.isNegative()
          || userRefreshJobRunEvery.isZero()) {
        throw new InvalidArgumentException(
            "Parameter userRefreshJobRunEvery (kadai.jobs.refresh.user.runEvery)"
                + " must be a positive duration");
      }
      if (jobSchedulerInitialStartDelay < 0) {
        throw new InvalidArgumentException(
            "Parameter jobSchedulerInitialStartDelay (kadai.jobs.scheduler.initialStartDelay)"
                + " must be a natural integer");
      }
      if (jobSchedulerPeriod <= 0) {
        throw new InvalidArgumentException(
            "Parameter jobSchedulerPeriod (kadai.jobs.scheduler.period) "
                + "must be a positive integer");
      }
      if (!new HashSet<>(classificationTypes)
          .containsAll(classificationCategoriesByType.keySet())) {
        throw new InvalidArgumentException(
            "Parameter classificationCategoriesByType (kadai.classification.categories.<KEY>)"
                + " is configured incorrectly. Please check whether all specified"
                + " Classification Types exist. Additionally, check whether the correct"
                + " separator is used in the property kadai.classification.types .");
      }

      if (!classificationCategoriesByType.keySet().containsAll(classificationTypes)) {
        throw new InvalidArgumentException(
            String.format(
                "Some Classification Categories for parameter classificationTypes "
                    + "(kadai.classification.types) are missing. "
                    + "configured: %s "
                    + "detected: %s",
                classificationTypes, classificationCategoriesByType.keySet()));
      }
    }

    private String initSchemaName(String schemaName) {
      if (schemaName == null || schemaName.isEmpty() || schemaName.isBlank()) {
        throw new SystemException("schema name can't be null or empty");
      }

      try (Connection connection = dataSource.getConnection()) {
        DB db = DB.getDB(connection);
        if (DB.POSTGRES == db) {
          return schemaName.toLowerCase();
        } else {
          return schemaName.toUpperCase();
        }
      } catch (SQLException ex) {
        throw new SystemException(
            "Caught exception when attempting to initialize the schema name", ex);
      }
    }

    private Map<String, String> loadProperties(String propertiesFile) {
      Properties props = new Properties();
      try (InputStream stream =
          FileLoaderUtil.openFileFromClasspathOrSystem(propertiesFile, KadaiConfiguration.class)) {
        props.load(stream);
      } catch (IOException e) {
        throw new SystemException(
            String.format("Could not process properties file '%s'", propertiesFile), e);
      }
      return props.entrySet().stream()
          .collect(
              Collectors.toUnmodifiableMap(
                  e -> e.getKey().toString(), e -> e.getValue().toString()));
    }

    private static Map<DayOfWeek, Set<LocalTimeInterval>> initDefaultWorkingTimeSchedule() {
      Map<DayOfWeek, Set<LocalTimeInterval>> workingTime = new EnumMap<>(DayOfWeek.class);
      Set<LocalTimeInterval> standardWorkingSlots =
          Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX));
      workingTime.put(DayOfWeek.MONDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.TUESDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.WEDNESDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.THURSDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.FRIDAY, standardWorkingSlots);
      return workingTime;
    }
  }
}
