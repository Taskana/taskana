package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pro.taskana.rest.resource.TaskanaPagedModelKeys.TASK_COMMENTS;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.TaskCommentController;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;

/** EntityModel assembler for {@link TaskCommentRepresentationModel}. */
@Component
public class TaskCommentRepresentationModelAssembler
    implements RepresentationModelAssembler<TaskComment, TaskCommentRepresentationModel> {

  private final TaskService taskService;

  @Autowired
  public TaskCommentRepresentationModelAssembler(TaskService taskService) {
    this.taskService = taskService;
  }

  @NonNull
  @Override
  public TaskCommentRepresentationModel toModel(@NonNull TaskComment taskComment) {
    TaskCommentRepresentationModel taskCommentRepresentationModel =
        new TaskCommentRepresentationModel(taskComment);
    try {
      taskCommentRepresentationModel.add(
          linkTo(methodOn(TaskCommentController.class).getTaskComment(taskComment.getId()))
              .withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }

    return taskCommentRepresentationModel;
  }

  public TaskComment toEntityModel(TaskCommentRepresentationModel taskCommentRepresentationModel) {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment(taskCommentRepresentationModel.getTaskId());
    taskComment.setId(taskCommentRepresentationModel.getTaskCommentId());

    BeanUtils.copyProperties(taskCommentRepresentationModel, taskComment);

    if (taskCommentRepresentationModel.getCreated() != null) {
      taskComment.setCreated(Instant.parse(taskCommentRepresentationModel.getCreated()));
    }
    if (taskCommentRepresentationModel.getModified() != null) {
      taskComment.setModified(Instant.parse(taskCommentRepresentationModel.getModified()));
    }

    return taskComment;
  }

  @PageLinks(Mapping.URL_TASK_COMMENTS)
  public TaskanaPagedModel<TaskCommentRepresentationModel> toPageModel(
      List<TaskComment> taskComments, PageMetadata pageMetadata) {
    return taskComments.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(TASK_COMMENTS, list, pageMetadata)));
  }
}
