package io.kadai.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.classification.api.ClassificationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KeyDomain;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.exceptions.WorkbasketInUseException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.jobs.WorkbasketCleanupJob;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Test for internal transaction management. */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = KadaiConfigTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inmemorydb", "dev"})
@Import({TransactionalJobsConfiguration.class})
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
class KadaiTransactionIntTest {

  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
  private static final String INTERNAL_SERVER_ERROR_STATUS = "500";
  @Autowired KadaiTransactionProvider springTransactionProvider;
  @Autowired private TestRestTemplate restTemplate;
  @Autowired private DataSource dataSource;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private KadaiEngine kadaiEngine;

  private static ObjectReference createDefaultObjRef() {
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
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
  void testKadaiSchema() {
    ResponseEntity<String> responseEntity = restTemplate.getForEntity("/schema", String.class);
    assertThat(responseEntity.getBody()).isEqualTo("KADAI");
  }

  @Test
  void testTransaction() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction", String.class);
    System.err.println("response: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence("workbaskets: 1");

    assertQuantities(1, 0);
  }

  @Test
  void testTransactionRollback() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/transaction?rollback={rollback}", String.class, "true");
    System.err.println("result: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_MESSAGE);
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_STATUS);

    assertQuantities(0, 0);
  }

  @Test
  void testTransactionCombined() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/transaction-many", String.class);
    System.err.println("response: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence("workbaskets: 3");

    assertQuantities(3, 0);
  }

  @Test
  void testTransactionCombinedRollback() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/transaction-many?rollback={rollback}", String.class, "true");
    System.err.println("result: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_MESSAGE);
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_STATUS);

    assertQuantities(0, 0);
  }

  @Test
  void testTransactionCustomdb() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customdb", String.class);
    System.err.println("response: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence("workbaskets: 2");
    assertThat(responseEntity.getBody()).containsSequence("tests: 2");

    assertQuantities(2, 2);
  }

  @Test
  void testTransactionCustomdbRollback() {
    assertQuantities(0, 0);

    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/customdb?rollback={rollback}", String.class, "true");
    System.err.println("response: " + responseEntity.getBody());
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_MESSAGE);
    assertThat(responseEntity.getBody()).containsSequence(INTERNAL_SERVER_ERROR_STATUS);

    assertQuantities(0, 0);
  }

  @Test
  void testTransactionCustomDbWithSchemaSetToKadai() throws Exception {

    final KadaiEngineImpl kadaiEngineImpl = (KadaiEngineImpl) kadaiEngine;
    try (Connection connection = dataSource.getConnection()) {

      assertThat(connection.getSchema()).isNotEqualTo("PUBLIC");
      jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('1', 'test')");
      jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('2', 'test2')");
      int result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
      assertThat(result).isEqualTo(2);
      Workbasket wbCreated =
          kadaiEngine
              .getWorkbasketService()
              .createWorkbasket(createWorkBasket("key1", "workbasket1"));
      Workbasket wb = kadaiEngineImpl.getWorkbasketService().getWorkbasket(wbCreated.getId());
      assertThat(wb).isNotNull();
      result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
      assertThat(result).isEqualTo(2);
    }
  }

  @Test
  void testWorkbasketCleanupJobTransaction() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    workbasketService.createWorkbasket(createWorkBasket("key1", "wb1"));
    workbasketService.createWorkbasket(createWorkBasket("key2", "wb2"));
    workbasketService.createWorkbasket(createWorkBasket("key3", "wb3"));
    TaskService taskService = kadaiEngine.getTaskService();
    ClassificationService classificationService = kadaiEngine.getClassificationService();
    classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskService.createTask(createTask("key1", "TEST"));
    taskService.createTask(createTask("key2", "TEST"));
    taskService.createTask(createTask("key3", "TEST"));

    assertThat(workbasketService.createWorkbasketQuery().count()).isEqualTo(3);
    assertThat(taskService.createTaskQuery().count()).isEqualTo(3);

    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("key1", "DOMAIN_A"))
            .list();
    taskService.claim(tasks.get(0).getId());
    taskService.completeTask(tasks.get(0).getId());
    tasks =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("key2", "DOMAIN_A"))
            .list();
    taskService.claim(tasks.get(0).getId());
    taskService.completeTask(tasks.get(0).getId());
    workbasketService.deleteWorkbasket(workbasketService.getWorkbasket("key1", "DOMAIN_A").getId());
    workbasketService.deleteWorkbasket(workbasketService.getWorkbasket("key2", "DOMAIN_A").getId());

    // Clean two tasks, key1 and key2.
    TaskCleanupJob taskCleanupJob =
        new TaskCleanupJob(kadaiEngine, springTransactionProvider, null);
    taskCleanupJob.run();

    ThrowingCallable httpCall =
        () ->
            workbasketService.deleteWorkbasket(
                workbasketService.getWorkbasket("key3", "DOMAIN_A").getId());
    assertThatThrownBy(httpCall)
        .isInstanceOf(WorkbasketInUseException.class)
        .hasMessageContaining("contains non-completed Tasks");

    WorkbasketCleanupJob job =
        new WorkbasketCleanupJob(kadaiEngine, springTransactionProvider, null);
    job.run();

    assertThatThrownBy(() -> workbasketService.getWorkbasket("key1", "DOMAIN_A"))
        .isInstanceOf(WorkbasketNotFoundException.class);
    assertThatThrownBy(() -> workbasketService.getWorkbasket("key2", "DOMAIN_A"))
        .isInstanceOf(WorkbasketNotFoundException.class);
    assertThatCode(() -> workbasketService.getWorkbasket("key3", "DOMAIN_A"))
        .doesNotThrowAnyException();
  }

  private void assertQuantities(int workbaskets, int tests) {
    assertWorkbaskets(workbaskets);
    assertCustomdbTests(tests);
  }

  private void assertWorkbaskets(int value) {
    int workbaskets = getWorkbaskets();
    assertThat(workbaskets).isEqualTo(value);
  }

  private void assertCustomdbTests(int value) {
    int tests = getCustomdbTests();
    assertThat(tests).isEqualTo(value);
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
    WorkbasketImpl workbasket =
        (WorkbasketImpl) kadaiEngine.getWorkbasketService().newWorkbasket(key, "DOMAIN_A");
    String id1 = IdGenerator.generateWithPrefix("TWB");
    workbasket.setId(id1);
    workbasket.setName(name);
    workbasket.setType(WorkbasketType.GROUP);
    return workbasket;
  }

  private Task createTask(String key, String classificationKey) {
    TaskImpl task = (TaskImpl) kadaiEngine.getTaskService().newTask(key, "DOMAIN_A");
    task.setClassificationKey(classificationKey);
    task.setPrimaryObjRef(createDefaultObjRef());
    return task;
  }
}
