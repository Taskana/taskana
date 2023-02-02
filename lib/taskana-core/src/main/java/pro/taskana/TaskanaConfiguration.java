package pro.taskana;

import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureAnnotatedFields;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureClassificationCategoriesForType;
import static pro.taskana.common.internal.configuration.TaskanaConfigurationInitializer.configureRoles;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
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
 * Security is enabled by default. <br>
 * All members are immutable, also Lists and Maps and Sets.
 */
public class TaskanaConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaConfiguration.class);

  private final DataSource dataSource;
  private final String schemaName;
  private final Map<String, String> properties;

  // Taskana role configuration
  private final Map<TaskanaRole, Set<String>> roleMap;

  private final boolean securityEnabled;

  private final boolean useManagedTransactions;

  private final List<String> domains;
  private final List<String> classificationTypes;

  private final Map<String, List<String>> classificationCategoriesByTypeMap;

  private final List<CustomHoliday> customHolidays;
  // Properties for the monitor
  private final boolean deleteHistoryOnTaskDeletionEnabled;

  private final boolean germanPublicHolidaysEnabled;

  private final boolean corpusChristiEnabled;

  private final int jobBatchSize;

  private final int maxNumberOfJobRetries;

  private final Instant cleanupJobFirstRun;

  private final Duration cleanupJobRunEvery;

  private final Duration cleanupJobMinimumAge;

  private final boolean taskCleanupJobAllCompletedSameParentBusiness;

  private final boolean validationAllowTimestampServiceLevelMismatch;
  private final boolean addAdditionalUserInfo;

  private final int priorityJobBatchSize;

  private final Instant priorityJobFirstRun;

  private final Duration priorityJobRunEvery;

  private final boolean priorityJobActive;

  private final Duration userRefreshJobRunEvery;

  private final Instant userRefreshJobFirstRun;

  private final List<WorkbasketPermission> minimalPermissionsToAssignDomains;

  protected TaskanaConfiguration(Builder builder) {

    this.dataSource = builder.dataSource;
    this.schemaName = builder.schemaName;

    this.properties = Collections.unmodifiableMap(builder.properties);

    this.roleMap =
        builder.roleMap.entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));

    this.securityEnabled = builder.securityEnabled;
    this.useManagedTransactions = builder.useManagedTransactions;
    this.domains = Collections.unmodifiableList(builder.domains);
    this.classificationTypes = Collections.unmodifiableList(builder.classificationTypes);

    this.classificationCategoriesByTypeMap =
        builder.classificationCategoriesByTypeMap.entrySet().stream()
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
    this.validationAllowTimestampServiceLevelMismatch =
        builder.validationAllowTimestampServiceLevelMismatch;
    this.addAdditionalUserInfo = builder.addAdditionalUserInfo;
    this.priorityJobBatchSize = builder.priorityJobBatchSize;
    this.priorityJobFirstRun = builder.priorityJobFirstRun;
    this.priorityJobRunEvery = builder.priorityJobRunEvery;
    this.priorityJobActive = builder.priorityJobActive;
    this.userRefreshJobRunEvery = builder.userRefreshJobRunEvery;
    this.userRefreshJobFirstRun = builder.userRefreshJobFirstRun;
    this.minimalPermissionsToAssignDomains =
        Collections.unmodifiableList(builder.minimalPermissionsToAssignDomains);

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

  public int getMaxNumberOfUpdatesPerTransaction() {
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

  public boolean isValidationAllowTimestampServiceLevelMismatch() {
    return validationAllowTimestampServiceLevelMismatch;
  }

  public boolean isDeleteHistoryOnTaskDeletionEnabled() {
    return deleteHistoryOnTaskDeletionEnabled;
  }

  public List<CustomHoliday> getCustomHolidays() {
    return customHolidays;
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

    private final DataSource dataSource;
    private final boolean securityEnabled;
    private final boolean useManagedTransactions;
    private String schemaName;
    private Map<String, String> properties;

    private Map<TaskanaRole, Set<String>> roleMap = new EnumMap<>(TaskanaRole.class);

    // List of configured domain names
    @TaskanaProperty("taskana.domains")
    private List<String> domains = new ArrayList<>();

    @TaskanaProperty("taskana.classification.types")
    private List<String> classificationTypes = new ArrayList<>();

    private Map<String, List<String>> classificationCategoriesByTypeMap = new HashMap<>();

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
    private List<WorkbasketPermission> minimalPermissionsToAssignDomains = new ArrayList<>();

    public Builder(TaskanaConfiguration tec) {
      this.dataSource = tec.getDatasource();
      this.schemaName = tec.getSchemaName();
      this.properties = tec.getProperties();
      this.roleMap = tec.getRoleMap();
      this.securityEnabled = tec.isSecurityEnabled();
      this.useManagedTransactions = tec.isUseManagedTransactions();
      this.domains = tec.getDomains();
      this.classificationTypes = tec.getClassificationTypes();
      this.classificationCategoriesByTypeMap = tec.getClassificationCategoriesByTypeMap();
      this.customHolidays = tec.getCustomHolidays();
      this.deleteHistoryOnTaskDeletionEnabled = tec.isDeleteHistoryOnTaskDeletionEnabled();
      this.germanPublicHolidaysEnabled = tec.isGermanPublicHolidaysEnabled();
      this.corpusChristiEnabled = tec.isCorpusChristiEnabled();
      this.jobBatchSize = tec.getMaxNumberOfUpdatesPerTransaction();
      this.maxNumberOfJobRetries = tec.getMaxNumberOfJobRetries();
      this.cleanupJobFirstRun = tec.getCleanupJobFirstRun();
      this.cleanupJobRunEvery = tec.getCleanupJobRunEvery();
      this.cleanupJobMinimumAge = tec.getCleanupJobMinimumAge();
      this.taskCleanupJobAllCompletedSameParentBusiness =
          tec.isTaskCleanupJobAllCompletedSameParentBusiness();
      this.validationAllowTimestampServiceLevelMismatch =
          tec.isValidationAllowTimestampServiceLevelMismatch();
      this.addAdditionalUserInfo = tec.isAddAdditionalUserInfo();
      this.priorityJobBatchSize = tec.getPriorityJobBatchSize();
      this.priorityJobFirstRun = tec.getPriorityJobFirstRun();
      this.priorityJobRunEvery = tec.getPriorityJobRunEvery();
      this.priorityJobActive = tec.isPriorityJobActive();
      this.userRefreshJobRunEvery = tec.getUserRefreshJobRunEvery();
      this.userRefreshJobFirstRun = tec.getUserRefreshJobFirstRun();
      this.minimalPermissionsToAssignDomains = tec.getMinimalPermissionsToAssignDomains();
    }

    public Builder(DataSource dataSource, boolean useManagedTransactions, String schemaName) {
      this(dataSource, useManagedTransactions, schemaName, true);
    }

    public Builder(
        DataSource dataSource,
        boolean useManagedTransactions,
        String schemaName,
        boolean securityEnabled) {
      this(
          dataSource,
          useManagedTransactions,
          schemaName,
          securityEnabled,
          TASKANA_PROPERTIES,
          TASKANA_PROPERTY_SEPARATOR);
    }

    public Builder(
        DataSource dataSource,
        boolean useManagedTransactions,
        String schemaName,
        boolean securityEnabled,
        String propertiesFileName,
        String propertySeparator) {
      this.useManagedTransactions = useManagedTransactions;
      this.securityEnabled = securityEnabled;

      if (dataSource != null) {
        this.dataSource = dataSource;
      } else {
        throw new SystemException("DataSource can't be null");
      }

      this.schemaName = initSchemaName(schemaName);

      this.initTaskanaProperties(propertiesFileName, propertySeparator);
    }

    @SuppressWarnings("unused")
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
      this.classificationCategoriesByTypeMap = classificationCategoriesByTypeMap;
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
    public Builder validationAllowTimestampServiceLevelMismatch(
        boolean validationAllowTimestampServiceLevelMismatch) {
      this.validationAllowTimestampServiceLevelMismatch =
          validationAllowTimestampServiceLevelMismatch;
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

    public TaskanaConfiguration build() {
      return new TaskanaConfiguration(this);
    }

    @SuppressWarnings("unused")
    public Builder initTaskanaProperties() {
      return this.initTaskanaProperties(TASKANA_PROPERTIES, TASKANA_PROPERTY_SEPARATOR);
    }

    @SuppressWarnings("unused")
    public Builder initTaskanaProperties(String propertiesFile) {
      return this.initTaskanaProperties(propertiesFile, TASKANA_PROPERTY_SEPARATOR);
    }

    public Builder initTaskanaProperties(String propertiesFile, String separator) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Reading taskana configuration from {} with separator {}", propertiesFile, separator);
      }
      loadProperties(propertiesFile);
      configureAnnotatedFields(this, separator, properties);
      roleMap = configureRoles(separator, properties, shouldUseLowerCaseForAccessIds());
      classificationCategoriesByTypeMap =
          configureClassificationCategoriesForType(properties, classificationTypes);
      return this;
    }

    private String initSchemaName(String schemaName) {
      String schemaNameTmp;
      if (schemaName != null && !schemaName.isEmpty()) {
        schemaNameTmp = schemaName;
      } else {
        throw new SystemException("SchemaName can't be null or empty");
      }

      try (Connection connection = dataSource.getConnection()) {
        String databaseProductId = DB.getDatabaseProductId(connection);
        if (DB.isPostgres(databaseProductId)) {
          schemaNameTmp = schemaNameTmp.toLowerCase();
        } else {
          schemaNameTmp = schemaNameTmp.toUpperCase();
        }
      } catch (SQLException ex) {
        LOGGER.error("Caught exception when attempting to initialize the schema name", ex);
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Using schema name {}", schemaNameTmp);
      }
      return schemaNameTmp;
    }

    private void loadProperties(String propertiesFile) {
      Properties props = new Properties();
      try (InputStream stream =
          FileLoaderUtil.openFileFromClasspathOrSystem(
              propertiesFile, TaskanaConfiguration.class)) {
        props.load(stream);
      } catch (IOException e) {
        throw new SystemException(
            "internal System error when processing properties file " + propertiesFile, e);
      }
      this.properties =
          props.entrySet().stream()
              .collect(
                  Collectors.toUnmodifiableMap(
                      e -> e.getKey().toString(), e -> e.getValue().toString()));
    }
  }
}
