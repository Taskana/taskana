package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.TaskCommentController;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;

/** Resource assembler for {@link TaskCommentResource}. */
@Component
public class TaskCommentResourceAssembler
    extends ResourceAssemblerSupport<TaskComment, TaskCommentResource> {

  private final TaskService taskService;

  @Autowired
  public TaskCommentResourceAssembler(TaskService taskService) {
    super(TaskCommentController.class, TaskCommentResource.class);
    this.taskService = taskService;
  }

  @Override
  public TaskCommentResource toResource(TaskComment taskComment) {

    TaskCommentResource taskCommentResource = new TaskCommentResource(taskComment);
    try {
      taskCommentResource.add(
          linkTo(methodOn(TaskCommentController.class).getTaskComment(taskComment.getId()))
              .withSelfRel());
    } catch (TaskCommentNotFoundException
        | TaskNotFoundException
        | NotAuthorizedException
        | InvalidArgumentException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }

    return taskCommentResource;
  }

  @PageLinks(Mapping.URL_TASK_COMMENTS)
  public TaskCommentListResource toListResource(
      List<TaskComment> taskComments) {
    return new TaskCommentListResource(toResources(taskComments));
  }

  public TaskComment toModel(TaskCommentResource taskCommentResource) {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment(taskCommentResource.getTaskId());
    taskComment.setId(taskCommentResource.getTaskCommentId());

    BeanUtils.copyProperties(taskCommentResource, taskComment);

    if (taskCommentResource.getCreated() != null) {
      taskComment.setCreated(Instant.parse(taskCommentResource.getCreated()));
    }
    if (taskCommentResource.getModified() != null) {
      taskComment.setModified(Instant.parse(taskCommentResource.getModified()));
    }

    return taskComment;
  }
}
