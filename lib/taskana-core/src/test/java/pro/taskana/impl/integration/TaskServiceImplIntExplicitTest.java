package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import pro.taskana.impl.ClassificationQueryImpl;
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.Classification;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.Workbasket;
import pro.taskana.security.SamplePrincipal;

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
        SamplePrincipal samplePrincipal = new SamplePrincipal("Elena");
        List<String> groups = new ArrayList<String>();
        groups.add("group1");
        groups.add("group2");
        groups.add("group3");
        samplePrincipal.setGroups(groups);
        subject.getPrincipals().add(samplePrincipal);
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
    public void testStart() {
        Subject.doAs(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    do_testStart();
                } catch (TaskNotFoundException | FileNotFoundException | NotAuthorizedException | SQLException | WorkbasketNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void do_testStart() throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);
        connection.commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());
        Assert.assertNotNull(resultTask);
        connection.commit();
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws TaskNotFoundException, FileNotFoundException, NotAuthorizedException, SQLException, WorkbasketNotFoundException {
                    do_testStartTransactionFail();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
        }
    }

    public void do_testStartTransactionFail()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
//        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);
        connection.commit();
        taskServiceImpl.getTaskById(id1);

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById(id1);
        connection.commit();
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb() {
        Subject.doAs(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    do_testCreateTaskInTaskanaWithDefaultDb();
                } catch (TaskNotFoundException | FileNotFoundException | NotAuthorizedException | SQLException | WorkbasketNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void do_testCreateTaskInTaskanaWithDefaultDb()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Task task = new Task();
        task.setName("Unit Test Task");
        //String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        connection.commit();
        te.setConnection(null);
    }

    @Test
    public void testCreateManualTask() {
        Subject.doAs(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    do_testCreateManualTask();
                } catch (NotAuthorizedException | SQLException | WorkbasketNotFoundException | ClassificationNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void do_testCreateManualTask() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket1");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = new Classification();
        classification.setDomain("domain");
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

        Task test = taskServiceImpl.createManualTask(workbasket.getId(), classification.getId(), "domain", null, "Name", null, objectReference, null);

        Assert.assertEquals(test.getPlanned(), test.getCreated());
        Assert.assertNotNull(test.getDue());

        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        Map<String, Object> customs = new HashMap<String, Object>();
        customs.put("Daimler", "Tons of money. And cars. And gold.");
        customs.put("Audi", 2);

        Task test2 = taskServiceImpl.createManualTask(workbasket.getId(), classification.getId(), "domain", tomorrow, "Name2", "desc", objectReference, customs);

        Assert.assertEquals(test.getClassification().getId(), test2.getClassification().getId());
        Assert.assertTrue(test.getDue().before(test2.getDue()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void createManualTaskShouldThrowWorkbasketNotFoundException() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    do_createManualTaskShouldThrowWorkbasketNotFoundException();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
        }
    }

    public void do_createManualTaskShouldThrowWorkbasketNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket workbasket = new Workbasket();
        workbasket.setName("wb");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        taskServiceImpl.createManualTask("1", "classification", "domain", null, null, null, null, null);
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void createManualTaskShouldThrowClassificationNotFoundException() throws Throwable {
        try {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                public Object run() throws NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    do_createManualTaskShouldThrowClassificationNotFoundException();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
        }
    }

    public void do_createManualTaskShouldThrowClassificationNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket workbasket = new Workbasket();
        workbasket.setName("wb");
        workbasket.setId("1");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = new Classification();
        taskanaEngine.getClassificationService().addClassification(classification);

        taskServiceImpl.createManualTask(workbasket.getId(), "classification", "domain", null, null, null, null, null);
    }

    @Test(expected = NotAuthorizedException.class)
    public void createManualTaskShouldThrowNotAuthorizedException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket workbasket = new Workbasket();
        workbasket.setName("wb");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = new Classification();
        taskanaEngine.getClassificationService().addClassification(classification);

        taskServiceImpl.createManualTask(workbasket.getId(), classification.getId(), "domain", null, null, null, null, null);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        Subject.doAs(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    do_should_ReturnList_when_BuilderIsUsed();
                } catch (NotAuthorizedException | SQLException | WorkbasketNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void do_should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException {

        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Task task = new Task();
        task.setName("Unit Test Task");
        //String id1 = IdGenerator.generateWithPrefix("TWB");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);

        Task task2 = new Task();
        task2.setName("Unit Test Task");
        task2.setWorkbasketId("2");
        task2 = taskServiceImpl.create(task2);

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

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
