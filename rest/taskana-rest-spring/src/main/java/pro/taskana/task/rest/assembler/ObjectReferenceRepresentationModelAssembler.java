package pro.taskana.task.rest.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;

@Controller
public class ObjectReferenceRepresentationModelAssembler
    implements RepresentationModelAssembler<ObjectReference, ObjectReferenceRepresentationModel> {

  @Override
  @NonNull
  public ObjectReferenceRepresentationModel toModel(@NonNull ObjectReference entity) {
    ObjectReferenceRepresentationModel repModel = new ObjectReferenceRepresentationModel();
    repModel.setId(entity.getId());
    repModel.setTaskId(entity.getTaskId());
    repModel.setCompany(entity.getCompany());
    repModel.setSystem(entity.getSystem());
    repModel.setSystemInstance(entity.getSystemInstance());
    repModel.setType(entity.getType());
    repModel.setValue(entity.getValue());
    return repModel;
  }

  public ObjectReference toEntity(ObjectReferenceRepresentationModel repModel) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setId(repModel.getId());
    objectReference.setTaskId(repModel.getTaskId());
    objectReference.setCompany(repModel.getCompany());
    objectReference.setSystem(repModel.getSystem());
    objectReference.setSystemInstance(repModel.getSystemInstance());
    objectReference.setType(repModel.getType());
    objectReference.setValue(repModel.getValue());
    return objectReference;
  }
}
