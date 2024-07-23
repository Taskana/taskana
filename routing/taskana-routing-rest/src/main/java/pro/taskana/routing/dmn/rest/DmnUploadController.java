package pro.taskana.routing.dmn.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.routing.dmn.service.DmnConverterService;

/** Controller for all DMN upload related endpoints. */
@RestController
public class DmnUploadController {

  private final DmnConverterService dmnConverterService;

  @Autowired
  public DmnUploadController(DmnConverterService dmnConverterService) {
    this.dmnConverterService = dmnConverterService;
  }

  /**
   * This endpoint converts an excel file to a DMN table and saves it on the filesystem.
   *
   * @param excelRoutingFile the excel file containing the routing rules
   * @return the result of the upload
   * @throws NotAuthorizedException if the current user is not authorized to upload/convert an excel
   *     file
   * @throws IOException if there is an I/O problem with the provided excel file
   */
  @Operation(
      summary =
          "This endpoint converts an excel file to a DMN table and saves it on the filesystem.",
      requestBody =
          @RequestBody(
              description =
                  "the excel file containing the routing rules<p> To try the request, copy the "
                      + "following to an excel file and save it in .xlsx format with any name, then"
                      + " upload the file"
                      + "<table>"
                      + "<thead>"
                      + "<tr>"
                      + "<th></th><th>Input</th><th>Input</th><th>Output</th><th>Output</th>"
                      + "<th></th>"
                      + "</tr>"
                      + "</thead>"
                      + "<tbody>"
                      + "<tr>"
                      + "<td></td>"
                      + "<td>BKN</td>"
                      + "<td>ClassificationKey</td>"
                      + "<td>workbasketKey</td>"
                      + "<td>domain</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td></td>"
                      + "<td>JUEL</td>"
                      + "<td>javascript</td>"
                      + "<td>Expression</td>"
                      + "<td>Expression</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td></td>"
                      + "<td>task.primaryObjRef.value</td>"
                      + "<td>task.classificationSummary.key + task.note</td>"
                      + "<td>workbasketKey</td>"
                      + "<td>domain</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td>FIRST</td>"
                      + "<td>string</td>"
                      + "<td>string</td>"
                      + "<td>string</td>"
                      + "<td>string</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td>1</td>"
                      + "<td>6260203</td>"
                      + "<td></td>"
                      + "<td>GPK_KSC</td>"
                      + "<td>DOMAIN_A</td>"
                      + "<td>VIP-Team</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td>2</td>"
                      + "<td></td>"
                      + "<td>matches(cellInput,\"11048|12012|12013|12513|12523|12619|12910<br>"
                      + "|12911|12912|12913|12914|12915|12916|12917|12918|12919|12920<br>"
                      + "|12921|12922|12923|12924|12925|12926|12927|12928|12929|12930<br>"
                      + "|12931|12932|12933|12934|13082|13093|17999|19012\")</td>"
                      + "<td>GPK_KSC</td>"
                      + "<td>DOMAIN_A</td>"
                      + "<td>Second-Level Team 1</td>"
                      + "</tr>"
                      + "<tr>"
                      + "<td>3</td>"
                      + "<td>12345678</td>"
                      + "<td></td>"
                      + "<td>GPK_KSC</td>"
                      + "<td>DOMAIN_A</td>"
                      + "</tr>"
                      + "</tbody></table>",
              required = true,
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the result of the upload",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = RoutingUploadResultRepresentationModel.class))
            })
      })
  @PutMapping(
      path = RoutingRestEndpoints.URL_ROUTING_RULES_DEFAULT,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<RoutingUploadResultRepresentationModel> convertAndUpload(
      @RequestParam("excelRoutingFile") MultipartFile excelRoutingFile)
      throws IOException, NotAuthorizedException {

    DmnModelInstance dmnModelInstance = dmnConverterService.convertExcelToDmn(excelRoutingFile);

    int importedRows = dmnModelInstance.getModelElementsByType(Rule.class).size();

    RoutingUploadResultRepresentationModel model = new RoutingUploadResultRepresentationModel();
    model.setAmountOfImportedRows(importedRows);
    model.setResult(
        "Successfully imported " + importedRows + " routing rules from the provided excel sheet");

    return ResponseEntity.ok(model);
  }

  /**
   * This endpoint checks if the taskana-routing-rest is in use.
   *
   * @return true, when the taskana-routing-rest is enabled, otherwise false
   */
  @Operation(
      summary = "This endpoint checks if the taskana-routing-rest is in use.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "true, when the taskana-routing-rest is enabled, otherwise false",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = Boolean.class))
            })
      })
  @GetMapping(path = RoutingRestEndpoints.ROUTING_REST_ENABLED)
  public ResponseEntity<Boolean> getIsRoutingRestEnabled() {
    return ResponseEntity.ok(true);
  }
}
