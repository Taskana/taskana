package pro.taskana.common.rest.models;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/** EntityModel for access id validation. */
public class AccessIdRepresentationModel extends RepresentationModel<AccessIdRepresentationModel> {

  /** The name of this Access Id. */
  private String name;
  /**
   * The value of the Access Id. This value will be used to determine the access to a workbasket.
   */
  private String accessId;

  public AccessIdRepresentationModel() {}

  public AccessIdRepresentationModel(String name, String accessId) {
    this.accessId = accessId;
    this.name = name;
  }

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public @NonNull String toString() {
    return "AccessIdResource [" + "name=" + this.name + ", accessId=" + this.accessId + "]";
  }
}
