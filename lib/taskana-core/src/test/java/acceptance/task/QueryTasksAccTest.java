package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;
import static pro.taskana.task.api.TaskQueryColumnName.A_CHANNEL;
import static pro.taskana.task.api.TaskQueryColumnName.A_CLASSIFICATION_ID;
import static pro.taskana.task.api.TaskQueryColumnName.A_REF_VALUE;
import static pro.taskana.task.api.TaskQueryColumnName.CLASSIFICATION_KEY;
import static pro.taskana.task.api.TaskQueryColumnName.OWNER;
import static pro.taskana.task.api.TaskQueryColumnName.STATE;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.AttachmentPersistenceException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineProxyForTest;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.Attachment;
import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskSummary;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.TaskImpl;
import pro.taskana.task.internal.TaskTestMapper;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws SQLException {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"admin"})
  @Test
  void testQueryTaskValuesForEveryColumn() {
    TaskService taskService = taskanaEngine.getTaskService();
    assertAll(
        () ->
            Arrays.stream(TaskQueryColumnName.values())
                .forEach(
                    columnName ->
                        Assertions.assertDoesNotThrow(
                            () -> taskService.createTaskQuery().listValues(columnName, ASCENDING),
                            "Column is not working " + columnName)));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"admin"})
  @Test
  void testQueryTaskValuesForColumnName() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(DESCENDING)
            .listValues(OWNER, null);
    assertNotNull(columnValueList);
    assertEquals(3, columnValueList.size());

    columnValueList = taskService.createTaskQuery().listValues(STATE, null);
    assertNotNull(columnValueList);
    assertEquals(3, columnValueList.size());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"admin"})
  @Test
  void testQueryTaskValuesForColumnNameOnAttachments() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueIn("val4")
            .listValues(A_CHANNEL, null);
    assertNotNull(columnValueList);
    assertEquals(2, columnValueList.size());

    columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueLike("%")
            .listValues(A_REF_VALUE, null);
    assertNotNull(columnValueList);
    assertEquals(6, columnValueList.size());

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByAttachmentClassificationId(DESCENDING)
            .listValues(A_CLASSIFICATION_ID, null);
    assertNotNull(columnValueList);
    assertEquals(12, columnValueList.size());

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByClassificationKey(DESCENDING)
            .listValues(CLASSIFICATION_KEY, null);
    assertNotNull(columnValueList);
    assertEquals(6, columnValueList.size());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForOwnerLike() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().ownerLike("%a%", "%u%").orderByCreated(ASCENDING).list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertFalse(previousSummary.getCreated().isAfter(taskSummary.getCreated()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForParentBusinessProcessId() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().parentBusinessProcessIdLike("%PBPI%", "doc%3%").list();
    assertThat(results.size(), equalTo(24));
    for (TaskSummary taskSummary : results) {
      assertNotNull(taskSummary.getExternalId());
    }

    String[] parentIds =
        results.stream().map(TaskSummary::getParentBusinessProcessId).toArray(String[]::new);

    List<TaskSummary> result2 =
        taskService.createTaskQuery().parentBusinessProcessIdIn(parentIds).list();
    assertThat(result2.size(), equalTo(24));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForName() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().nameLike("task%").list();
    assertThat(results.size(), equalTo(6));

    String[] ids = results.stream().map(TaskSummary::getName).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().nameIn(ids).list();
    assertThat(result2.size(), equalTo(6));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForClassificationKey() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().classificationKeyLike("L10%").list();
    assertThat(results.size(), equalTo(67));

    String[] ids =
        results.stream().map(t -> t.getClassificationSummary().getKey()).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().classificationKeyIn(ids).list();
    assertThat(result2.size(), equalTo(67));

    List<TaskSummary> result3 =
        taskService.createTaskQuery().classificationKeyNotIn("T2100", "T2000").list();
    assertThat(result3.size(), equalTo(71));

    List<TaskSummary> result4 =
        taskService.createTaskQuery().classificationKeyNotIn("L1050", "L1060", "T2100").list();
    assertThat(result4.size(), equalTo(6));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForAttachmentInSummary()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    TaskService taskService = taskanaEngine.getTaskService();

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
            createSimpleCustomProperties(3));

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.addAttachment(attachment);
    taskService.updateTask(task);

    List<TaskSummary> results =
        taskService.createTaskQuery().idIn("TKI:000000000000000000000000000000000000").list();
    assertThat(results.size(), equalTo(1));
    assertThat(results.get(0).getAttachmentSummaries().size(), equalTo(3));
    assertNotNull(results.get(0).getAttachmentSummaries().get(0));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"admin"})
  @Test
  void testQueryForExternalId() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .externalIdIn(
                "ETI:000000000000000000000000000000000000",
                "ETI:000000000000000000000000000000000001")
            .list();
    assertThat(results.size(), equalTo(2));

    List<String> resultValues =
        taskService
            .createTaskQuery()
            .externalIdLike("ETI:000000000000000000000000000000%")
            .listValues(TaskQueryColumnName.EXTERNAL_ID, DESCENDING);
    assertThat(resultValues.size(), equalTo(70));

    long countAllExternalIds = taskService.createTaskQuery().externalIdLike("ETI:%").count();
    long countAllIds = taskService.createTaskQuery().count();
    assertEquals(countAllIds, countAllExternalIds);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom1() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("1", "custom%", "p%", "%xyz%", "efg")
            .list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("1");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("1", ids).list();
    assertThat(result2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom2() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("2", "custom%", "a%").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("2");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("2", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom3() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("3", "ffg").list();
    assertThat(results.size(), equalTo(1));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("3");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("3", ids).list();
    assertThat(result2.size(), equalTo(1));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom4() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("4", "%ust%", "%ty").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("4");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("4", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom5() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("5", "ew", "al").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("5");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("5", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom6() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("6", "%custom6%", "%vvg%", "11%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("6");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("6", ids).list();
    assertThat(result2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom7WithExceptionInLike() {
    TaskService taskService = taskanaEngine.getTaskService();

    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> {
          List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("7").list();
          assertThat(results.size(), equalTo(0));
        });
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom7WithExceptionInIn() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService.createTaskQuery().customAttributeLike("7", "fsdhfshk%").list();
    assertThat(results.size(), equalTo(0));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("7");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> {
          List<TaskSummary> result2 =
              taskService.createTaskQuery().customAttributeIn("7", ids).list();
          assertThat(result2.size(), equalTo(0));
        });
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom7WithException() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("7", "%").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("7");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("7", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom8() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("8", "%").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("8");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("8", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom9() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("9", "%").list();
    assertThat(results.size(), equalTo(2));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("9");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("9", ids).list();
    assertThat(result2.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom10() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("10", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("10");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().customAttributeIn("10", ids).list();
    assertThat(result2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom11() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("11", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("11");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("11", ids).list();
    assertThat(results2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom12() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("12", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("12");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("12", ids).list();
    assertThat(results2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom13() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("13", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("13");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("13", ids).list();
    assertThat(results2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom14() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("14", "%").list();
    assertThat(results.size(), equalTo(48));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("14");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("14", ids).list();
    assertThat(results2.size(), equalTo(48));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom15() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("15", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("15");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("15", ids).list();
    assertThat(results2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForCustom16() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().customAttributeLike("16", "%").list();
    assertThat(results.size(), equalTo(3));

    String[] ids =
        results.stream()
            .map(
                t -> {
                  try {
                    return t.getCustomAttribute("16");
                  } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                  }
                })
            .toArray(String[]::new);
    List<TaskSummary> results2 = taskService.createTaskQuery().customAttributeIn("16", ids).list();
    assertThat(results2.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testQueryTaskByCustomAttributes()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, NoSuchFieldException,
          IllegalAccessException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setClassificationKey("T2100");
    Map<String, String> customAttributesForCreate =
        createSimpleCustomProperties(20000); // about 1 Meg
    newTask.setCustomAttributes(customAttributesForCreate);
    Task createdTask = taskService.createTask(newTask);

    assertNotNull(createdTask);
    // query the task by custom attributes
    TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
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

      assertEquals(1, queryResult.size());
      Task retrievedTask = queryResult.get(0);

      assertEquals(createdTask.getId(), retrievedTask.getId());

      // verify that the map is correctly retrieved from the database
      Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributes();
      assertNotNull(customAttributesFromDb);
      assertEquals(customAttributesForCreate, customAttributesFromDb);

    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testQueryAndCountMatch() {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskQuery taskQuery = taskService.createTaskQuery();
    List<TaskSummary> tasks = taskQuery.nameIn("Task99", "Task01", "Widerruf").list();
    long numberOfTasks = taskQuery.nameIn("Task99", "Task01", "Widerruf").count();
    assertEquals(numberOfTasks, tasks.size());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllPaged() {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskQuery taskQuery = taskService.createTaskQuery();
    long numberOfTasks = taskQuery.count();
    assertEquals(25, numberOfTasks);
    List<TaskSummary> tasks = taskQuery.orderByDue(DESCENDING).list();
    assertEquals(25, tasks.size());
    List<TaskSummary> tasksp = taskQuery.orderByDue(DESCENDING).listPage(4, 5);
    assertEquals(5, tasksp.size());
    tasksp = taskQuery.orderByDue(DESCENDING).listPage(5, 5);
    assertEquals(5, tasksp.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForCreatorIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().creatorIn("creator_user_id2", "creator_user_id3").list();
    assertEquals(4, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForCreatorLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().creatorLike("ersTeLlEr%").list();
    assertEquals(3, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForNoteLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().noteLike("Some%").list();
    assertEquals(6, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForClassificationCategoryIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().classificationCategoryIn("MANUAL", "AUTOMATIC").list();
    assertEquals(3, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForClassificationCategoryLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().classificationCategoryLike("AUTO%").list();
    assertEquals(1, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceCompanyLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceCompanyLike("My%").list();
    assertEquals(6, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceSystemLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceSystemLike("My%").list();
    assertEquals(6, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceSystemInstanceLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceSystemInstanceLike("My%").list();
    assertEquals(6, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForPrimaryObjectReferenceTypeLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceTypeLike("My%").list();
    assertEquals(6, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForReadEquals() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().readEquals(true).list();
    assertEquals(25, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForTransferredEquals() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().transferredEquals(true).list();
    assertEquals(2, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForBusinessProcessIdIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().businessProcessIdIn("PI_0000000000003", "BPI21").list();
    assertEquals(8, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForBusinessProcessIdLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().businessProcessIdLike("pI_%").list();
    assertEquals(67, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentClassificationKeyIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentClassificationKeyIn("L110102").list();
    assertEquals(1, results.size());
    assertEquals("TKI:000000000000000000000000000000000002", results.get(0).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentClassificationKeyLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentClassificationKeyLike("%10102").list();
    assertEquals(1, results.size());
    assertEquals("TKI:000000000000000000000000000000000002", results.get(0).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentclassificationIdIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentClassificationIdIn("CLI:000000000000000000000000000000000002")
            .list();
    assertEquals(1, results.size());
    assertEquals("TKI:000000000000000000000000000000000001", results.get(0).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentChannelLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().attachmentChannelLike("%6").list();
    assertEquals(2, results.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentReferenceIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().attachmentReferenceValueIn("val4").list();
    assertEquals(6, results.size());
    assertEquals(1, results.get(5).getAttachmentSummaries().size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentReceivedIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T12:00:00"), getInstant("2018-01-31T12:00:00"));
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentReceivedWithin(interval)
            .orderByWorkbasketId(DESCENDING)
            .list();
    assertEquals(2, results.size());
    assertEquals("TKI:000000000000000000000000000000000001", results.get(0).getId());
    assertEquals("TKI:000000000000000000000000000000000011", results.get(1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCreatorDesc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().orderByCreator(DESCENDING).list();
    assertEquals("erstellerSpezial", results.get(0).getCreator());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByWorkbasketIdDesc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().orderByWorkbasketId(DESCENDING).list();
    assertEquals(
        "WBI:100000000000000000000000000000000015", results.get(0).getWorkbasketSummary().getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom1Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("1", "%")
            .orderByCustomAttribute("1", ASCENDING)
            .list();
    assertEquals("custom1", results.get(0).getCustomAttribute("1"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom2Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("2", "%")
            .orderByCustomAttribute("2", DESCENDING)
            .list();
    assertEquals("custom2", results.get(0).getCustomAttribute("2"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom3Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("3", "%")
            .orderByCustomAttribute("3", ASCENDING)
            .list();
    assertEquals("custom3", results.get(0).getCustomAttribute("3"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom4Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("4", "%")
            .orderByCustomAttribute("4", DESCENDING)
            .list();

    assertEquals("rty", results.get(0).getCustomAttribute("4"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom5Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("5", "%")
            .orderByCustomAttribute("5", ASCENDING)
            .list();
    assertEquals("al", results.get(0).getCustomAttribute("5"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom6Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("6", "%")
            .orderByCustomAttribute("6", DESCENDING)
            .list();
    assertEquals("vvg", results.get(0).getCustomAttribute("6"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom7Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .orderByCustomAttribute("7", ASCENDING)
            .customAttributeLike("7", "%")
            .list();
    assertEquals("custom7", results.get(0).getCustomAttribute("7"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom8Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .orderByCustomAttribute("8", DESCENDING)
            .customAttributeLike("8", "%")
            .list();
    assertEquals("lnp", results.get(0).getCustomAttribute("8"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom9Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("9", "%")
            .orderByCustomAttribute("9", ASCENDING)
            .list();
    assertEquals("bbq", results.get(0).getCustomAttribute("9"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom10Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("10", "%")
            .orderByCustomAttribute("10", DESCENDING)
            .list();
    assertEquals("ert", results.get(0).getCustomAttribute("10"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom11Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .orderByCustomAttribute("11", DESCENDING)
            .customAttributeLike("11", "%")
            .list();

    assertEquals("ert", results.get(0).getCustomAttribute("11"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom12Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("12", "%")
            .orderByCustomAttribute("12", ASCENDING)
            .list();
    assertEquals("custom12", results.get(0).getCustomAttribute("12"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom13Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("13", "%")
            .orderByCustomAttribute("13", DESCENDING)
            .list();

    assertEquals("ert", results.get(0).getCustomAttribute("13"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom14Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("14", "%")
            .orderByCustomAttribute("14", ASCENDING)
            .list();
    assertEquals("abc", results.get(0).getCustomAttribute("14"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom15Desc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("15", "%")
            .orderByCustomAttribute("15", DESCENDING)
            .list();

    assertEquals("ert", results.get(0).getCustomAttribute("15"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByCustom16Asc() throws InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .customAttributeLike("16", "%")
            .orderByCustomAttribute("16", ASCENDING)
            .list();
    assertEquals("custom16", results.get(0).getCustomAttribute("16"));
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderWithDirectionNull() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().orderByPrimaryObjectReferenceSystemInstance(null).list();
    assertEquals("00", results.get(0).getPrimaryObjRef().getSystemInstance());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationIdAsc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(ASCENDING)
            .list();
    assertEquals("TKI:000000000000000000000000000000000011", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationIdDesc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(DESCENDING)
            .list();
    assertEquals("TKI:000000000000000000000000000000000010", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000011", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationKeyAsc() {

    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(ASCENDING)
            .list();

    assertEquals("TKI:000000000000000000000000000000000010", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000012", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentClassificationKeyDesc() {

    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(DESCENDING)
            .list();

    assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentRefValueDesc() {

    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentReference(DESCENDING)
            .list();

    assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForOrderByAttachmentChannelAscAndReferenceDesc() {

    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentChannel(ASCENDING)
            .orderByAttachmentReference(DESCENDING)
            .list();

    assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getId());
    assertEquals(
        "TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForAttachmentChannelLikeAndOrdering() {

    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(DESCENDING)
            .list();

    assertEquals("T2000", results.get(0).getClassificationSummary().getKey());
    assertEquals("L1050", results.get(results.size() - 1).getClassificationSummary().getKey());

    results =
        taskService
            .createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(ASCENDING)
            .list();

    assertEquals("L1050", results.get(0).getClassificationSummary().getKey());
    assertEquals("T2000", results.get(results.size() - 1).getClassificationSummary().getKey());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForExternalIdIn() {
    TaskService taskService = taskanaEngine.getTaskService();

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
    assertThat(results.size(), equalTo(10));

    String[] ids = results.stream().map(TaskSummary::getId).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().idIn(ids).list();
    assertThat(result2.size(), equalTo(10));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryForExternalIdLike() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .externalIdLike("ETI:00000000000000000000000000000000001%")
            .list();
    assertThat(results.size(), equalTo(10));

    String[] ids = results.stream().map(TaskSummary::getId).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().idIn(ids).list();
    assertThat(result2.size(), equalTo(10));
  }
}
