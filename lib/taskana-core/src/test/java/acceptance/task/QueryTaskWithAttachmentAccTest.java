package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result
 * correctly.
 */
@ExtendWith(JaasExtension.class)
class QueryTaskWithAttachmentAccTest extends AbstractAccTest {

  private static final Comparator<Object> REFERENCE_COMPARATOR = (o1, o2) -> o1 == o2 ? 0 : -1;

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetAttachmentSummariesFromTask() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks = taskService.createTaskQuery().classificationKeyIn("L110102").list();
    assertThat(tasks).hasSize(1);

    List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
    assertThat(attachmentSummaries).hasSize(2);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetNoAttachmentSummaryFromTask() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks = taskService.createTaskQuery().list();
    assertThat(tasks).hasSize(30);

    List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
    assertThat(attachmentSummaries).isEmpty();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testIfNewTaskHasEmptyAttachmentList() {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.newTask("WBI:100000000000000000000000000000000006");
    assertThat(task.getAttachments()).isNotNull();
    assertThat(task.asSummary().getAttachmentSummaries()).isNotNull();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ConfirmIfAttachmentSummariesAreCorrect_When_UsingTaskQueryAndGetTaskById()
      throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with ID TKI:00...00
    List<TaskSummary> tasks =
        taskService.createTaskQuery().idIn("TKI:000000000000000000000000000000000000").list();
    assertThat(tasks).hasSize(1);
    List<AttachmentSummary> queryAttachmentSummaries = tasks.get(0).getAttachmentSummaries();

    Task originalTask = taskService.getTask("TKI:000000000000000000000000000000000000");
    List<AttachmentSummary> originalAttachments =
        originalTask.getAttachments().stream()
            .map(Attachment::asSummary)
            .collect(Collectors.toList());

    assertThat(queryAttachmentSummaries)
        .hasSize(originalAttachments.size())
        .containsExactlyInAnyOrderElementsOf(originalAttachments) // same values
        .usingElementComparator(REFERENCE_COMPARATOR)
        .doesNotContainAnyElementsOf(originalAttachments); // but not same reference
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ConfirmIfAttachmentSummariesAreCorrect() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with ID TKI:00...00
    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .classificationKeyIn("T2000")
            .customAttributeIn(TaskCustomField.CUSTOM_1, "custom1")
            .list();
    assertThat(tasks).hasSize(1);
    List<AttachmentSummary> queryAttachmentSummaries = tasks.get(0).getAttachmentSummaries();

    Task originalTask = taskService.getTask("TKI:000000000000000000000000000000000000");
    List<AttachmentSummary> originalAttachments =
        originalTask.getAttachments().stream()
            .map(Attachment::asSummary)
            .collect(Collectors.toList());

    assertThat(queryAttachmentSummaries)
        .hasSize(originalAttachments.size())
        .containsExactlyInAnyOrderElementsOf(originalAttachments) // same values
        .usingElementComparator(REFERENCE_COMPARATOR)
        .doesNotContainAnyElementsOf(originalAttachments); // but not same reference
  }
}
