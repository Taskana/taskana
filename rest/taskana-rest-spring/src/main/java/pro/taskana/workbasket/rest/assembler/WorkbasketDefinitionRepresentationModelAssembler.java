package pro.taskana.workbasket.rest.assembler;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;

/**
 * Transforms {@link Workbasket} into a {@link WorkbasketDefinitionRepresentationModel} containing
 * all additional information about that workbasket.
 */
@Component
public class WorkbasketDefinitionRepresentationModelAssembler
    implements CollectionRepresentationModelAssembler<
        Workbasket,
        WorkbasketDefinitionRepresentationModel,
        WorkbasketDefinitionCollectionRepresentationModel> {

  private final WorkbasketService workbasketService;
  private final WorkbasketAccessItemRepresentationModelAssembler accessItemAssembler;
  private final WorkbasketRepresentationModelAssembler workbasketAssembler;

  @Autowired
  public WorkbasketDefinitionRepresentationModelAssembler(
      WorkbasketService workbasketService,
      WorkbasketAccessItemRepresentationModelAssembler accessItemAssembler,
      WorkbasketRepresentationModelAssembler workbasketAssembler) {
    this.workbasketService = workbasketService;
    this.accessItemAssembler = accessItemAssembler;
    this.workbasketAssembler = workbasketAssembler;
  }

  @NonNull
  public WorkbasketDefinitionRepresentationModel toModel(@NonNull Workbasket workbasket) {
    WorkbasketRepresentationModel basket = workbasketAssembler.toModel(workbasket);
    Collection<WorkbasketAccessItemRepresentationModel> authorizations;
    Set<String> distroTargets;
    try {
      List<WorkbasketAccessItem> workbasketAccessItems =
          workbasketService.getWorkbasketAccessItems(basket.getWorkbasketId());
      authorizations = accessItemAssembler.toCollectionModel(workbasketAccessItems).getContent();
      distroTargets =
          workbasketService.getDistributionTargets(workbasket.getId()).stream()
              .map(WorkbasketSummary::getId)
              .collect(Collectors.toSet());
    } catch (WorkbasketNotFoundException
        | NotAuthorizedException
        | NotAuthorizedOnWorkbasketException e) {
      throw new SystemException("Caught Exception", e);
    }

    WorkbasketDefinitionRepresentationModel repModel =
        new WorkbasketDefinitionRepresentationModel();

    repModel.setWorkbasket(basket);
    repModel.setAuthorizations(authorizations);
    repModel.setDistributionTargets(distroTargets);
    return repModel;
  }

  @Override
  public WorkbasketDefinitionCollectionRepresentationModel buildCollectionEntity(
      List<WorkbasketDefinitionRepresentationModel> content) {
    return new WorkbasketDefinitionCollectionRepresentationModel(content);
  }
}
