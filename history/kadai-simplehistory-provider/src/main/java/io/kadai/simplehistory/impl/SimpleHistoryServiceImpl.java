package io.kadai.simplehistory.impl;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryQuery;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryQueryMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryEventMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryQuery;
import io.kadai.simplehistory.impl.task.TaskHistoryQueryMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.exceptions.KadaiHistoryEventNotFoundException;
import io.kadai.user.api.models.User;
import io.kadai.user.internal.UserMapper;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This is the implementation of KadaiHistory. */
public class SimpleHistoryServiceImpl implements KadaiHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHistoryServiceImpl.class);
  private TaskHistoryEventMapper taskHistoryEventMapper;
  private WorkbasketHistoryEventMapper workbasketHistoryEventMapper;
  private ClassificationHistoryEventMapper classificationHistoryEventMapper;
  private UserMapper userMapper;

  private InternalKadaiEngine internalKadaiEngine;

  public void initialize(KadaiEngine kadaiEngine) {
    LOGGER.info(
        "Simple history service implementation initialized with schemaName: {} ",
        kadaiEngine.getConfiguration().getSchemaName());

    Field sessionManager = null;
    try {
      Field internalKadaiEngineImpl =
          KadaiEngineImpl.class.getDeclaredField("internalKadaiEngineImpl");
      internalKadaiEngineImpl.setAccessible(true);
      this.internalKadaiEngine =
          (InternalKadaiEngine) internalKadaiEngineImpl.get(kadaiEngine);
      sessionManager = KadaiEngineImpl.class.getDeclaredField("sessionManager");
      sessionManager.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new SystemException("SQL Session could not be retrieved. Aborting Startup");
    } catch (IllegalAccessException e) {
      throw new SystemException(e.getMessage());
    }
    try {
      SqlSession sqlSession = (SqlSession) sessionManager.get(kadaiEngine);
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
          "KADAI engine of Session Manager could not be retrieved. Aborting Startup");
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

    internalKadaiEngine.getEngine().checkRoleMembership(KadaiRole.ADMIN);

    if (taskIds == null) {
      throw new InvalidArgumentException("List of taskIds must not be null.");
    }

    try {
      internalKadaiEngine.openConnection();
      taskHistoryEventMapper.deleteMultipleByTaskIds(taskIds);
    } finally {

      internalKadaiEngine.returnConnection();
    }
  }

  public TaskHistoryEvent getTaskHistoryEvent(String historyEventId)
      throws KadaiHistoryEventNotFoundException {
    TaskHistoryEvent resultEvent = null;
    try {
      internalKadaiEngine.openConnection();
      resultEvent = taskHistoryEventMapper.findById(historyEventId);

      if (resultEvent == null) {
        throw new KadaiHistoryEventNotFoundException(historyEventId);
      }

      if (internalKadaiEngine.getEngine().getConfiguration().isAddAdditionalUserInfo()) {
        User user = userMapper.findById(resultEvent.getUserId());
        if (user != null) {
          resultEvent.setUserLongName(user.getLongName());
        }
      }
      return resultEvent;

    } finally {
      internalKadaiEngine.returnConnection();
    }
  }

  public TaskHistoryQuery createTaskHistoryQuery() {
    return new TaskHistoryQueryImpl(internalKadaiEngine);
  }

  public WorkbasketHistoryQuery createWorkbasketHistoryQuery() {
    return new WorkbasketHistoryQueryImpl(internalKadaiEngine);
  }

  public ClassificationHistoryQuery createClassificationHistoryQuery() {
    return new ClassificationHistoryQueryImpl(internalKadaiEngine);
  }
}
