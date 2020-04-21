package pro.taskana.rest.resource;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.rest.Mapping;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/** EntityModel assembler for {@link WorkbasketSummaryResource}. */
@Component
public class WorkbasketSummaryResourceAssembler
    extends RepresentationModelAssemblerSupport<WorkbasketSummary, WorkbasketSummaryResource> {

  @Autowired private WorkbasketService workbasketService;

  public WorkbasketSummaryResourceAssembler() {
    super(WorkbasketController.class, WorkbasketSummaryResource.class);
  }

  @PageLinks(Mapping.URL_WORKBASKET)
  public WorkbasketSummaryListResource toCollectionModel(
      List<WorkbasketSummary> entities, PageMetadata pageMetadata) {
    return new WorkbasketSummaryListResource(
        toCollectionModel(entities).getContent(), pageMetadata);
  }

  @Override
  public WorkbasketSummaryResource toModel(WorkbasketSummary workbasketSummary) {
    return new WorkbasketSummaryResource(workbasketSummary);
  }

  public WorkbasketSummary toModel(WorkbasketSummaryResource resource) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket(resource.getKey(), resource.getDomain());
    workbasket.setId(resource.getWorkbasketId());
    BeanUtils.copyProperties(resource, workbasket);
    return workbasket.asSummary();
  }
}
