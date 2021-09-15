package acceptance;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.user.api.models.User;

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

    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();
    taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(true);
    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, taskanaEngineConfiguration.getSchemaName());
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    converter = taskanaEngine.getWorkingDaysToDaysConverter();
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected JobMapper getJobMapper() throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = TaskanaEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaEngine);

    return sqlSessionManager.getMapper(JobMapper.class);
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

  protected Attachment createExampleAttachment(
      String classificationKey,
      ObjectReference objRef,
      String channel,
      Instant receivedTimestamp,
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
    attachment.setReceived(receivedTimestamp);
    if (customAttributes != null) {
      attachment.setCustomAttributeMap(customAttributes);
    }

    return attachment;
  }

  protected User createExampleUser(String id) {
    User user = taskanaEngine.getUserService().newUser();
    user.setId(id);
    user.setFirstName("Hans");
    user.setLastName("Georg");
    user.setFullName("Georg, Hans");
    user.setLongName("Georg, Hans - (user-10-20)");
    user.setEmail("hans.georg@web.com");
    user.setPhone("1234");
    user.setMobilePhone("01574275632");
    user.setOrgLevel4("level4");
    user.setOrgLevel3("level3");
    user.setOrgLevel2("level2");
    user.setOrgLevel1("level1");
    user.setData("ab");

    return user;
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
