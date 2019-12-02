package pro.taskana;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.jobs.TaskCleanupJob;
import pro.taskana.jobs.WorkbasketCleanupJob;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TaskanaConfigTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inmemorydb", "dev"})
@Import({TransactionalJobsConfiguration.class})
class TaskanaTransactionIntTest {

    private static final int POOL_TIME_TO_WAIT = 50;
    @Autowired
    TaskanaTransactionProvider<Object> springTransactionProvider;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TaskanaEngine taskanaEngine;

    private static ObjectReference createDefaultObjRef() {
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany("company");
        objRef.setSystem("system");
        objRef.setSystemInstance("instance");
        objRef.setType("type");
        objRef.setValue("value");
        return objRef;
    }

    @BeforeEach
    void before() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM WORKBASKET");
        jdbcTemplate.execute("DELETE FROM CUSTOMDB.TEST");
    }

    @Test
    void testTaskanaSchema() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/schema", String.class);
        assertThat(responseEntity.getBody(), is("TASKANA"));
    }

    @Test
    void testTransaction() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 1"));

        assertAfter(1, 0);
    }

    @Test
    void testTransactionRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction?rollback={rollback}",
            String.class, "true");
        System.err.println("result: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 1"));

        assertAfter(0, 0);
    }

    @Test
    void testTransactionCombined() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction-many", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 3"));

        assertAfter(3, 0);
    }

    @Test
    void testTransactionCombinedRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction-many?rollback={rollback}",
            String.class, "true");
        System.err.println("result: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 3"));

        assertAfter(0, 0);
    }

    @Test
    void testTransactionCustomdb() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customdb", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 2"));
        assertThat(responseEntity.getBody(), containsString("tests: 2"));

        assertAfter(2, 2);

    }

    @Test
    void testTransactionCustomdbRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customdb?rollback={rollback}",
            String.class, "true");
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 2"));
        assertThat(responseEntity.getBody(), containsString("tests: 2"));

        assertAfter(0, 0);
    }

    @Test
    void testTransactionCustomDbWithSchemaSetToTaskana()
        throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, DomainNotFoundException,
        InvalidWorkbasketException, WorkbasketAlreadyExistException {

        TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        Connection connection = dataSource.getConnection();

        assertNotEquals(connection.getSchema(), "PUBLIC");
        jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('1', 'test')");
        jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('2', 'test2')");
        int result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
        assertEquals(2, result);
        Workbasket wbCreated = taskanaEngine.getWorkbasketService()
            .createWorkbasket(createWorkBasket("key1", "workbasket1"));
        Workbasket wb = taskanaEngineImpl.getWorkbasketService().getWorkbasket(wbCreated.getId());
        assertNotNull(wb);
        result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
        assertEquals(2, result);
    }

    @Test
    void testWorkbasketCleanupJobTransaction() {
        try {

            WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
            workbasketService.createWorkbasket(createWorkBasket("key1", "wb1"));
            workbasketService.createWorkbasket(createWorkBasket("key2", "wb2"));
            workbasketService.createWorkbasket(createWorkBasket("key3", "wb3"));
            TaskService taskService = taskanaEngine.getTaskService();
            ClassificationService classificationService = taskanaEngine.getClassificationService();
            classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
            taskService.createTask(createTask("key1", "TEST"));
            taskService.createTask(createTask("key2", "TEST"));
            taskService.createTask(createTask("key3", "TEST"));

            assertEquals(workbasketService.createWorkbasketQuery()
                .count(), 3);
            assertEquals(taskService.createTaskQuery()
                .count(), 3);

            List<TaskSummary> tasks = taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("key1", "DOMAIN_A"))
                .list();
            taskService.claim(tasks.get(0).getTaskId());
            taskService.completeTask(tasks.get(0).getTaskId());
            tasks = taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("key2", "DOMAIN_A"))
                .list();
            taskService.claim(tasks.get(0).getTaskId());
            taskService.completeTask(tasks.get(0).getTaskId());
            workbasketService.deleteWorkbasket(workbasketService.getWorkbasket("key1", "DOMAIN_A").getId());
            workbasketService.deleteWorkbasket(workbasketService.getWorkbasket("key2", "DOMAIN_A").getId());

            // Clean two tasks, key1 and key2.
            TaskCleanupJob taskCleanupJob = new TaskCleanupJob(taskanaEngine, springTransactionProvider, null);
            taskCleanupJob.run();

            tasks = taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("key3", "DOMAIN_A"))
                .list();
            taskService.claim(tasks.get(0).getTaskId());
            taskService.completeTask(tasks.get(0).getTaskId());

            try {
                workbasketService.deleteWorkbasket(workbasketService.getWorkbasket("key3", "DOMAIN_A").getId());
            } catch (TaskanaException ex) {
                assertEquals(ex.getMessage().contains("contains non-completed tasks"), true);
            }

            WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, springTransactionProvider, null);
            job.run();

            assertNull(workbasketService.getWorkbasket("key1", "DOMAIN_A"));
            assertNull(workbasketService.getWorkbasket("key2", "DOMAIN_A"));

        } catch (TaskanaException e) {
            e.printStackTrace();
        }
    }

    private void assertBefore(int workbaskets, int tests) {
        assertWorkbaskets("before", workbaskets);
        assertCustomdbTests("before", tests);
    }

    private void assertAfter(int workbaskets, int tests) {
        assertWorkbaskets("after", workbaskets);
        assertCustomdbTests("after", tests);
    }

    private void assertWorkbaskets(String assertion, int value) {
        int workbaskets = getWorkbaskets();
        System.err.println(assertion + " workbaskets: " + workbaskets);
        assertThat(workbaskets, is(value));
    }

    private void assertCustomdbTests(String assertion, int value) {
        int tests = getCustomdbTests();
        System.err.println(assertion + " tests: " + tests);
        assertThat(tests, is(value));
    }

    private int getWorkbaskets() {
        ResponseEntity<Integer> workbaskets = restTemplate.getForEntity("/workbaskets", Integer.class);
        if (workbaskets.getStatusCode().is2xxSuccessful()) {
            return workbaskets.getBody();
        } else {
            throw new RuntimeException("error get workbaskets: " + workbaskets.getBody());
        }
    }

    private int getCustomdbTests() {
        ResponseEntity<Integer> tests = restTemplate.getForEntity("/customdb-tests", Integer.class);
        if (tests.getStatusCode().is2xxSuccessful()) {
            return tests.getBody();
        } else {
            throw new RuntimeException("error get customdb.tests: " + tests.getBody());
        }
    }

    private Workbasket createWorkBasket(String key, String name) {
        WorkbasketImpl workbasket = (WorkbasketImpl) taskanaEngine.getWorkbasketService().newWorkbasket(key,
            "DOMAIN_A");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setName(name);
        workbasket.setType(WorkbasketType.GROUP);
        return workbasket;
    }

    private Task createTask(String key, String classificationKey) {
        TaskImpl task = (TaskImpl) taskanaEngine.getTaskService().newTask(key,
            "DOMAIN_A");
        task.setClassificationKey(classificationKey);
        task.setPrimaryObjRef(createDefaultObjRef());
        return task;
    }
}
