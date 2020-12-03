package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import pro.taskana.task.api.models.Task;

/** EntityModel class for {@link Task}. */
@JsonIgnoreProperties("attachmentSummaries")
public class TaskRepresentationModel extends TaskSummaryRepresentationModel {

  // All objects have to be serializable
  /** Additional information of the task. */
  private List<CustomAttribute> customAttributes = Collections.emptyList();

  /** Callback Information of the task. */
  private List<CustomAttribute> callbackInfo = Collections.emptyList();

  /** Attachments of the task. */
  private List<AttachmentRepresentationModel> attachments = new ArrayList<>();

  public List<CustomAttribute> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(List<CustomAttribute> customAttributes) {
    this.customAttributes = customAttributes;
  }

  public List<CustomAttribute> getCallbackInfo() {
    return callbackInfo;
  }

  public void setCallbackInfo(List<CustomAttribute> callbackInfo) {
    this.callbackInfo = callbackInfo;
  }

  public List<AttachmentRepresentationModel> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<AttachmentRepresentationModel> attachments) {
    this.attachments = attachments;
  }

  /**
   * A CustomAttribute is a user customized attribute which is saved as a Map and can be retreived
   * from either {@link Task#getCustomAttributeMap()} or {@link Task#getCallbackInfo()}.
   */
  public static class CustomAttribute {

    /** the key of the custom attribute. */
    private String key;
    /** the value of the custom attribute. */
    private String value;

    public static CustomAttribute of(Entry<String, String> entry) {
      return of(entry.getKey(), entry.getValue());
    }

    public static CustomAttribute of(String key, String value) {
      CustomAttribute customAttribute = new CustomAttribute();
      customAttribute.setKey(key);
      customAttribute.setValue(value);
      return customAttribute;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
