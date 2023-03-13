package pro.taskana.task.rest.models;

import java.util.HashMap;
import java.util.Map;
import pro.taskana.task.api.models.Attachment;

/** EntityModel class for {@link Attachment}. */
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
