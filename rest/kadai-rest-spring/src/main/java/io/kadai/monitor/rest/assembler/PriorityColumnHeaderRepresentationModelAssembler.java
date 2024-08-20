package io.kadai.monitor.rest.assembler;

import io.kadai.monitor.api.reports.header.PriorityColumnHeader;
import io.kadai.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PriorityColumnHeaderRepresentationModelAssembler
    implements RepresentationModelAssembler<
        PriorityColumnHeader, PriorityColumnHeaderRepresentationModel> {

  @Override
  @NonNull
  public PriorityColumnHeaderRepresentationModel toModel(@NonNull PriorityColumnHeader entity) {
    return new PriorityColumnHeaderRepresentationModel(
        entity.getLowerBoundInc(), entity.getUpperBoundInc());
  }

  public PriorityColumnHeader toEntityModel(PriorityColumnHeaderRepresentationModel repModel) {
    return new PriorityColumnHeader(repModel.getLowerBound(), repModel.getUpperBound());
  }
}
