package acceptance;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.internal.JobMapper;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.jobs.JobScheduler;
import io.kadai.common.test.config.DataSourceGenerator;
import io.kadai.sampledata.SampleDataGenerator;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.internal.TaskServiceImpl;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/** Base class for all acceptance tests. */
public abstract class AbstractAccTest {

  public static final String GROUP_1_DN =
      "cn=Organisationseinheit KSC 1,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=KADAI";
  public static final String GROUP_2_DN =
      "cn=Organisationseinheit KSC 2,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=KADAI";
  public static final String PERM_1 = "kadai:callcenter:ab:ab/a:callcenter";

  protected static KadaiConfiguration kadaiConfiguration;
  protected static KadaiEngine kadaiEngine;

  protected static TaskServiceImpl taskService;
  protected static WorkingTimeCalculator workingTimeCalculator;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  @AfterAll
  protected static void destroyClass() {
    Optional.ofNullable(((KadaiEngineImpl) kadaiEngine).getJobScheduler())
        .ifPresent(JobScheduler::stop);
  }

  protected static void initKadaiEngine(KadaiConfiguration configuration) throws SQLException {
    kadaiConfiguration = configuration;
    kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    taskService = (TaskServiceImpl) kadaiEngine.getTaskService();
    workingTimeCalculator = kadaiEngine.getWorkingTimeCalculator();
  }

  protected static void resetDb(boolean dropTables) throws Exception {

    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();

    kadaiConfiguration =
        new KadaiConfiguration.Builder(dataSource, false, schemaName)
            .initKadaiProperties()
            .germanPublicHolidaysEnabled(true)
            .build();
    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, kadaiConfiguration.getSchemaName());
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    workingTimeCalculator = kadaiEngine.getWorkingTimeCalculator();
    taskService = (TaskServiceImpl) kadaiEngine.getTaskService();

    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  protected JobMapper getJobMapper(KadaiEngine kadaiEngine)
      throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = KadaiEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager = (SqlSessionManager) sessionManagerField.get(kadaiEngine);

    return sqlSessionManager.getMapper(JobMapper.class);
  }

  protected ObjectReferenceImpl createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
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
    Attachment attachment = kadaiEngine.getTaskService().newAttachment();

    attachment.setClassificationSummary(
        kadaiEngine
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
    return workingTimeCalculator.addWorkingTime(date, Duration.ZERO);
  }

  protected Instant moveBackToWorkingDay(Instant date) {
    return workingTimeCalculator.subtractWorkingTime(date, Duration.ZERO);
  }
}
