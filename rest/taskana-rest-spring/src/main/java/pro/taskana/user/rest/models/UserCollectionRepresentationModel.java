package pro.taskana.user.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class UserCollectionRepresentationModel
    extends CollectionRepresentationModel<UserRepresentationModel> {
  @ConstructorProperties("users")
  public UserCollectionRepresentationModel(Collection<UserRepresentationModel> content) {
    super(content);
  }

  /** The embedded users. */
  @Schema(name = "users", description = "The embedded users.")
  @JsonProperty("users")
  @Override
  public Collection<UserRepresentationModel> getContent() {
    return super.getContent();
  }
}
