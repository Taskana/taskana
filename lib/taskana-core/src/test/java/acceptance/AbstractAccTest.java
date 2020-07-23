package acceptance;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;

/** Base class for all acceptance tests. */
public abstract class AbstractAccTest {

  public static final String GROUP_1_DN =
      "cn=Organisationseinheit KSC 1,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA";
  public static final String GROUP_2_DN =
      "cn=Organisationseinheit KSC 2,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA";

  protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected static TaskanaEngine taskanaEngine;
  protected static WorkingDaysToDaysConverter converter;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  protected static void resetDb(boolean dropTables) throws Exception {

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
    converter = taskanaEngine.getWorkingDaysToDaysConverter();
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

  protected Map<String, String> createSimpleCustomPropertyMap(int propertiesCount) {
    return IntStream.rangeClosed(1, propertiesCount)
        .mapToObj(String::valueOf)
        .collect(Collectors.toMap("Property_"::concat, "Property Value of Property_"::concat));
  }

  protected Attachment createAttachment(
      String classificationKey,
      ObjectReference objRef,
      String channel,
      String receivedDate,
      Map<String, String> customAttributes)
      throws Exception {
    Attachment attachment = taskanaEngine.getTaskService().newAttachment();

    attachment.setClassificationSummary(
        taskanaEngine
            .getClassificationService()
            .getClassification(classificationKey, "DOMAIN_A")
            .asSummary());
    attachment.setObjectReference(objRef);
    attachment.setChannel(channel);
    final Instant receivedTimestamp;
    if (receivedDate != null && receivedDate.length() < 11) {
      // contains only the date, not the time
      LocalDate date = LocalDate.parse(receivedDate);
      receivedTimestamp = date.atStartOfDay().toInstant(ZoneOffset.UTC);
    } else {
      receivedTimestamp = Instant.parse(receivedDate);
    }
    attachment.setReceived(receivedTimestamp);
    if (customAttributes != null) {
      attachment.setCustomAttributeMap(customAttributes);
    }

    return attachment;
  }

  protected TimeInterval toDaysInterval() {
    Instant begin =
        ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.MIN, ZoneId.of("UTC"))
            .toInstant();
    Instant end =
        ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.MAX, ZoneId.of("UTC"))
            .toInstant();
    return new TimeInterval(begin, end);
  }

  protected Instant getInstant(String datetime) {
    return LocalDateTime.parse(datetime).atZone(ZoneId.of("UTC")).toInstant();
  }

  protected Instant moveForwardToWorkingDay(Instant date) {
    return converter.addWorkingDaysToInstant(date, Duration.ZERO);
  }

  protected Instant moveBackToWorkingDay(Instant date) {
    return converter.subtractWorkingDaysFromInstant(date, Duration.ZERO);
  }
}
