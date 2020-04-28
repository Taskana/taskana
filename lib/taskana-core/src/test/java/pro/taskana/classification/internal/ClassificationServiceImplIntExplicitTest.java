package pro.taskana.classification.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
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
 * Integration Test for ClassificationServiceImpl with connection management mode EXPLICIT.
 *
 * @author BBR
 */
public class ClassificationServiceImplIntExplicitTest {

  private static final String ID_PREFIX_CLASSIFICATION = "CLI";

  static int counter = 0;

  private DataSource dataSource;
  private ClassificationService classificationService;
  private TaskanaEngineConfiguration taskanaEngineConfiguration;
  private TaskanaEngine taskanaEngine;
  private TaskanaEngineImpl taskanaEngineImpl;

  @BeforeAll
  public static void resetDb() throws SQLException {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
  }

  @BeforeEach
  public void setup() throws SQLException {
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, false, schemaName);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    classificationService = taskanaEngine.getClassificationService();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
  }

  @AfterEach
  public void cleanUp() throws SQLException {
    taskanaEngineImpl.setConnection(null);
  }

  @Test
  public void testInsertClassification()
      throws SQLException, ClassificationNotFoundException, ClassificationAlreadyExistException,
          NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      final String domain = "DOMAIN_A";
      final String key = "dummy-key";
      ClassificationImpl expectedClassification;

      // new classification but master existing
      expectedClassification =
          (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
      expectedClassification.setKey(key);
      expectedClassification.setDomain("DOMAIN_B");
      classificationService.createClassification(expectedClassification);
      connection.commit();
      Classification actualClassification =
          classificationService.getClassification(key, "DOMAIN_B");
      assertThat(actualClassification).isNotNull();
      assertThat(actualClassification.getCreated()).isNotNull();
      assertThat(actualClassification.getId()).isNotNull();
      assertThat(actualClassification.getKey()).isEqualTo(key);
      assertThat(actualClassification.getDomain()).isEqualTo("DOMAIN_B");
      assertThat(actualClassification.getId()).startsWith(ID_PREFIX_CLASSIFICATION);
      Classification masterResult = classificationService.getClassification(key, "");
      assertThat(masterResult).isNotNull();

      // invalid serviceLevel
      ClassificationImpl expectedClassificationCreated =
          (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
      expectedClassificationCreated.setDomain(domain);
      expectedClassificationCreated.setKey("");
      expectedClassificationCreated.setServiceLevel("ASAP");

      ThrowingCallable call =
          () -> {
            classificationService.createClassification(expectedClassificationCreated);
          };
      assertThatThrownBy(call)
          .describedAs(
              "Should have thrown IllegalArgumentException, because ServiceLevel is invalid.")
          .isInstanceOf(InvalidArgumentException.class);

      connection.commit();
    }
  }

  @Test
  public void testFindAllClassifications()
      throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
          DomainNotFoundException, InvalidArgumentException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Classification classification0 = this.createNewClassificationWithUniqueKey("", "TASK");
      classificationService.createClassification(classification0);
      Classification classification1 = this.createNewClassificationWithUniqueKey("", "TASK");
      classificationService.createClassification(classification1);
      Classification classification2 = this.createNewClassificationWithUniqueKey("", "TASK");
      classification2.setParentId(classification0.getId());
      classificationService.createClassification(classification2);

      assertThat(classificationService.createClassificationQuery().list()).hasSize(2 + 1);
      connection.commit();
    }
  }

  @Test
  public void testModifiedClassification()
      throws SQLException, ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {

    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
      connection.commit();
      classification = classificationService.createClassification(classification);

      String updatedDescription = "TEST SOMETHING";
      classification.setDescription(updatedDescription);
      classification = classificationService.updateClassification(classification);
      connection.commit();

      classification =
          classificationService.getClassification(
              classification.getKey(), classification.getDomain());
      assertThat(classification.getDescription()).isEqualTo(updatedDescription);
    }
  }

  @Test
  public void testInsertAndClassificationQuery()
      throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
          DomainNotFoundException, InvalidArgumentException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
      classificationService.createClassification(classification);
      List<ClassificationSummary> list =
          classificationService
              .createClassificationQuery()
              .validInDomainEquals(Boolean.TRUE)
              .createdWithin(today())
              .list();
      assertThat(list).hasSize(1);
    }
  }

  @Test
  public void testUpdateAndClassificationQuery()
      throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
          ClassificationNotFoundException, ConcurrencyException, DomainNotFoundException,
          InvalidArgumentException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
      classification.setDescription("");
      classification = classificationService.createClassification(classification);
      classification.setDescription("description");
      classification = classificationService.updateClassification(classification);

      List<ClassificationSummary> list = classificationService.createClassificationQuery().list();
      assertThat(list).hasSize(2);
      list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
      assertThat(list).hasSize(1);
      classification =
          classificationService.getClassification(
              classification.getKey(), classification.getDomain());
      assertThat(classification.getDescription()).isEqualTo("description");

      classification = classificationService.updateClassification(classification);
      list = classificationService.createClassificationQuery().list();
      assertThat(list).hasSize(2);

      List<ClassificationSummary> allClassifications =
          classificationService.createClassificationQuery().list();
      assertThat(allClassifications).hasSize(2);
      connection.commit();
    }
  }

  @Test
  public void testDefaultSettingsWithClassificationQuery()
      throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
          ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
          DomainNotFoundException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
      classification = classificationService.createClassification(classification);

      Classification classification1 =
          this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
      classification1 = classificationService.createClassification(classification1);
      classification1.setParentId(classification.getId());
      classification1 = classificationService.updateClassification(classification1);

      List<ClassificationSummary> list =
          classificationService.createClassificationQuery().parentIdIn("").list();
      assertThat(list).hasSize(3);
      list = classificationService.createClassificationQuery().list();
      assertThat(list).hasSize(4);
      connection.commit();

      list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
      assertThat(list).hasSize(2);
      list = classificationService.createClassificationQuery().createdWithin(today()).list();
      assertThat(list).hasSize(4);
      list =
          classificationService
              .createClassificationQuery()
              .domainIn("DOMAIN_C")
              .validInDomainEquals(false)
              .list();
      assertThat(list).isEmpty();
      list =
          classificationService.createClassificationQuery().keyIn(classification1.getKey()).list();
      assertThat(list).hasSize(2);

      list =
          classificationService
              .createClassificationQuery()
              .parentIdIn(classification.getId())
              .list();
      assertThat(list).hasSize(1);
      assertThat(list.get(0).getKey()).isEqualTo(classification1.getKey());
      connection.commit();
    }
  }

  private Classification createNewClassificationWithUniqueKey(String domain, String type) {
    Classification classification =
        classificationService.newClassification("TEST" + counter, domain, type);
    counter++;
    return classification;
  }

  private TimeInterval today() {
    Instant begin =
        LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
    Instant end =
        LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    return new TimeInterval(begin, end);
  }
}
