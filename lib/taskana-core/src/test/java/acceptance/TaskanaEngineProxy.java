package acceptance;

import java.lang.reflect.Field;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;

/** Utility class to enable unit tests to access mappers directly. */
public class TaskanaEngineProxy {

  private final InternalTaskanaEngine engine;

  public TaskanaEngineProxy(TaskanaEngine taskanaEngine) throws Exception {
    Field internal = TaskanaEngineImpl.class.getDeclaredField("internalTaskanaEngineImpl");
    internal.setAccessible(true);
    engine = (InternalTaskanaEngine) internal.get(taskanaEngine);
  }

  public InternalTaskanaEngine getEngine() {
    return engine;
  }

  public SqlSession getSqlSession() {
    return engine.getSqlSession();
  }

  public void openConnection() {
    engine.openConnection();
  }

  public void returnConnection() {
    engine.returnConnection();
  }
}
