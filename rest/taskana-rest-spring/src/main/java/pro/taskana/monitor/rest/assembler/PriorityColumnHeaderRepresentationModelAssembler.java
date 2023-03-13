package pro.taskana.monitor.rest.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;

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
