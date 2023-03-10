package acceptance;

import static pro.taskana.common.test.OracleSchemaHelper.initOracleSchema;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.platform.commons.JUnitException;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

/** Set up database for tests. */
public abstract class AbstractAccTest {

  protected static TaskanaConfiguration taskanaConfiguration;
  protected static TaskanaHistoryEngineImpl taskanaHistoryEngine;
  protected static TaskanaEngine taskanaEngine;
  protected static SimpleHistoryServiceImpl historyService;

  protected static TaskService taskService;

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
    historyEvent.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT));
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
      String workbasketKey, String type, String userid, String details) {
    WorkbasketHistoryEvent historyEvent = new WorkbasketHistoryEvent();
    historyEvent.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT));
    historyEvent.setUserId(userid);
    historyEvent.setDetails(details);
    historyEvent.setKey(workbasketKey);
    historyEvent.setEventType(type);
    return historyEvent;
  }

  protected static void resetDb(String schemaName) throws Exception {
    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaNameTmp =
        schemaName != null && !schemaName.isEmpty()
            ? schemaName
            : DataSourceGenerator.getSchemaName();
    try (Connection connection = dataSource.getConnection()) {
      DB db = DB.getDB(connection);
      if (DB.ORACLE == db) {
        initOracleSchema(dataSource, schemaNameTmp);
      }
    }
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(dataSource, false, schemaNameTmp)
            .initTaskanaProperties()
            .build();
    initTaskanaEngine(configuration);

    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, configuration.getSchemaName());
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected static void initTaskanaEngine(TaskanaConfiguration configuration) throws SQLException {
    taskanaConfiguration = configuration;
    taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    taskanaHistoryEngine = TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngine);
    taskService = taskanaEngine.getTaskService();
    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(taskanaEngine);
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

  protected JobMapper getJobMapper() throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = TaskanaEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaEngine);

    return sqlSessionManager.getMapper(JobMapper.class);
  }

  protected ObjectReference createObjectRef(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectRef = new ObjectReferenceImpl();
    objectRef.setCompany(company);
    objectRef.setSystem(system);
    objectRef.setSystemInstance(systemInstance);
    objectRef.setType(type);
    objectRef.setValue(value);
    return objectRef;
  }

  protected static WorkbasketHistoryEventMapper getWorkbasketHistoryEventMapper() {
    try {
      Field sessionManager = TaskanaHistoryEngineImpl.class.getDeclaredField("sessionManager");
      sessionManager.setAccessible(true);
      SqlSessionManager manager = (SqlSessionManager) sessionManager.get(taskanaHistoryEngine);
      return manager.getMapper(WorkbasketHistoryEventMapper.class);
    } catch (Exception e) {
      throw new JUnitException(
          String.format(
              "Could not extract %s from %s",
              WorkbasketHistoryEventMapper.class, TaskanaHistoryEngineImpl.class));
    }
  }

  protected static ClassificationHistoryEventMapper getClassificationHistoryEventMapper() {
    try {
      Field sessionManager = TaskanaHistoryEngineImpl.class.getDeclaredField("sessionManager");

      sessionManager.setAccessible(true);

      SqlSessionManager manager = (SqlSessionManager) sessionManager.get(taskanaHistoryEngine);
      return manager.getMapper(ClassificationHistoryEventMapper.class);

    } catch (Exception e) {
      throw new JUnitException(
          String.format(
              "Could not extract %s from %s",
              ClassificationHistoryEventMapper.class, TaskanaHistoryEngineImpl.class));
    }
  }

  @BeforeAll
  static void setupTest() throws Exception {
    resetDb(null);
  }
}
