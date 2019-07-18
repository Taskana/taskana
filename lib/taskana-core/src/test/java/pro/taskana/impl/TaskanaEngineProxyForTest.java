package pro.taskana.impl;

import java.lang.reflect.Field;

import org.apache.ibatis.session.SqlSession;

import pro.taskana.TaskanaEngine;

/**
 * Utility class to enable unit tests to access mappers directly.
 *
 * @author bbr
 */
public class TaskanaEngineProxyForTest {

    TaskanaEngine.Internal engine;

    public TaskanaEngineProxyForTest(TaskanaEngine taskanaEngine) throws NoSuchFieldException, IllegalAccessException {
        Field internal = TaskanaEngineImpl.class.getDeclaredField("internal");
        internal.setAccessible(true);
        engine = (TaskanaEngine.Internal) internal.get(taskanaEngine);
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
