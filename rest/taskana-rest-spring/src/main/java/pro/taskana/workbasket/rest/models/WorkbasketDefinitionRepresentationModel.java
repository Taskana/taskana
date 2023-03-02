package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

/** this class represents a workbasket including its distro targets and authorisations. */
@Getter
@Setter
public class WorkbasketDefinitionRepresentationModel
    extends RepresentationModel<WorkbasketDefinitionRepresentationModel> {

  /** The workbasket which is represented. */
  @JsonIgnoreProperties("_links")
  private WorkbasketRepresentationModel workbasket;
  /** The workbasket authorizations. */
  private Collection<WorkbasketAccessItemRepresentationModel> authorizations = new ArrayList<>();
  /** The distribution targets for this workbasket. */
  private Set<String> distributionTargets = new HashSet<>();
}
