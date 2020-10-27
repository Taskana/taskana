package acceptance.task;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_7;
import static pro.taskana.task.api.TaskQueryColumnName.A_CHANNEL;
import static pro.taskana.task.api.TaskQueryColumnName.A_CLASSIFICATION_ID;
import static pro.taskana.task.api.TaskQueryColumnName.A_REF_VALUE;
import static pro.taskana.task.api.TaskQueryColumnName.CLASSIFICATION_KEY;
import static pro.taskana.task.api.TaskQueryColumnName.OWNER;
import static pro.taskana.task.api.TaskQueryColumnName.STATE;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.TaskanaEngineProxy;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.TaskTestMapper;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksAccTest extends AbstractAccTest {

  private static TaskService taskService;

  @BeforeAll
  static void setup() {
    taskService = taskanaEngine.getTaskService();
  }

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskValuesForEveryColumn() {
    SoftAssertions softly = new SoftAssertions();
    Arrays.stream(TaskQueryColumnName.values())
        .forEach(
            columnName ->
                softly
                    .assertThatCode(
                        () -> taskService.createTaskQuery().listValues(columnName, ASCENDING))
                    .describedAs("Column is not working " + columnName)
                    .doesNotThrowAnyException());
    softly.assertAll();
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskValuesForColumnName() {
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(DESCENDING)
            .listValues(OWNER, null);
    assertThat(columnValueList).hasSize(3);

    columnValueList = taskService.createTaskQuery().listValues(STATE, null);
    assertThat(columnValueList).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskValuesForColumnNameOnAttachments() {
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueIn("val4")
            .listValues(A_CHANNEL, null);
    assertThat(columnValueList).hasSize(2);

    columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueLike("%")
            .listValues(A_REF_VALUE, null);
    assertThat(columnValueList).hasSize(6);

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByAttachmentClassificationId(DESCENDING)
            .listValues(A_CLASSIFICATION_ID, null);
    assertThat(columnValueList).hasSize(12);

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByClassificationKey(DESCENDING)
            .listValues(CLASSIFICATION_KEY, null);
    assertThat(columnValueList).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOwnerLike() {

    List<TaskSummary> results =
        taskService.createTaskQuery().ownerLike("%a%", "%u%").orderByCreated(ASCENDING).list();

    assertThat(results).hasSize(39);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(previousSummary.getCreated().isAfter(taskSummary.getCreated())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForParentBusinessProcessId() {

    List<TaskSummary> results =
        taskService.createTaskQuery().parentBusinessProcessIdLike("%PBPI%", "doc%3%").list();
    assertThat(results).hasSize(33);
    for (TaskSummary taskSummary : results) {
      assertThat(taskSummary.getExternalId()).isNotNull();
    }

    String[] parentIds =
        results.stream().map(TaskSummary::getParentBusinessProcessId).toArray(String[]::new);

    List<TaskSummary> result2 =
        taskService.createTaskQuery().parentBusinessProcessIdIn(parentIds).list();
    assertThat(result2).hasSize(33);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForName() {

    List<TaskSummary> results = taskService.createTaskQuery().nameLike("task%").list();
    assertThat(results).hasSize(7);

    String[] ids = results.stream().map(TaskSummary::getName).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().nameIn(ids).list();
    assertThat(result2).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForClassificationKey() {

    List<TaskSummary> results = taskService.createTaskQuery().classificationKeyLike("L10%").list();
    assertThat(results).hasSize(77);

    String[] ids =
        results.stream().map(t -> t.getClassificationSummary().getKey()).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().classificationKeyIn(ids).list();
    assertThat(result2).hasSize(77);

    List<TaskSummary> result3 =
        taskService.createTaskQuery().classificationKeyNotIn("T2100", "T2000").list();
    assertThat(result3).hasSize(82);

    List<TaskSummary> result4 =
        taskService.createTaskQuery().classificationKeyNotIn("L1050", "L1060", "T2100").list();
    assertThat(result4).hasSize(10);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentInSummary() throws Exception {

    Attachment attachment =
        createAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchivETI",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomPropertyMap(3));

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.addAttachment(attachment);
    taskService.updateTask(task);

    List<TaskSummary> results =
        taskService.createTaskQuery().idIn("TKI:000000000000000000000000000000000000").list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getAttachmentSummaries()).hasSize(3);

    assertThat(results.get(0).getAttachmentSummaries().get(0)).isNotNull();
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForExternalId() {

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .externalIdIn(
                "ETI:000000000000000000000000000000000000",
                "ETI:000000000000000000000000000000000001")
            .list();
    assertThat(results).hasSize(2);

    List<String> resultValues =
        taskService
            .createTaskQuery()
            .externalIdLike("ETI:000000000000000000000000000000%")
            .listValues(TaskQueryColumnName.EXTERNAL_ID, DESCENDING);
    assertThat(resultValues).hasSize(74);

    long countAllExternalIds = taskService.createTaskQuery().externalIdLike("ETI:%").count();
    long countAllIds = taskService.createTaskQuery().count();
    assertThat(countAllExternalIds).isEqualTo(countAllIds);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> testQueryForCustomX() {
    List<Triplet<TaskCustomField, String[], Integer>> list =
        Arrays.asList(
            new Triplet<>(
                TaskCustomField.CUSTOM_1, new String[] {"custom%", "p%", "%xyz%", "efg"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_2, new String[] {"custom%", "a%"}, 2),
            new Triplet<>(TaskCustomField.CUSTOM_3, new String[] {"ffg"}, 1),
            new Triplet<>(TaskCustomField.CUSTOM_4, new String[] {"%ust%", "%ty"}, 2),
            new Triplet<>(TaskCustomField.CUSTOM_5, new String[] {"ew", "al"}, 6),
            new Triplet<>(TaskCustomField.CUSTOM_6, new String[] {"%custom6%", "%vvg%", "11%"}, 5),
            new Triplet<>(TaskCustomField.CUSTOM_7, new String[] {"%"}, 2),
            new Triplet<>(TaskCustomField.CUSTOM_8, new String[] {"%"}, 2),
            new Triplet<>(TaskCustomField.CUSTOM_9, new String[] {"%"}, 2),
            new Triplet<>(TaskCustomField.CUSTOM_10, new String[] {"%"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_11, new String[] {"%"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_12, new String[] {"%"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_13, new String[] {"%"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_14, new String[] {"%"}, 87),
            new Triplet<>(TaskCustomField.CUSTOM_15, new String[] {"%"}, 3),
            new Triplet<>(TaskCustomField.CUSTOM_16, new String[] {"%"}, 3));
    assertThat(list).hasSameSizeAs(TaskCustomField.values());

    return DynamicTest.stream(
        list.iterator(),
        t -> t.getLeft().name(),
        t -> testQueryForCustomX(t.getLeft(), t.getMiddle(), t.getRight()));
  }

  void testQueryForCustomX(
      TaskCustomField customField, String[] searchArguments, int expectedResult) throws Exception {
    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike(customField, searchArguments).list();
    assertThat(results).hasSize(expectedResult);

    String[] ids =
        results.stream().map(t -> t.getCustomAttribute(customField)).toArray(String[]::new);

    List<TaskSummary> result2 =
        taskService.createTaskQuery().customAttributeIn(customField, ids).list();
    assertThat(result2).hasSize(expectedResult);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom7WithExceptionInLike() {
    assertThatThrownBy(() -> taskService.createTaskQuery().customAttributeLike(CUSTOM_7).list())
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom7WithExceptionInIn() throws Exception {
    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike(CUSTOM_7, "fsdhfshk%").list();
    assertThat(results).isEmpty();

    assertThatThrownBy(() -> taskService.createTaskQuery().customAttributeIn(CUSTOM_7).list())
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom7WithException() throws Exception {
    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike(CUSTOM_7, "%").list();
    assertThat(results).hasSize(2);

    String[] ids = results.stream().map(t -> t.getCustomAttribute(CUSTOM_7)).toArray(String[]::new);

    List<TaskSummary> result2 =
        taskService.createTaskQuery().customAttributeIn(CUSTOM_7, ids).list();
    assertThat(result2).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskByCustomAttributes() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setClassificationKey("T2100");
    Map<String, String> customAttributesForCreate =
        createSimpleCustomPropertyMap(20000); // about 1 Meg
    newTask.setCustomAttributeMap(customAttributesForCreate);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    // query the task by custom attributes
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    try {
      SqlSession session = engineProxy.getSqlSession();
      Configuration config = session.getConfiguration();
      if (!config.hasMapper(TaskTestMapper.class)) {
        config.addMapper(TaskTestMapper.class);
      }

      TaskTestMapper mapper = session.getMapper(TaskTestMapper.class);
      engineProxy.openConnection();
      List<TaskImpl> queryResult =
          mapper.selectTasksByCustomAttributeLike("%Property Value of Property_1339%");

      assertThat(queryResult).hasSize(1);
      Task retrievedTask = queryResult.get(0);

      assertThat(retrievedTask.getId()).isEqualTo(createdTask.getId());

      // verify that the map is correctly retrieved from the database
      Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributeMap();
      assertThat(customAttributesFromDb).isEqualTo(customAttributesForCreate);

    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryAndCountMatch() {
    TaskQuery taskQuery = taskService.createTaskQuery();
    List<TaskSummary> tasks = taskQuery.nameIn("Task99", "Task01", "Widerruf").list();
    long numberOfTasks = taskQuery.nameIn("Task99", "Task01", "Widerruf").count();
    assertThat(tasks).hasSize((int) numberOfTasks);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryAllPaged() {
    TaskQuery taskQuery = taskService.createTaskQuery();
    long numberOfTasks = taskQuery.count();
    assertThat(numberOfTasks).isEqualTo(87);
    List<TaskSummary> tasks = taskQuery.orderByDue(DESCENDING).list();
    assertThat(tasks).hasSize(87);
    List<TaskSummary> tasksp = taskQuery.orderByDue(DESCENDING).listPage(4, 5);
    assertThat(tasksp).hasSize(5);
    tasksp = taskQuery.orderByDue(DESCENDING).listPage(5, 5);
    assertThat(tasksp).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCreatorIn() {
    List<TaskSummary> results =
        taskService.createTaskQuery().creatorIn("creator_user_id2", "creator_user_id3").list();
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCreatorLike() {
    List<TaskSummary> results = taskService.createTaskQuery().creatorLike("ersTeLlEr%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForNoteLike() {
    List<TaskSummary> results = taskService.createTaskQuery().noteLike("Some%").list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForClassificationCategoryIn() {
    List<TaskSummary> results =
        taskService.createTaskQuery().classificationCategoryIn("MANUAL", "AUTOMATIC").list();
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForClassificationCategoryLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().classificationCategoryLike("AUTO%").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceCompanyLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceCompanyLike("My%").list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceSystemLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceSystemLike("My%").list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceSystemInstanceLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceSystemInstanceLike("My%").list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceTypeLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceTypeLike("My%").list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForReadEquals() {
    List<TaskSummary> results = taskService.createTaskQuery().readEquals(true).list();
    assertThat(results).hasSize(39);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForTransferredEquals() {
    List<TaskSummary> results = taskService.createTaskQuery().transferredEquals(true).list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForBusinessProcessIdIn() {
    List<TaskSummary> results =
        taskService.createTaskQuery().businessProcessIdIn("PI_0000000000003", "BPI21").list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForBusinessProcessIdLike() {
    List<TaskSummary> results = taskService.createTaskQuery().businessProcessIdLike("pI_%").list();
    assertThat(results).hasSize(80);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentClassificationKeyIn() {
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentClassificationKeyIn("L110102").list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo("TKI:000000000000000000000000000000000002");
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentClassificationKeyLike() {
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentClassificationKeyLike("%10102").list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo("TKI:000000000000000000000000000000000002");
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentclassificationIdIn() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentClassificationIdIn("CLI:000000000000000000000000000000000002")
            .list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo("TKI:000000000000000000000000000000000001");
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentChannelLike() {
    List<TaskSummary> results = taskService.createTaskQuery().attachmentChannelLike("%6").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentReferenceIn() {
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentReferenceValueIn("val4").list();
    assertThat(results).hasSize(6);
    assertThat(results.get(5).getAttachmentSummaries()).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentReceivedIn() {
    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T12:00:00"), getInstant("2018-01-31T12:00:00"));
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentReceivedWithin(interval)
            .orderByWorkbasketId(DESCENDING)
            .list();
    assertThat(results).hasSize(2);
    assertThat(results.get(0).getId()).isEqualTo("TKI:000000000000000000000000000000000001");
    assertThat(results.get(1).getId()).isEqualTo("TKI:000000000000000000000000000000000011");
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByCreatorDesc() {
    List<TaskSummary> results = taskService.createTaskQuery().orderByCreator(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(TaskSummary::getCreator)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByWorkbasketIdDesc() {
    List<TaskSummary> results =
        taskService.createTaskQuery().orderByWorkbasketId(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(TaskSummary::getWorkbasketSummary)
        .extracting(WorkbasketSummary::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> testQueryForOrderByCustomXAsc() {
    Iterator<TaskCustomField> iterator = Arrays.stream(TaskCustomField.values()).iterator();
    return DynamicTest.stream(
        iterator,
        s -> String.format("order by %s asc", s),
        s -> testQueryForOrderByCustomX(s, ASCENDING));
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> testQueryForOrderByCustomXDesc() {
    Iterator<TaskCustomField> iterator = Arrays.stream(TaskCustomField.values()).iterator();

    return DynamicTest.stream(
        iterator,
        s -> String.format("order by %s desc", s),
        s -> testQueryForOrderByCustomX(s, DESCENDING));
  }

  void testQueryForOrderByCustomX(TaskCustomField customField, SortDirection sortDirection) {
    List<TaskSummary> results =
        taskService.createTaskQuery().orderByCustomAttribute(customField, sortDirection).list();

    Comparator<String> comparator =
        sortDirection == ASCENDING ? CASE_INSENSITIVE_ORDER : CASE_INSENSITIVE_ORDER.reversed();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(t -> t.getCustomAttribute(customField))
        .filteredOn(Objects::nonNull)
        .isSortedAccordingTo(comparator);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderWithDirectionNull() {
    List<TaskSummary> results =
        taskService.createTaskQuery().orderByPrimaryObjectReferenceSystemInstance(null).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(TaskSummary::getPrimaryObjRef)
        .extracting(ObjectReference::getSystemInstance)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationIdAsc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .extracting(AttachmentSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationIdDesc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .extracting(AttachmentSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationKeyAsc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .extracting(AttachmentSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationKeyDesc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .extracting(AttachmentSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentRefValueDesc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentReference(DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .extracting(AttachmentSummary::getObjectReference)
        .extracting(ObjectReference::getValue)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByAttachmentChannelAscAndReferenceDesc() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentChannel(ASCENDING)
            .orderByAttachmentReference(DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .flatExtracting(TaskSummary::getAttachmentSummaries)
        .isSortedAccordingTo(
            Comparator.comparing(AttachmentSummary::getChannel, CASE_INSENSITIVE_ORDER)
                .thenComparing(
                    a -> a.getObjectReference().getValue(), CASE_INSENSITIVE_ORDER.reversed()));
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForAttachmentChannelLikeAndOrdering() {
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(DESCENDING)
            .list();

    assertThat(results)
        .extracting(TaskSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());

    results =
        taskService
            .createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(ASCENDING)
            .list();

    assertThat(results)
        .extracting(TaskSummary::getClassificationSummary)
        .extracting(ClassificationSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForExternalIdIn() {

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .externalIdIn(
                "ETI:000000000000000000000000000000000010",
                "ETI:000000000000000000000000000000000011",
                "ETI:000000000000000000000000000000000012",
                "ETI:000000000000000000000000000000000013",
                "ETI:000000000000000000000000000000000014",
                "ETI:000000000000000000000000000000000015",
                "ETI:000000000000000000000000000000000016",
                "ETI:000000000000000000000000000000000017",
                "ETI:000000000000000000000000000000000018",
                "ETI:000000000000000000000000000000000019")
            .list();
    assertThat(results).hasSize(10);

    String[] ids = results.stream().map(TaskSummary::getId).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().idIn(ids).list();
    assertThat(result2).hasSize(10);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForExternalIdLike() {

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .externalIdLike("ETI:00000000000000000000000000000000001%")
            .list();
    assertThat(results).hasSize(10);

    String[] ids = results.stream().map(TaskSummary::getId).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().idIn(ids).list();
    assertThat(result2).hasSize(10);
  }
}
