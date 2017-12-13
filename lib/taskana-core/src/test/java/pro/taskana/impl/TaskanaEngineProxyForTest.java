package pro.taskana.impl;

import org.apache.ibatis.session.SqlSession;

/**
 * Utility class to enable unit tests to access mappers directly.
 *
 * @author bbr
 */
public class TaskanaEngineProxyForTest {

    TaskanaEngineImpl engine;

    public TaskanaEngineProxyForTest(TaskanaEngineImpl taskanaEngine) {
        engine = taskanaEngine;
    }

    public SqlSession getSqlSession() {
        return engine.sessionManager;
    }

    public void openConnection() {
        engine.openConnection();
    }

    public void returnConnection() {
        engine.returnConnection();
    }

}
