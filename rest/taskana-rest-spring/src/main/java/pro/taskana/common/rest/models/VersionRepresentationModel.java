package pro.taskana.common.rest.models;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/** EntityModel class for version information. */
@Getter
@Setter
@ToString
public class VersionRepresentationModel extends RepresentationModel<VersionRepresentationModel> {

  /** The current TASKANA version of the REST Service. */
  @NotNull private String version;
}
