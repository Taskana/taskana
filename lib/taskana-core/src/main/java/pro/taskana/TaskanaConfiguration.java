package pro.taskana;

import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureAnnotatedFields;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureClassificationCategoriesForType;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureRoles;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.configuration.TaskanaProperty;
import pro.taskana.common.internal.util.FileLoaderUtil;
import pro.taskana.workbasket.api.WorkbasketPermission;

/**
 * This central class creates the TaskanaEngine and holds all the information about DB and Security.
 * <br>
 * Security is enabled by default. <br>
 * All members are immutable, also Lists and Maps and Sets.
 */
public class TaskanaConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaConfiguration.class);

  // region general configuration
  private final DataSource dataSource;
  private final boolean securityEnabled;
  private final boolean useManagedTransactions;
  private final String schemaName;

  @TaskanaProperty("taskana.german.holidays.enabled")
  private final boolean germanPublicHolidaysEnabled;
  // endregion
  @TaskanaProperty("taskana.german.holidays.corpus-christi.enabled")
  private final boolean corpusChristiEnabled;
  // endregion
  // region history configuration
  @TaskanaProperty("taskana.history.deletion.on.task.deletion.enabled")
  private final boolean deleteHistoryOnTaskDeletionEnabled;
  // region custom configuration
  private final Map<String, String> properties;
  private final Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule;
  private final boolean jobSchedulerEnabled;
  private final long jobSchedulerInitialStartDelay;
  private final long jobSchedulerPeriod;
  private final TimeUnit jobSchedulerPeriodTimeUnit;
  private final boolean jobSchedulerEnableTaskCleanupJob;
  private final boolean jobSchedulerEnableTaskUpdatePriorityJob;
  private final boolean jobSchedulerEnableWorkbasketCleanupJob;
  private final boolean jobSchedulerEnableUserInfoRefreshJob;
  private final boolean jobSchedulerEnableHistorieCleanupJob;
  private final List<String> jobSchedulerCustomJobs;

  @TaskanaProperty("taskana.domains")
  private List<String> domains = new ArrayList<>();
  // endregion
  // region authentication configuration
  private Map<TaskanaRole, Set<String>> roleMap = new EnumMap<>(TaskanaRole.class);
  // region classification configuration
  @TaskanaProperty("taskana.classification.types")
  private List<String> classificationTypes = new ArrayList<>();
  // TODO: make this a Set
  private Map<String, List<String>> classificationCategoriesByType = new HashMap<>();
  // endregion
  @TaskanaProperty("taskana.validation.allowTimestampServiceLevelMismatch")
  private boolean allowTimestampServiceLevelMismatch = false;
  // endregion
  // region holiday configuration
  @TaskanaProperty("taskana.custom.holidays")
  private List<CustomHoliday> customHolidays = new ArrayList<>();
  // region job configuration
  // TODO validate this is positive
  @TaskanaProperty("taskana.jobs.batchSize")
  private int jobBatchSize = 100;
  // TODO validate this is positive
  @TaskanaProperty("taskana.jobs.maxRetries")
  private int maxNumberOfJobRetries = 3;

  @TaskanaProperty("taskana.jobs.cleanup.firstRunAt")
  private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  // TODO: validate this is positive
  @TaskanaProperty("taskana.jobs.cleanup.runEvery")
  private Duration cleanupJobRunEvery = Duration.ofDays(1);
  // endregion
  // TODO: validate this is positive
  @TaskanaProperty("taskana.jobs.cleanup.minimumAge")
  private Duration cleanupJobMinimumAge = Duration.ofDays(14);

  @TaskanaProperty("taskana.jobs.cleanup.allCompletedSameParentBusiness")
  private boolean taskCleanupJobAllCompletedSameParentBusiness = true;
  // TODO: validate this is positive
  @TaskanaProperty("taskana.jobs.priority.batchSize")
  private int priorityJobBatchSize = 100;

  @TaskanaProperty("taskana.jobs.priority.firstRunAt")
  private Instant priorityJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  // TODO: validate this is positive
  @TaskanaProperty("taskana.jobs.priority.runEvery")
  private Duration priorityJobRunEvery = Duration.ofDays(1);

  @TaskanaProperty("taskana.jobs.priority.active")
  private boolean priorityJobActive = false;
  // TODO: validate this is positive
  @TaskanaProperty("taskana.jobs.user.refresh.runEvery")
  private Duration userRefreshJobRunEvery = Duration.ofDays(1);
  // endregion
  @TaskanaProperty("taskana.jobs.user.refresh.firstRunAt")
  private Instant userRefreshJobFirstRun = Instant.parse("2018-01-01T23:00:00Z");
  // region user configuration
  @TaskanaProperty("taskana.addAdditionalUserInfo")
  private boolean addAdditionalUserInfo = false;
  // endregion
  // TODO: make Set
  @TaskanaProperty("taskana.user.minimalPermissionsToAssignDomains")
  private List<WorkbasketPermission> minimalPermissionsToAssignDomains = new ArrayList<>();

  protected TaskanaConfiguration(Builder builder) {
    this.dataSource = builder.dataSource;
    this.schemaName = builder.schemaName;
    this.properties = Map.copyOf(builder.properties);
    this.roleMap =
        builder.roleMap.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));
    this.securityEnabled = builder.securityEnabled;
    this.useManagedTransactions = builder.useManagedTransactions;
    this.domains = Collections.unmodifiableList(builder.domains);
    this.classificationTypes = Collections.unmodifiableList(builder.classificationTypes);
    this.classificationCategoriesByType =
        builder.classificationCategoriesByType.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableList(e.getValue())));
    this.customHolidays = Collections.unmodifiableList(builder.customHolidays);
    this.deleteHistoryOnTaskDeletionEnabled = builder.deleteHistoryOnTaskDeletionEnabled;
    this.germanPublicHolidaysEnabled = builder.germanPublicHolidaysEnabled;
    this.corpusChristiEnabled = builder.corpusChristiEnabled;
    this.workingTimeSchedule =
        builder.workingTimeSchedule.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));
    this.jobBatchSize = builder.jobBatchSize;
    this.maxNumberOfJobRetries = builder.maxNumberOfJobRetries;
    this.cleanupJobFirstRun = builder.cleanupJobFirstRun;
    this.cleanupJobRunEvery = builder.cleanupJobRunEvery;
    this.cleanupJobMinimumAge = builder.cleanupJobMinimumAge;
    this.taskCleanupJobAllCompletedSameParentBusiness =
        builder.taskCleanupJobAllCompletedSameParentBusiness;
    this.allowTimestampServiceLevelMismatch = builder.allowTimestampServiceLevelMismatch;
    this.addAdditionalUserInfo = builder.addAdditionalUserInfo;
    this.priorityJobBatchSize = builder.priorityJobBatchSize;
    this.priorityJobFirstRun = builder.priorityJobFirstRun;
    this.priorityJobRunEvery = builder.priorityJobRunEvery;
    this.priorityJobActive = builder.priorityJobActive;
    this.userRefreshJobRunEvery = builder.userRefreshJobRunEvery;
    this.userRefreshJobFirstRun = builder.userRefreshJobFirstRun;
    this.minimalPermissionsToAssignDomains =
        Collections.unmodifiableList(builder.minimalPermissionsToAssignDomains);
    this.jobSchedulerEnabled = builder.jobSchedulerEnabled;
    this.jobSchedulerInitialStartDelay = builder.jobSchedulerInitialStartDelay;
    this.jobSchedulerPeriod = builder.jobSchedulerPeriod;
    this.jobSchedulerPeriodTimeUnit = builder.jobSchedulerPeriodTimeUnit;
    this.jobSchedulerEnableTaskCleanupJob = builder.jobSchedulerEnableTaskCleanupJob;
    this.jobSchedulerEnableTaskUpdatePriorityJob = builder.jobSchedulerEnableTaskUpdatePriorityJob;
    this.jobSchedulerEnableWorkbasketCleanupJob = builder.jobSchedulerEnableWorkbasketCleanupJob;
    this.jobSchedulerEnableUserInfoRefreshJob = builder.jobSchedulerEnableUserInfoRefreshJob;
    this.jobSchedulerEnableHistorieCleanupJob = builder.jobSchedulerEnableHistorieCleanupJob;
    this.jobSchedulerCustomJobs = Collections.unmodifiableList(builder.jobSchedulerCustomJobs);

    if (LOGGER.isDebugEnabled()) {
      // TODO remove the reflection magic when introducing lombok toString magic :-)
      StringBuilder result = new StringBuilder();
      String newLine = System.getProperty("line.separator");
      Field[] fields = this.getClass().getDeclaredFields();
      result.append("TaskanaConfiguration:").append(newLine);
      // print field names paired with their values
      for (Field field : fields) {
        try {
          result.append(field.getName());
          result.append(": ");
          // requires access to private field:
          result.append(field.get(this));
        } catch (IllegalAccessException ex) {
          // ignore this error
        }
        result.append(newLine);
      }
      LOGGER.debug(result.toString());
    }
  }

  public boolean isSecurityEnabled() {
    return this.securityEnabled;
  }

  public DataSource getDatasource() {
    return this.dataSource;
  }

  /**
   * return all properties loaded from taskana properties file. Per Design the normal Properties are
   * not immutable, so we return here an ImmutableMap, because we don't want direct changes in the
   * configuration.
   *
   * @return all properties loaded from taskana properties file
   */
  public Map<String, String> getProperties() {
    return this.properties;
  }

  public boolean isUseManagedTransactions() {
    return this.useManagedTransactions;
  }

  public int getJobBatchSize() {
    return jobBatchSize;
  }

  public int getMaxNumberOfJobRetries() {
    return maxNumberOfJobRetries;
  }

  public boolean isCorpusChristiEnabled() {
    return corpusChristiEnabled;
  }

  public boolean isGermanPublicHolidaysEnabled() {
    return this.germanPublicHolidaysEnabled;
  }

  public boolean isAllowTimestampServiceLevelMismatch() {
    return allowTimestampServiceLevelMismatch;
  }

  public boolean isDeleteHistoryOnTaskDeletionEnabled() {
    return deleteHistoryOnTaskDeletionEnabled;
  }

  public List<CustomHoliday> getCustomHolidays() {
    return customHolidays;
  }

  public Map<DayOfWeek, Set<LocalTimeInterval>> getWorkingTimeSchedule() {
    return workingTimeSchedule;
  }

  public Map<TaskanaRole, Set<String>> getRoleMap() {
    return roleMap;
  }

  public List<String> getDomains() {
    return domains;
  }

  public boolean isAddAdditionalUserInfo() {
    return addAdditionalUserInfo;
  }

  public List<String> getClassificationTypes() {
    return classificationTypes;
  }

  public List<String> getAllClassificationCategories() {
    List<String> classificationCategories = new ArrayList<>();
    for (Map.Entry<String, List<String>> type : this.classificationCategoriesByType.entrySet()) {
      classificationCategories.addAll(type.getValue());
    }
    return classificationCategories;
  }

  public Map<String, List<String>> getClassificationCategoriesByType() {
    return this.classificationCategoriesByType.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue())));
  }

  public List<String> getClassificationCategoriesByType(String type) {
    return classificationCategoriesByType.getOrDefault(type, Collections.emptyList());
  }

  public Instant getCleanupJobFirstRun() {
    return cleanupJobFirstRun;
  }

  public Duration getCleanupJobRunEvery() {
    return cleanupJobRunEvery;
  }

  public Duration getCleanupJobMinimumAge() {
    return cleanupJobMinimumAge;
  }

  public boolean isTaskCleanupJobAllCompletedSameParentBusiness() {
    return taskCleanupJobAllCompletedSameParentBusiness;
  }

  public int getPriorityJobBatchSize() {
    return priorityJobBatchSize;
  }

  public Instant getPriorityJobFirstRun() {
    return priorityJobFirstRun;
  }

  public Duration getPriorityJobRunEvery() {
    return priorityJobRunEvery;
  }

  public Duration getUserRefreshJobRunEvery() {
    return userRefreshJobRunEvery;
  }

  public List<WorkbasketPermission> getMinimalPermissionsToAssignDomains() {
    return minimalPermissionsToAssignDomains;
  }

  public Instant getUserRefreshJobFirstRun() {
    return userRefreshJobFirstRun;
  }

  public boolean isPriorityJobActive() {
    return priorityJobActive;
  }

  public String getSchemaName() {
    return schemaName;
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

  public boolean isJobSchedulerEnableTaskCleanupJob() {
    return jobSchedulerEnableTaskCleanupJob;
  }

  public boolean isJobSchedulerEnableTaskUpdatePriorityJob() {
    return jobSchedulerEnableTaskUpdatePriorityJob;
  }

  public boolean isJobSchedulerEnableWorkbasketCleanupJob() {
    return jobSchedulerEnableWorkbasketCleanupJob;
  }

  public boolean isJobSchedulerEnableUserInfoRefreshJob() {
    return jobSchedulerEnableUserInfoRefreshJob;
  }

  public boolean isJobSchedulerEnableHistorieCleanupJob() {
    return jobSchedulerEnableHistorieCleanupJob;
  }

  public List<String> getJobSchedulerCustomJobs() {
    return jobSchedulerCustomJobs;
  }

  /**
   * Helper method to determine whether all access ids (user Id and group ids) should be used in
   * lower case.
   *
   * @return true if all access ids should be used in lower case, false otherwise
   */
  public static boolean shouldUseLowerCaseForAccessIds() {
    return true;
  }

  public static class Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);
    private static final String TASKANA_PROPERTIES = "/taskana.properties";
    private static final String TASKANA_PROPERTY_SEPARATOR = "|";

    // region general configuration
    private final DataSource dataSource;
    private final boolean securityEnabled;
    private final boolean useManagedTransactions;
    private String schemaName;

    @TaskanaProperty("taskana.domains")
    private List<String> domains = new ArrayList<>();
    // endregion

    // region authentication configuration
    private Map<TaskanaRole, Set<String>> roleMap = new EnumMap<>(TaskanaRole.class);
    // endregion

    // region classification configuration
    @TaskanaProperty("taskana.classification.types")
    private List<String> classificationTypes = new ArrayList<>();

    // TODO: make this a Set
    private Map<String, List<String>> classificationCategoriesByType = new HashMap<>();

    @TaskanaProperty("taskana.validation.allowTimestampServiceLevelMismatch")
    private boolean allowTimestampServiceLevelMismatch = false;
    // endregion

    // region holiday configuration
    @TaskanaProperty("taskana.custom.holidays")
    private List<CustomHoliday> customHolidays = new ArrayList<>();

    @TaskanaProperty("taskana.german.holidays.enabled")
    private boolean germanPublicHolidaysEnabled;

    @TaskanaProperty("taskana.german.holidays.corpus-christi.enabled")
    private boolean corpusChristiEnabled;

    @TaskanaProperty("taskana.workingtime.schedule")
    private Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule =
        initDefaultWorkingTimeSchedule();
    // endregion

    // region history configuration
    @TaskanaProperty("taskana.history.deletion.on.task.deletion.enabled")
    private boolean deleteHistoryOnTaskDeletionEnabled;
    // endregion

    // region job configuration
    // TODO validate this is positive
    @TaskanaProperty("taskana.jobs.batchSize")
    private int jobBatchSize = 100;

    // TODO validate this is positive
    @TaskanaProperty("taskana.jobs.maxRetries")
    private int maxNumberOfJobRetries = 3;

    @TaskanaProperty("taskana.jobs.cleanup.firstRunAt")
    private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");

    // TODO: validate this is positive
    @TaskanaProperty("taskana.jobs.cleanup.runEvery")
    private Duration cleanupJobRunEvery = Duration.ofDays(1);
    // TODO: validate this is positive
    @TaskanaProperty("taskana.jobs.cleanup.minimumAge")
    private Duration cleanupJobMinimumAge = Duration.ofDays(14);

    @TaskanaProperty("taskana.jobs.cleanup.allCompletedSameParentBusiness")
    private boolean taskCleanupJobAllCompletedSameParentBusiness = true;

    // TODO: validate this is positive
    @TaskanaProperty("taskana.jobs.priority.batchSize")
    private int priorityJobBatchSize = 100;

    @TaskanaProperty("taskana.jobs.priority.firstRunAt")
    private Instant priorityJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");

    // TODO: validate this is positive
    @TaskanaProperty("taskana.jobs.priority.runEvery")
    private Duration priorityJobRunEvery = Duration.ofDays(1);

    @TaskanaProperty("taskana.jobs.priority.active")
    private boolean priorityJobActive = false;
    // TODO: validate this is positive
    @TaskanaProperty("taskana.jobs.user.refresh.runEvery")
    private Duration userRefreshJobRunEvery = Duration.ofDays(1);

    @TaskanaProperty("taskana.jobs.user.refresh.firstRunAt")
    private Instant userRefreshJobFirstRun = Instant.parse("2018-01-01T23:00:00Z");

    @TaskanaProperty("taskana.jobscheduler.enabled")
    private boolean jobSchedulerEnabled = true;

    @TaskanaProperty("taskana.jobscheduler.initialstartdelay")
    private long jobSchedulerInitialStartDelay = 100;

    @TaskanaProperty("taskana.jobscheduler.period")
    private long jobSchedulerPeriod = 12;

    @TaskanaProperty("taskana.jobscheduler.periodtimeunit")
    private TimeUnit jobSchedulerPeriodTimeUnit = TimeUnit.HOURS;

    @TaskanaProperty("taskana.jobscheduler.enableTaskCleanupJob")
    private boolean jobSchedulerEnableTaskCleanupJob = true;

    @TaskanaProperty("taskana.jobscheduler.enableTaskUpdatePriorityJob")
    private boolean jobSchedulerEnableTaskUpdatePriorityJob = true;

    @TaskanaProperty("taskana.jobscheduler.enableWorkbasketCleanupJob")
    private boolean jobSchedulerEnableWorkbasketCleanupJob = true;

    @TaskanaProperty("taskana.jobscheduler.enableUserInfoRefreshJob")
    private boolean jobSchedulerEnableUserInfoRefreshJob = true;

    @TaskanaProperty("taskana.jobscheduler.enableHistorieCleanupJob")
    private boolean jobSchedulerEnableHistorieCleanupJob = true;

    @TaskanaProperty("taskana.jobscheduler.customJobs")
    private List<String> jobSchedulerCustomJobs = new ArrayList<>();
    // endregion

    // region user configuration
    @TaskanaProperty("taskana.addAdditionalUserInfo")
    private boolean addAdditionalUserInfo = false;

    // TODO: make Set
    @TaskanaProperty("taskana.user.minimalPermissionsToAssignDomains")
    private List<WorkbasketPermission> minimalPermissionsToAssignDomains = new ArrayList<>();
    // endregion

    // region custom configuration
    private Map<String, String> properties = Collections.emptyMap();
    // endregion

    public Builder(TaskanaConfiguration tec) {
      this.dataSource = tec.getDatasource();
      this.schemaName = tec.getSchemaName();
      this.properties = tec.getProperties();
      this.roleMap = tec.getRoleMap();
      this.securityEnabled = tec.isSecurityEnabled();
      this.useManagedTransactions = tec.isUseManagedTransactions();
      this.domains = tec.getDomains();
      this.classificationTypes = tec.getClassificationTypes();
      this.classificationCategoriesByType = tec.getClassificationCategoriesByType();
      this.customHolidays = tec.getCustomHolidays();
      this.deleteHistoryOnTaskDeletionEnabled = tec.isDeleteHistoryOnTaskDeletionEnabled();
      this.germanPublicHolidaysEnabled = tec.isGermanPublicHolidaysEnabled();
      this.corpusChristiEnabled = tec.isCorpusChristiEnabled();
      this.workingTimeSchedule = tec.getWorkingTimeSchedule();
      this.jobBatchSize = tec.getJobBatchSize();
      this.maxNumberOfJobRetries = tec.getMaxNumberOfJobRetries();
      this.cleanupJobFirstRun = tec.getCleanupJobFirstRun();
      this.cleanupJobRunEvery = tec.getCleanupJobRunEvery();
      this.cleanupJobMinimumAge = tec.getCleanupJobMinimumAge();
      this.taskCleanupJobAllCompletedSameParentBusiness =
          tec.isTaskCleanupJobAllCompletedSameParentBusiness();
      this.allowTimestampServiceLevelMismatch = tec.isAllowTimestampServiceLevelMismatch();
      this.addAdditionalUserInfo = tec.isAddAdditionalUserInfo();
      this.priorityJobBatchSize = tec.getPriorityJobBatchSize();
      this.priorityJobFirstRun = tec.getPriorityJobFirstRun();
      this.priorityJobRunEvery = tec.getPriorityJobRunEvery();
      this.priorityJobActive = tec.isPriorityJobActive();
      this.userRefreshJobRunEvery = tec.getUserRefreshJobRunEvery();
      this.userRefreshJobFirstRun = tec.getUserRefreshJobFirstRun();
      this.minimalPermissionsToAssignDomains = tec.getMinimalPermissionsToAssignDomains();
      this.jobSchedulerEnabled = tec.isJobSchedulerEnabled();
      this.jobSchedulerInitialStartDelay = tec.getJobSchedulerInitialStartDelay();
      this.jobSchedulerPeriod = tec.getJobSchedulerPeriod();
      this.jobSchedulerPeriodTimeUnit = tec.getJobSchedulerPeriodTimeUnit();
      this.jobSchedulerEnableTaskCleanupJob = tec.isJobSchedulerEnableTaskCleanupJob();
      this.jobSchedulerEnableTaskUpdatePriorityJob =
          tec.isJobSchedulerEnableTaskUpdatePriorityJob();
      this.jobSchedulerEnableWorkbasketCleanupJob = tec.isJobSchedulerEnableWorkbasketCleanupJob();
      this.jobSchedulerEnableUserInfoRefreshJob = tec.isJobSchedulerEnableUserInfoRefreshJob();
      this.jobSchedulerEnableHistorieCleanupJob = tec.isJobSchedulerEnableHistorieCleanupJob();
      this.jobSchedulerCustomJobs = tec.getJobSchedulerCustomJobs();
    }

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

    // region builder methods

    @SuppressWarnings("unused")
    // TODO: why do we need this method?
    public Builder schemaName(String schemaName) {
      this.schemaName = initSchemaName(schemaName);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder roleMap(Map<TaskanaRole, Set<String>> roleMap) {
      this.roleMap = roleMap;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder domains(List<String> domains) {
      this.domains = domains;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder classificationTypes(List<String> classificationTypes) {
      this.classificationTypes = classificationTypes;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder classificationCategoriesByTypeMap(
        Map<String, List<String>> classificationCategoriesByTypeMap) {
      this.classificationCategoriesByType = classificationCategoriesByTypeMap;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder customHolidays(List<CustomHoliday> customHolidays) {
      this.customHolidays = customHolidays;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder deleteHistoryOnTaskDeletionEnabled(boolean deleteHistoryOnTaskDeletionEnabled) {
      this.deleteHistoryOnTaskDeletionEnabled = deleteHistoryOnTaskDeletionEnabled;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder germanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
      this.germanPublicHolidaysEnabled = germanPublicHolidaysEnabled;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder corpusChristiEnabled(boolean corpusChristiEnabled) {
      this.corpusChristiEnabled = corpusChristiEnabled;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobBatchSize(int jobBatchSize) {
      this.jobBatchSize = jobBatchSize;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder maxNumberOfJobRetries(int maxNumberOfJobRetries) {
      this.maxNumberOfJobRetries = maxNumberOfJobRetries;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder cleanupJobFirstRun(Instant cleanupJobFirstRun) {
      this.cleanupJobFirstRun = cleanupJobFirstRun;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder cleanupJobRunEvery(Duration cleanupJobRunEvery) {
      this.cleanupJobRunEvery = cleanupJobRunEvery;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder cleanupJobMinimumAge(Duration cleanupJobMinimumAge) {
      this.cleanupJobMinimumAge = cleanupJobMinimumAge;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder taskCleanupJobAllCompletedSameParentBusiness(
        boolean taskCleanupJobAllCompletedSameParentBusiness) {
      this.taskCleanupJobAllCompletedSameParentBusiness =
          taskCleanupJobAllCompletedSameParentBusiness;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder allowTimestampServiceLevelMismatch(
        boolean validationAllowTimestampServiceLevelMismatch) {
      this.allowTimestampServiceLevelMismatch = validationAllowTimestampServiceLevelMismatch;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addAdditionalUserInfo(boolean addAdditionalUserInfo) {
      this.addAdditionalUserInfo = addAdditionalUserInfo;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder priorityJobBatchSize(int priorityJobBatchSize) {
      this.priorityJobBatchSize = priorityJobBatchSize;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder priorityJobFirstRun(Instant priorityJobFirstRun) {
      this.priorityJobFirstRun = priorityJobFirstRun;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder priorityJobRunEvery(Duration priorityJobRunEvery) {
      this.priorityJobRunEvery = priorityJobRunEvery;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder priorityJobActive(boolean priorityJobActive) {
      this.priorityJobActive = priorityJobActive;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder userRefreshJobRunEvery(Duration userRefreshJobRunEvery) {
      this.userRefreshJobRunEvery = userRefreshJobRunEvery;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder userRefreshJobFirstRun(Instant userRefreshJobFirstRun) {
      this.userRefreshJobFirstRun = userRefreshJobFirstRun;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder minimalPermissionsToAssignDomains(
        List<WorkbasketPermission> minimalPermissionsToAssignDomains) {
      this.minimalPermissionsToAssignDomains = minimalPermissionsToAssignDomains;
      return this;
    }

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

    public Builder jobSchedulerEnableTaskCleanupJob(boolean jobSchedulerEnableTaskCleanupJob) {
      this.jobSchedulerEnableTaskCleanupJob = jobSchedulerEnableTaskCleanupJob;
      return this;
    }

    public Builder jobSchedulerEnableTaskUpdatePriorityJob(
        boolean jobSchedulerEnableTaskUpdatePriorityJob) {
      this.jobSchedulerEnableTaskUpdatePriorityJob = jobSchedulerEnableTaskUpdatePriorityJob;
      return this;
    }

    public Builder jobSchedulerEnableWorkbasketCleanupJob(
        boolean jobSchedulerEnableWorkbasketCleanupJob) {
      this.jobSchedulerEnableWorkbasketCleanupJob = jobSchedulerEnableWorkbasketCleanupJob;
      return this;
    }

    public Builder jobSchedulerEnableUserInfoRefreshJob(
        boolean jobSchedulerEnableUserInfoRefreshJob) {
      this.jobSchedulerEnableUserInfoRefreshJob = jobSchedulerEnableUserInfoRefreshJob;
      return this;
    }

    public Builder jobSchedulerEnableHistorieCleanupJob(
        boolean jobSchedulerEnableHistorieCleanupJob) {
      this.jobSchedulerEnableHistorieCleanupJob = jobSchedulerEnableHistorieCleanupJob;
      return this;
    }

    public Builder jobSchedulerCustomJobs(List<String> jobSchedulerCustomJobs) {
      this.jobSchedulerCustomJobs = jobSchedulerCustomJobs;
      return this;
    }

    public Builder workingTimeSchedule(Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule) {
      this.workingTimeSchedule = workingTimeSchedule;
      return this;
    }

    public TaskanaConfiguration build() {
      return new TaskanaConfiguration(this);
    }

    // endregion

    /**
     * Configure the {@linkplain TaskanaConfiguration} with the default {@linkplain
     * #TASKANA_PROPERTIES property file location} and {@linkplain #TASKANA_PROPERTY_SEPARATOR
     * property separator}.
     *
     * @see #initTaskanaProperties(String, String)
     */
    @SuppressWarnings({"unused", "checkstyle:JavadocMethod"})
    public Builder initTaskanaProperties() {
      return this.initTaskanaProperties(TASKANA_PROPERTIES, TASKANA_PROPERTY_SEPARATOR);
    }

    /**
     * Configure the {@linkplain TaskanaConfiguration} with the default {@linkplain
     * #TASKANA_PROPERTY_SEPARATOR property separator}.
     *
     * @see #initTaskanaProperties(String, String)
     */
    @SuppressWarnings({"unused", "checkstyle:JavadocMethod"})
    public Builder initTaskanaProperties(String propertiesFile) {
      return this.initTaskanaProperties(propertiesFile, TASKANA_PROPERTY_SEPARATOR);
    }

    /**
     * Configure the {@linkplain TaskanaConfiguration} using a property file from the classpath of
     * {@linkplain TaskanaConfiguration TaskanaConfigurations} or the system.
     *
     * <p>Please check this builders instance fields for the {@linkplain TaskanaProperty} for
     * property naming.
     *
     * @param propertiesFile path to the properties file.
     * @param separator if a property is a collection type, this separator determines which sequence
     *     delimits each individual value.
     * @return the builder
     * @throws SystemException if propertiesFile or separator is null or empty
     */
    public Builder initTaskanaProperties(String propertiesFile, String separator) {
      if (propertiesFile == null || propertiesFile.isEmpty() || propertiesFile.isBlank()) {
        throw new SystemException("property file can't be null or empty");
      }
      if (separator == null || separator.isEmpty() || separator.isBlank()) {
        throw new SystemException("separator file can't be null or empty");
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Reading taskana configuration from {} with separator {}", propertiesFile, separator);
      }
      properties = loadProperties(propertiesFile);
      configureAnnotatedFields(this, separator, properties);
      roleMap = configureRoles(separator, properties, shouldUseLowerCaseForAccessIds());
      classificationCategoriesByType =
          configureClassificationCategoriesForType(properties, classificationTypes);
      return this;
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
          FileLoaderUtil.openFileFromClasspathOrSystem(
              propertiesFile, TaskanaConfiguration.class)) {
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

      // Default working time schedule is from Monday 00:00 - Friday 24:00, but CET (hence -1 hour)
      Set<LocalTimeInterval> standardWorkingSlots =
          Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX));
      workingTime.put(
          DayOfWeek.SUNDAY, Set.of(new LocalTimeInterval(LocalTime.of(23, 0), LocalTime.MAX)));
      workingTime.put(DayOfWeek.MONDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.TUESDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.WEDNESDAY, standardWorkingSlots);
      workingTime.put(DayOfWeek.THURSDAY, standardWorkingSlots);
      workingTime.put(
          DayOfWeek.FRIDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.of(23, 0))));
      return workingTime;
    }
  }
}
