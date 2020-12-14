package pro.taskana.common.rest.models;

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/** EntityModel class for version information. */
public class VersionRepresentationModel extends RepresentationModel<VersionRepresentationModel> {

  /** The current TASKANA version of the REST Service. */
  @NotNull private String version;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public @NonNull String toString() {
    return "VersionResource [" + "version= " + this.version + "]";
  }
}
