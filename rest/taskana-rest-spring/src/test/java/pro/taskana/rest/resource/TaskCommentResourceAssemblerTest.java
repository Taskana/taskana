package pro.taskana.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;

/** Test for {@link TaskCommentResourceAssembler}. */
@TaskanaSpringBootTest
public class TaskCommentResourceAssemblerTest {

  private final TaskCommentResourceAssembler taskCommentResourceAssembler;
  private final TaskService taskService;

  @Autowired
  TaskCommentResourceAssemblerTest(
      TaskCommentResourceAssembler taskCommentResourceAssembler, TaskService taskService) {
    this.taskCommentResourceAssembler = taskCommentResourceAssembler;
    this.taskService = taskService;
  }

  @Test
  void taskCommentModelToResource() {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment("TKI:000000000000000000000000000000000000");

    taskComment.setCreator("user_1_1");
    taskComment.setTextField("this is a task comment");
    taskComment.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskComment.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskCommentResource taskCommentResource = taskCommentResourceAssembler.toResource(taskComment);

    testEquality(taskComment, taskCommentResource);
  }

  @Test
  void taskCommentResourceToModel() {

    TaskCommentResource taskCommentResource = new TaskCommentResource();
    taskCommentResource.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentResource.setTaskCommentId("TCI:000000000000000000000000000000000000");
    taskCommentResource.setCreator("user_1_1");
    taskCommentResource.setCreated("2010-01-01T12:00:00Z");
    taskCommentResource.setModified("2011-11-11T11:00:00Z");

    TaskComment taskComment = taskCommentResourceAssembler.toModel(taskCommentResource);

    testEquality(taskComment, taskCommentResource);
  }

  private void testEquality(TaskComment taskComment, TaskCommentResource taskCommentResource) {

    assertThat(taskComment.getTaskId()).isEqualTo(taskCommentResource.getTaskId());
    assertThat(taskComment.getId()).isEqualTo(taskCommentResource.getTaskCommentId());
    assertThat(taskComment.getCreator()).isEqualTo(taskCommentResource.getCreator());
    assertThat(taskComment.getTextField()).isEqualTo(taskCommentResource.getTextField());
    assertThat(taskComment.getCreated()).isEqualTo(taskCommentResource.getCreated());
    assertThat(taskComment.getModified()).isEqualTo(taskCommentResource.getModified());
  }
}
