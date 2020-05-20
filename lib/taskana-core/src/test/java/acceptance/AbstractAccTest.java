package acceptance;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;

/** Base class for all acceptance tests. */
public abstract class AbstractAccTest {

  protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected static TaskanaEngine taskanaEngine;

  @BeforeAll
  public static void setupTest() throws Exception {
    resetDb(false);
  }

  public static void resetDb(boolean dropTables) throws SQLException {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(true);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany(company);
    objectReference.setSystem(system);
    objectReference.setSystemInstance(systemInstance);
    objectReference.setType(type);
    objectReference.setValue(value);
    return objectReference;
  }

  protected Map<String, String> createSimpleCustomProperties(int propertiesCount) {
    HashMap<String, String> properties = new HashMap<>();
    for (int i = 1; i <= propertiesCount; i++) {
      properties.put("Property_" + i, "Property Value of Property_" + i);
    }
    return properties;
  }

  protected Attachment createAttachment(
      String classificationKey,
      ObjectReference objRef,
      String channel,
      String receivedDate,
      Map<String, String> customAttributes)
      throws ClassificationNotFoundException {
    Attachment attachment = taskanaEngine.getTaskService().newAttachment();

    attachment.setClassificationSummary(
        taskanaEngine
            .getClassificationService()
            .getClassification(classificationKey, "DOMAIN_A")
            .asSummary());
    attachment.setObjectReference(objRef);
    attachment.setChannel(channel);
    Instant receivedTimestamp = null;
    if (receivedDate != null && receivedDate.length() < 11) {
      // contains only the date, not the time
      LocalDate date = LocalDate.parse(receivedDate);
      receivedTimestamp = date.atStartOfDay().toInstant(ZoneOffset.UTC);
    } else {
      receivedTimestamp = Instant.parse(receivedDate);
    }
    attachment.setReceived(receivedTimestamp);
    if (customAttributes != null) {
      attachment.setCustomAttributes(customAttributes);
    }

    return attachment;
  }

  protected TimeInterval toDaysInterval() {
    Instant begin =
        ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.MIN, ZoneId.of("UTC")).toInstant();
    Instant end =
        ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.MAX, ZoneId.of("UTC")).toInstant();
    return new TimeInterval(begin, end);
  }

  protected Instant getInstant(String datetime) {
    return LocalDateTime.parse(datetime).atZone(ZoneId.of("UTC")).toInstant();
  }
}
