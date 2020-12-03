package pro.taskana.classification.rest;

import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationQuery;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationCollectionRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.RestEndpoints;

/** Controller for Importing / Exporting classifications. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ClassificationDefinitionController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClassificationDefinitionController.class);

  private final ObjectMapper mapper;
  private final ClassificationService classificationService;
  private final ClassificationRepresentationModelAssembler
      classificationRepresentationModelAssembler;

  @Autowired
  ClassificationDefinitionController(
      ObjectMapper mapper,
      ClassificationService classificationService,
      ClassificationRepresentationModelAssembler classificationRepresentationModelAssembler) {
    this.mapper = mapper;
    this.classificationService = classificationService;
    this.classificationRepresentationModelAssembler = classificationRepresentationModelAssembler;
  }

  /**
   * This endpoint exports all configured Classifications.
   *
   * @title Export Classifications
   * @param domain Filter the export by domain
   * @return the configured Classifications.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationCollectionRepresentationModel> exportClassifications(
      @RequestParam(required = false) String[] domain) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to exportClassifications(domain= {})", Arrays.toString(domain));
    }
    ClassificationQuery query = classificationService.createClassificationQuery();

    List<ClassificationSummary> summaries =
        domain != null ? query.domainIn(domain).list() : query.list();

    ClassificationCollectionRepresentationModel collectionModel =
        summaries.stream()
            .map(ClassificationSummary::getId)
            .map(wrap(classificationService::getClassification))
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    classificationRepresentationModelAssembler::toTaskanaCollectionModel));

    ResponseEntity<ClassificationCollectionRepresentationModel> response =
        ResponseEntity.ok(collectionModel);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from exportClassifications(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint imports all Classifications. Existing Classifications will not be removed.
   * Existing Classifications with the same key/domain will be overridden.
   *
   * @title Import Classifications
   * @param file the file containing the Classifications which should be imported.
   * @return nothing
   * @throws InvalidArgumentException if any Classification within the import file is invalid
   * @throws NotAuthorizedException if the current user is not authorized to import Classifications
   * @throws ConcurrencyException TODO: this makes no sense
   * @throws ClassificationNotFoundException TODO: this makes no sense
   * @throws ClassificationAlreadyExistException TODO: this makes no sense
   * @throws DomainNotFoundException if the domain for a specific Classification does not exist
   * @throws IOException if the import file could not be parsed
   */
  @PostMapping(path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> importClassifications(@RequestParam("file") MultipartFile file)
      throws InvalidArgumentException, NotAuthorizedException, ConcurrencyException,
          ClassificationNotFoundException, ClassificationAlreadyExistException,
          DomainNotFoundException, IOException {
    LOGGER.debug("Entry to importClassifications()");
    Map<String, String> systemIds = getSystemIds();
    ClassificationCollectionRepresentationModel collection =
        extractClassificationResourcesFromFile(file);
    checkForDuplicates(collection.getContent());

    Map<Classification, String> childrenInFile =
        mapChildrenToParentKeys(collection.getContent(), systemIds);
    insertOrUpdateClassificationsWithoutParent(collection.getContent(), systemIds);
    updateParentChildrenRelations(childrenInFile);
    ResponseEntity<Void> response = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from importClassifications(), returning {}", response);
    return response;
  }

  private Map<String, String> getSystemIds() {
    return classificationService.createClassificationQuery().list().stream()
        .collect(
            Collectors.toMap(i -> i.getKey() + "|" + i.getDomain(), ClassificationSummary::getId));
  }

  private ClassificationCollectionRepresentationModel extractClassificationResourcesFromFile(
      MultipartFile file) throws IOException {
    return mapper.readValue(
        file.getInputStream(), ClassificationCollectionRepresentationModel.class);
  }

  private void checkForDuplicates(
      Collection<ClassificationRepresentationModel> classificationList) {
    List<String> identifiers = new ArrayList<>();
    Set<String> duplicates = new HashSet<>();
    for (ClassificationRepresentationModel classification : classificationList) {
      String identifier = classification.getKey() + "|" + classification.getDomain();
      if (identifiers.contains(identifier)) {
        duplicates.add(identifier);
      } else {
        identifiers.add(identifier);
      }
    }
    if (!duplicates.isEmpty()) {
      throw new DuplicateKeyException(
          "The 'key|domain'-identifier is not unique for the value(s): " + duplicates.toString());
    }
  }

  private Map<Classification, String> mapChildrenToParentKeys(
      Collection<ClassificationRepresentationModel> classificationRepresentationModels,
      Map<String, String> systemIds) {
    LOGGER.debug("Entry to mapChildrenToParentKeys()");
    Map<Classification, String> childrenInFile = new HashMap<>();
    Set<String> newKeysWithDomain = new HashSet<>();
    classificationRepresentationModels.forEach(
        cl -> newKeysWithDomain.add(cl.getKey() + "|" + cl.getDomain()));

    for (ClassificationRepresentationModel cl : classificationRepresentationModels) {
      cl.setParentId(cl.getParentId() == null ? "" : cl.getParentId());
      cl.setParentKey(cl.getParentKey() == null ? "" : cl.getParentKey());

      if (!cl.getParentId().equals("") && cl.getParentKey().equals("")) {
        for (ClassificationRepresentationModel parent : classificationRepresentationModels) {
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
        childrenInFile.put(
            classificationRepresentationModelAssembler.toEntityModel(cl), cl.getParentKey());
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from mapChildrenToParentKeys(), returning {}", childrenInFile);
    }

    return childrenInFile;
  }

  private void insertOrUpdateClassificationsWithoutParent(
      Collection<ClassificationRepresentationModel> classificationRepresentationModels,
      Map<String, String> systemIds)
      throws ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException,
          ClassificationAlreadyExistException, DomainNotFoundException, ConcurrencyException {
    LOGGER.debug("Entry to insertOrUpdateClassificationsWithoutParent()");

    for (ClassificationRepresentationModel classificationRepresentationModel :
        classificationRepresentationModels) {
      classificationRepresentationModel.setParentKey(null);
      classificationRepresentationModel.setParentId(null);
      classificationRepresentationModel.setClassificationId(null);

      String systemId =
          systemIds.get(
              classificationRepresentationModel.getKey()
                  + "|"
                  + classificationRepresentationModel.getDomain());
      if (systemId != null) {
        updateExistingClassification(classificationRepresentationModel, systemId);
      } else {
        classificationService.createClassification(
            classificationRepresentationModelAssembler.toEntityModel(
                classificationRepresentationModel));
      }
    }
    LOGGER.debug("Exit from insertOrUpdateClassificationsWithoutParent()");
  }

  private void updateParentChildrenRelations(Map<Classification, String> childrenInFile)
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException {
    LOGGER.debug("Entry to updateParentChildrenRelations()");

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
    LOGGER.debug("Exit from updateParentChildrenRelations()");
  }

  private void updateExistingClassification(ClassificationRepresentationModel cl, String systemId)
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException {
    LOGGER.debug("Entry to updateExistingClassification()");
    Classification currentClassification = classificationService.getClassification(systemId);
    if (cl.getType() != null && !cl.getType().equals(currentClassification.getType())) {
      throw new InvalidArgumentException("Can not change the type of a classification.");
    }
    currentClassification.setCategory(cl.getCategory());
    currentClassification.setIsValidInDomain(cl.getIsValidInDomain());
    currentClassification.setName(cl.getName());
    currentClassification.setParentId(cl.getParentId());
    currentClassification.setParentKey(cl.getParentKey());
    currentClassification.setDescription(cl.getDescription());
    currentClassification.setPriority(cl.getPriority());
    currentClassification.setServiceLevel(cl.getServiceLevel());
    currentClassification.setApplicationEntryPoint(cl.getApplicationEntryPoint());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_1, cl.getCustom1());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_2, cl.getCustom2());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_3, cl.getCustom3());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_4, cl.getCustom4());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_5, cl.getCustom5());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_6, cl.getCustom6());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_7, cl.getCustom7());
    currentClassification.setCustomAttribute(ClassificationCustomField.CUSTOM_8, cl.getCustom8());
    classificationService.updateClassification(currentClassification);
    LOGGER.debug("Exit from updateExistingClassification()");
  }
}
