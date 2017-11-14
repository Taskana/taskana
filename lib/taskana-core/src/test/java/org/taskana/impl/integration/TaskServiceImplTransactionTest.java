package org.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.taskana.TaskanaEngine;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.impl.TaskServiceImpl;
import org.taskana.impl.TaskanaEngineImpl;
import org.taskana.impl.configuration.DBCleaner;
import org.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import org.taskana.impl.persistence.ClassificationQueryImpl;
import org.taskana.impl.persistence.ObjectReferenceQueryImpl;
import org.taskana.impl.util.IdGenerator;
import org.taskana.model.Task;
import org.taskana.model.TaskState;
import org.taskana.persistence.ClassificationQuery;
import org.taskana.persistence.ObjectReferenceQuery;

/**
 * Integration Test for TaskServiceImpl transactions.
 * @author EH
 */
public class TaskServiceImplTransactionTest {

    private DataSource dataSource;
    private TaskServiceImpl taskServiceImpl;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, false);

        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource);
    }

    @Test
    public void testStart() throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {

        Task task = new Task();
        task.setName("Unit Test Task");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId(id1);
        task = taskServiceImpl.create(task);
        taskanaEngineImpl.getSession().commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());
        Assert.assertNotNull(resultTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {

        Task task = new Task();
        task.setName("Unit Test Task");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId("id1");
        task = taskServiceImpl.create(task);
        taskServiceImpl.getTaskById(id1);

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById(id1);
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException {
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(null, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId(id1);
        task = taskServiceImpl.create(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException {

        Task task = new Task();
        task.setName("Unit Test Task");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId(id1);
        task = taskServiceImpl.create(task);

        TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        ClassificationQuery classificationQuery = new ClassificationQueryImpl(taskanaEngineImpl).tenantId("asdasdasd")
                .parentClassification("pId1", "pId2").category("cat1", "cat2").type("oneType").name("1Name", "name2")
                .descriptionLike("my desc").priority(1, 2, 1).serviceLevel("me", "and", "you");

        ObjectReferenceQuery objectReferenceQuery = new ObjectReferenceQueryImpl(taskanaEngineImpl).tenantId("tenant1")
                .company("first comp", "sonstwo gmbh").system("sys").type("type1", "type2")
                .systemInstance("sysInst1", "sysInst2").value("val1", "val2", "val3");

        List<Task> results = taskServiceImpl.createTaskQuery().tenantId("1").name("bla", "test").descriptionLike("test")
                .priority(1, 2, 2).state(TaskState.CLAIMED).workbasketId("asd", "asdasdasd")
                .owner("test", "test2", "bla").customFields("test").classification(classificationQuery)
                .objectReference(objectReferenceQuery).list();

        Assert.assertEquals(0, results.size());

    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.closeSession();
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
