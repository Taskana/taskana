package pro.taskana.monitor.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.beans.ConstructorProperties;
import org.springframework.hateoas.RepresentationModel;

@JsonIgnoreProperties("links")
public class PriorityColumnHeaderRepresentationModel
    extends RepresentationModel<PriorityColumnHeaderRepresentationModel> {

  /** Determine the lower priority for this column header. This value is inclusive. */
  private final int lowerBound;

  /** Determine the upper priority for this column header. This value is inclusive. */
  private final int upperBound;

  @ConstructorProperties({"lowerBound", "upperBound"})
  public PriorityColumnHeaderRepresentationModel(int lowerBound, int upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  public int getLowerBound() {
    return lowerBound;
  }

  public int getUpperBound() {
    return upperBound;
  }
}
