package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.TASK_COMMENTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.assembler.TaskanaPagingAssembler;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.rest.TaskCommentController;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** EntityModel assembler for {@link TaskCommentRepresentationModel}. */
@Component
public class TaskCommentRepresentationModelAssembler
    implements TaskanaPagingAssembler<TaskComment, TaskCommentRepresentationModel> {

  private final TaskService taskService;

  @Autowired
  public TaskCommentRepresentationModelAssembler(TaskService taskService) {
    this.taskService = taskService;
  }

  @NonNull
  @Override
  public TaskCommentRepresentationModel toModel(@NonNull TaskComment taskComment) {
    TaskCommentRepresentationModel repModel = new TaskCommentRepresentationModel();
    repModel.setTaskCommentId(taskComment.getId());
    repModel.setTaskId(taskComment.getTaskId());
    repModel.setTextField(taskComment.getTextField());
    repModel.setCreator(taskComment.getCreator());
    repModel.setCreated(taskComment.getCreated());
    repModel.setModified(taskComment.getModified());
    try {
      repModel.add(
          linkTo(methodOn(TaskCommentController.class).getTaskComment(taskComment.getId()))
              .withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  public TaskComment toEntityModel(TaskCommentRepresentationModel repModel) {
    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment(repModel.getTaskId());
    taskComment.setId(repModel.getTaskCommentId());
    taskComment.setTextField(repModel.getTextField());
    taskComment.setCreator(repModel.getCreator());
    taskComment.setCreated(repModel.getCreated());
    taskComment.setModified(repModel.getModified());
    return taskComment;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return TASK_COMMENTS;
  }

  @Override
  public TaskanaPagedModel<TaskCommentRepresentationModel> toPageModel(
      Iterable<TaskComment> taskComments, PageMetadata pageMetadata) {
    return addLinksToPagedResource(
        TaskanaPagingAssembler.super.toPageModel(taskComments, pageMetadata));
  }
}
