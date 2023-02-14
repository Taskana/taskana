package pro.taskana.common.rest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/** EntityModel for Access Id. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessIdRepresentationModel extends RepresentationModel<AccessIdRepresentationModel> {

  /** The name of this Access Id. */
  private String name;
  /**
   * The value of the Access Id. This value will be used to determine the access to a workbasket.
   */
  private String accessId;
}
