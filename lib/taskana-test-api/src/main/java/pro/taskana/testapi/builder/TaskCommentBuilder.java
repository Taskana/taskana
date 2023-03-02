package pro.taskana.testapi.builder;

import java.time.Instant;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;

public class TaskCommentBuilder implements EntityBuilder<TaskComment, TaskService> {

  private final TaskCommentTestImpl testTaskComment = new TaskCommentTestImpl();

  public static TaskCommentBuilder newTaskComment() {
    return new TaskCommentBuilder();
  }

  public TaskCommentBuilder taskId(String taskId) {
    testTaskComment.setTaskId(taskId);
    return this;
  }

  public TaskCommentBuilder textField(String textField) {
    testTaskComment.setTextField(textField);
    return this;
  }

  public TaskCommentBuilder created(Instant created) {
    testTaskComment.setCreatedIgnoringFreeze(created);
    if (created != null) {
      testTaskComment.freezeCreated();
    } else {
      testTaskComment.unfreezeCreated();
    }
    return this;
  }

  public TaskCommentBuilder modified(Instant modified) {
    testTaskComment.setModifiedIgnoringFreeze(modified);
    if (modified != null) {
      testTaskComment.freezeModified();
    } else {
      testTaskComment.unfreezeModified();
    }
    return this;
  }

  @Override
  public TaskComment buildAndStore(TaskService taskService)
      throws InvalidArgumentException, TaskNotFoundException, TaskCommentNotFoundException,
          NotAuthorizedOnWorkbasketException {
    try {
      TaskComment t = taskService.createTaskComment(testTaskComment);
      return taskService.getTaskComment(t.getId());
    } finally {
      testTaskComment.setId(null);
    }
  }
}
