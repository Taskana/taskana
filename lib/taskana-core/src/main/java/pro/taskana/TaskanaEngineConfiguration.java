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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Security is enabled by default.
 */
@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public class TaskanaEngineConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineConfiguration.class);

  private static final String TASKANA_PROPERTIES = "/taskana.properties";
  private static final String TASKANA_PROPERTY_SEPARATOR = "|";

  // TASKANA_SCHEMA_VERSION
  private static final String DEFAULT_SCHEMA_NAME = "TASKANA";

  // Taskana properties file
  protected String propertiesFileName = TASKANA_PROPERTIES;
  // Taskana datasource configuration
  protected DataSource dataSource;
  protected String schemaName;
  // Taskana role configuration
  protected String propertiesSeparator = TASKANA_PROPERTY_SEPARATOR;
  protected Map<TaskanaRole, Set<String>> roleMap;

  // global switch to enable JAAS based authentication and Taskana
  // authorizations
  protected boolean securityEnabled;
  protected boolean useManagedTransactions;
  // List of configured domain names
  @TaskanaProperty("taskana.domains")
  protected List<String> domains = new ArrayList<>();
  // List of configured classification types
  @TaskanaProperty("taskana.classification.types")
  protected List<String> classificationTypes = new ArrayList<>();

  protected Map<String, List<String>> classificationCategoriesByTypeMap = new HashMap<>();

  @TaskanaProperty("taskana.custom.holidays")
  private List<CustomHoliday> customHolidays = new ArrayList<>();
  // Properties for the monitor
  @TaskanaProperty("taskana.history.deletion.on.task.deletion.enabled")
  private boolean deleteHistoryOnTaskDeletionEnabled;

  @TaskanaProperty("taskana.german.holidays.enabled")
  private boolean germanPublicHolidaysEnabled;

  @TaskanaProperty("taskana.german.holidays.corpus-christi.enabled")
  private boolean corpusChristiEnabled;

  // Properties for general job execution
  @TaskanaProperty("taskana.jobs.batchSize")
  private int jobBatchSize = 100;

  @TaskanaProperty("taskana.jobs.maxRetries")
  private int maxNumberOfJobRetries = 3;

  // Properties for the cleanup job
  @TaskanaProperty("taskana.jobs.cleanup.firstRunAt")
  private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");

  @TaskanaProperty("taskana.jobs.cleanup.runEvery")
  private Duration cleanupJobRunEvery = Duration.parse("P1D");

  @TaskanaProperty("taskana.jobs.cleanup.minimumAge")
  private Duration cleanupJobMinimumAge = Duration.parse("P14D");
  // TASKANA behavior

  @TaskanaProperty("taskana.jobs.cleanup.allCompletedSameParentBusiness")
  private boolean taskCleanupJobAllCompletedSameParentBusiness = true;

  @TaskanaProperty("taskana.validation.allowTimestampServiceLevelMismatch")
  private boolean validationAllowTimestampServiceLevelMismatch = false;
  // Property to enable/disable the addition of user full/long name through joins
  @TaskanaProperty("taskana.addAdditionalUserInfo")
  private boolean addAdditionalUserInfo = false;

  @TaskanaProperty("taskana.jobs.priority.batchSize")
  private int priorityJobBatchSize = 100;

  @TaskanaProperty("taskana.jobs.priority.firstRunAt")
  private Instant priorityJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");

  @TaskanaProperty("taskana.jobs.priority.runEvery")
  private Duration priorityJobRunEvery = Duration.parse("P1D");

  @TaskanaProperty("taskana.jobs.priority.active")
  private boolean priorityJobActive = false;

  @TaskanaProperty("taskana.jobs.user.refresh.runEvery")
  private Duration userRefreshJobRunEvery = Duration.parse("P1D");

  @TaskanaProperty("taskana.jobs.user.refresh.firstRunAt")
  private Instant userRefreshJobFirstRun = Instant.parse("2018-01-01T23:00:00Z");

  @TaskanaProperty("taskana.user.minimalPermissionsToAssignDomains")
  private List<WorkbasketPermission> minimalPermissionsToAssignDomains;

  public TaskanaEngineConfiguration(
      DataSource dataSource, boolean useManagedTransactions, String schemaName) {
    this(dataSource, useManagedTransactions, true, schemaName);
  }

  public TaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String schemaName) {
    this(dataSource, useManagedTransactions, securityEnabled, null, null, schemaName);
  }

  public TaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String propertiesFileName,
      String propertySeparator,
      String schemaName) {
    this.useManagedTransactions = useManagedTransactions;
    this.securityEnabled = securityEnabled;

    if (propertiesFileName != null) {
      this.propertiesFileName = propertiesFileName;
    }

    if (propertySeparator != null) {
      this.propertiesSeparator = propertySeparator;
    }

    if (dataSource != null) {
      this.dataSource = dataSource;
    } else {
      // use default In Memory datasource
      this.dataSource = createDefaultDataSource();
    }

    initSchemaName(schemaName);
    initTaskanaProperties(this.propertiesFileName, this.propertiesSeparator);
  }

  public static Properties loadProperties(String propertiesFile) {
    Properties props = new Properties();
    try (InputStream stream =
        FileLoaderUtil.openFileFromClasspathOrSystem(
            propertiesFile, TaskanaEngineConfiguration.class)) {
      props.load(stream);
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing properties file {}.", propertiesFile);
      throw new SystemException(
          "internal System error when processing properties file " + propertiesFile, e.getCause());
    }
    return props;
  }

  public void initTaskanaProperties(String propertiesFile, String separator) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Reading taskana configuration from {} with separator {}", propertiesFile, separator);
    }
    Properties props = loadProperties(propertiesFile);
    configureAnnotatedFields(this, separator, props);
    roleMap = configureRoles(separator, props, shouldUseLowerCaseForAccessIds());
    classificationCategoriesByTypeMap =
        configureClassificationCategoriesForType(props, classificationTypes);

    if (LOGGER.isDebugEnabled()) {
      roleMap.forEach((k, v) -> LOGGER.debug("Found Taskana RoleConfig {} : {} ", k, v));
      LOGGER.debug(
          "Configured number of task and workbasket updates per transaction: {}", jobBatchSize);
      LOGGER.debug("Number of retries of failed task updates: {}", maxNumberOfJobRetries);
      LOGGER.debug("CleanupJob configuration: first run at {}", cleanupJobFirstRun);
      LOGGER.debug("CleanupJob configuration: runs every {}", cleanupJobRunEvery);
      LOGGER.debug(
          "CleanupJob configuration: minimum age of tasks to be cleanup up is {}",
          cleanupJobMinimumAge);
      LOGGER.debug(
          "TaskCleanupJob configuration: all completed task with the "
              + "same parent business property id {}",
          taskCleanupJobAllCompletedSameParentBusiness);
      LOGGER.debug("Configured classification categories : {}", classificationCategoriesByTypeMap);
      LOGGER.debug("Configured domains: {}", domains);
      LOGGER.debug("Configured classificationTypes: {}", classificationTypes);
      LOGGER.debug("Configured custom Holidays : {}", customHolidays);
      LOGGER.debug(
          "Configured minimalPermissionsToAssignDomains : {}", minimalPermissionsToAssignDomains);
    }
  }

  public static DataSource createDefaultDataSource() {
    String driverClass = "org.h2.Driver";
    String jdbcUrl =
        "jdbc:h2:mem:taskana;NON_KEYWORDS=KEY,VALUE;"
            + "IGNORECASE=TRUE;LOCK_MODE=0;"
            + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
            + "SET COLLATION DEFAULT_de_DE";
    String username = "sa";
    String password = "sa";
    LOGGER.info(
        "No datasource is provided. An in-memory db is used: " + "'{}', '{}', '{}', '{}'",
        driverClass,
        jdbcUrl,
        username,
        password);
    return createDatasource(driverClass, jdbcUrl, username, password);
  }

  /**
   * This method creates a PooledDataSource, if the needed properties are provided.
   *
   * @param driver the name of the jdbc driver
   * @param jdbcUrl the url to which the jdbc driver connects
   * @param username the user name for database access
   * @param password the password for database access
   * @return DataSource
   */
  public static DataSource createDatasource(
      String driver, String jdbcUrl, String username, String password) {
    return new PooledDataSource(driver, jdbcUrl, username, password);
  }

  public boolean isSecurityEnabled() {
    return this.securityEnabled;
  }

  public DataSource getDatasource() {
    return this.dataSource;
  }

  public boolean getUseManagedTransactions() {
    return this.useManagedTransactions;
  }

  public int getMaxNumberOfUpdatesPerTransaction() {
    return jobBatchSize;
  }

  public void setMaxNumberOfUpdatesPerTransaction(int jobBatchSize) {
    this.jobBatchSize = jobBatchSize;
  }

  public int getMaxNumberOfJobRetries() {
    return maxNumberOfJobRetries;
  }

  public void setMaxNumberOfJobRetries(int maxNumberOfJobRetries) {
    this.maxNumberOfJobRetries = maxNumberOfJobRetries;
  }

  public boolean isCorpusChristiEnabled() {
    return corpusChristiEnabled;
  }

  public void setCorpusChristiEnabled(boolean corpusChristiEnabled) {
    this.corpusChristiEnabled = corpusChristiEnabled;
  }

  public boolean isGermanPublicHolidaysEnabled() {
    return this.germanPublicHolidaysEnabled;
  }

  public void setGermanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
    this.germanPublicHolidaysEnabled = germanPublicHolidaysEnabled;
  }

  public boolean isValidationAllowTimestampServiceLevelMismatch() {
    return validationAllowTimestampServiceLevelMismatch;
  }

  public void setValidationAllowTimestampServiceLevelMismatch(
      boolean validationAllowTimestampServiceLevelMismatch) {
    this.validationAllowTimestampServiceLevelMismatch =
        validationAllowTimestampServiceLevelMismatch;
  }

  public boolean isDeleteHistoryOnTaskDeletionEnabled() {
    return deleteHistoryOnTaskDeletionEnabled;
  }

  public void setDeleteHistoryOnTaskDeletionEnabled(boolean deleteHistoryOnTaskDeletionEnabled) {
    this.deleteHistoryOnTaskDeletionEnabled = deleteHistoryOnTaskDeletionEnabled;
  }

  public List<CustomHoliday> getCustomHolidays() {
    return customHolidays;
  }

  public void setCustomHolidays(List<CustomHoliday> customHolidays) {
    this.customHolidays = new ArrayList<>(customHolidays);
  }

  public void addCustomHolidays(List<CustomHoliday> customHolidays) {
    this.customHolidays.addAll(customHolidays);
  }

  public Map<TaskanaRole, Set<String>> getRoleMap() {
    return roleMap;
  }

  public void setRoleMap(Map<TaskanaRole, Set<String>> roleMap) {
    this.roleMap = roleMap;
  }

  public List<String> getDomains() {
    return domains;
  }

  public void setDomains(List<String> domains) {
    this.domains = domains;
  }

  public boolean getAddAdditionalUserInfo() {
    return addAdditionalUserInfo;
  }

  public void setAddAdditionalUserInfo(boolean addAdditionalUserInfo) {
    this.addAdditionalUserInfo = addAdditionalUserInfo;
  }

  public List<String> getClassificationTypes() {
    return classificationTypes;
  }

  public void setClassificationTypes(List<String> classificationTypes) {
    this.classificationTypes = classificationTypes;
  }

  public List<String> getAllClassificationCategories() {
    List<String> classificationCategories = new ArrayList<>();
    for (Map.Entry<String, List<String>> type : this.classificationCategoriesByTypeMap.entrySet()) {
      classificationCategories.addAll(type.getValue());
    }
    return classificationCategories;
  }

  public Map<String, List<String>> getClassificationCategoriesByTypeMap() {
    return this.classificationCategoriesByTypeMap.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue())));
  }

  public List<String> getClassificationCategoriesByType(String type) {
    return classificationCategoriesByTypeMap.getOrDefault(type, Collections.emptyList());
  }

  public void setClassificationCategoriesByType(
      Map<String, List<String>> classificationCategoriesByType) {
    this.classificationCategoriesByTypeMap = classificationCategoriesByType;
  }

  public Instant getCleanupJobFirstRun() {
    return cleanupJobFirstRun;
  }

  public void setCleanupJobFirstRun(Instant cleanupJobFirstRun) {
    this.cleanupJobFirstRun = cleanupJobFirstRun;
  }

  public Duration getCleanupJobRunEvery() {
    return cleanupJobRunEvery;
  }

  public void setCleanupJobRunEvery(Duration cleanupJobRunEvery) {
    this.cleanupJobRunEvery = cleanupJobRunEvery;
  }

  public Duration getCleanupJobMinimumAge() {
    return cleanupJobMinimumAge;
  }

  public void setCleanupJobMinimumAge(Duration cleanupJobMinimumAge) {
    this.cleanupJobMinimumAge = cleanupJobMinimumAge;
  }

  public boolean isTaskCleanupJobAllCompletedSameParentBusiness() {
    return taskCleanupJobAllCompletedSameParentBusiness;
  }

  public void setTaskCleanupJobAllCompletedSameParentBusiness(
      boolean taskCleanupJobAllCompletedSameParentBusiness) {
    this.taskCleanupJobAllCompletedSameParentBusiness =
        taskCleanupJobAllCompletedSameParentBusiness;
  }

  public int getPriorityJobBatchSize() {
    return priorityJobBatchSize;
  }

  public void setPriorityJobBatchSize(int priorityJobBatchSize) {
    this.priorityJobBatchSize = priorityJobBatchSize;
  }

  public Instant getPriorityJobFirstRun() {
    return priorityJobFirstRun;
  }

  public void setPriorityJobFirstRun(Instant priorityJobFirstRun) {
    this.priorityJobFirstRun = priorityJobFirstRun;
  }

  public Duration getPriorityJobRunEvery() {
    return priorityJobRunEvery;
  }

  public void setPriorityJobRunEvery(Duration priorityJobRunEvery) {
    this.priorityJobRunEvery = priorityJobRunEvery;
  }

  public Duration getUserRefreshJobRunEvery() {
    return userRefreshJobRunEvery;
  }

  public void setUserRefreshJobRunEvery(Duration userRefreshJobRunEvery) {
    this.userRefreshJobRunEvery = userRefreshJobRunEvery;
  }

  public List<WorkbasketPermission> getMinimalPermissionsToAssignDomains() {
    return minimalPermissionsToAssignDomains;
  }

  public void setMinimalPermissionsToAssignDomains(
      List<WorkbasketPermission> minimalPermissionsToAssignDomains) {
    this.minimalPermissionsToAssignDomains = minimalPermissionsToAssignDomains;
  }

  public Instant getUserRefreshJobFirstRun() {
    return userRefreshJobFirstRun;
  }

  public void setUserRefreshJobFirstRun(Instant userRefreshJobFirstRun) {
    this.userRefreshJobFirstRun = userRefreshJobFirstRun;
  }

  public boolean isPriorityJobActive() {
    return priorityJobActive;
  }

  public void setPriorityJobActive(boolean priorityJobActive) {
    this.priorityJobActive = priorityJobActive;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
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

  public Properties readPropertiesFromFile() {
    return loadProperties(this.propertiesFileName);
  }

  private void initSchemaName(String schemaName) {
    if (schemaName != null && !schemaName.isEmpty()) {
      this.setSchemaName(schemaName);
    } else {
      this.setSchemaName(DEFAULT_SCHEMA_NAME);
    }

    try (Connection connection = dataSource.getConnection()) {
      String databaseProductId =
          DB.getDatabaseProductId(connection.getMetaData().getDatabaseProductName());
      if (DB.isPostgres(databaseProductId)) {
        this.schemaName = this.schemaName.toLowerCase();
      } else {
        this.schemaName = this.schemaName.toUpperCase();
      }
    } catch (SQLException ex) {
      LOGGER.error("Caught exception when attempting to initialize the schema name", ex);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Using schema name {}", this.getSchemaName());
    }
  }
}
