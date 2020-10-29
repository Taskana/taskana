package pro.taskana.classification.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationQuery;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.AbstractPagingController;
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModel;

/** Controller for all {@link Classification} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class ClassificationController extends AbstractPagingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);

  private static final String LIKE = "%";
  private static final String NAME = "name";
  private static final String NAME_LIKE = "name-like";
  private static final String KEY = "key";
  private static final String DOMAIN = "domain";
  private static final String CATEGORY = "category";
  private static final String TYPE = "type";

  private final ClassificationService classificationService;
  private final ClassificationRepresentationModelAssembler modelAssembler;
  private final ClassificationSummaryRepresentationModelAssembler summaryModelAssembler;

  @Autowired
  ClassificationController(
      ClassificationService classificationService,
      ClassificationRepresentationModelAssembler modelAssembler,
      ClassificationSummaryRepresentationModelAssembler summaryModelAssembler) {
    this.classificationService = classificationService;
    this.modelAssembler = modelAssembler;
    this.summaryModelAssembler = summaryModelAssembler;
  }

  @GetMapping(path = RestEndpoints.URL_CLASSIFICATIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>>
      getClassifications(@RequestParam MultiValueMap<String, String> params)
          throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getClassifications(params= {})", params);
    }

    ClassificationQuery query = classificationService.createClassificationQuery();
    applyFilterParams(query, params);
    applySortingParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<ClassificationSummary> classificationSummaries = getQueryList(query, pageMetadata);

    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        ResponseEntity.ok(summaryModelAssembler.toPageModel(classificationSummaries, pageMetadata));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassifications(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> getClassification(
      @PathVariable String classificationId) throws ClassificationNotFoundException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getClassification(classificationId= {})", classificationId);
    }

    Classification classification = classificationService.getClassification(classificationId);
    ResponseEntity<ClassificationRepresentationModel> response =
        ResponseEntity.ok(modelAssembler.toModel(classification));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassification(), returning {}", response);
    }

    return response;
  }

  @PostMapping(path = RestEndpoints.URL_CLASSIFICATIONS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> createClassification(
      @RequestBody ClassificationRepresentationModel resource)
      throws NotAuthorizedException, ClassificationAlreadyExistException, DomainNotFoundException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to createClassification(resource= {})", resource);
    }
    Classification classification = modelAssembler.toEntityModel(resource);
    classification = classificationService.createClassification(classification);

    ResponseEntity<ClassificationRepresentationModel> response =
        ResponseEntity.status(HttpStatus.CREATED).body(modelAssembler.toModel(classification));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createClassification(), returning {}", response);
    }

    return response;
  }

  @PutMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> updateClassification(
      @PathVariable(value = "classificationId") String classificationId,
      @RequestBody ClassificationRepresentationModel resource)
      throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to updateClassification(classificationId= {}, resource= {})",
          classificationId,
          resource);
    }

    ResponseEntity<ClassificationRepresentationModel> result;
    if (classificationId.equals(resource.getClassificationId())) {
      Classification classification = modelAssembler.toEntityModel(resource);
      classification = classificationService.updateClassification(classification);
      result = ResponseEntity.ok(modelAssembler.toModel(classification));
    } else {
      throw new InvalidArgumentException(
          "ClassificationId ('"
              + classificationId
              + "') of the URI is not identical with the classificationId ('"
              + resource.getClassificationId()
              + "') of the object in the payload.");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateClassification(), returning {}", result);
    }

    return result;
  }

  @DeleteMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> deleteClassification(
      @PathVariable String classificationId)
      throws ClassificationNotFoundException, ClassificationInUseException, NotAuthorizedException {
    LOGGER.debug("Entry to deleteClassification(classificationId= {})", classificationId);
    classificationService.deleteClassification(classificationId);
    ResponseEntity<ClassificationRepresentationModel> response = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from deleteClassification(), returning {}", response);
    return response;
  }

  private void applySortingParams(ClassificationQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(query= {}, params= {})", query, params);
    }

    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          switch (sortBy) {
            case (CATEGORY):
              query.orderByCategory(sortDirection);
              break;
            case (DOMAIN):
              query.orderByDomain(sortDirection);
              break;
            case (KEY):
              query.orderByKey(sortDirection);
              break;
            case (NAME):
              query.orderByName(sortDirection);
              break;
            default:
              throw new InvalidArgumentException("Unknown order '" + sortBy + "'");
          }
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", query);
    }
  }

  private void applyFilterParams(ClassificationQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyFilterParams(query= {}, params= {})", query, params);
    }

    if (params.containsKey(NAME)) {
      String[] names = extractCommaSeparatedFields(params.get(NAME));
      query.nameIn(names);
      params.remove(NAME);
    }
    if (params.containsKey(NAME_LIKE)) {
      query.nameLike(LIKE + params.get(NAME_LIKE).get(0) + LIKE);
      params.remove(NAME_LIKE);
    }
    if (params.containsKey(KEY)) {
      String[] names = extractCommaSeparatedFields(params.get(KEY));
      query.keyIn(names);
      params.remove(KEY);
    }
    if (params.containsKey(CATEGORY)) {
      String[] names = extractCommaSeparatedFields(params.get(CATEGORY));
      query.categoryIn(names);
      params.remove(CATEGORY);
    }
    if (params.containsKey(DOMAIN)) {
      String[] names = extractCommaSeparatedFields(params.get(DOMAIN));
      query.domainIn(names);
      params.remove(DOMAIN);
    }
    if (params.containsKey(TYPE)) {
      String[] names = extractCommaSeparatedFields(params.get(TYPE));
      query.typeIn(names);
      params.remove(TYPE);
    }

    for (ClassificationCustomField customField : ClassificationCustomField.values()) {
      List<String> customFieldParams =
          params.remove(customField.name().replace("_", "-").toLowerCase() + "-like");
      if (customFieldParams != null) {
        String[] customValues = extractCommaSeparatedFields(customFieldParams);
        for (int i = 0; i < customValues.length; i++) {
          customValues[i] = LIKE + customValues[i] + LIKE;
        }
        query.customAttributeLike(customField, customValues);
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
    }
  }
}
