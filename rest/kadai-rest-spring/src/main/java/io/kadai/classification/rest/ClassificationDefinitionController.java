package io.kadai.classification.rest;

import static io.kadai.common.internal.util.CheckedFunction.wrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationQuery;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.exceptions.ClassificationAlreadyExistException;
import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.exceptions.MalformedServiceLevelException;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.rest.assembler.ClassificationDefinitionCollectionRepresentationModel;
import io.kadai.classification.rest.assembler.ClassificationDefinitionRepresentationModelAssembler;
import io.kadai.classification.rest.models.ClassificationDefinitionRepresentationModel;
import io.kadai.classification.rest.models.ClassificationRepresentationModel;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.rest.RestEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Controller for Importing / Exporting classifications. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ClassificationDefinitionController {

  private final ObjectMapper mapper;
  private final ClassificationService classificationService;
  private final ClassificationDefinitionRepresentationModelAssembler assembler;

  @Autowired
  ClassificationDefinitionController(
      ObjectMapper mapper,
      ClassificationService classificationService,
      ClassificationDefinitionRepresentationModelAssembler assembler) {
    this.mapper = mapper;
    this.classificationService = classificationService;
    this.assembler = assembler;
  }

  @Operation(
      summary = "Export Classifications",
      description = "This endpoint exports all configured Classifications.",
      parameters = {@Parameter(name = "domain", description = "Filter the export by domain")},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the configured Classifications.",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema =
                        @Schema(
                            implementation =
                                ClassificationDefinitionCollectionRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationDefinitionCollectionRepresentationModel>
      exportClassifications(@RequestParam(value = "domain", required = false) String[] domain) {
    ClassificationQuery query = classificationService.createClassificationQuery();

    List<ClassificationSummary> summaries =
        domain != null ? query.domainIn(domain).list() : query.list();

    ClassificationDefinitionCollectionRepresentationModel collectionModel =
        summaries.stream()
            .map(ClassificationSummary::getId)
            .map(wrap(classificationService::getClassification))
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(), assembler::toKadaiCollectionModel));

    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Import Classifications",
      description =
          "This endpoint imports all Classifications. Existing Classifications will not be removed."
              + " Existing Classifications with the same key/domain will be overridden.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description =
                  "the file containing the Classifications which should be imported. To get an "
                      + "example file containing the Classificatioins, go to the "
                      + "[KADAI UI](http://localhost:8080/kadai/index.html) and export the "
                      + "Classifications",
              required = true,
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)),
      responses = {
        @ApiResponse(
            responseCode = "204",
            content = {@Content(schema = @Schema())})
      })
  @PostMapping(
      path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> importClassifications(@RequestParam("file") MultipartFile file)
      throws InvalidArgumentException,
          ConcurrencyException,
          ClassificationNotFoundException,
          ClassificationAlreadyExistException,
          DomainNotFoundException,
          IOException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    Map<String, String> systemIds = getSystemIds();
    ClassificationDefinitionCollectionRepresentationModel collection =
        extractClassificationResourcesFromFile(file);
    checkForDuplicates(collection.getContent());

    Map<Classification, String> childrenInFile =
        mapChildrenToParentKeys(collection.getContent(), systemIds);
    insertOrUpdateClassificationsWithoutParent(collection.getContent(), systemIds);
    updateParentChildrenRelations(childrenInFile);
    return ResponseEntity.noContent().build();
  }

  private Map<String, String> getSystemIds() {
    return classificationService.createClassificationQuery().list().stream()
        .collect(
            Collectors.toMap(i -> i.getKey() + "|" + i.getDomain(), ClassificationSummary::getId));
  }

  private ClassificationDefinitionCollectionRepresentationModel
      extractClassificationResourcesFromFile(MultipartFile file) throws IOException {
    return mapper.readValue(
        file.getInputStream(), ClassificationDefinitionCollectionRepresentationModel.class);
  }

  private void checkForDuplicates(
      Collection<ClassificationDefinitionRepresentationModel> definitionList)
      throws ClassificationAlreadyExistException {
    List<String> identifiers = new ArrayList<>();
    for (ClassificationDefinitionRepresentationModel definition : definitionList) {
      ClassificationRepresentationModel classification = definition.getClassification();
      String identifier = classification.getKey() + "|" + classification.getDomain();
      if (identifiers.contains(identifier)) {
        throw new ClassificationAlreadyExistException(
            definition.getClassification().getKey(), definition.getClassification().getDomain());
      }
      identifiers.add(identifier);
    }
  }

  private Map<Classification, String> mapChildrenToParentKeys(
      Collection<ClassificationDefinitionRepresentationModel> definitionList,
      Map<String, String> systemIds) {
    Map<Classification, String> childrenInFile = new HashMap<>();
    Set<String> newKeysWithDomain = new HashSet<>();
    definitionList.stream()
        .map(ClassificationDefinitionRepresentationModel::getClassification)
        .forEach(cl -> newKeysWithDomain.add(cl.getKey() + "|" + cl.getDomain()));

    for (ClassificationDefinitionRepresentationModel def : definitionList) {
      ClassificationRepresentationModel cl = def.getClassification();
      cl.setParentId(cl.getParentId() == null ? "" : cl.getParentId());
      cl.setParentKey(cl.getParentKey() == null ? "" : cl.getParentKey());

      if (!cl.getParentId().equals("") && cl.getParentKey().equals("")) {
        for (ClassificationDefinitionRepresentationModel parentDef : definitionList) {
          ClassificationRepresentationModel parent = parentDef.getClassification();
          if (cl.getParentId().equals(parent.getClassificationId())) {
            cl.setParentKey(parent.getKey());
          }
        }
      }

      String parentKeyAndDomain = cl.getParentKey() + "|" + cl.getDomain();
      if ((!cl.getParentKey().isEmpty()
          && !cl.getParentKey().equals("")
          && (newKeysWithDomain.contains(parentKeyAndDomain)
              || systemIds.containsKey(parentKeyAndDomain)))) {
        childrenInFile.put(assembler.toEntityModel(def), cl.getParentKey());
      }
    }
    return childrenInFile;
  }

  private void insertOrUpdateClassificationsWithoutParent(
      Collection<ClassificationDefinitionRepresentationModel> definitionList,
      Map<String, String> systemIds)
      throws ClassificationNotFoundException,
          InvalidArgumentException,
          ClassificationAlreadyExistException,
          DomainNotFoundException,
          ConcurrencyException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    for (ClassificationDefinitionRepresentationModel definition : definitionList) {
      ClassificationRepresentationModel classificationRepModel = definition.getClassification();
      classificationRepModel.setParentKey(null);
      classificationRepModel.setParentId(null);
      classificationRepModel.setClassificationId(null);

      Classification newClassification = assembler.toEntityModel(definition);

      String systemId =
          systemIds.get(classificationRepModel.getKey() + "|" + classificationRepModel.getDomain());
      if (systemId != null) {
        updateExistingClassification(newClassification, systemId);
      } else {
        classificationService.createClassification(newClassification);
      }
    }
  }

  private void updateParentChildrenRelations(Map<Classification, String> childrenInFile)
      throws ClassificationNotFoundException,
          ConcurrencyException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    for (Map.Entry<Classification, String> entry : childrenInFile.entrySet()) {
      Classification childRes = entry.getKey();
      String parentKey = entry.getValue();
      String classificationKey = childRes.getKey();
      String classificationDomain = childRes.getDomain();

      Classification child =
          classificationService.getClassification(classificationKey, classificationDomain);
      String parentId =
          (parentKey == null)
              ? ""
              : classificationService.getClassification(parentKey, classificationDomain).getId();

      child.setParentKey(parentKey);
      child.setParentId(parentId);

      classificationService.updateClassification(child);
    }
  }

  private void updateExistingClassification(Classification newClassification, String systemId)
      throws ClassificationNotFoundException,
          ConcurrencyException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    Classification currentClassification = classificationService.getClassification(systemId);
    if (newClassification.getType() != null
        && !newClassification.getType().equals(currentClassification.getType())) {
      throw new InvalidArgumentException("Can not change the type of a classification.");
    }
    currentClassification.setCategory(newClassification.getCategory());
    currentClassification.setIsValidInDomain(newClassification.getIsValidInDomain());
    currentClassification.setName(newClassification.getName());
    currentClassification.setParentId(newClassification.getParentId());
    currentClassification.setParentKey(newClassification.getParentKey());
    currentClassification.setDescription(newClassification.getDescription());
    currentClassification.setPriority(newClassification.getPriority());
    currentClassification.setServiceLevel(newClassification.getServiceLevel());
    currentClassification.setApplicationEntryPoint(newClassification.getApplicationEntryPoint());
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_1,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_1));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_2,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_2));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_3,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_3));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_4,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_4));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_5,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_5));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_6,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_6));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_7,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_7));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_8,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_8));
    classificationService.updateClassification(currentClassification);
  }
}
