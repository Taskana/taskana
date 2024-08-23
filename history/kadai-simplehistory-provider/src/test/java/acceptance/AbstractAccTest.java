package acceptance;

import static io.kadai.common.test.OracleSchemaHelper.initOracleSchema;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.internal.JobMapper;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.common.test.config.DataSourceGenerator;
import io.kadai.sampledata.SampleDataGenerator;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryQueryMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.platform.commons.JUnitException;

/** Set up database for tests. */
public abstract class AbstractAccTest {

  protected static KadaiConfiguration kadaiConfiguration;
  protected static KadaiEngine kadaiEngine;
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
    KadaiConfiguration configuration =
        new KadaiConfiguration.Builder(dataSource, false, schemaNameTmp)
            .initKadaiProperties()
            .build();
    initKadaiEngine(configuration);

    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, configuration.getSchemaName());
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected static void initKadaiEngine(KadaiConfiguration configuration) throws SQLException {
    kadaiConfiguration = configuration;
    kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    taskService = kadaiEngine.getTaskService();
    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(kadaiEngine);
  }

  protected static SimpleHistoryServiceImpl getHistoryService() {
    return historyService;
  }

  protected static WorkbasketHistoryEventMapper getWorkbasketHistoryEventMapper() {
    try {
      Field sessionManager = KadaiEngineImpl.class.getDeclaredField("sessionManager");
      sessionManager.setAccessible(true);
      SqlSessionManager manager = (SqlSessionManager) sessionManager.get(kadaiEngine);
      return manager.getMapper(WorkbasketHistoryEventMapper.class);
    } catch (Exception e) {
      throw new JUnitException(
          String.format(
              "Could not extract %s from %s",
              WorkbasketHistoryEventMapper.class, KadaiEngineImpl.class));
    }
  }

  protected static ClassificationHistoryEventMapper getClassificationHistoryEventMapper() {
    try {
      Field sessionManager = KadaiEngineImpl.class.getDeclaredField("sessionManager");

      sessionManager.setAccessible(true);

      SqlSessionManager manager = (SqlSessionManager) sessionManager.get(kadaiEngine);
      return manager.getMapper(ClassificationHistoryEventMapper.class);

    } catch (Exception e) {
      throw new JUnitException(
          String.format(
              "Could not extract %s from %s",
              ClassificationHistoryEventMapper.class, KadaiEngineImpl.class));
    }
  }

  @BeforeAll
  static void setupTest() throws Exception {
    resetDb(null);
  }

  protected TaskHistoryQueryMapper getHistoryQueryMapper()
      throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = KadaiEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager = (SqlSessionManager) sessionManagerField.get(kadaiEngine);

    return sqlSessionManager.getMapper(TaskHistoryQueryMapper.class);
  }

  protected JobMapper getJobMapper() throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = KadaiEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager = (SqlSessionManager) sessionManagerField.get(kadaiEngine);

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
}
