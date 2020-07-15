package acceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.task.api.models.ObjectReference;

/** Set up database for tests. */
public abstract class AbstractAccTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAccTest.class);
  private static final String ID_PREFIX_HISTORY_EVENT = "HEI";
  private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
  private static final int POOL_TIME_TO_WAIT = 50;
  private static final DataSource DATA_SOURCE;
  protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected static TaskanaHistoryEngineImpl taskanaHistoryEngine;
  protected static TaskanaEngine taskanaEngine;
  private static SimpleHistoryServiceImpl historyService;
  private static String schemaName;

  static {
    String propertiesFileName = USER_HOME_DIRECTORY + "/taskanaUnitTest.properties";
    File f = new File(propertiesFileName);
    if (f.exists() && !f.isDirectory()) {
      DATA_SOURCE = createDataSourceFromProperties(propertiesFileName);
    } else {
      DATA_SOURCE = createDefaultDataSource();
    }
  }

  protected AbstractAccTest() {
    // not called
  }

  /**
   * create taskHistoryEvent object.
   *
   * @param workbasketKey the workbasketKey, the task currently resides in.
   * @param taskId the taskid the event belongs to.
   * @param type the type of the event.
   * @param previousWorkbasketId the workbasketId of the previous workbasket (if applicable).
   * @param userid the ID of the user that triggered the event.
   * @param details the details of the changes that happened.
   * @return Task History event object created.
   */
  public static TaskHistoryEvent createTaskHistoryEvent(
      String workbasketKey,
      String taskId,
      String type,
      String previousWorkbasketId,
      String userid,
      String details) {
    TaskHistoryEvent historyEvent = new TaskHistoryEvent();
    historyEvent.setId(IdGenerator.generateWithPrefix(ID_PREFIX_HISTORY_EVENT));
    historyEvent.setUserId(userid);
    historyEvent.setDetails(details);
    historyEvent.setWorkbasketKey(workbasketKey);
    historyEvent.setTaskId(taskId);
    historyEvent.setEventType(type);
    historyEvent.setOldValue(previousWorkbasketId);
    return historyEvent;
  }

  /**
   * create workbasketHistoryEvent object.
   *
   * @param workbasketKey the workbasketKey.
   * @param type the type of the event.
   * @param userid the ID of the user that triggered the event.
   * @param details the details of the changes that happened.
   * @return Workbasket History event object created.
   */
  public static WorkbasketHistoryEvent createWorkbasketHistoryEvent(
      String workbasketKey,
      String type,
      String userid,
      String details) {
    WorkbasketHistoryEvent historyEvent = new WorkbasketHistoryEvent();
    historyEvent.setId(IdGenerator.generateWithPrefix(ID_PREFIX_HISTORY_EVENT));
    historyEvent.setUserId(userid);
    historyEvent.setDetails(details);
    historyEvent.setWorkbasketKey(workbasketKey);
    historyEvent.setEventType(type);
    return historyEvent;
  }

  protected static void resetDb(String schemaName) throws Exception {
    DataSource dataSource = getDataSource();

    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(
            dataSource,
            false,
            schemaName != null && !schemaName.isEmpty() ? schemaName : getSchemaName());
    taskanaHistoryEngine = TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineConfiguration);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(taskanaEngineConfiguration.buildTaskanaEngine());

    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, getSchemaName());
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected static DataSource getDataSource() {
    if (DATA_SOURCE == null) {
      throw new RuntimeException("Datasource should be already initialized");
    }
    return DATA_SOURCE;
  }

  /**
   * returns the SchemaName used for Junit test. If the file {user.home}/taskanaUnitTest.properties
   * is present, the SchemaName is created according to the property schemaName. a sample properties
   * file for DB2 looks as follows: jdbcDriver=com.ibm.db2.jcc.DB2Driver
   * jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user dbPassword=db2password
   * schemaName=TASKANA If any of these properties is missing, or the file doesn't exist, the
   * default schemaName TASKANA is created used.
   *
   * @return String for unit test
   */
  protected static String getSchemaName() {
    if (schemaName == null) {
      String propertiesFileName = USER_HOME_DIRECTORY + "/taskanaUnitTest.properties";
      File f = new File(propertiesFileName);
      if (f.exists() && !f.isDirectory()) {
        schemaName = getSchemaNameFromPropertiesObject(propertiesFileName);
      } else {
        schemaName = "TASKANA";
      }
    }
    return schemaName;
  }

  protected static SimpleHistoryServiceImpl getHistoryService() {
    return historyService;
  }

  protected TaskHistoryQueryMapper getHistoryQueryMapper()
      throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = TaskanaHistoryEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaHistoryEngine);

    return sqlSessionManager.getMapper(TaskHistoryQueryMapper.class);
  }

  protected ObjectReference createObjectRef(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReference objectRef = new ObjectReference();
    objectRef.setCompany(company);
    objectRef.setSystem(system);
    objectRef.setSystemInstance(systemInstance);
    objectRef.setType(type);
    objectRef.setValue(value);
    return objectRef;
  }

  @BeforeAll
  static void setupTest() throws Exception {
    resetDb(null);
  }

  /**
   * create Default DataSource for in-memory database.
   *
   * @return the TASKANA default datasource.
   */
  private static DataSource createDefaultDataSource() {

    String jdbcDriver = "org.h2.Driver";
    String jdbcUrl = "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0";
    String dbUserName = "sa";
    String dbPassword = "sa";
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            jdbcDriver,
            jdbcUrl,
            dbUserName,
            dbPassword);
    ds.setPoolTimeToWait(POOL_TIME_TO_WAIT);
    ds.forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly

    return ds;
  }

  /**
   * create data source from properties file.
   *
   * @param propertiesFileName the name of the properties file.
   * @return the datasource constructed from the information in the properties file.
   */
  private static DataSource createDataSourceFromProperties(String propertiesFileName) {
    DataSource ds = null;
    try (InputStream input = new FileInputStream(propertiesFileName)) {
      Properties prop = new Properties();
      prop.load(input);
      boolean propertiesFileIsComplete = true;
      String warningMessage = "";
      String jdbcDriver = prop.getProperty("jdbcDriver");
      if (jdbcDriver == null || jdbcDriver.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", jdbcDriver property missing";
      }
      String jdbcUrl = prop.getProperty("jdbcUrl");
      if (jdbcUrl == null || jdbcUrl.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", jdbcUrl property missing";
      }
      String dbUserName = prop.getProperty("dbUserName");
      if (dbUserName == null || dbUserName.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", dbUserName property missing";
      }
      String dbPassword = prop.getProperty("dbPassword");
      if (dbPassword == null || dbPassword.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", dbPassword property missing";
      }

      if (propertiesFileIsComplete) {
        ds =
            new PooledDataSource(
                Thread.currentThread().getContextClassLoader(),
                jdbcDriver,
                jdbcUrl,
                dbUserName,
                dbPassword);
        ((PooledDataSource) ds)
            .forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly
      } else {
        LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
        LOGGER.warn("Using default Datasource for Test");
      }

    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
      LOGGER.warn("Using default Datasource for Test");
    }
    if (Objects.isNull(ds)) {
      ds = createDefaultDataSource();
    }
    return ds;
  }

  private static String getSchemaNameFromPropertiesObject(String propertiesFileName) {
    String schemaName = "TASKANA";
    try (InputStream input = new FileInputStream(propertiesFileName)) {
      Properties prop = new Properties();
      prop.load(input);
      boolean propertiesFileIsComplete = true;
      String warningMessage = "";
      schemaName = prop.getProperty("schemaName");
      if (schemaName == null || schemaName.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", schemaName property missing";
      }

      if (!propertiesFileIsComplete) {
        LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        schemaName = "TASKANA";
      }

    } catch (FileNotFoundException e) {
      LOGGER.warn("getSchemaNameFromPropertiesObject caught Exception " + e);
      LOGGER.warn("Using default schemaName for Test");
    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
      LOGGER.warn("Using default Datasource for Test");
    }

    return schemaName;
  }
}
