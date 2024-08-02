package pro.taskana.routing.dmn.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

/** Model class for a routing upload result. */
public class RoutingUploadResultRepresentationModel
    extends RepresentationModel<RoutingUploadResultRepresentationModel> {

  @Schema(
      name = "amountOfImportedRows",
      description = "The total amount of imported rows from the provided excel sheet.")
  protected int amountOfImportedRows;

  @Schema(
      name = "result",
      description = "A human readable String that contains the amount of imported rows.")
  protected String result;

  public int getAmountOfImportedRows() {
    return amountOfImportedRows;
  }

  public void setAmountOfImportedRows(int amountOfImportedRows) {
    this.amountOfImportedRows = amountOfImportedRows;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
