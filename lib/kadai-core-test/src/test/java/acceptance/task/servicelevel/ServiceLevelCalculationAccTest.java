package acceptance.task.servicelevel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class ServiceLevelCalculationAccTest {

  private static final String TWO_HOURS = "PT2H";
  private static final String ZERO_SECONDS = "PT0S";
  @KadaiInject TaskService taskService;

  @KadaiInject ClassificationService classificationService;

  Workbasket workbasket;
  ObjectReference primaryObjRef;

  @WithAccessId(user = "businessadmin")
  @BeforeEach
  void before(WorkbasketService workbasketService) throws Exception {
    workbasket = DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasket.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculateDueInclusive_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    Task createdTask = createTaskWithPlanned("2023-03-02T16:00:00.000Z", classificationSummary);

    // then
    assertThat(createdTask.getDue()).isEqualTo(Instant.parse("2023-03-02T17:59:59.999Z"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculateDueInclusiveButNotLowerThanPlanned_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(ZERO_SECONDS);

    // when
    Task createdTask = createTaskWithPlanned("2023-03-02T16:00:00.000Z", classificationSummary);

    // then
    assertThat(createdTask.getDue()).isEqualTo(Instant.parse("2023-03-02T16:00:00.000Z"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NormalizeDueInclusiveIfNotOnWorkingDay_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    Task createdTask = createTaskWithDue("2023-03-04T16:00:00.000Z", classificationSummary);

    // then
    assertThat(createdTask.getDue())
        .isEqualTo(Instant.parse("2023-03-03T22:59:59.999Z")); // Friday end of business day
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NormalizeDueInclusiveWithoutChangeIfDueOnEndOfBusinessDay_When_CreatingTask()
      throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    Task createdTask = createTaskWithDue("2023-03-03T22:59:59.999Z", classificationSummary);

    // then
    assertThat(createdTask.getDue())
        .isEqualTo(Instant.parse("2023-03-03T22:59:59.999Z")); // Friday end of business day
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NormalizeDueInclusiveOnStartOfWeekend_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    String startOfWeekend = "2023-03-03T23:00:00Z";
    Task createdTask = createTaskWithDue(startOfWeekend, classificationSummary);

    // then
    Instant fridayEndOfBusinessDay = Instant.parse("2023-03-03T22:59:59.999Z");
    assertThat(createdTask.getDue()).isEqualTo(fridayEndOfBusinessDay);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculatePlannedInclusive_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    Task createdTask = createTaskWithDue("2023-03-02T16:59:59.999Z", classificationSummary);

    // then
    assertThat(createdTask.getPlanned()).isEqualTo(Instant.parse("2023-03-02T15:00:00.000Z"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculatePlannedInclusiveForServiceLevelZero_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(ZERO_SECONDS);

    // when
    Task createdTask = createTaskWithDue("2023-03-02T16:00:00Z", classificationSummary);

    // then
    assertThat(createdTask.getPlanned()).isEqualTo(Instant.parse("2023-03-02T16:00:00Z"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NormalizePlannedInclusiveIfNotOnWorkingDay_When_CreatingTask() throws Exception {
    // given
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    // when
    Task createdTask = createTaskWithPlanned("2023-03-04T16:00:00.000Z", classificationSummary);

    // then
    Instant mondayStartOfBusinessDay = Instant.parse("2023-03-05T23:00:00.000Z");
    assertThat(createdTask.getPlanned()).isEqualTo(mondayStartOfBusinessDay);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotFailDueToServiceLevelMismatchForNonZeroServiceLevel_When_CreatingTask()
      throws Exception {
    ClassificationSummary classificationSummary = classificationWithServiceLevel(TWO_HOURS);

    assertThatCode(
            () ->
                createTaskWithPlannedAndDue(
                    "2023-03-02T16:00:00.000Z", "2023-03-02T17:59:59.999Z", classificationSummary))
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotFailDueToServiceLevelMismatchForZeroServiceLevel_When_CreatingTask()
      throws Exception {
    ClassificationSummary classificationSummary = classificationWithServiceLevel(ZERO_SECONDS);

    assertThatCode(
            () ->
                createTaskWithPlannedAndDue(
                    "2023-03-03T16:00:00.000Z", "2023-03-03T16:00:00.000Z", classificationSummary))
        .doesNotThrowAnyException();
  }

  private Task createTaskWithPlannedAndDue(
      String planned, String due, ClassificationSummary classificationSummary) throws Exception {
    Task task = taskService.newTask(workbasket.getId());
    task.setPrimaryObjRef(primaryObjRef);
    task.setClassificationKey(classificationSummary.getKey());
    if (planned != null) {
      task.setPlanned(Instant.parse(planned));
    }
    if (due != null) {
      task.setDue(Instant.parse(due));
    }
    return taskService.createTask(task);
  }

  private Task createTaskWithDue(String due, ClassificationSummary classificationSummary)
      throws Exception {
    return createTaskWithPlannedAndDue(null, due, classificationSummary);
  }

  private Task createTaskWithPlanned(String planned, ClassificationSummary classificationSummary)
      throws Exception {
    return createTaskWithPlannedAndDue(planned, null, classificationSummary);
  }

  private ClassificationSummary classificationWithServiceLevel(String serviceLevel)
      throws Exception {
    return DefaultTestEntities.defaultTestClassification()
        .serviceLevel(serviceLevel)
        .buildAndStoreAsSummary(classificationService, "businessadmin");
  }
}
