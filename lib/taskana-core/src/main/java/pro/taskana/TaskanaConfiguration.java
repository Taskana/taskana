package pro.taskana;

import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureAnnotatedFields;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureClassificationCategoriesForType;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureRoles;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.CustomHoliday;
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
@Slf4j
@Getter
@EqualsAndHashCode
@ToString
public class TaskanaConfiguration {

  private final DataSource dataSource;
  private final boolean securityEnabled;
  private final boolean useManagedTransactions;
  private final String schemaName;

  private final boolean germanPublicHolidaysEnabled;
  // endregion
  private final boolean corpusChristiEnabled;
  // endregion
  // region history configuration
  private final boolean deleteHistoryOnTaskDeletionEnabled;
  // region custom configuration
  private final Map<String, String> properties;
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
  private List<String> domains = new ArrayList<>();
  // endregion
  // region authentication configuration
  private Map<TaskanaRole, Set<String>> roleMap = new EnumMap<>(TaskanaRole.class);
  // region classification configuration
  private List<String> classificationTypes = new ArrayList<>();
  // TODO: make this a Set
  private Map<String, List<String>> classificationCategoriesByType = new HashMap<>();
  // endregion
  private boolean allowTimestampServiceLevelMismatch = false;
  // endregion
  // region holiday configuration
  private List<CustomHoliday> customHolidays = new ArrayList<>();
  // region job configuration
  private int jobBatchSize = 100;
  private int maxNumberOfJobRetries = 3;
  private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration cleanupJobRunEvery = Duration.ofDays(1);
  // endregion
  private Duration cleanupJobMinimumAge = Duration.ofDays(14);
  private boolean taskCleanupJobAllCompletedSameParentBusiness = true;
  private int priorityJobBatchSize = 100;
  private Instant priorityJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration priorityJobRunEvery = Duration.ofDays(1);
  private boolean priorityJobActive = false;
  private Duration userRefreshJobRunEvery = Duration.ofDays(1);
  // endregion
  private Instant userRefreshJobFirstRun = Instant.parse("2018-01-01T23:00:00Z");
  // region user configuration
  private boolean addAdditionalUserInfo = false;
  // endregion
  // TODO: make Set
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
    this.jobSchedulerPeriodTimeUnit =
        determineTimeUnitFromString(builder.jobSchedulerPeriodTimeUnit);
    this.jobSchedulerEnableTaskCleanupJob = builder.jobSchedulerEnableTaskCleanupJob;
    this.jobSchedulerEnableTaskUpdatePriorityJob = builder.jobSchedulerEnableTaskUpdatePriorityJob;
    this.jobSchedulerEnableWorkbasketCleanupJob = builder.jobSchedulerEnableWorkbasketCleanupJob;
    this.jobSchedulerEnableUserInfoRefreshJob = builder.jobSchedulerEnableUserInfoRefreshJob;
    this.jobSchedulerEnableHistorieCleanupJob = builder.jobSchedulerEnableHistorieCleanupJob;
    this.jobSchedulerCustomJobs = Collections.unmodifiableList(builder.jobSchedulerCustomJobs);

    log.debug(this.toString());
  }

  public List<String> getAllClassificationCategories() {
    List<String> classificationCategories = new ArrayList<>();
    for (Map.Entry<String, List<String>> type : this.classificationCategoriesByType.entrySet()) {
      classificationCategories.addAll(type.getValue());
    }
    return classificationCategories;
  }

  public Map<String, List<String>> getClassificationCategoriesByType() {
    if (this.classificationCategoriesByType == null) {
      return Map.of();
    }
    return this.classificationCategoriesByType.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue())));
  }

  public List<String> getClassificationCategoriesByType(String type) {
    return classificationCategoriesByType.getOrDefault(type, Collections.emptyList());
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

  private TimeUnit determineTimeUnitFromString(String timeUnit) {
    switch (timeUnit) {
      case "MILLISECONDS":
        return TimeUnit.MILLISECONDS;
      case "SECONDS":
        return TimeUnit.SECONDS;
      case "MINUTES":
        return TimeUnit.MINUTES;
      case "DAYS":
        return TimeUnit.DAYS;
      default:
        return TimeUnit.HOURS;
    }
  }

  @Slf4j
  public static class Builder {

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

    @TaskanaProperty("taskana.jobscheduler.enabled")
    private boolean jobSchedulerEnabled = true;

    @TaskanaProperty("taskana.jobscheduler.initialstartdelay")
    private long jobSchedulerInitialStartDelay = 100;

    @TaskanaProperty("taskana.jobscheduler.period")
    private long jobSchedulerPeriod = 12;

    @TaskanaProperty("taskana.jobscheduler.periodtimeunit")
    private String jobSchedulerPeriodTimeUnit = "HOURS";

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

    public Builder(TaskanaConfiguration tec) {
      this.dataSource = tec.getDataSource();
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
      this.jobSchedulerPeriodTimeUnit = tec.getJobSchedulerPeriodTimeUnit().name();
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

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnabled(boolean jobSchedulerEnabled) {
      this.jobSchedulerEnabled = jobSchedulerEnabled;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerInitialStartDelay(long jobSchedulerInitialStartDelay) {
      this.jobSchedulerInitialStartDelay = jobSchedulerInitialStartDelay;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerPeriod(long jobSchedulerPeriod) {
      this.jobSchedulerPeriod = jobSchedulerPeriod;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerPeriodTimeUnit(String jobSchedulerPeriodTimeUnit) {
      this.jobSchedulerPeriodTimeUnit = jobSchedulerPeriodTimeUnit;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnableTaskCleanupJob(boolean jobSchedulerEnableTaskCleanupJob) {
      this.jobSchedulerEnableTaskCleanupJob = jobSchedulerEnableTaskCleanupJob;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnableTaskUpdatePriorityJob(
        boolean jobSchedulerEnableTaskUpdatePriorityJob) {
      this.jobSchedulerEnableTaskUpdatePriorityJob = jobSchedulerEnableTaskUpdatePriorityJob;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnableWorkbasketCleanupJob(
        boolean jobSchedulerEnableWorkbasketCleanupJob) {
      this.jobSchedulerEnableWorkbasketCleanupJob = jobSchedulerEnableWorkbasketCleanupJob;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnableUserInfoRefreshJob(
        boolean jobSchedulerEnableUserInfoRefreshJob) {
      this.jobSchedulerEnableUserInfoRefreshJob = jobSchedulerEnableUserInfoRefreshJob;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerEnableHistorieCleanupJob(
        boolean jobSchedulerEnableHistorieCleanupJob) {
      this.jobSchedulerEnableHistorieCleanupJob = jobSchedulerEnableHistorieCleanupJob;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder jobSchedulerCustomJobs(List<String> jobSchedulerCustomJobs) {
      this.jobSchedulerCustomJobs = jobSchedulerCustomJobs;
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
      log.debug(
          "Reading taskana configuration from {} with separator {}", propertiesFile, separator);
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
        String databaseProductId = DB.getDatabaseProductId(connection);
        if (DB.isPostgres(databaseProductId)) {
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
  }
}
