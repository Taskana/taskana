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
  private List<CustomAttribute> customAttributes = Collections.emptyList();
  private List<CustomAttribute> callbackInfo = Collections.emptyList();
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
   * from either {@link Task#getCustomAttributes()} or {@link Task#getCallbackInfo()}.
   */
  public static class CustomAttribute {

    private String key;
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
