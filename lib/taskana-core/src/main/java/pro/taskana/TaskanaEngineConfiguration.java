package pro.taskana;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.internal.configuration.SecurityVerifier;

/**
 * This central class creates the TaskanaEngine and holds all the information about DB and
 * Security.
 * <br>
 * Security is enabled by default.
 */
public class TaskanaEngineConfiguration {

  protected static final String TASKANA_SCHEMA_VERSION =
      "3.0.0"; // must match the VERSION value in table
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineConfiguration.class);
  private static final String USER_NAME = "sa";
  private static final String USER_PASSWORD = "sa";
  private static final String JDBC_H2_MEM_TASKANA =
      "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;"
          + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
          + "SET COLLATION DEFAULT_de_DE ";
  private static final String H2_DRIVER = "org.h2.Driver";
  private static final String TASKANA_PROPERTIES = "/taskana.properties";
  private static final String TASKANA_ROLES_SEPARATOR = "|";
  private static final String TASKANA_JOB_BATCHSIZE = "taskana.jobs.batchSize";
  private static final String TASKANA_JOB_RETRIES = "taskana.jobs.maxRetries";
  private static final String TASKANA_JOB_CLEANUP_RUN_EVERY = "taskana.jobs.cleanup.runEvery";
  private static final String TASKANA_JOB_CLEANUP_FIRST_RUN = "taskana.jobs.cleanup.firstRunAt";
  private static final String TASKANA_JOB_CLEANUP_MINIMUM_AGE = "taskana.jobs.cleanup.minimumAge";
  private static final String TASKANA_JOB_TASK_CLEANUP_ALL_COMPLETED_SAME_PARENTE_BUSINESS =
      "taskana.jobs.cleanup.allCompletedSameParentBusiness";
  private static final String TASKANA_DOMAINS_PROPERTY = "taskana.domains";
  private static final String TASKANA_CLASSIFICATION_TYPES_PROPERTY =
      "taskana.classification.types";
  private static final String TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY =
      "taskana.classification.categories";
  private static final String TASKANA_GERMAN_HOLIDAYS_ENABLED = "taskana.german.holidays.enabled";
  // TASKANA_SCHEMA_VERSION
  private static final String DEFAULT_SCHEMA_NAME = "TASKANA";

  // Taskana properties file
  protected String propertiesFileName = TASKANA_PROPERTIES;

  // Taskana datasource configuration
  protected DataSource dataSource;
  protected DbSchemaCreator dbSchemaCreator;
  protected String schemaName;

  // Taskana role configuration
  protected String rolesSeparator = TASKANA_ROLES_SEPARATOR;
  protected Map<TaskanaRole, Set<String>> roleMap = new HashMap<>();

  // global switch to enable JAAS based authentication and Taskana
  // authorizations
  protected boolean securityEnabled;
  protected SecurityVerifier securityVerifier;
  protected boolean useManagedTransactions;
  // List of configured domain names
  protected List<String> domains = new ArrayList<>();
  // List of configured classification types
  protected List<String> classificationTypes = new ArrayList<>();
  protected Map<String, List<String>> classificationCategoriesByTypeMap = new HashMap<>();
  // Properties for the monitor
  private boolean germanPublicHolidaysEnabled;
  private List<LocalDate> customHolidays;
  // Properties for generalo job execution
  private int jobBatchSize = 100;
  private int maxNumberOfJobRetries = 3;
  // Properties for the cleanup job
  private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration cleanupJobRunEvery = Duration.parse("P1D");
  private Duration cleanupJobMinimumAge = Duration.parse("P14D");
  private boolean taskCleanupJobAllCompletedSameParentBusiness = true;

  public TaskanaEngineConfiguration(
      DataSource dataSource, boolean useManagedTransactions, String schemaName)
      throws SQLException {
    this(dataSource, useManagedTransactions, true, schemaName);
  }

  public TaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String schemaName)
      throws SQLException {
    this(dataSource, useManagedTransactions, securityEnabled, null, null, schemaName);
  }

  public TaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String propertiesFileName,
      String rolesSeparator,
      String schemaName)
      throws SQLException {
    this.useManagedTransactions = useManagedTransactions;
    this.securityEnabled = securityEnabled;

    if (propertiesFileName != null) {
      this.propertiesFileName = propertiesFileName;
    }

    if (rolesSeparator != null) {
      this.rolesSeparator = rolesSeparator;
    }

    if (dataSource != null) {
      this.dataSource = dataSource;
    } else {
      // use default In Memory datasource
      this.dataSource = createDefaultDataSource();
    }

    initSchemaName(schemaName);
    initTaskanaProperties(this.propertiesFileName, this.rolesSeparator);

    dbSchemaCreator = new DbSchemaCreator(this.dataSource, this.getSchemaName());
    dbSchemaCreator.run();

    if (!dbSchemaCreator.isValidSchemaVersion(TASKANA_SCHEMA_VERSION)) {
      throw new SystemException(
          "The Database Schema Version doesn't match the expected version "
              + TASKANA_SCHEMA_VERSION);
    }

    securityVerifier = new SecurityVerifier(this.dataSource, this.getSchemaName());
    securityVerifier.checkSecureAccess(securityEnabled);
  }

  public void initTaskanaProperties(String propertiesFile, String rolesSeparator) {
    LOGGER.debug(
        "Reading taskana configuration from {} with role separator {}",
        propertiesFile,
        rolesSeparator);
    Properties props = readPropertiesFromFile(propertiesFile);
    initTaskanaRoles(props, rolesSeparator);
    initJobParameters(props);
    initDomains(props);
    initClassificationTypes(props);
    initClassificationCategories(props);
    initGermanHolidaysEnabled(props);
  }

  public static DataSource createDefaultDataSource() {
    LOGGER.info(
        "No datasource is provided. A inmemory db is used: "
            + "'org.h2.Driver', 'jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;"
            + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
            + "SET COLLATION DEFAULT_de_DE', 'sa', 'sa'");
    return createDatasource(H2_DRIVER, JDBC_H2_MEM_TASKANA, USER_NAME, USER_PASSWORD);
  }

  /**
   * This method creates the TaskanaEngine without an sqlSessionFactory.
   *
   * @return the TaskanaEngine
   */
  public TaskanaEngine buildTaskanaEngine() {
    return TaskanaEngineImpl.createTaskanaEngine(this);
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

  public String getPropertiesFileName() {
    return this.propertiesFileName;
  }

  public void setPropertiesFileName(String propertiesFileName) {
    this.propertiesFileName = propertiesFileName;
  }

  public int getMaxNumberOfUpdatesPerTransaction() {
    return jobBatchSize;
  }

  public int getMaxNumberOfJobRetries() {
    return maxNumberOfJobRetries;
  }

  public String getPropertiesSeparator() {
    return this.rolesSeparator;
  }

  public void setPropertiesSeparator(String propertiesSeparator) {
    this.rolesSeparator = propertiesSeparator;
  }

  public boolean isGermanPublicHolidaysEnabled() {
    return this.germanPublicHolidaysEnabled;
  }

  public void setGermanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
    this.germanPublicHolidaysEnabled = germanPublicHolidaysEnabled;
  }

  public List<LocalDate> getCustomHolidays() {
    return customHolidays;
  }

  public void setCustomHolidays(List<LocalDate> customHolidays) {
    this.customHolidays = customHolidays;
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

  public Duration getCleanupJobRunEvery() {
    return cleanupJobRunEvery;
  }

  public Duration getCleanupJobMinimumAge() {
    return cleanupJobMinimumAge;
  }

  public boolean isTaskCleanupJobAllCompletedSameParentBusiness() {
    return taskCleanupJobAllCompletedSameParentBusiness;
  }

  public void setTaskCleanupJobAllCompletedSameParentBusiness(
      boolean taskCleanupJobAllCompletedSameParentBusiness) {
    this.taskCleanupJobAllCompletedSameParentBusiness =
        taskCleanupJobAllCompletedSameParentBusiness;
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

  private void initGermanHolidaysEnabled(Properties props) {
    String enabled = props.getProperty(TASKANA_GERMAN_HOLIDAYS_ENABLED);
    if (enabled != null && !enabled.isEmpty()) {
      germanPublicHolidaysEnabled = Boolean.parseBoolean(enabled);
    } else {
      germanPublicHolidaysEnabled = false;
    }
    LOGGER.debug("GermanPublicHolidaysEnabled = {}", germanPublicHolidaysEnabled);
  }

  private void initJobParameters(Properties props) {
    String jobBatchSizeProperty = props.getProperty(TASKANA_JOB_BATCHSIZE);
    if (jobBatchSizeProperty != null && !jobBatchSizeProperty.isEmpty()) {
      try {
        jobBatchSize = Integer.parseInt(jobBatchSizeProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse jobBatchSizeProperty ({}). Using default. Exception: {} ",
            jobBatchSizeProperty,
            e.getMessage());
      }
    }

    String maxNumberOfJobRetriesProperty = props.getProperty(TASKANA_JOB_RETRIES);
    if (maxNumberOfJobRetriesProperty != null && !maxNumberOfJobRetriesProperty.isEmpty()) {
      try {
        maxNumberOfJobRetries = Integer.parseInt(maxNumberOfJobRetriesProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse maxNumberOfJobRetriesProperty ({}). Using default. Exception: {} ",
            maxNumberOfJobRetriesProperty,
            e.getMessage());
      }
    }

    String taskCleanupJobFirstRunProperty = props.getProperty(TASKANA_JOB_CLEANUP_FIRST_RUN);
    if (taskCleanupJobFirstRunProperty != null && !taskCleanupJobFirstRunProperty.isEmpty()) {
      try {
        cleanupJobFirstRun = Instant.parse(taskCleanupJobFirstRunProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse taskCleanupJobFirstRunProperty ({}). Using default. Exception: {} ",
            taskCleanupJobFirstRunProperty,
            e.getMessage());
      }
    }

    String taskCleanupJobRunEveryProperty = props.getProperty(TASKANA_JOB_CLEANUP_RUN_EVERY);
    if (taskCleanupJobRunEveryProperty != null && !taskCleanupJobRunEveryProperty.isEmpty()) {
      try {
        cleanupJobRunEvery = Duration.parse(taskCleanupJobRunEveryProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse taskCleanupJobRunEveryProperty ({}). Using default. Exception: {} ",
            taskCleanupJobRunEveryProperty,
            e.getMessage());
      }
    }

    String taskCleanupJobMinimumAgeProperty = props.getProperty(TASKANA_JOB_CLEANUP_MINIMUM_AGE);
    if (taskCleanupJobMinimumAgeProperty != null && !taskCleanupJobMinimumAgeProperty.isEmpty()) {
      try {
        cleanupJobMinimumAge = Duration.parse(taskCleanupJobMinimumAgeProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse taskCleanupJobMinimumAgeProperty ({}). Using default. Exception: {} ",
            taskCleanupJobMinimumAgeProperty,
            e.getMessage());
      }
    }

    String taskCleanupJobAllCompletedSameParentBusinessProperty =
        props.getProperty(TASKANA_JOB_TASK_CLEANUP_ALL_COMPLETED_SAME_PARENTE_BUSINESS);
    if (taskCleanupJobAllCompletedSameParentBusinessProperty != null
            && !taskCleanupJobAllCompletedSameParentBusinessProperty.isEmpty()) {
      try {
        taskCleanupJobAllCompletedSameParentBusiness =
            Boolean.parseBoolean(taskCleanupJobAllCompletedSameParentBusinessProperty);
      } catch (Exception e) {
        LOGGER.warn(
            "Could not parse taskCleanupJobAllCompletedSameParentBusinessProperty "
                + "({}). Using default. Exception: {} ",
            taskCleanupJobAllCompletedSameParentBusinessProperty,
            e.getMessage());
      }
    }

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
  }

  private void initDomains(Properties props) {
    String domainNames = props.getProperty(TASKANA_DOMAINS_PROPERTY);
    if (domainNames != null && !domainNames.isEmpty()) {
      StringTokenizer st = new StringTokenizer(domainNames, ",");
      while (st.hasMoreTokens()) {
        domains.add(st.nextToken().trim().toUpperCase());
      }
    }
    LOGGER.debug("Configured domains: {}", domains);
  }

  private void initClassificationTypes(Properties props) {
    String classificationTypesNames = props.getProperty(TASKANA_CLASSIFICATION_TYPES_PROPERTY);
    if (classificationTypesNames != null && !classificationTypesNames.isEmpty()) {
      StringTokenizer st = new StringTokenizer(classificationTypesNames, ",");
      while (st.hasMoreTokens()) {
        classificationTypes.add(st.nextToken().trim().toUpperCase());
      }
    } else {
      LOGGER.warn("Configuration issue. Classification type is missing");
    }
    LOGGER.debug("Configured classificationTypes: {}", classificationTypes);
  }

  private void initClassificationCategories(Properties props) {
    if (classificationTypes != null && !classificationTypes.isEmpty()) {
      String classificationCategoryNames;
      StringTokenizer st;
      List<String> classificationCategoriesAux;
      for (String type : classificationTypes) {
        classificationCategoriesAux = new ArrayList<>();
        classificationCategoryNames =
            props.getProperty(
                TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY + "." + type.toLowerCase());
        if (classificationCategoryNames != null && !classificationCategoryNames.isEmpty()) {
          st = new StringTokenizer(classificationCategoryNames, ",");
          while (st.hasMoreTokens()) {
            classificationCategoriesAux.add(st.nextToken().trim().toUpperCase());
          }
          classificationCategoriesByTypeMap.put(type, classificationCategoriesAux);
        } else {
          LOGGER.warn("Configuration issue. Classification categories by type is missing");
        }
      }
    }
    LOGGER.debug("Configured classification categories : {}", domains);
  }

  private void initSchemaName(String schemaName) {
    if (schemaName != null && !schemaName.isEmpty()) {
      this.setSchemaName(schemaName);
    } else {
      this.setSchemaName(DEFAULT_SCHEMA_NAME);
    }

    try (Connection connection = dataSource.getConnection()) {
      String databaseProductName = connection.getMetaData().getDatabaseProductName();
      if (DB.isPostgreSql(databaseProductName)) {
        this.schemaName = this.schemaName.toLowerCase();
      } else {
        this.schemaName = this.schemaName.toUpperCase();
      }
    } catch (SQLException ex) {
      LOGGER.error("Caught exception when attempting to initialize the schema name", ex);
    }

    LOGGER.debug("Using schema name {}", this.getSchemaName());
  }

  private void initTaskanaRoles(Properties props, String rolesSeparator) {
    List<String> validPropertyNames = TaskanaRole.getValidPropertyNames();

    props.keySet().stream()
        .map(String::valueOf)
        .filter(propertyName -> validPropertyNames.contains(propertyName.toLowerCase().trim()))
        .forEach(
            validPropertyName ->
                roleMap.put(
                    TaskanaRole.fromPropertyName(validPropertyName),
                    getTokensWithCollection(props.getProperty(validPropertyName),
                        rolesSeparator)));

    ensureRoleMapIsFullyInitialized();

    if (LOGGER.isDebugEnabled()) {
      roleMap.forEach(
          (k, v) ->
              LOGGER.debug("Found Taskana RoleConfig {} : {} ", k, LoggerUtils.setToString(v)));
    }
  }

  private HashSet<String> getTokensWithCollection(String str, String rolesSeparator) {
    return Collections.list(new StringTokenizer(str, rolesSeparator)).stream()
               .map(token -> String.valueOf(token).toLowerCase().trim())
               .collect(Collectors.toCollection(HashSet::new));
  }

  private Properties readPropertiesFromFile(String propertiesFile) {
    Properties props = new Properties();
    boolean loadFromClasspath = loadFromClasspath(propertiesFile);
    try {
      if (loadFromClasspath) {
        InputStream inputStream =
            TaskanaEngineConfiguration.class.getResourceAsStream(propertiesFile);
        if (inputStream == null) {
          LOGGER.error("taskana properties file {} was not found on classpath.", propertiesFile);
        } else {
          props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
          LOGGER
              .debug("Role properties were loaded from file {} from classpath.", propertiesFile);
        }
      } else {
        props.load(new FileInputStream(propertiesFile));
        LOGGER.debug("Role properties were loaded from file {}.", propertiesFile);
      }
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing properties file {}.", propertiesFile);
      throw new SystemException(
          "internal System error when processing properties file " + propertiesFile,
          e.getCause());
    }
    return props;
  }

  private boolean loadFromClasspath(String propertiesFile) {
    boolean loadFromClasspath = true;
    File f = new File(propertiesFile);
    if (f.exists() && !f.isDirectory()) {
      loadFromClasspath = false;
    }
    return loadFromClasspath;
  }

  private void ensureRoleMapIsFullyInitialized() {
    // make sure that roleMap does not return null for any role
    Arrays.stream(TaskanaRole.values())
        .forEach(role -> roleMap.putIfAbsent(role, new HashSet<>()));
  }
}
