package pro.taskana.routing.dmn.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.camunda.bpm.dmn.xlsx.AdvancedSpreadsheetAdapter;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.routing.dmn.service.util.InputEntriesSanitizer;
import pro.taskana.routing.dmn.spi.internal.DmnValidatorManager;
import pro.taskana.workbasket.api.WorkbasketService;

/** This class converts an Excel file with routing roules to a DMN table. */
@Service
public class DmnConverterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DmnConverterService.class);

  private final TaskanaEngine taskanaEngine;

  @Value("${taskana.routing.dmn.upload.path}")
  private String dmnUploadPath;

  @Autowired
  public DmnConverterService(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    DmnValidatorManager.getInstance(taskanaEngine);
  }

  public String getDmnUploadPath() {
    return dmnUploadPath;
  }

  public void setDmnUploadPath(String dmnUploadPath) {
    this.dmnUploadPath = dmnUploadPath;
  }

  public DmnModelInstance convertExcelToDmn(MultipartFile excelRoutingFile)
      throws IOException, NotAuthorizedException {

    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    StringBuilder serializedRules = new StringBuilder();

    try (InputStream inputStream = new BufferedInputStream(excelRoutingFile.getInputStream())) {

      XlsxConverter converter = new XlsxConverter();
      converter.setIoDetectionStrategy(new AdvancedSpreadsheetAdapter());

      DmnModelInstance dmnModelInstance = converter.convert(inputStream, serializedRules);
      DmnModelInstance patchedModel =
          mergeSerializedRulesIntoDmn(dmnModelInstance, serializedRules);
      validateOutputs(patchedModel);

      InputEntriesSanitizer.sanitizeRegexInsideInputEntries(patchedModel);

      if (DmnValidatorManager.isDmnUploadProviderEnabled()) {
        DmnValidatorManager.getInstance(taskanaEngine).validate(patchedModel);
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(String.format("Persisting generated DMN table to %s", dmnUploadPath));
      }

      File uploadDestinationFile = new File(dmnUploadPath);
      Dmn.writeModelToFile(uploadDestinationFile, patchedModel);

      return patchedModel;
    }
  }

  private DmnModelInstance mergeSerializedRulesIntoDmn(
      DmnModelInstance originalInstance, StringBuilder serializedRules) {
    String dmnString = Dmn.convertToString(originalInstance);
    StringBuilder finalDmn = new StringBuilder();
    int splitPosition = dmnString.indexOf("</decisionTable>");
    finalDmn.append(dmnString, 0, splitPosition);
    finalDmn.append(serializedRules);
    finalDmn.append(dmnString.substring(splitPosition));
    DmnModelInstance patchedModel =
        Dmn.readModelFromStream(new ByteArrayInputStream(finalDmn.toString().getBytes()));

    return patchedModel;
  }

  private Set<KeyDomain> getOutputKeyDomains(DmnModelInstance dmnModel) {
    Set<KeyDomain> outputKeyDomains = new HashSet<>();

    for (Rule rule : dmnModel.getModelElementsByType(Rule.class)) {

      List<OutputEntry> outputEntries = new ArrayList<>(rule.getOutputEntries());
      String workbasketKey = outputEntries.get(0).getTextContent().replaceAll("(^\")|(\"$)", "");
      String domain = outputEntries.get(1).getTextContent().replaceAll("(^\")|(\"$)", "");
      outputKeyDomains.add(new KeyDomain(workbasketKey, domain));
    }
    return outputKeyDomains;
  }

  private void validateOutputs(DmnModelInstance dmnModel) {
    Set<KeyDomain> outputKeyDomains = getOutputKeyDomains(dmnModel);
    Set<KeyDomain> existingKeyDomains = getExistingKeyDomains();
    outputKeyDomains.removeAll(existingKeyDomains);

    if (!outputKeyDomains.isEmpty()) {
      throw new SystemException(
          String.format(
              "Unknown workbasket Key/Domain pairs defined in DMN Table: %s", outputKeyDomains));
    }
  }

  private Set<KeyDomain> getExistingKeyDomains() {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    return taskanaEngine.runAsAdmin(
        () ->
            workbasketService.createWorkbasketQuery().list().stream()
                .map(
                    workbasketSummary ->
                        new KeyDomain(workbasketSummary.getKey(), workbasketSummary.getDomain()))
                .collect(Collectors.toSet()));
  }
}
