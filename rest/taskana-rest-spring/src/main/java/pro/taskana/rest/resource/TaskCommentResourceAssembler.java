package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
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

/** EntityModel assembler for {@link TaskCommentResource}. */
@Component
public class TaskCommentResourceAssembler
    extends RepresentationModelAssemblerSupport<TaskComment, TaskCommentResource> {

  private final TaskService taskService;

  @Autowired
  public TaskCommentResourceAssembler(TaskService taskService) {
    super(TaskCommentController.class, TaskCommentResource.class);
    this.taskService = taskService;
  }

  @PageLinks(Mapping.URL_TASK_COMMENTS)
  public TaskCommentListResource toListResource(List<TaskComment> taskComments) {
    Collection<TaskCommentResource> col = toCollectionModel(taskComments).getContent();
    List<TaskCommentResource> resourceList = new ArrayList<>(col);
    return new TaskCommentListResource(resourceList);
  }

  @Override
  public TaskCommentResource toModel(TaskComment taskComment) {

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
