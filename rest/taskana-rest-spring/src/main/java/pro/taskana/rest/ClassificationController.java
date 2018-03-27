package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.mapper.ClassificationMapper;

/**
 * Controller for all {@link Classification} related endpoints.
 */
@RestController
@RequestMapping(path = "/v1/classifications", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ClassificationController {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationMapper classificationMapper;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<ClassificationSummary>> getClassifications() {
        try {
            List<ClassificationSummary> classificationTree = classificationService.createClassificationQuery().list();
            return ResponseEntity.status(HttpStatus.OK).body(classificationTree);
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(path = "/{classificationId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> getClassification(@PathVariable String classificationId)
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException,
        ConcurrencyException, DomainNotFoundException {
        Classification classification = classificationService.getClassification(classificationId);
        return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
    }

    @GetMapping(path = "/{classificationKey}/{domain}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> getClassification(@PathVariable String classificationKey,
        @PathVariable String domain) throws ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, ConcurrencyException, DomainNotFoundException {
        Classification classification = classificationService.getClassification(classificationKey, domain);
        return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> createClassification(
        @RequestBody ClassificationResource resource)
        throws NotAuthorizedException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        ConcurrencyException, DomainNotFoundException {
        Classification classification = classificationMapper.toModel(resource);
        classification = classificationService.createClassification(classification);
        return ResponseEntity.status(HttpStatus.CREATED).body(classificationMapper.toResource(classification));
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> updateClassification(@RequestBody ClassificationResource resource)
        throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
        ClassificationAlreadyExistException, DomainNotFoundException {
        Classification classification = classificationMapper.toModel(resource);
        classification = classificationService.updateClassification(classification);
        return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
    }
}
