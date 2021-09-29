package pro.taskana.common.rest.models;

import java.beans.ConstructorProperties;
import java.util.Map;
import org.springframework.hateoas.RepresentationModel;

public class CustomAttributesRepresentationModel
    extends RepresentationModel<CustomAttributesRepresentationModel> {

  private final Map<String, Object> customAttributes;

  @ConstructorProperties({"customAttributes"})
  public CustomAttributesRepresentationModel(Map<String, Object> customAttributes) {
    this.customAttributes = customAttributes;
  }

  public Map<String, Object> getCustomAttributes() {
    return customAttributes;
  }
}
