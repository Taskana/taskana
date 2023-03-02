package pro.taskana.monitor.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@JsonIgnoreProperties("links")
@Getter
@RequiredArgsConstructor(
    onConstructor = @__({@ConstructorProperties({"lowerBound", "upperBound"})}))
public class PriorityColumnHeaderRepresentationModel
    extends RepresentationModel<PriorityColumnHeaderRepresentationModel> {

  /** Determine the lower priority for this column header. This value is inclusive. */
  private final int lowerBound;

  /** Determine the upper priority for this column header. This value is inclusive. */
  private final int upperBound;
}
