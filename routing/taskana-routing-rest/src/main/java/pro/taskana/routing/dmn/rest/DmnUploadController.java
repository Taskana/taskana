/*-
 * #%L
 * pro.taskana:taskana-routing-rest
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.routing.dmn.rest;

import java.io.IOException;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
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
   * @throws MismatchedRoleException if the current user is not authorized to upload/convert an
   *     excel file
   * @throws IOException if there is an I/O problem with the provided excel file
   */
  @PutMapping(RoutingRestEndpoints.URL_ROUTING_RULES_DEFAULT)
  public ResponseEntity<RoutingUploadResultRepresentationModel> convertAndUpload(
      @RequestParam("excelRoutingFile") MultipartFile excelRoutingFile)
      throws IOException, MismatchedRoleException {

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
  @GetMapping(path = RoutingRestEndpoints.ROUTING_REST_ENABLED)
  public ResponseEntity<Boolean> getIsRoutingRestEnabled() {
    return ResponseEntity.ok(true);
  }
}
