package io.kadai.workbasket.rest.assembler;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.rest.assembler.CollectionRepresentationModelAssembler;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketDefinitionCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketRepresentationModel;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
