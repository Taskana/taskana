package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_7;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaEngineProxy;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.UserBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class QueryTasksAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  Task task1;
  Task task2;
  Task task3;
  Task task4;
  Task taskWithCustomAttributes;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();

    UserBuilder.newUser()
        .id("user-1-2")
        .firstName("Max")
        .lastName("Mustermann")
        .longName("Long name of user-1-2")
        .buildAndStore(userService);

    UserBuilder.newUser()
        .id("user-1-1")
        .firstName("Pia")
        .lastName("Maier")
        .longName("Long name of user-1-1")
        .buildAndStore(userService);

    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .owner("user-1-2")
            .customAttribute(TaskCustomField.CUSTOM_1, "custom1")
            .customAttribute(TaskCustomField.CUSTOM_2, "custom2")
            .customAttribute(TaskCustomField.CUSTOM_3, "custom3")
            .customAttribute(TaskCustomField.CUSTOM_4, "custom4")
            .customAttribute(TaskCustomField.CUSTOM_5, "custom5")
            .customAttribute(TaskCustomField.CUSTOM_6, "custom6")
            .customAttribute(TaskCustomField.CUSTOM_7, "custom7")
            .customAttribute(TaskCustomField.CUSTOM_8, "custom8")
            .customAttribute(TaskCustomField.CUSTOM_9, "custom9")
            .customAttribute(TaskCustomField.CUSTOM_10, "custom10")
            .customAttribute(TaskCustomField.CUSTOM_11, "custom11")
            .customAttribute(TaskCustomField.CUSTOM_12, "custom12")
            .customAttribute(TaskCustomField.CUSTOM_13, "custom13")
            .customAttribute(TaskCustomField.CUSTOM_14, "abc")
            .customAttribute(TaskCustomField.CUSTOM_15, "custom15")
            .customAttribute(TaskCustomField.CUSTOM_16, "custom16")
            .buildAndStore(taskService);

    task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .owner("user-1-1")
            .customAttribute(TaskCustomField.CUSTOM_1, "")
            .customAttribute(TaskCustomField.CUSTOM_2, "ade")
            .customAttribute(TaskCustomField.CUSTOM_3, "ffg")
            .customAttribute(TaskCustomField.CUSTOM_4, "99rty")
            .customAttribute(TaskCustomField.CUSTOM_5, "rty")
            .customAttribute(TaskCustomField.CUSTOM_6, "vvg")
            .customAttribute(TaskCustomField.CUSTOM_14, "abc")
            .buildAndStore(taskService);

    task3 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .owner("user-b-1")
            .customAttribute(TaskCustomField.CUSTOM_1, null)
            .customAttribute(TaskCustomField.CUSTOM_7, "ijk")
            .customAttribute(TaskCustomField.CUSTOM_8, "ijk")
            .customAttribute(TaskCustomField.CUSTOM_9, "ijk")
            .customAttribute(TaskCustomField.CUSTOM_10, "ijk")
            .customAttribute(TaskCustomField.CUSTOM_11, "ijk")
            .customAttribute(TaskCustomField.CUSTOM_14, "abc")
            .buildAndStore(taskService);

    task4 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .owner("user-1-2")
            .customAttribute(TaskCustomField.CUSTOM_1, "")
            .customAttribute(TaskCustomField.CUSTOM_12, "dde")
            .customAttribute(TaskCustomField.CUSTOM_13, "dde")
            .customAttribute(TaskCustomField.CUSTOM_14, "abc")
            .customAttribute(TaskCustomField.CUSTOM_15, "dde")
            .customAttribute(TaskCustomField.CUSTOM_16, "dde")
            .buildAndStore(taskService);

    taskWithCustomAttributes =
        TaskBuilder.newTask()
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(
                createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"))
            .classificationSummary(defaultClassificationSummary)
            .buildAndStore(taskService);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_NotSetOwnerLongNameOfTask_When_PropertyDisabled() throws Exception {
    List<TaskSummary> tasks = taskService.createTaskQuery().idIn(task1.getId()).list();

    assertThat(tasks).hasSize(1);
    String longName = userService.getUser(tasks.get(0).getOwner()).getLongName();
    assertThat(tasks.get(0)).extracting(TaskSummary::getOwnerLongName).isNull();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SplitTaskListIntoChunksOf32000_When_AugmentingTasksAfterTaskQuery() {
    try (MockedStatic<CollectionUtil> listUtilMock =
        Mockito.mockStatic(CollectionUtil.class, new CallsRealMethods())) {
      taskService.createTaskQuery().list();

      listUtilMock.verify(() -> CollectionUtil.partitionBasedOnSize(any(), eq(32000)));
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnTasksWithEmptyCustomFields_When_FilteringWithEmptyStringOnCustomField()
      throws InvalidArgumentException {
    List<TaskSummary> query =
        taskService.createTaskQuery().customAttributeIn(TaskCustomField.CUSTOM_1, "").list();
    assertThat(query).hasSize(2);
  }

  void testQueryForCustomXLikeAndIn(
      TaskCustomField customField, String[] searchArguments, int expectedResult) throws Exception {
    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike(customField, searchArguments).list();
    assertThat(results).hasSize(expectedResult);

    String[] customAttributes =
        results.stream().map(t -> t.getCustomField(customField)).toArray(String[]::new);

    List<TaskSummary> result2 =
        taskService.createTaskQuery().customAttributeIn(customField, customAttributes).list();
    assertThat(result2).hasSize(expectedResult);
  }

  void testQueryForCustomXNotIn(
      TaskCustomField customField, String[] searchArguments, int expectedCount) throws Exception {
    long results =
        taskService.createTaskQuery().customAttributeNotIn(customField, searchArguments).count();
    assertThat(results).isEqualTo(expectedCount);
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }

  private ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    return ObjectReferenceBuilder.newObjectReference()
        .company(company)
        .system(system)
        .systemInstance(systemInstance)
        .type(type)
        .value(value)
        .build();
  }

  private Map<String, String> createSimpleCustomPropertyMap(int propertiesCount) {
    return IntStream.rangeClosed(1, propertiesCount)
        .mapToObj(String::valueOf)
        .collect(Collectors.toMap("Property_"::concat, "Property Value of Property_"::concat));
  }

  public interface TaskTestMapper {

    @Select(
        "SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, "
            + "DESCRIPTION, NOTE, PRIORITY, STATE, CLASSIFICATION_CATEGORY, "
            + "CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, "
            + "WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, "
            + "PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, "
            + "POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, "
            + "IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, "
            + "CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, "
            + "CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
            + "FROM TASK "
            + "WHERE CUSTOM_ATTRIBUTES like #{searchText}")
    @Results(
        value = {
          @Result(property = "id", column = "ID"),
          @Result(property = "created", column = "CREATED"),
          @Result(property = "claimed", column = "CLAIMED"),
          @Result(property = "completed", column = "COMPLETED"),
          @Result(property = "modified", column = "MODIFIED"),
          @Result(property = "planned", column = "PLANNED"),
          @Result(property = "due", column = "DUE"),
          @Result(property = "name", column = "NAME"),
          @Result(property = "creator", column = "CREATOR"),
          @Result(property = "description", column = "DESCRIPTION"),
          @Result(property = "note", column = "NOTE"),
          @Result(property = "priority", column = "PRIORITY"),
          @Result(property = "state", column = "STATE"),
          @Result(
              property = "classificationSummaryImpl.category",
              column = "CLASSIFICATION_CATEGORY"),
          @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
          @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
          @Result(property = "domain", column = "DOMAIN"),
          @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
          @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
          @Result(property = "owner", column = "OWNER"),
          @Result(property = "primaryObjRefImpl.company", column = "POR_COMPANY"),
          @Result(property = "primaryObjRefImpl.system", column = "POR_SYSTEM"),
          @Result(property = "primaryObjRefImpl.systemInstance", column = "POR_INSTANCE"),
          @Result(property = "primaryObjRefImpl.type", column = "POR_TYPE"),
          @Result(property = "primaryObjRefImpl.value", column = "POR_VALUE"),
          @Result(property = "isRead", column = "IS_READ"),
          @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
          @Result(
              property = "customAttributes",
              column = "CUSTOM_ATTRIBUTES",
              javaType = Map.class,
              typeHandler = MapTypeHandler.class),
          @Result(property = "custom1", column = "CUSTOM_1"),
          @Result(property = "custom2", column = "CUSTOM_2"),
          @Result(property = "custom3", column = "CUSTOM_3"),
          @Result(property = "custom4", column = "CUSTOM_4"),
          @Result(property = "custom5", column = "CUSTOM_5"),
          @Result(property = "custom6", column = "CUSTOM_6"),
          @Result(property = "custom7", column = "CUSTOM_7"),
          @Result(property = "custom8", column = "CUSTOM_8"),
          @Result(property = "custom9", column = "CUSTOM_9"),
          @Result(property = "custom10", column = "CUSTOM_10")
        })
    List<TaskImpl> selectTasksByCustomAttributeLike(@Param("searchText") String searchText);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CustomAttributeTest {

    @WithAccessId(user = "admin")
    @TestFactory
    Stream<DynamicTest> should_ReturnCorrectResults_When_QueryingForCustomXStatements() {
      List<Triplet<TaskCustomField, String[], Integer>> list =
          List.of(
              Triplet.of(TaskCustomField.CUSTOM_1, new String[] {"custom%", ""}, 3),
              Triplet.of(TaskCustomField.CUSTOM_2, new String[] {"custom%", "a%"}, 2),
              Triplet.of(TaskCustomField.CUSTOM_3, new String[] {"ffg"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_4, new String[] {"%ust%", "%ty"}, 2),
              Triplet.of(TaskCustomField.CUSTOM_5, new String[] {"rt%"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_6, new String[] {"%custom6%", "%vvg%", "11%"}, 2),
              Triplet.of(TaskCustomField.CUSTOM_7, new String[] {"ijk%"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_8, new String[] {"ij%"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_9, new String[] {"%jk"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_10, new String[] {"%ijk%"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_11, new String[] {"%ijk", "%tom"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_12, new String[] {"dd%"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_13, new String[] {"%dd_"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_14, new String[] {"%"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_15, new String[] {"___"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_16, new String[] {"___"}, 1));
      assertThat(list).hasSameSizeAs(TaskCustomField.values());

      return DynamicTest.stream(
          list.iterator(),
          t -> t.getLeft().name(),
          t -> testQueryForCustomXLikeAndIn(t.getLeft(), t.getMiddle(), t.getRight()));
    }

    @WithAccessId(user = "admin")
    @TestFactory
    Stream<DynamicTest> should_ReturnCorrectResults_When_QueryingForCustomXNotIn() {
      List<Triplet<TaskCustomField, String[], Integer>> list =
          List.of(
              Triplet.of(TaskCustomField.CUSTOM_1, new String[] {"custom1"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_2, new String[] {""}, 5),
              Triplet.of(TaskCustomField.CUSTOM_3, new String[] {"custom3"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_4, new String[] {""}, 5),
              Triplet.of(TaskCustomField.CUSTOM_5, new String[] {"ew", "al", "el"}, 5),
              Triplet.of(TaskCustomField.CUSTOM_6, new String[] {"11", "vvg"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_7, new String[] {"custom7", "ijk"}, 3),
              Triplet.of(TaskCustomField.CUSTOM_8, new String[] {"not_existing"}, 5),
              Triplet.of(TaskCustomField.CUSTOM_9, new String[] {"custom9"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_10, new String[] {"custom10"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_11, new String[] {"custom11"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_12, new String[] {"custom12"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_13, new String[] {"custom13"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_14, new String[] {"abc"}, 1),
              Triplet.of(TaskCustomField.CUSTOM_15, new String[] {"custom15"}, 4),
              Triplet.of(TaskCustomField.CUSTOM_16, new String[] {"custom16"}, 4));
      assertThat(list).hasSameSizeAs(TaskCustomField.values());

      return DynamicTest.stream(
          list.iterator(),
          t -> t.getLeft().name(),
          t -> testQueryForCustomXNotIn(t.getLeft(), t.getMiddle(), t.getRight()));
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnTasksWithEmptyCustomField_When_QueriedByCustomFieldWhichIsNull()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService
              .createTaskQuery()
              .customAttributeIn(TaskCustomField.CUSTOM_9, new String[] {null})
              .list();
      assertThat(results).hasSize(3);

      results =
          taskService
              .createTaskQuery()
              .customAttributeIn(TaskCustomField.CUSTOM_9, null, "custom9")
              .list();
      assertThat(results).hasSize(4);

      results =
          taskService
              .createTaskQuery()
              .customAttributeIn(TaskCustomField.CUSTOM_9, new String[] {null})
              .customAttributeIn(TaskCustomField.CUSTOM_14, "abc")
              .list();
      assertThat(results).hasSize(2);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnTasksWithNullCustomField_When_QueriedByCustomFieldWhichIsEmpty()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService.createTaskQuery().customAttributeIn(TaskCustomField.CUSTOM_1, "").list();
      assertThat(results).hasSize(2);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_AllowToQueryTasksByCustomFieldWithNullAndEmptyInParallel()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService
              .createTaskQuery()
              .customAttributeIn(TaskCustomField.CUSTOM_1, "", null)
              .list();
      assertThat(results).hasSize(4);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnTasksWithEmptyCustomField_When_QueriedByCustomFieldWhichIsNotNull()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService
              .createTaskQuery()
              .customAttributeNotIn(TaskCustomField.CUSTOM_1, new String[] {null})
              .list();
      assertThat(results).hasSize(3);

      results =
          taskService
              .createTaskQuery()
              .customAttributeNotIn(TaskCustomField.CUSTOM_1, null, "custom1")
              .list();
      assertThat(results).hasSize(2);

      results =
          taskService
              .createTaskQuery()
              .customAttributeNotIn(TaskCustomField.CUSTOM_1, new String[] {null})
              .customAttributeNotIn(TaskCustomField.CUSTOM_10, "custom10")
              .list();
      assertThat(results).hasSize(2);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnTasksWithNullCustomField_When_QueriedByCustomFieldWhichIsNotEmpty()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService.createTaskQuery().customAttributeNotIn(TaskCustomField.CUSTOM_1, "").list();
      assertThat(results).hasSize(3);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_AllowToQueryTasksByCustomFieldWithNeitherNullOrEmptyInParallel()
        throws InvalidArgumentException {
      List<TaskSummary> results =
          taskService
              .createTaskQuery()
              .customAttributeNotIn(TaskCustomField.CUSTOM_1, "", null)
              .list();
      assertThat(results).hasSize(1);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ThrowException_When_SearchArgumentInLikeQueryIsNotGiven() {
      assertThatThrownBy(() -> taskService.createTaskQuery().customAttributeLike(CUSTOM_7).list())
          .isInstanceOf(InvalidArgumentException.class);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ThrowException_When_SearchArgumentInInQueryIsNotGiven() {
      assertThatThrownBy(() -> taskService.createTaskQuery().customAttributeIn(CUSTOM_7).list())
          .isInstanceOf(InvalidArgumentException.class);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_QueryTaskByCustomAttributes() throws Exception {
      Map<String, String> customAttributesForCreate =
          createSimpleCustomPropertyMap(20000); // about 1 Meg
      taskWithCustomAttributes.setCustomAttributeMap(
          createSimpleCustomPropertyMap(20000)); // about 1 Meg
      taskService.updateTask(taskWithCustomAttributes);

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

        assertThat(retrievedTask.getId()).isEqualTo(taskWithCustomAttributes.getId());

        // verify that the map is correctly retrieved from the database
        Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributeMap();
        assertThat(customAttributesFromDb).isEqualTo(customAttributesForCreate);

      } finally {
        engineProxy.returnConnection();
      }
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoEnabled implements TaskanaConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @Override
    public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
      return builder.addAdditionalUserInfo(true);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongNameOfTask_When_PropertyEnabled() throws Exception {
      List<TaskSummary> tasks = taskService.createTaskQuery().idIn(task1.getId()).list();

      assertThat(tasks).hasSize(1);
      String longName = userService.getUser(tasks.get(0).getOwner()).getLongName();
      assertThat(tasks.get(0)).extracting(TaskSummary::getOwnerLongName).isEqualTo(longName);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongNameOfTask_When_FilteringWithOwnerLongNameIn() throws Exception {
      String longName = "Long name of user-1-2";
      List<TaskSummary> tasks = taskService.createTaskQuery().ownerLongNameIn(longName).list();

      assertThat(tasks)
          .hasSize(2)
          .extracting(TaskSummary::getOwnerLongName)
          .doesNotContainNull()
          .containsOnly(longName);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongNameOfTask_When_FilteringWithOwnerLongNameNotIn() throws Exception {
      List<TaskSummary> tasks =
          taskService
              .createTaskQuery()
              .idIn(task1.getId(), task2.getId())
              .ownerLongNameNotIn("Long name of user-1-2")
              .list();

      assertThat(tasks).hasSize(1);
      assertThat(tasks.get(0))
          .extracting(TaskSummary::getOwnerLongName)
          .isEqualTo("Long name of user-1-1");
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongNameOfTask_When_FilteringWithOwnerLongNameLike() throws Exception {
      List<TaskSummary> tasks = taskService.createTaskQuery().ownerLongNameLike("%1-2%").list();

      assertThat(tasks)
          .hasSize(2)
          .extracting(TaskSummary::getOwnerLongName)
          .doesNotContainNull()
          .containsOnly("Long name of user-1-2");
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongNameOfTask_When_FilteringWithOwnerLongNameNotLike() throws Exception {
      List<TaskSummary> tasks = taskService.createTaskQuery().ownerLongNameNotLike("%1-1%").list();

      assertThat(tasks)
          .hasSize(2)
          .extracting(TaskSummary::getOwnerLongName)
          .doesNotContainNull()
          .containsOnly("Long name of user-1-2");
    }

    @WithAccessId(user = "admin")
    @Test
    void should_SetOwnerLongNameOfTaskToNull_When_OwnerNotExistingAsUserInDatabase()
        throws Exception {
      List<TaskSummary> tasks = taskService.createTaskQuery().idIn(task3.getId()).list();

      assertThat(tasks).hasSize(1);
      ThrowingCallable call = () -> userService.getUser(tasks.get(0).getOwner()).getLongName();
      assertThatThrownBy(call).isInstanceOf(UserNotFoundException.class);
      assertThat(tasks.get(0)).extracting(TaskSummary::getOwnerLongName).isNull();
    }

    @WithAccessId(user = "admin")
    @Test
    void should_OrderByOwnerLongName_When_QueryingTask() throws Exception {
      List<TaskSummary> tasks =
          taskService
              .createTaskQuery()
              .ownerNotIn("user-b-1")
              .orderByOwnerLongName(ASCENDING)
              .list();
      assertThat(tasks).extracting(TaskSummary::getOwnerLongName).hasSize(3).isSorted();
      tasks =
          taskService
              .createTaskQuery()
              .ownerNotIn("user-b-1")
              .orderByOwnerLongName(DESCENDING)
              .list();
      assertThat(tasks)
          .hasSize(3)
          .extracting(TaskSummary::getOwnerLongName)
          .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ListValues_For_OwnerLongName() throws Exception {
      List<String> longNames =
          taskService
              .createTaskQuery()
              .listValues(TaskQueryColumnName.OWNER_LONG_NAME, ASCENDING)
              .stream()
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      assertThat(longNames)
          .hasSize(2)
          .isSorted()
          .containsExactly("Long name of user-1-1", "Long name of user-1-2");

      longNames =
          taskService
              .createTaskQuery()
              .listValues(TaskQueryColumnName.OWNER_LONG_NAME, DESCENDING)
              .stream()
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      assertThat(longNames)
          .hasSize(2)
          .contains("Long name of user-1-2", "Long name of user-1-1")
          .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_ListValuesCorrectly_When_FilteringWithOwnerLongName() throws Exception {
      String longName = "Long name of user-1-2";
      List<String> listedValues =
          taskService
              .createTaskQuery()
              .ownerLongNameIn(longName)
              .orderByTaskId(null)
              .listValues(TaskQueryColumnName.ID, null);
      assertThat(listedValues).hasSize(2);

      List<TaskSummary> query =
          taskService.createTaskQuery().ownerLongNameIn(longName).orderByTaskId(null).list();
      assertThat(query).hasSize(2).extracting(TaskSummary::getId).isEqualTo(listedValues);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_CountCorrectly_When_FilteringWithOwnerLongName() throws Exception {
      String longName = "Long name of user-1-2";

      long count = taskService.createTaskQuery().ownerLongNameIn(longName).count();
      assertThat(count).isEqualTo(2);

      List<TaskSummary> query = taskService.createTaskQuery().ownerLongNameIn(longName).list();
      assertThat(query).hasSize((int) count);
    }
  }
}
