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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery;
import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.mapper.ClassificationResourceAssembler;
import pro.taskana.rest.resource.mapper.ClassificationSummaryResourcesAssembler;

/**
 * Controller for all {@link Classification} related endpoints.
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(path = "/v1/classifications", produces = "application/hal+json")
public class ClassificationController extends AbstractPagingController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String DOMAIN = "domain";
    private static final String CATEGORY = "category";
    private static final String DESC = "desc";

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<PagedResources<ClassificationSummaryResource>> getClassifications(
        @RequestParam(value = "sortBy", defaultValue = "key", required = false) String sortBy,
        @RequestParam(value = "order", defaultValue = "asc", required = false) String order,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "nameLike", required = false) String nameLike,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "category", required = false) String category,
        @RequestParam(value = "domain", required = false) String domain,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "page", required = false) String page,
        @RequestParam(value = "pagesize", required = false) String pageSize) throws InvalidArgumentException {

        ClassificationQuery query = classificationService.createClassificationQuery();
        addSortingToQuery(query, sortBy, order);
        addAttributeFilter(query, name, nameLike, key, category, type, domain);

        PageMetadata pageMetadata = null;
        List<ClassificationSummary> classificationSummaries = null;
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

    private void addSortingToQuery(ClassificationQuery query, String sortBy, String order)
        throws IllegalArgumentException {
        BaseQuery.SortDirection sortDirection = getSortDirection(order);

        switch (sortBy) {
            case CATEGORY:
                query.orderByCategory(sortDirection);
                break;
            case DOMAIN:
                query.orderByDomain(sortDirection);
                break;
            case KEY:
                query.orderByKey(sortDirection);
                break;
            case NAME:
                query.orderByName(sortDirection);
                break;
            default:
                throw new IllegalArgumentException("Unknown order '" + sortBy + "'");
        }
    }

    private BaseQuery.SortDirection getSortDirection(String order) {
        if (order.equals(DESC)) {
            return BaseQuery.SortDirection.DESCENDING;
        }
        return BaseQuery.SortDirection.ASCENDING;
    }

    private void addAttributeFilter(ClassificationQuery query,
        String name, String nameLike,
        String key, String category,
        String type, String domain) throws InvalidArgumentException {
        if (name != null) {
            query.nameIn(name);
        }
        if (nameLike != null) {
            query.nameLike(LIKE + nameLike + LIKE);
        }
        if (key != null) {
            query.keyIn(key);
        }
        if (category != null) {
            query.categoryIn(category);
        }
        if (type != null) {
            query.typeIn(type);
        }
        if (domain != null) {
            query.domainIn(domain);
        }
    }

}
