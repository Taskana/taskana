package pro.taskana.classification.internal;

import static org.assertj.core.api.Assertions.assertThat;

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

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/** Integration Test for ClassificationServiceImpl with connection management mode AUTOCOMMIT. */
class ClassificationServiceImplIntAutoCommitTest {

  private static SampleDataGenerator sampleDataGenerator;
  private static TaskanaEngineConfiguration taskanaEngineConfiguration;
  private ClassificationService classificationService;

  @BeforeAll
  static void beforeAll() throws Exception {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, false, schemaName);
  }

  @BeforeEach
  void setup() {
    TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    classificationService = taskanaEngine.getClassificationService();
    TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    sampleDataGenerator.clearDb();
  }

  @Test
  void testFindAllClassifications() throws Exception {
    Classification classification0 = classificationService.newClassification("TEST1", "", "TASK");
    classification0.setServiceLevel("P1D");
    classificationService.createClassification(classification0);
    Classification classification1 = classificationService.newClassification("TEST2", "", "TASK");
    classification1.setServiceLevel("P1D");
    classificationService.createClassification(classification1);
    Classification classification2 = classificationService.newClassification("TEST3", "", "TASK");
    classification2.setParentId(classification0.getId());
    classification2.setServiceLevel("P1D");
    classificationService.createClassification(classification2);

    assertThat(classificationService.createClassificationQuery().list()).hasSize(2 + 1);
  }

  @Test
  void testModifiedClassification() throws Exception {
    final String description = "TEST SOMETHING";
    Classification classification =
        classificationService.newClassification("TEST434", "DOMAIN_A", "TASK");
    classification.setDescription("");
    classification.setServiceLevel("P1D");
    classification = classificationService.createClassification(classification);
    classification.setDescription("TEST SOMETHING");
    classificationService.updateClassification(classification);

    classification =
        classificationService.getClassification(
            classification.getKey(), classification.getDomain());
    assertThat(classification.getDescription()).isEqualTo(description);
  }

  @Test
  void testInsertClassification() throws Exception {
    Classification classification =
        classificationService.newClassification("TEST1333", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    classificationService.createClassification(classification);

    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .createdWithin(today())
            .list();

    assertThat(list).hasSize(1);
  }

  @Test
  void testUpdateClassification() throws Exception {
    Classification classification =
        classificationService.newClassification("TEST32451", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    classification = classificationService.createClassification(classification);
    classification.setDescription("description");
    classification = classificationService.updateClassification(classification);

    List<ClassificationSummary> list =
        classificationService.createClassificationQuery().validInDomainEquals(true).list();
    assertThat(list).hasSize(1);

    classificationService.updateClassification(classification);
    list = classificationService.createClassificationQuery().list();
    assertThat(list).hasSize(2);

    List<ClassificationSummary> allClassifications =
        classificationService.createClassificationQuery().list();
    assertThat(allClassifications).hasSize(2);
  }

  @Test
  void testDefaultSettings() throws Exception {
    Classification classification =
        classificationService.newClassification("TEST7771", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    classification = classificationService.createClassification(classification);

    Classification classification1 =
        classificationService.newClassification("TEST1865", "DOMAIN_A", "TASK");
    classification1.setServiceLevel("P1D");
    classification1 = classificationService.createClassification(classification1);

    classification1.setParentId(classification.getId());
    classificationService.updateClassification(classification1);

    List<ClassificationSummary> list =
        classificationService.createClassificationQuery().parentIdIn("").list();
    assertThat(list).hasSize(3);
    list = classificationService.createClassificationQuery().list();
    assertThat(list).hasSize(4);

    List<ClassificationSummary> listAll = classificationService.createClassificationQuery().list();
    list = classificationService.createClassificationQuery().list();
    assertThat(list).hasSize(listAll.size());

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

    list = classificationService.createClassificationQuery().list();
    assertThat(list).hasSize(4);
  }

  private TimeInterval today() {
    Instant begin =
        LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
    Instant end =
        LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    return new TimeInterval(begin, end);
  }
}
