package pro.taskana.rest.resource;

import java.util.List;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.task.api.models.TaskSummary;

/** EntityModel assembler for {@link TaskSummaryResource}. */
@Component
public class TaskSummaryResourceAssembler
    extends RepresentationModelAssemblerSupport<TaskSummary, TaskSummaryResource> {

  public TaskSummaryResourceAssembler() {
    super(TaskController.class, TaskSummaryResource.class);
  }

  @Override
  public TaskSummaryResource toModel(TaskSummary taskSummary) {
    TaskSummaryResource resource;
    try {
      resource = new TaskSummaryResource(taskSummary);
      return resource;
    } catch (InvalidArgumentException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
  }

  @PageLinks(Mapping.URL_TASKS)
  public TaskSummaryListResource toCollectionModel(
      List<TaskSummary> taskSummaries, PageMetadata pageMetadata) {
    return new TaskSummaryListResource(toCollectionModel(taskSummaries).getContent(), pageMetadata);
  }
}
