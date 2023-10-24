package pro.taskana.simplehistory.impl;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQuery;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQueryMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryEventMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.UserMapper;

/** This is the implementation of TaskanaHistory. */
public class SimpleHistoryServiceImpl implements TaskanaHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHistoryServiceImpl.class);
  private TaskHistoryEventMapper taskHistoryEventMapper;
  private WorkbasketHistoryEventMapper workbasketHistoryEventMapper;
  private ClassificationHistoryEventMapper classificationHistoryEventMapper;
  private UserMapper userMapper;

  private InternalTaskanaEngine internalTaskanaEngine;

  public void initialize(TaskanaEngine taskanaEngine) {

    LOGGER.info(
        "Simple history service implementation initialized with schemaName: {} ",
        taskanaEngine.getConfiguration().getSchemaName());

    Field sessionManager = null;
    try {
      Field internalTaskanaEngineImpl =
          TaskanaEngineImpl.class.getDeclaredField("internalTaskanaEngineImpl");
      internalTaskanaEngineImpl.setAccessible(true);
      this.internalTaskanaEngine =
          (InternalTaskanaEngine) internalTaskanaEngineImpl.get(taskanaEngine);
      sessionManager = TaskanaEngineImpl.class.getDeclaredField("sessionManager");
      sessionManager.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new SystemException("SQL Session could not be retrieved. Aborting Startup");
    } catch (IllegalAccessException e) {
      throw new SystemException(e.getMessage());
    }
    try {
      SqlSession sqlSession = (SqlSession) sessionManager.get(taskanaEngine);
      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(TaskHistoryEventMapper.class)) {
        sqlSession.getConfiguration().addMapper(TaskHistoryEventMapper.class);
      }
      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(WorkbasketHistoryEventMapper.class)) {

        sqlSession.getConfiguration().addMapper(WorkbasketHistoryEventMapper.class);
      }
      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(ClassificationHistoryEventMapper.class)) {

        sqlSession.getConfiguration().addMapper(ClassificationHistoryEventMapper.class);
      }

      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(ClassificationHistoryQueryMapper.class)) {

        sqlSession.getConfiguration().addMapper(ClassificationHistoryQueryMapper.class);
      }

      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(TaskHistoryQueryMapper.class)) {

        sqlSession.getConfiguration().addMapper(TaskHistoryQueryMapper.class);
      }

      if (!sqlSession
          .getConfiguration()
          .getMapperRegistry()
          .hasMapper(WorkbasketHistoryQueryMapper.class)) {

        sqlSession.getConfiguration().addMapper(WorkbasketHistoryQueryMapper.class);
      }

      this.taskHistoryEventMapper = sqlSession.getMapper(TaskHistoryEventMapper.class);
      this.workbasketHistoryEventMapper = sqlSession.getMapper(WorkbasketHistoryEventMapper.class);
      this.classificationHistoryEventMapper =
          sqlSession.getMapper(ClassificationHistoryEventMapper.class);
      this.userMapper = sqlSession.getMapper(UserMapper.class);
    } catch (IllegalAccessException e) {
      throw new SystemException(
          "TASKANA engine of Session Manager could not be retrieved. Aborting Startup");
    }
  }

  @Override
  public void create(TaskHistoryEvent event) {

    if (event.getCreated() == null) {
      Instant now = Instant.now();
      event.setCreated(now);
    }
    taskHistoryEventMapper.insert(event);
  }

  @Override
  public void create(WorkbasketHistoryEvent event) {

    if (event.getCreated() == null) {
      Instant now = Instant.now();
      event.setCreated(now);
    }
    workbasketHistoryEventMapper.insert(event);
  }

  @Override
  public void create(ClassificationHistoryEvent event) {

    if (event.getCreated() == null) {
      Instant now = Instant.now();
      event.setCreated(now);
    }
    classificationHistoryEventMapper.insert(event);
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException {

    internalTaskanaEngine.openConnection();
    internalTaskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN);

    if (taskIds == null) {
      throw new InvalidArgumentException("List of taskIds must not be null.");
    }

    taskHistoryEventMapper.deleteMultipleByTaskIds(taskIds);

    internalTaskanaEngine.returnConnection();
  }

  public TaskHistoryEvent getTaskHistoryEvent(String historyEventId)
      throws TaskanaHistoryEventNotFoundException {
    TaskHistoryEvent resultEvent = null;
    try {
      internalTaskanaEngine.openConnection();
      resultEvent = taskHistoryEventMapper.findById(historyEventId);

      if (resultEvent == null) {
        throw new TaskanaHistoryEventNotFoundException(historyEventId);
      }

      if (internalTaskanaEngine.getEngine().getConfiguration().isAddAdditionalUserInfo()) {
        User user = userMapper.findById(resultEvent.getUserId());
        if (user != null) {
          resultEvent.setUserLongName(user.getLongName());
        }
      }
      return resultEvent;

    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  public TaskHistoryQuery createTaskHistoryQuery() {
    return new TaskHistoryQueryImpl(internalTaskanaEngine);
  }

  public WorkbasketHistoryQuery createWorkbasketHistoryQuery() {
    return new WorkbasketHistoryQueryImpl(internalTaskanaEngine);
  }

  public ClassificationHistoryQuery createClassificationHistoryQuery() {
    return new ClassificationHistoryQueryImpl(internalTaskanaEngine);
  }
}
