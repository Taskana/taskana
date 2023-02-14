package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@RequiredArgsConstructor(onConstructor = @__({@JsonCreator}))
public class ClassificationDefinitionRepresentationModel
    extends RepresentationModel<ClassificationDefinitionRepresentationModel> {

  @JsonIgnoreProperties("_links")
  @JsonUnwrapped
  private final ClassificationRepresentationModel classification;
}
