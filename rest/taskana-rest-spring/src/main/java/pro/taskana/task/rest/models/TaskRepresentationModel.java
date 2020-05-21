package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.models.Task;

/**
 * EntityModel class for {@link Task}.
 */
@JsonIgnoreProperties("attachmentSummaries")
public class TaskRepresentationModel extends TaskSummaryRepresentationModel {


  // All objects have to be serializable
  private List<CustomAttribute> customAttributes = Collections.emptyList();
  private List<CustomAttribute> callbackInfo = Collections.emptyList();
  private List<AttachmentRepresentationModel> attachments = new ArrayList<>();

  public TaskRepresentationModel() {
  }

  public TaskRepresentationModel(Task task) throws InvalidArgumentException {
    super(task);
    customAttributes =
        task.getCustomAttributes().entrySet().stream()
            .map(e -> new TaskRepresentationModel.CustomAttribute(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    callbackInfo =
        task.getCallbackInfo().entrySet().stream()
            .map(e -> new TaskRepresentationModel.CustomAttribute(e.getKey(), e.getValue()))
            .collect(Collectors.toList());

    attachments =
        task.getAttachments().stream()
            .map(AttachmentRepresentationModel::new)
            .collect(Collectors.toList());
  }

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

  @Override
  public String toString() {
    return "TaskResource ["
               + "taskId= "
               + this.taskId
               + "externalId= "
               + this.externalId
               + "created= "
               + this.created
               + "modified= "
               + this.modified
               + "claimed= "
               + this.claimed
               + "completed= "
               + this.completed
               + "planned= "
               + this.planned
               + "due= "
               + this.due
               + "name= "
               + this.name
               + "creator= "
               + this.creator
               + "description= "
               + this.description
               + "priority= "
               + this.priority
               + "owner= "
               + this.owner
               + "]";
  }

  /**
   * A CustomAttribute is a user customized attribute which is saved as a Map and can be retreived
   * from either {@link Task#getCustomAttributes()} or {@link Task#getCallbackInfo()}.
   */
  public static class CustomAttribute {

    private final String key;
    private final String value;

    @SuppressWarnings("unused")
    public CustomAttribute() {
      this(null, null);
      // necessary for jackson.
    }

    public CustomAttribute(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return "CustomAttribute [" + "key= " + this.key + "value= " + this.value + "]";
    }
  }
}
