package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.internal.WorkbasketImpl;

/**
 * Transforms {@link Workbasket} to its resource counterpart {@link WorkbasketResource} and vice
 * versa.
 */
@Component
public class WorkbasketResourceAssembler
    extends ResourceAssemblerSupport<Workbasket, WorkbasketResource> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketResourceAssembler(WorkbasketService workbasketService) {
    super(WorkbasketController.class, WorkbasketResource.class);
    this.workbasketService = workbasketService;
  }

  public WorkbasketResource toResource(Workbasket wb) {
    try {
      WorkbasketResource resource = new WorkbasketResource(wb);
      return addLinks(resource, wb);
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
  }

  public Workbasket toModel(WorkbasketResource wbResource) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket(wbResource.key, wbResource.domain);
    BeanUtils.copyProperties(wbResource, workbasket);

    workbasket.setId(wbResource.workbasketId);
    if (wbResource.modified != null) {
      workbasket.setModified(Instant.parse(wbResource.modified));
    }
    if (wbResource.created != null) {
      workbasket.setCreated(Instant.parse(wbResource.created));
    }
    return workbasket;
  }

  private WorkbasketResource addLinks(WorkbasketResource resource, Workbasket wb)
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
