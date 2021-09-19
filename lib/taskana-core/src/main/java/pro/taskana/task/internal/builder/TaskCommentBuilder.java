package pro.taskana.task.internal.builder;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import javax.security.auth.Subject;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;

public class TaskCommentBuilder {

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

  public TaskComment buildAndStore(TaskService taskService)
      throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException,
          TaskCommentNotFoundException {
    try {
      TaskComment t = taskService.createTaskComment(testTaskComment);
      return taskService.getTaskComment(t.getId());
    } finally {
      testTaskComment.setId(null);
    }
  }

  public TaskComment buildAndStore(TaskService taskService, String userId)
      throws PrivilegedActionException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(userId));
    PrivilegedExceptionAction<TaskComment> performBuildAndStore = () -> buildAndStore(taskService);

    return Subject.doAs(subject, performBuildAndStore);
  }
}
