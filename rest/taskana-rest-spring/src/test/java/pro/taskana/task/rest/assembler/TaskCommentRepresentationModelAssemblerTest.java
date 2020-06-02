package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/**
 * Test for {@link TaskCommentRepresentationModelAssembler}.
 */
@TaskanaSpringBootTest
class TaskCommentRepresentationModelAssemblerTest {

  private final TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler;
  private final TaskService taskService;

  @Autowired
  TaskCommentRepresentationModelAssemblerTest(
      TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler,
      TaskService taskService) {
    this.taskCommentRepresentationModelAssembler = taskCommentRepresentationModelAssembler;
    this.taskService = taskService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment("TKI:000000000000000000000000000000000000");

    taskComment.setCreator("user_1_1");
    taskComment.setTextField("this is a task comment");
    taskComment.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskComment.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskCommentRepresentationModel taskCommentRepresentationModel =
        taskCommentRepresentationModelAssembler.toModel(taskComment);

    testEquality(taskComment, taskCommentRepresentationModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {

    TaskCommentRepresentationModel taskCommentRepresentationModel =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModel.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentRepresentationModel.setTaskCommentId("TCI:000000000000000000000000000000000000");
    taskCommentRepresentationModel.setCreator("user_1_1");
    taskCommentRepresentationModel.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskCommentRepresentationModel.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskComment taskComment =
        taskCommentRepresentationModelAssembler.toEntityModel(taskCommentRepresentationModel);

    testEquality(taskComment, taskCommentRepresentationModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment("TKI:000000000000000000000000000000000000");
    taskComment.setCreator("user_1_1");
    taskComment.setTextField("this is a task comment");
    taskComment.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskComment.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskCommentRepresentationModel repModel =
        taskCommentRepresentationModelAssembler.toModel(taskComment);
    TaskCommentImpl taskComment2
        = (TaskCommentImpl) taskCommentRepresentationModelAssembler.toEntityModel(repModel);

    testEqualityOfEntities(taskComment, taskComment2);
  }

  private void testEquality(
      TaskComment taskComment, TaskCommentRepresentationModel taskCommentRepresentationModel) {

    assertThat(taskComment.getTaskId()).isEqualTo(taskCommentRepresentationModel.getTaskId());
    assertThat(taskComment.getId()).isEqualTo(taskCommentRepresentationModel.getTaskCommentId());
    assertThat(taskComment.getCreator()).isEqualTo(taskCommentRepresentationModel.getCreator());
    assertThat(taskComment.getTextField()).isEqualTo(taskCommentRepresentationModel.getTextField());
    assertThat(taskComment.getCreated()).isEqualTo(taskCommentRepresentationModel.getCreated());
    assertThat(taskComment.getModified()).isEqualTo(taskCommentRepresentationModel.getModified());
  }

  private void testEqualityOfEntities(
      TaskComment taskComment, TaskComment taskComment2) {
    assertThat(taskComment.getTaskId()).isEqualTo(taskComment2.getTaskId());
    assertThat(taskComment.getId()).isEqualTo(taskComment2.getId());
    assertThat(taskComment.getCreator()).isEqualTo(taskComment2.getCreator());
    assertThat(taskComment.getTextField()).isEqualTo(taskComment2.getTextField());
    assertThat(taskComment.getCreated()).isEqualTo(taskComment2.getCreated());
    assertThat(taskComment.getModified()).isEqualTo(taskComment2.getModified());
  }

  private void testLinks() {
  }
}
