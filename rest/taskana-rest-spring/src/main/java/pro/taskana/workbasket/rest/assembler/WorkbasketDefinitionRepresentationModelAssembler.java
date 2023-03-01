/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.workbasket.rest.assembler;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
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
        | MismatchedRoleException
        | MismatchedWorkbasketPermissionException e) {
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
