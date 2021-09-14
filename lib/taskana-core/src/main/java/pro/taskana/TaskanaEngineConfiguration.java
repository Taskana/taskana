package pro.taskana;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.WrongCustomHolidayFormatException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.util.CheckedFunction;
import pro.taskana.common.internal.util.FileLoaderUtil;
import pro.taskana.common.internal.util.Pair;

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
  private static final String TASKANA_JOB_BATCH_SIZE = "taskana.jobs.batchSize";
  private static final String TASKANA_JOB_RETRIES = "taskana.jobs.maxRetries";
  private static final String TASKANA_JOB_CLEANUP_RUN_EVERY = "taskana.jobs.cleanup.runEvery";
  private static final String TASKANA_JOB_CLEANUP_FIRST_RUN = "taskana.jobs.cleanup.firstRunAt";
  private static final String TASKANA_JOB_CLEANUP_MINIMUM_AGE = "taskana.jobs.cleanup.minimumAge";
  private static final String TASKANA_JOB_TASK_CLEANUP_ALL_COMPLETED_SAME_PARENT_BUSINESS =
      "taskana.jobs.cleanup.allCompletedSameParentBusiness";
  private static final String TASKANA_JOB_PRIORITY_BATCHSIZE = "taskana.jobs.priority.batchSize";
  private static final String TASKANA_JOB_PRIORITY_RUN_EVERY = "taskana.jobs.priority.runEvery";
  private static final String TASKANA_JOB_PRIORITY_FIRST_RUN = "taskana.jobs.priority.firstRunAt";
  private static final String TASKANA_JOB_PRIORITY_ACTIVE = "taskana.jobs.priority.active";
  private static final String TASKANA_DOMAINS_PROPERTY = "taskana.domains";
  private static final String TASKANA_CLASSIFICATION_TYPES_PROPERTY =
      "taskana.classification.types";
  private static final String TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY =
      "taskana.classification.categories";
  private static final String TASKANA_GERMAN_HOLIDAYS_ENABLED = "taskana.german.holidays.enabled";
  private static final String TASKANA_GERMAN_HOLIDAYS_CORPUS_CHRISTI_ENABLED =
      "taskana.german.holidays.corpus-christi.enabled";
  private static final String TASKANA_CUSTOM_HOLIDAY = "taskana.custom.holidays";
  private static final String TASKANA_CUSTOM_HOLIDAY_DAY_MONTH_SEPARATOR = ".";
  private static final String TASKANA_HISTORY_DELETION_ON_TASK_DELETION_ENABLED =
      "taskana.history.deletion.on.task.deletion.enabled";
  private static final String TASKANA_VALIDATION_ALLOW_TIMESTAMP_SERVICE_LEVEL_MISMATCH =
      "taskana.validation.allowTimestampServiceLevelMismatch";
  private static final String TASKANA_ADD_ADDITIONAL_USER_INFO = "taskana.addAdditionalUserInfo";
  // TASKANA_SCHEMA_VERSION
  private static final String DEFAULT_SCHEMA_NAME = "TASKANA";

  private final List<CustomHoliday> customHolidays = new ArrayList<>();
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
  protected List<String> domains = new ArrayList<>();
  // List of configured classification types
  protected List<String> classificationTypes = new ArrayList<>();
  protected Map<String, List<String>> classificationCategoriesByTypeMap = new HashMap<>();
  // Properties for the monitor
  private boolean deleteHistoryOnTaskDeletionEnabled;
  private boolean germanPublicHolidaysEnabled;
  private boolean corpusChristiEnabled;
  // Properties for general job execution
  private int jobBatchSize = 100;
  private int maxNumberOfJobRetries = 3;
  // Properties for the cleanup job
  private Instant cleanupJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration cleanupJobRunEvery = Duration.parse("P1D");
  private Duration cleanupJobMinimumAge = Duration.parse("P14D");
  // TASKANA behavior
  private boolean taskCleanupJobAllCompletedSameParentBusiness = true;
  private boolean validationAllowTimestampServiceLevelMismatch = false;
  //Property to enable/disable the addition of user full/long name through joins
  private boolean addAdditionalUserInfo = false;

  private int priorityJobBatchSize = 100;
  private Instant priorityJobFirstRun = Instant.parse("2018-01-01T00:00:00Z");
  private Duration priorityJobRunEvery = Duration.parse("P1D");
  private boolean priorityJobActive = false;

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

  public void initTaskanaProperties(String propertiesFile, String separator) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Reading taskana configuration from {} with separator {}", propertiesFile, separator);
    }
    Properties props = readPropertiesFromFile(propertiesFile);
    initTaskanaRoles(props, separator);
    initJobParameters(props);
    initDomains(props);
    initClassificationTypes(props);
    initClassificationCategories(props);
    initBooleanProperty(
        props, TASKANA_GERMAN_HOLIDAYS_ENABLED, this::setGermanPublicHolidaysEnabled);
    initBooleanProperty(
        props, TASKANA_GERMAN_HOLIDAYS_CORPUS_CHRISTI_ENABLED, this::setCorpusChristiEnabled);
    initBooleanProperty(
        props,
        TASKANA_HISTORY_DELETION_ON_TASK_DELETION_ENABLED,
        this::setDeleteHistoryOnTaskDeletionEnabled);
    initBooleanProperty(
        props,
        TASKANA_VALIDATION_ALLOW_TIMESTAMP_SERVICE_LEVEL_MISMATCH,
        this::setValidationAllowTimestampServiceLevelMismatch);
    initBooleanProperty(
        props, TASKANA_ADD_ADDITIONAL_USER_INFO, this::setAddAdditionalUserInfo);
    initCustomHolidays(props, separator);
  }

  public static DataSource createDefaultDataSource() {
    String driverClass = "org.h2.Driver";
    String jdbcUrl =
        "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;"
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
   * This method creates the TaskanaEngine without an sqlSessionFactory.
   *
   * @return the TaskanaEngine
   * @throws SQLException if a database access error occurs
   */
  public TaskanaEngine buildTaskanaEngine() throws SQLException {
    return TaskanaEngineImpl.createTaskanaEngine(this);
  }

  public TaskanaEngine buildTaskanaEngine(ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return TaskanaEngineImpl.createTaskanaEngine(this, connectionManagementMode);
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
    return readPropertiesFromFile(this.propertiesFileName);
  }

  private <T> Optional<T> parseProperty(
      Properties props, String key, CheckedFunction<String, T, Exception> function) {
    String property = props.getProperty(key, "");
    if (!property.isEmpty()) {
      try {
        return Optional.ofNullable(function.apply(property));
      } catch (Exception t) {
        LOGGER.warn(
            "Could not parse property {} ({}). Using default. Exception: {}",
            key,
            property,
            t.getMessage());
      }
    }
    return Optional.empty();
  }

  private void initJobParameters(Properties props) {

    parseProperty(props, TASKANA_JOB_BATCH_SIZE, Integer::parseInt)
        .ifPresent(this::setMaxNumberOfUpdatesPerTransaction);

    parseProperty(props, TASKANA_JOB_RETRIES, Integer::parseInt)
        .ifPresent(this::setMaxNumberOfJobRetries);

    parseProperty(props, TASKANA_JOB_CLEANUP_FIRST_RUN, Instant::parse)
        .ifPresent(this::setCleanupJobFirstRun);

    parseProperty(props, TASKANA_JOB_CLEANUP_RUN_EVERY, Duration::parse)
        .ifPresent(this::setCleanupJobRunEvery);

    parseProperty(props, TASKANA_JOB_CLEANUP_MINIMUM_AGE, Duration::parse)
        .ifPresent(this::setCleanupJobMinimumAge);

    parseProperty(props, TASKANA_JOB_PRIORITY_BATCHSIZE, Integer::parseInt)
        .ifPresent(this::setPriorityJobBatchSize);

    parseProperty(props, TASKANA_JOB_PRIORITY_RUN_EVERY, Duration::parse)
        .ifPresent(this::setPriorityJobRunEvery);

    parseProperty(props, TASKANA_JOB_PRIORITY_FIRST_RUN, Instant::parse)
        .ifPresent(this::setPriorityJobFirstRun);

    parseProperty(props, TASKANA_JOB_PRIORITY_ACTIVE, Boolean::parseBoolean)
        .ifPresent(this::setPriorityJobActive);

    parseProperty(
            props,
            TASKANA_JOB_TASK_CLEANUP_ALL_COMPLETED_SAME_PARENT_BUSINESS,
            Boolean::parseBoolean)
        .ifPresent(this::setTaskCleanupJobAllCompletedSameParentBusiness);

    if (LOGGER.isDebugEnabled()) {
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
  }

  private void initDomains(Properties props) {
    CheckedFunction<String, List<String>, Exception> parseFunction =
        p -> splitStringAndTrimElements(p, ",", String::toUpperCase);
    parseProperty(props, TASKANA_DOMAINS_PROPERTY, parseFunction).ifPresent(this::setDomains);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Configured domains: {}", domains);
    }
  }

  private void initClassificationTypes(Properties props) {
    CheckedFunction<String, List<String>, Exception> parseFunction =
        p -> splitStringAndTrimElements(p, ",", String::toUpperCase);
    parseProperty(props, TASKANA_CLASSIFICATION_TYPES_PROPERTY, parseFunction)
        .ifPresent(this::setClassificationTypes);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Configured classificationTypes: {}", classificationTypes);
    }
  }

  private void initClassificationCategories(Properties props) {
    Function<String, List<String>> getClassificationCategoriesForType =
        type -> {
          CheckedFunction<String, List<String>, Exception> parseFunction =
              s -> splitStringAndTrimElements(s, ",", String::toUpperCase);
          return parseProperty(
                  props,
                  TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY + "." + type.toLowerCase(),
                  parseFunction)
              .orElseGet(ArrayList::new);
        };

    classificationCategoriesByTypeMap =
        classificationTypes.stream()
            .map(type -> Pair.of(type, getClassificationCategoriesForType.apply(type)))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Configured classification categories : {}", classificationCategoriesByTypeMap);
    }
  }

  private void initBooleanProperty(
      Properties props, String propertyName, Consumer<Boolean> consumer) {
    parseProperty(props, propertyName, Boolean::parseBoolean).ifPresent(consumer);
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

  private void initTaskanaRoles(Properties props, String rolesSeparator) {
    Function<TaskanaRole, Set<String>> getAccessIdsForRole =
        role -> {
          List<String> accessIds =
              splitStringAndTrimElements(
                  props.getProperty(role.getPropertyName().toLowerCase(), ""),
                  rolesSeparator,
                  shouldUseLowerCaseForAccessIds()
                      ? String::toLowerCase
                      : UnaryOperator.identity());
          return new HashSet<>(accessIds);
        };

    roleMap =
        Arrays.stream(TaskanaRole.values())
            .map(role -> Pair.of(role, getAccessIdsForRole.apply(role)))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

    if (LOGGER.isDebugEnabled()) {
      roleMap.forEach((k, v) -> LOGGER.debug("Found Taskana RoleConfig {} : {} ", k, v));
    }
  }

  private void initCustomHolidays(Properties props, String separator) {
    CheckedFunction<String, List<CustomHoliday>, Exception> parseFunction =
        s ->
            splitStringAndTrimElements(s, separator).stream()
                .map(
                    str -> {
                      try {
                        return createCustomHolidayFromPropsEntry(str);
                      } catch (WrongCustomHolidayFormatException e) {
                        LOGGER.warn(e.getMessage());
                        return null;
                      }
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    parseProperty(props, TASKANA_CUSTOM_HOLIDAY, parseFunction).ifPresent(this::addCustomHolidays);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Configured custom Holidays : {}", customHolidays);
    }
  }

  private CustomHoliday createCustomHolidayFromPropsEntry(String customHolidayEntry)
      throws WrongCustomHolidayFormatException {
    List<String> parts =
        splitStringAndTrimElements(customHolidayEntry, TASKANA_CUSTOM_HOLIDAY_DAY_MONTH_SEPARATOR);
    if (parts.size() == 2) {
      return CustomHoliday.of(Integer.valueOf(parts.get(0)), Integer.valueOf(parts.get(1)));
    }
    throw new WrongCustomHolidayFormatException(customHolidayEntry);
  }

  private List<String> splitStringAndTrimElements(String str, String separator) {
    return splitStringAndTrimElements(str, separator, UnaryOperator.identity());
  }

  private List<String> splitStringAndTrimElements(
      String str, String separator, UnaryOperator<String> modifier) {
    return Arrays.stream(str.split(Pattern.quote(separator)))
        .filter(s -> !s.isEmpty())
        .map(String::trim)
        .map(modifier)
        .collect(Collectors.toList());
  }

  private Properties readPropertiesFromFile(String propertiesFile) {
    Properties props = new Properties();
    try (InputStream stream =
        FileLoaderUtil.openFileFromClasspathOrSystem(propertiesFile, getClass())) {
      props.load(stream);
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing properties file {}.", propertiesFile);
      throw new SystemException(
          "internal System error when processing properties file " + propertiesFile, e.getCause());
    }
    return props;
  }
}
