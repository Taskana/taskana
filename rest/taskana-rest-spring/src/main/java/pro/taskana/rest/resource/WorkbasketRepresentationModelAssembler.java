package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/**
 * Transforms {@link Workbasket} to its resource counterpart {@link WorkbasketRepresentationModel}
 * and vice versa.
 */
@Component
public class WorkbasketRepresentationModelAssembler
    implements RepresentationModelAssembler<Workbasket, WorkbasketRepresentationModel> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketRepresentationModelAssembler(
      WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketRepresentationModel toModel(@NonNull Workbasket wb) {
    try {
      WorkbasketRepresentationModel resource = new WorkbasketRepresentationModel(wb);
      return addLinks(resource, wb);
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
  }

  public Workbasket toEntityModel(WorkbasketRepresentationModel wbResource) {
    String wbKey = wbResource.getKey();
    String wbDomain = wbResource.getDomain();
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket(wbKey, wbDomain);
    BeanUtils.copyProperties(wbResource, workbasket);

    workbasket.setId(wbResource.getWorkbasketId());
    if (wbResource.getModified() != null) {
      workbasket.setModified(Instant.parse(wbResource.getModified()));
    }
    if (wbResource.getCreated() != null) {
      workbasket.setCreated(Instant.parse(wbResource.getCreated()));
    }
    return workbasket;
  }

  private WorkbasketRepresentationModel addLinks(
      WorkbasketRepresentationModel resource, Workbasket wb)
      throws NotAuthorizedException, WorkbasketNotFoundException, InvalidArgumentException {
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getDistributionTargets(wb.getId()))
            .withRel("distributionTargets"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(wb.getId()))
            .withRel("accessItems"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbaskets(new LinkedMultiValueMap<>()))
            .withRel("allWorkbaskets"));
    resource.add(
        linkTo(
                methodOn(WorkbasketController.class)
                    .removeDistributionTargetForWorkbasketId(wb.getId()))
            .withRel("removeDistributionTargets"));
    return resource;
  }
}
