package acceptance.task.query;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.TaskAttachmentBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@KadaiIntegrationTest
@DisabledIfEnvironmentVariable(named = "DB", matches = "ORACLE")
class TaskQueryImplGroupByAccTest implements KadaiConfigurationModifier {
  @KadaiInject TaskService taskService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject CurrentUserContext currentUserContext;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasket;
  TaskSummary taskSummary1;
  TaskSummary taskSummary2;
  TaskSummary taskSummary3;

  @Override
  public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
    return builder.addAdditionalUserInfo(true).useSpecificDb2Taskquery(false);
  }

  @WithAccessId(user = "user-1-1")
  @BeforeAll
  void setup() throws Exception {
    UserBuilder.newUser()
        .id("user-1-1")
        .longName("Mustermann, Max - (user-1-1)")
        .firstName("Max")
        .lastName("Mustermann")
        .buildAndStore(userService, "businessadmin");
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService, "businessadmin");
    defaultWorkbasket = createWorkbasketWithPermission();
    ObjectReference sor2 =
        ObjectReferenceBuilder.newObjectReference()
            .company("FirstCompany")
            .value("FirstValue")
            .type("SecondType")
            .build();
    ObjectReference por2 = defaultTestObjectReference().build();
    taskSummary2 =
        taskInWorkbasket(defaultWorkbasket)
            .owner("user-1-1")
            .primaryObjRef(por2)
            .objectReferences(sor2)
            .due(Instant.parse("2022-11-10T09:45:00.000Z"))
            .name("Name2")
            .attachments(
                TaskAttachmentBuilder.newAttachment()
                    .channel("A")
                    .classificationSummary(defaultClassificationSummary)
                    .objectReference(por2)
                    .build())
            .buildAndStore(taskService)
            .asSummary();
    ObjectReference por1 = defaultTestObjectReference().company("15").build();
    ObjectReference sor1 =
        ObjectReferenceBuilder.newObjectReference()
            .company("FirstCompany")
            .value("FirstValue")
            .type("FirstType")
            .build();
    taskSummary1 =
        taskInWorkbasket(defaultWorkbasket)
            .owner("user-1-1")
            .primaryObjRef(por1)
            .objectReferences(sor1)
            .due(Instant.parse("2022-11-09T09:42:00.000Z"))
            .name("Name3")
            .attachments(
                TaskAttachmentBuilder.newAttachment()
                    .channel("B")
                    .classificationSummary(defaultClassificationSummary)
                    .objectReference(por1)
                    .build())
            .buildAndStoreAsSummary(taskService);
    ObjectReference sor2copy = sor2.copy();
    ObjectReference sor1copy = sor1.copy();
    taskSummary3 =
        taskInWorkbasket(defaultWorkbasket)
            .owner("user-1-1")
            .objectReferences(sor2copy, sor1copy)
            .due(Instant.parse("2022-11-15T09:45:00.000Z"))
            .name("Name1")
            .attachments(
                TaskAttachmentBuilder.newAttachment()
                    .channel("C")
                    .classificationSummary(defaultClassificationSummary)
                    .objectReference(por1)
                    .build())
            .buildAndStoreAsSummary(taskService);
    taskInWorkbasket(createWorkbasketWithPermission()).buildAndStore(taskService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupByPor_When_OrderingByName() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupByPor()
            .orderByName(SortDirection.ASCENDING)
            .list();
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary3)
        .extracting("groupByCount")
        .containsExactly(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupByPor_When_JoiningWithAllTablesAndPaging() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupByPor()
            .ownerLongNameNotIn("Unexisting")
            .attachmentChannelNotLike("Unexisting")
            .sorTypeLike("%Type%")
            .orderByOwnerLongName(SortDirection.ASCENDING)
            .orderByAttachmentChannel(SortDirection.ASCENDING)
            .orderByClassificationName(SortDirection.ASCENDING)
            .listPage(1, 1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary2)
        .extracting("groupByCount")
        .containsExactly(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupBySor_When_JoiningWithAllTablesAndPaging() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupBySor("SecondType")
            .ownerLongNameNotIn("Unexisting")
            .attachmentChannelNotLike("Unexisting")
            .sorTypeLike("%Type%")
            .orderByOwnerLongName(SortDirection.ASCENDING)
            .orderByAttachmentChannel(SortDirection.ASCENDING)
            .orderByClassificationName(SortDirection.ASCENDING)
            .listPage(1, 1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary2)
        .extracting("groupByCount")
        .containsExactly(2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupByPorWithOrderingByDue_When_OrderingByPorValue() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupByPor()
            .orderByPrimaryObjectReferenceValue(SortDirection.ASCENDING)
            .list();
    assertThat(list).hasSize(1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary1)
        .extracting("groupByCount")
        .containsExactly(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupByPorWithOrderingByDue_When_NotOrdering() {
    List<TaskSummary> list =
        taskService.createTaskQuery().workbasketIdIn(defaultWorkbasket.getId()).groupByPor().list();
    assertThat(list).hasSize(1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary1)
        .extracting("groupByCount")
        .containsExactly(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UseSingleCorrectly_When_GroupingByPor() {
    TaskSummary result =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupByPor()
            .single();
    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("groupByCount")
        .isEqualTo(taskSummary1);
    assertThat(result.getGroupByCount()).isEqualTo(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UseSingleCorrectly_When_GroupingBySor() {
    TaskSummary result =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupBySor("SecondType")
            .single();
    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("groupByCount")
        .isEqualTo(taskSummary2);
    assertThat(result.getGroupByCount()).isEqualTo(2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_Count_When_GroupingByPor() {
    Long numberOfTasks =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupByPor()
            .count();
    assertThat(numberOfTasks).isEqualTo(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupBySor_When_OrderingByName() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupBySor("SecondType")
            .orderByName(SortDirection.ASCENDING)
            .list();
    assertThat(list).hasSize(1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary3)
        .extracting("groupByCount")
        .containsExactly(2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GroupBySorWithOrderingByDue_When_NotOrdering() {
    List<TaskSummary> list =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupBySor("SecondType")
            .list();
    assertThat(list).hasSize(1);
    assertThat(list)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("groupByCount")
        .containsExactly(taskSummary2)
        .extracting("groupByCount")
        .containsExactly(2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_Count_When_GroupingBySor() {
    Long numberOfTasks =
        taskService
            .createTaskQuery()
            .workbasketIdIn(defaultWorkbasket.getId())
            .groupBySor("SecondType")
            .count();
    assertThat(numberOfTasks).isEqualTo(1);
  }

  private TaskBuilder taskInWorkbasket(WorkbasketSummary wb) {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .primaryObjRef(defaultTestObjectReference().build())
        .workbasketSummary(wb);
  }

  private WorkbasketSummary createWorkbasketWithPermission() throws Exception {
    WorkbasketSummary workbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService, "businessadmin");
    persistPermission(workbasketSummary);
    return workbasketSummary;
  }

  private void persistPermission(WorkbasketSummary workbasketSummary) throws Exception {
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId(currentUserContext.getUserid())
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "businessadmin");
  }
}
