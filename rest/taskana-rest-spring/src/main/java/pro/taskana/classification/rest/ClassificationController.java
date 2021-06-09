package pro.taskana.classification.rest;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
import pro.taskana.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.util.QueryParamsValidator;

/** Controller for all {@link Classification} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class ClassificationController {

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

  /**
   * This endpoint retrieves a list of existing Classifications. Filters can be applied.
   *
   * @title Get a list of all Classifications
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the classifications with the given filter, sort and paging options.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationSummaryPagedRepresentationModel> getClassifications(
      HttpServletRequest request,
      final ClassificationQueryFilterParameter filterParameter,
      final ClassificationQuerySortParameter sortParameter,
      final QueryPagingParameter<ClassificationSummary, ClassificationQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        ClassificationQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    final ClassificationQuery query = classificationService.createClassificationQuery();
    filterParameter.applyToQuery(query);
    sortParameter.applyToQuery(query);
    List<ClassificationSummary> classificationSummaries = pagingParameter.applyToQuery(query);

    return ResponseEntity.ok(
        summaryModelAssembler.toPagedModel(
            classificationSummaries, pagingParameter.getPageMetadata()));
  }

  /**
   * This endpoint retrieves a single Classification.
   *
   * @param classificationId the Id of the requested Classification.
   * @return the requested classification
   * @throws ClassificationNotFoundException if the requested classification is not found.
   * @title Get a single Classification
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> getClassification(
      @PathVariable String classificationId) throws ClassificationNotFoundException {
    Classification classification = classificationService.getClassification(classificationId);
    return ResponseEntity.ok(modelAssembler.toModel(classification));
  }

  /**
   * This endpoint creates a new Classification.
   *
   * @title Create a new Classification
   * @param repModel the Classification which should be created.
   * @return The inserted Classification
   * @throws NotAuthorizedException if the current user is not allowed to create a Classification.
   * @throws ClassificationAlreadyExistException if the new Classification already exists. This
   *     means that a Classification with the requested key and domain already exist.
   * @throws DomainNotFoundException if the domain within the new Classification does not exist.
   * @throws InvalidArgumentException if the new Classification does not contain all relevant
   *     information.
   */
  @PostMapping(path = RestEndpoints.URL_CLASSIFICATIONS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> createClassification(
      @RequestBody ClassificationRepresentationModel repModel)
      throws NotAuthorizedException, ClassificationAlreadyExistException, DomainNotFoundException,
          InvalidArgumentException {
    Classification classification = modelAssembler.toEntityModel(repModel);
    classification = classificationService.createClassification(classification);

    return ResponseEntity.status(HttpStatus.CREATED).body(modelAssembler.toModel(classification));
  }

  /**
   * This endpoint updates a Classification.
   *
   * @title Update a Classification
   * @param classificationId the Id of the Classification which should be updated.
   * @param resource the new Classification for the requested id.
   * @return the updated Classification
   * @throws NotAuthorizedException if the current user is not authorized to update a Classification
   * @throws ClassificationNotFoundException if the requested Classification is not found
   * @throws ConcurrencyException if the requested Classification Id has been modified in the
   *     meantime by a different process.
   * @throws InvalidArgumentException if the Id in the path and in the request body does not match
   */
  @PutMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> updateClassification(
      @PathVariable(value = "classificationId") String classificationId,
      @RequestBody ClassificationRepresentationModel resource)
      throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
          InvalidArgumentException {
    if (!classificationId.equals(resource.getClassificationId())) {
      throw new InvalidArgumentException(
          String.format(
              "ClassificationId ('%s') of the URI is not identical"
                  + " with the classificationId ('%s') of the object in the payload.",
              classificationId, resource.getClassificationId()));
    }
    Classification classification = modelAssembler.toEntityModel(resource);
    classification = classificationService.updateClassification(classification);

    return ResponseEntity.ok(modelAssembler.toModel(classification));
  }

  /**
   * This endpoint deletes a requested Classification if possible.
   *
   * @title Delete a Classification
   * @param classificationId the requested Classification Id which should be deleted
   * @return no content
   * @throws ClassificationNotFoundException if the requested Classification could not be found
   * @throws ClassificationInUseException if there are tasks existing referring to the requested
   *     Classification
   * @throws NotAuthorizedException if the user is not authorized to delete a Classification
   */
  @DeleteMapping(path = RestEndpoints.URL_CLASSIFICATIONS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ClassificationRepresentationModel> deleteClassification(
      @PathVariable String classificationId)
      throws ClassificationNotFoundException, ClassificationInUseException, NotAuthorizedException {
    classificationService.deleteClassification(classificationId);
    return ResponseEntity.noContent().build();
  }

  enum ClassificationQuerySortBy implements QuerySortBy<ClassificationQuery> {
    DOMAIN(ClassificationQuery::orderByDomain),
    KEY(ClassificationQuery::orderByKey),
    CATEGORY(ClassificationQuery::orderByCategory),
    NAME(ClassificationQuery::orderByName);

    private final BiConsumer<ClassificationQuery, SortDirection> consumer;

    ClassificationQuerySortBy(BiConsumer<ClassificationQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(ClassificationQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class ClassificationQuerySortParameter
      extends QuerySortParameter<ClassificationQuery, ClassificationQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public ClassificationQuerySortParameter(
        List<ClassificationQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<ClassificationQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
