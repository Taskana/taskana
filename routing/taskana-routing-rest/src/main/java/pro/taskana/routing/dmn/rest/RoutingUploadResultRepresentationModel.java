package pro.taskana.routing.dmn.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

/** Model class for a routing upload result. */
@Getter
@Setter
public class RoutingUploadResultRepresentationModel
    extends RepresentationModel<RoutingUploadResultRepresentationModel> {

  /** The total amount of imported rows from the provided excel sheet. */
  protected int amountOfImportedRows;

  /** A human readable String that contains the amount of imported rows. */
  protected String result;
}
