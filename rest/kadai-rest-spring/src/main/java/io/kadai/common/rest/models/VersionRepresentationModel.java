package io.kadai.common.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

@Schema(description = "EntityModel class for version information.")
public class VersionRepresentationModel extends RepresentationModel<VersionRepresentationModel> {

  @Schema(name = "version", description = "The current KADAI version of the REST Service.")
  @NotNull
  private String version;

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
