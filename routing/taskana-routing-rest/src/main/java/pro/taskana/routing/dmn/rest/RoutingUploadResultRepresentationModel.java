package pro.taskana.routing.dmn.rest;

import org.springframework.hateoas.RepresentationModel;

/** Model class for a routing upload result. */
public class RoutingUploadResultRepresentationModel
    extends RepresentationModel<RoutingUploadResultRepresentationModel> {

  protected int amountOfImportedRows;

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
