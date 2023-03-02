package pro.taskana.common.rest.models;

import java.beans.ConstructorProperties;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@RequiredArgsConstructor(onConstructor = @__({@ConstructorProperties({"customAttributes"})}))
public class CustomAttributesRepresentationModel
    extends RepresentationModel<CustomAttributesRepresentationModel> {

  /** The custom configuration attributes. */
  private final Map<String, Object> customAttributes;
}
