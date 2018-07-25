package pro.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.assembler.ClassificationResourceAssembler;
import pro.taskana.rest.resource.assembler.ClassificationSummaryResourcesAssembler;

/**
 * Controller for all {@link Classification} related endpoints.
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(path = "/v1/classifications", produces = "application/hal+json")
public class ClassificationController extends AbstractPagingController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String NAME_LIKE = "name-like";
    private static final String KEY = "key";
    private static final String DOMAIN = "domain";
    private static final String CATEGORY = "category";
    private static final String TYPE = "type";
    private static final String CUSTOM_1_LIKE = "custom-1-like";
    private static final String CUSTOM_2_LIKE = "custom-2-like";
    private static final String CUSTOM_3_LIKE = "custom-3-like";
    private static final String CUSTOM_4_LIKE = "custom-4-like";
    private static final String CUSTOM_5_LIKE = "custom-5-like";
    private static final String CUSTOM_6_LIKE = "custom-6-like";
    private static final String CUSTOM_7_LIKE = "custom-7-like";
    private static final String CUSTOM_8_LIKE = "custom-8-like";

    private static final String SORT_BY = "sort-by";
    private static final String SORT_DIRECTION = "order";

    private static final String PAGING_PAGE = "page";
    private static final String PAGING_PAGE_SIZE = "page-size";

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<PagedResources<ClassificationSummaryResource>> getClassifications(
        @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {

        ClassificationQuery query = classificationService.createClassificationQuery();
        query = applySortingParams(query, params);
        query = applyFilterParams(query, params);

        PageMetadata pageMetadata = null;
        List<ClassificationSummary> classificationSummaries = null;
        String page = params.getFirst(PAGING_PAGE);
        String pageSize = params.getFirst(PAGING_PAGE_SIZE);
        params.remove(PAGING_PAGE);
        params.remove(PAGING_PAGE_SIZE);
        validateNoInvalidParameterIsLeft(params);
        if (page != null && pageSize != null) {
            // paging
            long totalElements = query.count();
            pageMetadata = initPageMetadata(pageSize, page, totalElements);
            classificationSummaries = query.listPage((int) pageMetadata.getNumber(),
                (int) pageMetadata.getSize());
        } else if (page == null && pageSize == null) {
            // not paging
            classificationSummaries = query.list();
        } else {
            throw new InvalidArgumentException("Paging information is incomplete.");
        }

        ClassificationSummaryResourcesAssembler assembler = new ClassificationSummaryResourcesAssembler();
        PagedResources<ClassificationSummaryResource> pagedResources = assembler.toResources(classificationSummaries,
            pageMetadata);

        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }

    @GetMapping(path = "/{classificationId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> getClassification(@PathVariable String classificationId)
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException,
        ConcurrencyException, DomainNotFoundException, InvalidArgumentException {
        Classification classification = classificationService.getClassification(classificationId);
        return ResponseEntity.status(HttpStatus.OK).body(classificationResourceAssembler.toResource(classification));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> createClassification(
        @RequestBody ClassificationResource resource)
        throws NotAuthorizedException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        ConcurrencyException, DomainNotFoundException, InvalidArgumentException {
        Classification classification = classificationResourceAssembler.toModel(resource);
        classification = classificationService.createClassification(classification);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(classificationResourceAssembler.toResource(classification));
    }

    @PutMapping(path = "/{classificationId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> updateClassification(
        @PathVariable(value = "classificationId") String classificationId, @RequestBody ClassificationResource resource)
        throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
        ClassificationAlreadyExistException, DomainNotFoundException, InvalidArgumentException {

        ResponseEntity<ClassificationResource> result;
        if (classificationId.equals(resource.classificationId)) {
            Classification classification = classificationResourceAssembler.toModel(resource);
            classification = classificationService.updateClassification(classification);
            result = ResponseEntity.ok(classificationResourceAssembler.toResource(classification));
        } else {
            throw new InvalidArgumentException(
                "ClassificationId ('" + classificationId
                    + "') of the URI is not identical with the classificationId ('"
                    + resource.getClassificationId() + "') of the object in the payload.");
        }
        return result;
    }

    @DeleteMapping(path = "/{classificationId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<?> deleteClassification(@PathVariable String classificationId)
        throws ClassificationNotFoundException, ClassificationInUseException, NotAuthorizedException {
        classificationService.deleteClassification(classificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ClassificationQuery applySortingParams(ClassificationQuery query, MultiValueMap<String, String> params)
        throws IllegalArgumentException {
        // sorting
        String sortBy = params.getFirst(SORT_BY);
        if (sortBy != null) {
            SortDirection sortDirection;
            if (params.getFirst(SORT_DIRECTION) != null && "desc".equals(params.getFirst(SORT_DIRECTION))) {
                sortDirection = SortDirection.DESCENDING;
            } else {
                sortDirection = SortDirection.ASCENDING;
            }
            switch (sortBy) {
                case (CATEGORY):
                    query = query.orderByCategory(sortDirection);
                    break;
                case (DOMAIN):
                    query = query.orderByDomain(sortDirection);
                    break;
                case (KEY):
                    query = query.orderByKey(sortDirection);
                    break;
                case (NAME):
                    query = query.orderByName(sortDirection);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown order '" + sortBy + "'");
            }
        }
        params.remove(SORT_BY);
        params.remove(SORT_DIRECTION);
        return query;
    }

    private ClassificationQuery applyFilterParams(ClassificationQuery query,
        MultiValueMap<String, String> params) throws InvalidArgumentException {
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
        if (params.containsKey(CUSTOM_1_LIKE)) {
            query.customAttributeLike("1", LIKE + params.get(CUSTOM_1_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_1_LIKE);
        }
        if (params.containsKey(CUSTOM_2_LIKE)) {
            query.customAttributeLike("2", LIKE + params.get(CUSTOM_2_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_2_LIKE);
        }
        if (params.containsKey(CUSTOM_3_LIKE)) {
            query.customAttributeLike("3", LIKE + params.get(CUSTOM_3_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_3_LIKE);
        }
        if (params.containsKey(CUSTOM_4_LIKE)) {
            query.customAttributeLike("4", LIKE + params.get(CUSTOM_4_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_4_LIKE);
        }
        if (params.containsKey(CUSTOM_5_LIKE)) {
            query.customAttributeLike("5", LIKE + params.get(CUSTOM_5_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_5_LIKE);
        }
        if (params.containsKey(CUSTOM_6_LIKE)) {
            query.customAttributeLike("6", LIKE + params.get(CUSTOM_6_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_6_LIKE);
        }
        if (params.containsKey(CUSTOM_7_LIKE)) {
            query.customAttributeLike("7", LIKE + params.get(CUSTOM_7_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_7_LIKE);
        }
        if (params.containsKey(CUSTOM_8_LIKE)) {
            query.customAttributeLike("8", LIKE + params.get(CUSTOM_8_LIKE).get(0) + LIKE);
            params.remove(CUSTOM_8_LIKE);
        }
        return query;
    }

}
