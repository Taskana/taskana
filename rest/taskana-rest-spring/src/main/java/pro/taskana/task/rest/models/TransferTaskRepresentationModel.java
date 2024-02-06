package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.List;

public class TransferTaskRepresentationModel {

  /** The value to set the Task property owner. */
  @JsonProperty("owner")
  private final String owner;

  /** The value to set the Task property setTransferFlag. */
  @JsonProperty("setTransferFlag")
  private final Boolean setTransferFlag;

  /** The value to set the Task property taskIds. */
  @JsonProperty("taskIds")
  private final List<String> taskIds;

  @ConstructorProperties({"setTransferFlag", "owner", "taskIds"})
  public TransferTaskRepresentationModel(
      Boolean setTransferFlag, String owner, List<String> taskIds) {
    this.setTransferFlag = setTransferFlag == null || setTransferFlag;
    this.owner = owner;
    this.taskIds = taskIds;
  }

  public Boolean getSetTransferFlag() {
    return setTransferFlag;
  }

  public String getOwner() {
    return owner;
  }

  public List<String> getTaskIds() {
    return taskIds;
  }
}
