package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.TaskSummary;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result
 * correctly.
 */
@ExtendWith(JaasExtension.class)
class QueryTaskByClassificationNameAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  QueryTaskByClassificationNameAccTest() {
    super();
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskValuesForAttachmentClassificationName() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(desc)
            .listValues(TaskQueryColumnName.A_CLASSIFICATION_NAME, null);
    assertThat(columnValueList).isNotNull();
    assertThat(columnValueList).hasSize(8);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTaskValuesForClassificationName() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .ownerLike("%user%")
            .orderByClassificationName(asc)
            .listValues(TaskQueryColumnName.CLASSIFICATION_NAME, null);
    assertThat(columnValueList).isNotNull();
    assertThat(columnValueList).hasSize(5);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testQueryByClassificationNameIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks =
        taskService.createTaskQuery().classificationNameIn("Dynamik-Ablehnung").list();
    assertThat(tasks).hasSize(1);

    List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
    assertThat(attachmentSummaries).isNotNull();
    assertThat(attachmentSummaries).hasSize(2);

    tasks =
        taskService
            .createTaskQuery()
            .classificationNameIn("Dynamik-Ablehnung")
            .orderByClassificationName(SortDirection.ASCENDING)
            .list();
    assertThat(tasks).hasSize(1);
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void testQueryByClassificationNameLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .classificationNameLike("Dynamik%", "Widerruf")
            .orderByClassificationName(SortDirection.ASCENDING)
            .list();
    assertThat(tasks).hasSize(32);

    // without sort, the same number of tasks should be returned
    tasks = taskService.createTaskQuery().classificationNameLike("Dynamik%", "Widerruf").list();
    assertThat(tasks).hasSize(32);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testSelectByAttachmentClassificationNameLike() {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with attachment classification names
    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .attachmentClassificationNameLike("Widerruf", "Beratungsprotokoll", "Dynamik%")
            .orderByAttachmentClassificationName(SortDirection.ASCENDING)
            .list();
    assertThat(tasks).hasSize(10);
    // make sure that unordered query returns the same number of objects
    tasks =
        taskService
            .createTaskQuery()
            .attachmentClassificationNameLike("Widerruf", "Beratungsprotokoll", "Dynamik%")
            .list();
    assertThat(tasks).hasSize(10);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testSelectByAttachmentClassificationNameIn() {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with attachment classification names
    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .orderByAttachmentClassificationName(SortDirection.ASCENDING)
            .list();
    assertThat(tasks).hasSize(7);
    // make sure that unordered query returns the same number of objects
    tasks =
        taskService
            .createTaskQuery()
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .list();
    assertThat(tasks).hasSize(7);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testQueryAndCountMatchForClassificationName() {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskQuery taskQuery = taskService.createTaskQuery();
    List<TaskSummary> tasks =
        taskQuery.classificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung").list();
    long numberOfTasks =
        taskQuery.classificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung").count();
    assertThat(tasks).hasSize((int) numberOfTasks);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testQueryAndCountForAttachmentClassificationName() {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskQuery taskQuery = taskService.createTaskQuery();
    List<TaskSummary> tasks =
        taskQuery
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .list();
    // we expect 4 result objects in this case, because task
    // TKI:000000000000000000000000000000000001  has 2 attachments with different Classifications
    // therefore task TKI:000000000000000000000000000000000001 occurs twice in the result set
    assertThat(tasks).hasSize(7);
    long numberOfTasks =
        taskQuery
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .count();
    assertThat(numberOfTasks).isEqualTo(6);
    // the count returns only the number of tasks that have an attachment with the specified
    // classification name.
    // therefore, task 001 is counted only once.
  }
}
