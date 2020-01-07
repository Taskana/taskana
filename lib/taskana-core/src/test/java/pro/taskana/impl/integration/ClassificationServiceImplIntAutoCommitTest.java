package pro.taskana.impl.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Integration Test for ClassificationServiceImpl with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
class ClassificationServiceImplIntAutoCommitTest {

  static int counter = 0;
  private DataSource dataSource;
  private ClassificationService classificationService;
  private TaskanaEngineConfiguration taskanaEngineConfiguration;
  private TaskanaEngine taskanaEngine;
  private TaskanaEngineImpl taskanaEngineImpl;

  @BeforeAll
  static void resetDb() {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    new SampleDataGenerator(ds, schemaName).dropDb();
  }

  @BeforeEach
  void setup() throws SQLException {
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, false, schemaName);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    classificationService = taskanaEngine.getClassificationService();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    new SampleDataGenerator(dataSource, schemaName).clearDb();
  }

  @Test
  void testFindAllClassifications()
      throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
          InvalidArgumentException {
    Classification classification0 = this.createDummyClassificationWithUniqueKey("", "TASK");
    classificationService.createClassification(classification0);
    Classification classification1 = this.createDummyClassificationWithUniqueKey("", "TASK");
    classificationService.createClassification(classification1);
    Classification classification2 = this.createDummyClassificationWithUniqueKey("", "TASK");
    classification2.setParentId(classification0.getId());
    classificationService.createClassification(classification2);

    assertEquals(2 + 1, classificationService.createClassificationQuery().list().size());
  }

  @Test
  void testModifiedClassification()
      throws ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {
    String description = "TEST SOMETHING";
    Classification classification = this.createDummyClassificationWithUniqueKey("DOMAIN_A", "TASK");
    classification.setDescription("");
    classification = classificationService.createClassification(classification);
    classification.setDescription("TEST SOMETHING");
    classificationService.updateClassification(classification);

    classification =
        classificationService.getClassification(
            classification.getKey(), classification.getDomain());
    assertThat(description, equalTo(classification.getDescription()));
  }

  @Test
  void testInsertClassification()
      throws NotAuthorizedException, ClassificationAlreadyExistException, InvalidArgumentException,
          DomainNotFoundException {
    Classification classification = this.createDummyClassificationWithUniqueKey("DOMAIN_A", "TASK");
    classification = classificationService.createClassification(classification);

    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .createdWithin(today())
            .list();

    assertEquals(1, list.size());
  }

  @Test
  void testUpdateClassification()
      throws NotAuthorizedException, ClassificationAlreadyExistException,
          ClassificationNotFoundException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {
    Classification classification = this.createDummyClassificationWithUniqueKey("DOMAIN_A", "TASK");
    classification = classificationService.createClassification(classification);
    classification.setDescription("description");
    classification = classificationService.updateClassification(classification);

    List<ClassificationSummary> list =
        classificationService.createClassificationQuery().validInDomainEquals(true).list();
    assertEquals(1, list.size());

    classification = classificationService.updateClassification(classification);
    list = classificationService.createClassificationQuery().list();
    assertEquals(2, list.size());

    List<ClassificationSummary> allClassifications =
        classificationService.createClassificationQuery().list();
    assertEquals(2, allClassifications.size());
  }

  @Test
  void testDefaultSettings()
      throws NotAuthorizedException, ClassificationAlreadyExistException,
          ClassificationNotFoundException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {
    Classification classification = this.createDummyClassificationWithUniqueKey("DOMAIN_A", "TASK");
    classification = classificationService.createClassification(classification);

    Classification classification1 =
        this.createDummyClassificationWithUniqueKey("DOMAIN_A", "TASK");
    classification1 = classificationService.createClassification(classification1);

    classification1.setParentId(classification.getId());
    classification1 = classificationService.updateClassification(classification1);

    List<ClassificationSummary> list =
        classificationService.createClassificationQuery().parentIdIn("").list();
    assertEquals(3, list.size());
    list = classificationService.createClassificationQuery().list();
    assertEquals(4, list.size());

    List<ClassificationSummary> listAll = classificationService.createClassificationQuery().list();
    list = classificationService.createClassificationQuery().list();
    assertEquals(listAll.size(), list.size());

    list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
    assertEquals(2, list.size());

    list = classificationService.createClassificationQuery().createdWithin(today()).list();
    assertEquals(4, list.size());

    list =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_C")
            .validInDomainEquals(false)
            .list();
    assertEquals(0, list.size());

    list = classificationService.createClassificationQuery().list();
    assertEquals(4, list.size());
  }

  private TimeInterval today() {
    Instant begin =
        LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
    Instant end =
        LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    return new TimeInterval(begin, end);
  }

  private Classification createDummyClassificationWithUniqueKey(String domain, String type) {
    Classification classification =
        classificationService.newClassification("TEST" + counter, domain, type);
    counter++;
    return classification;
  }
}
