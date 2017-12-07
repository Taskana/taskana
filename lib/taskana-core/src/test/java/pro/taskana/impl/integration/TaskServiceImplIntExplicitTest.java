package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.ClassificationQuery;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.*;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.Classification;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.Workbasket;
import pro.taskana.security.GroupPrincipal;
import pro.taskana.security.UserPrincipal;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode EXPLICIT.
 * @author EH
 */
public class TaskServiceImplIntExplicitTest {

    private DataSource dataSource;
    private TaskServiceImpl taskServiceImpl;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private Subject subject;

    @BeforeClass
    public static void resetDb() throws SQLException {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);

        subject = new Subject();
        List<Principal> principalList = new ArrayList<>();
        principalList.add(new UserPrincipal("Elena"));
        principalList.add(new GroupPrincipal("group1"));
        principalList.add(new GroupPrincipal("group2"));
        principalList.add(new GroupPrincipal("group3"));
        subject.getPrincipals().addAll(principalList);

        try {
            Connection connection = dataSource.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.runScript(
                    new InputStreamReader(this.getClass().getResourceAsStream("/sql/workbasket-access-list.sql")));

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testTaskService() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws TaskNotFoundException, WorkbasketNotFoundException, FileNotFoundException, NotAuthorizedException, SQLException, ClassificationNotFoundException {
                    do_testTaskService();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw e.getCause();
        }
    }

    public void do_testTaskService() throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Task task = this.generateDummyTask();
        task = taskServiceImpl.createTask(task);
        connection.commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());
        Assert.assertNotNull(resultTask);
        connection.commit();
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail() throws TaskNotFoundException {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws TaskNotFoundException, FileNotFoundException, NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    do_testStartTransactionFail();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                Assert.assertTrue(cause instanceof TaskNotFoundException);
                throw (TaskNotFoundException) cause;
            }
        }
    }

    public void do_testStartTransactionFail()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
//        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        Classification classification = new Classification();
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngine.getClassificationService().addClassification(classification);

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);
        connection.commit();
        taskServiceImpl.getTaskById(workbasket.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById(workbasket.getId());
        connection.commit();
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws TaskNotFoundException, WorkbasketNotFoundException, FileNotFoundException, NotAuthorizedException, SQLException, ClassificationNotFoundException {
                    do_testCreateTaskInTaskanaWithDefaultDb();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw e.getCause();
        }
    }

    public void do_testCreateTaskInTaskanaWithDefaultDb()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();
        WorkbasketServiceImpl workbasketServiceImpl = (WorkbasketServiceImpl) te.getWorkbasketService();
        ClassificationServiceImpl classificationServiceImpl = (ClassificationServiceImpl) te.getClassificationService();

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        Classification classification = new Classification();
        workbasketServiceImpl.createWorkbasket(workbasket);
        classificationServiceImpl.addClassification(classification);

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        connection.commit();
        te.setConnection(null);
    }

    @Test
    public void testCreateTaskWithPlannedAndName() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException, SQLException {
                    do_testCreateTaskWithPlannedAndName();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw e.getCause();
        }
    }

    public void do_testCreateTaskWithPlannedAndName() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Classification classification = new Classification();
        classification.setCategory("MANUAL");
        classification.setName("classification name");
        classification.setServiceLevel("P1D");
        taskanaEngine.getClassificationService().addClassification(classification);

        ObjectReference objectReference = new ObjectReference();
        objectReference.setCompany("Novatec");
        objectReference.setSystem("System");
        objectReference.setSystemInstance("2");
        objectReference.setValue("4444");
        objectReference.setType("type");

        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        Task test = this.generateDummyTask();
        test.setClassification(classification);
        test.setName("Name");
        test.setPrimaryObjRef(objectReference);
        test.setPlanned(tomorrow);
        test = taskServiceImpl.createTask(test);

        Assert.assertNotEquals(test.getPlanned(), test.getCreated());
        Assert.assertNotNull(test.getDue());

        Task test2 = new Task();
        test2.setWorkbasketId(test.getWorkbasketId());
        test2.setClassification(classification);
        test2.setPrimaryObjRef(objectReference);
        test2.setDescription("desc");
        taskServiceImpl.createTask(test2);

        Assert.assertEquals(test2.getPlanned(), test2.getCreated());
        Assert.assertTrue(test2.getName().equals(classification.getName()));

        Assert.assertEquals(test.getClassification().getId(), test2.getClassification().getId());
        Assert.assertTrue(test.getDue().after(test2.getDue()));
        Assert.assertFalse(test.getName().equals(test2.getName()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void createTaskShouldThrowWorkbasketNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    do_createTaskShouldThrowWorkbasketNotFoundException();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                Assert.assertTrue(cause instanceof WorkbasketNotFoundException);
                throw (WorkbasketNotFoundException) cause;
            }
        }
    }

    public void do_createTaskShouldThrowWorkbasketNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Task test = this.generateDummyTask();
        test.setWorkbasketId("2");
        taskServiceImpl.createTask(test);
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void createManualTaskShouldThrowClassificationNotFoundException() throws ClassificationNotFoundException  {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    do_createManualTaskShouldThrowClassificationNotFoundException();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                Assert.assertTrue(cause instanceof ClassificationNotFoundException);
                throw (ClassificationNotFoundException) cause;
            }
        }
    }

    public void do_createManualTaskShouldThrowClassificationNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Task test = this.generateDummyTask();
        test.setClassification(new Classification());
        taskServiceImpl.createTask(test);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, ClassificationNotFoundException {
                    do_should_ReturnList_when_BuilderIsUsed();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw e.getCause();
        }
    }

    public void do_should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {

        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        Classification classification = new Classification();
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngine.getClassificationService().addClassification(classification);

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        ClassificationQuery classificationQuery = new ClassificationQueryImpl(taskanaEngineImpl)
                .parentClassification("pId1", "pId2").category("cat1", "cat2").type("oneType").name("1Name", "name2")
                .descriptionLike("my desc").priority(1, 2, 1).serviceLevel("me", "and", "you");

        ObjectReferenceQuery objectReferenceQuery = new ObjectReferenceQueryImpl(taskanaEngineImpl)
                .company("first comp", "sonstwo gmbh").system("sys").type("type1", "type2")
                .systemInstance("sysInst1", "sysInst2").value("val1", "val2", "val3");

        List<Task> results = taskServiceImpl.createTaskQuery().name("bla", "test").descriptionLike("test")
                .priority(1, 2, 2).state(TaskState.CLAIMED).workbasketId("1", "2")
                .owner("test", "test2", "bla").customFields("test").classification(classificationQuery)
                .objectReference(objectReferenceQuery).list();

        Assert.assertEquals(0, results.size());
        connection.commit();
    }

    private Task generateDummyTask() {
        Workbasket workbasket = new Workbasket();
        workbasket.setName("wb");
        workbasket.setId("1"); // set id manually for authorization tests
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = new Classification();
        taskanaEngine.getClassificationService().addClassification(classification);

        Task task = new Task();
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        return task;
    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
