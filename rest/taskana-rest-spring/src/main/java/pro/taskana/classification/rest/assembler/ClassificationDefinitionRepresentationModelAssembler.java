package pro.taskana.classification.rest.assembler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.rest.models.ClassificationDefinitionRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;

@Component
public class ClassificationDefinitionRepresentationModelAssembler
    implements RepresentationModelAssembler<
            Classification, ClassificationDefinitionRepresentationModel>,
        CollectionRepresentationModelAssembler<
            Classification,
            ClassificationDefinitionRepresentationModel,
            ClassificationDefinitionCollectionRepresentationModel> {

  private final ClassificationRepresentationModelAssembler classificationAssembler;

  @Autowired
  public ClassificationDefinitionRepresentationModelAssembler(
      ClassificationRepresentationModelAssembler classificationAssembler) {
    this.classificationAssembler = classificationAssembler;
  }

  @Override
  @NonNull
  public ClassificationDefinitionRepresentationModel toModel(
      @NonNull Classification classification) {
    ClassificationRepresentationModel classificationRepModel =
        classificationAssembler.toModel(classification);
    return new ClassificationDefinitionRepresentationModel(classificationRepModel);
  }

  @Override
  public ClassificationDefinitionCollectionRepresentationModel buildCollectionEntity(
      List<ClassificationDefinitionRepresentationModel> content) {
    return new ClassificationDefinitionCollectionRepresentationModel(content);
  }

  public Classification toEntityModel(ClassificationDefinitionRepresentationModel repModel) {
    return classificationAssembler.toEntityModel(repModel.getClassification());
  }
}
