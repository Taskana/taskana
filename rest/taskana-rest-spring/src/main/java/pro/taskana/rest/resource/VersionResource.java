package pro.taskana.rest.resource;

import org.springframework.hateoas.RepresentationModel;

/** EntityModel class for version information. */
public class VersionResource extends RepresentationModel<VersionResource> {

  private String version;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "VersionResource [" + "version= " + this.version + "]";
  }
}
