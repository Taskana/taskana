package org.taskana.impl.integration;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.taskana.TaskanaEngine;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.impl.TaskServiceImpl;
import org.taskana.impl.TaskanaEngineImpl;
import org.taskana.model.Task;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Integration Test for TaskServiceImpl transactions.
 * @author EH
 */
public class TaskServiceImplTransactionTest {

    @Test
    public void testStart() throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/data/test-db-taskservice-int1");
        ds.setPassword("sa");
        ds.setUser("sa");
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, true, false);

        TaskanaEngineImpl te = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);
        te.closeSession();

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());

        Assert.assertNotNull(resultTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/data/test-db-taskservice-trans2");
        ds.setPassword("sa");
        ds.setUser("sa");
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);

        TaskanaEngineImpl te = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);
        taskServiceImpl.getTaskById("1");
        te.closeSession();

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById("1");
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(null, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }
}
