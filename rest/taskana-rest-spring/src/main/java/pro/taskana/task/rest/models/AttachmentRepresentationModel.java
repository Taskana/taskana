package pro.taskana.task.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.Map;

@Schema(description = "EntityModel class for Attachment")
public class AttachmentRepresentationModel extends AttachmentSummaryRepresentationModel {

  /** All additional information of the Attachment. */
  private Map<String, String> customAttributes = new HashMap<>();

  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }
}
