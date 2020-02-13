package pro.taskana.classification.internal;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Integration Test for ClassificationServiceImpl with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
class ClassificationServiceImplIntAutoCommitTest {

  private ClassificationService classificationService;

  @BeforeEach
  void setup() throws SQLException {

    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, false, schemaName);
    TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    classificationService = taskanaEngine.getClassificationService();
    TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    new SampleDataGenerator(dataSource, schemaName).clearDb();
  }

  @AfterEach
  void teardown() {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    new SampleDataGenerator(dataSource, schemaName).dropDb();
  }

  @Test
  void testFindAllClassifications()
      throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
          InvalidArgumentException {
    Classification classification0 = classificationService.newClassification("TEST1", "", "TASK");
    classificationService.createClassification(classification0);
    Classification classification1 = classificationService.newClassification("TEST2", "", "TASK");
    classificationService.createClassification(classification1);
    Classification classification2 = classificationService.newClassification("TEST3", "", "TASK");
    classification2.setParentId(classification0.getId());
    classificationService.createClassification(classification2);

    assertEquals(2 + 1, classificationService.createClassificationQuery().list().size());
  }

  @Test
  void testModifiedClassification()
      throws ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {
    final String description = "TEST SOMETHING";
    Classification classification =
        classificationService.newClassification("TEST434", "DOMAIN_A", "TASK");
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
    Classification classification =
        classificationService.newClassification("TEST1333", "DOMAIN_A", "TASK");
    classificationService.createClassification(classification);

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
    Classification classification =
        classificationService.newClassification("TEST32451", "DOMAIN_A", "TASK");
    classification = classificationService.createClassification(classification);
    classification.setDescription("description");
    classification = classificationService.updateClassification(classification);

    List<ClassificationSummary> list =
        classificationService.createClassificationQuery().validInDomainEquals(true).list();
    assertEquals(1, list.size());

    classificationService.updateClassification(classification);
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
    Classification classification =
        classificationService.newClassification("TEST7771", "DOMAIN_A", "TASK");
    classification = classificationService.createClassification(classification);

    Classification classification1 =
        classificationService.newClassification("TEST1865", "DOMAIN_A", "TASK");
    classification1 = classificationService.createClassification(classification1);

    classification1.setParentId(classification.getId());
    classificationService.updateClassification(classification1);

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
}
