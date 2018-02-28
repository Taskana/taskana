package pro.taskana.rest;

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

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
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
    public ResponseEntity<ClassificationResource> getClassification(@PathVariable String classificationId) {
        try {
            Classification classification = classificationService.getClassification(classificationId);
            return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
        } catch (ClassificationNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/{classificationKey}/{domain}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> getClassification(@PathVariable String classificationKey,
        @PathVariable String domain) {
        try {
            Classification classification = classificationService.getClassification(classificationKey, domain);
            return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
        } catch (ClassificationNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> createClassification(
        @RequestBody ClassificationResource resource) {
        try {
            Classification classification = classificationMapper.toModel(resource);
            classification = classificationService.createClassification(classification);
            return ResponseEntity.status(HttpStatus.CREATED).body(classificationMapper.toResource(classification));
        } catch (ClassificationAlreadyExistException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotAuthorizedException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ClassificationNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ClassificationResource> updateClassification(@RequestBody ClassificationResource resource) {
        try {
            Classification classification = classificationMapper.toModel(resource);
            classification = classificationService.updateClassification(classification);
            return ResponseEntity.status(HttpStatus.OK).body(classificationMapper.toResource(classification));
        } catch (ClassificationNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NotAuthorizedException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ConcurrencyException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }
    }
}
