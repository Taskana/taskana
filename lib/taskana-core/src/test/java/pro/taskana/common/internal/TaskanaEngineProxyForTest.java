package pro.taskana.common.internal;

import java.lang.reflect.Field;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.common.api.TaskanaEngine;

/**
 * Utility class to enable unit tests to access mappers directly.
 *
 * @author bbr
 */
public class TaskanaEngineProxyForTest {

  private InternalTaskanaEngine engine;

  public TaskanaEngineProxyForTest(TaskanaEngine taskanaEngine)
      throws NoSuchFieldException, IllegalAccessException {
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
