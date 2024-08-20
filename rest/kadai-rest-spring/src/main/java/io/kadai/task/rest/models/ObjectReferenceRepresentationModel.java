package io.kadai.task.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

public class ObjectReferenceRepresentationModel
    extends RepresentationModel<ObjectReferenceRepresentationModel> {

  @Schema(description = "Unique ID.")
  private String id;

  @Schema(description = "Task Id.")
  private String taskId;

  @Schema(description = "The company the referenced primary object belongs to.")
  private String company;

  @Schema(
      description =
          "The (kind of) system, the referenced primary object resides in (e.g. SAP, MySystem A, "
              + "...).")
  private String system;

  @Schema(
      description = "The instance of the system where the referenced primary object is located.")
  private String systemInstance;

  @Schema(
      description =
          "The type of the referenced primary object (contract, claim, policy, customer, ...).")
  private String type;

  @Schema(description = "The value of the primary object reference.")
  private String value;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getSystemInstance() {
    return systemInstance;
  }

  public void setSystemInstance(String systemInstance) {
    this.systemInstance = systemInstance;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
