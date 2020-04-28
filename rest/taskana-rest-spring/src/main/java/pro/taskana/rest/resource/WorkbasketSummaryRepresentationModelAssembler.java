package pro.taskana.rest.resource;

import static pro.taskana.rest.resource.TaskanaPagedModelKeys.WORKBASKETS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.rest.Mapping;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/**
 * EntityModel assembler for {@link WorkbasketSummaryRepresentationModel}.
 */
@Component
public class WorkbasketSummaryRepresentationModelAssembler implements
    RepresentationModelAssembler<WorkbasketSummary, WorkbasketSummaryRepresentationModel> {

  private WorkbasketService workbasketService;

  public WorkbasketSummaryRepresentationModelAssembler() {
  }

  @Autowired
  public WorkbasketSummaryRepresentationModelAssembler(WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketSummaryRepresentationModel toModel(
      @NonNull WorkbasketSummary workbasketSummary) {
    return new WorkbasketSummaryRepresentationModel(workbasketSummary);
  }

  public WorkbasketSummary toEntityModel(WorkbasketSummaryRepresentationModel resource) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket(resource.getKey(), resource.getDomain());
    workbasket.setId(resource.getWorkbasketId());
    BeanUtils.copyProperties(resource, workbasket);
    return workbasket.asSummary();
  }

  @PageLinks(Mapping.URL_WORKBASKET)
  public TaskanaPagedModel<WorkbasketSummaryRepresentationModel> toPageModel(
      List<WorkbasketSummary> workbasketSummaries, PageMetadata pageMetadata) {
    return workbasketSummaries.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(getKey(), list, pageMetadata)));
  }

  protected TaskanaPagedModelKeys getKey() {
    return WORKBASKETS;
  }
}
