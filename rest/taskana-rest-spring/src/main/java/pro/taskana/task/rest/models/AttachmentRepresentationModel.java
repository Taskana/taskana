package pro.taskana.task.rest.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

import pro.taskana.task.api.models.Attachment;

/** EntityModel class for {@link Attachment}. */
@Getter
@Setter
public class AttachmentRepresentationModel extends AttachmentSummaryRepresentationModel {

  /** All additional information of the Attachment. */
  private Map<String, String> customAttributes = new HashMap<>();
}
