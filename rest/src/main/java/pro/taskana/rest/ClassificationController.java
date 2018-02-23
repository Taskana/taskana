/**
 *
 */
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

@RestController
@RequestMapping(path = "/v1/classifications", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ClassificationController {

    @Autowired
    private ClassificationService classificationService;

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

    @GetMapping(path = "/{classificationKey}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Classification> getClassification(@PathVariable String classificationKey) {
        try {
            Classification classification = classificationService.getClassification(classificationKey, "");
            return ResponseEntity.status(HttpStatus.OK).body(classification);
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/{classificationKey}/{domain}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Classification> getClassification(@PathVariable String classificationKey,
        @PathVariable String domain) {
        try {
            Classification classification = classificationService.getClassification(classificationKey, domain);
            return ResponseEntity.status(HttpStatus.OK).body(classification);
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Classification> createClassification(@RequestBody Classification classification) {
        try {
            classificationService.createClassification(classification);
            return ResponseEntity.status(HttpStatus.CREATED).body(classification);
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Classification> updateClassification(@RequestBody Classification classification) {
        try {
            classificationService.updateClassification(classification);
            return ResponseEntity.status(HttpStatus.CREATED).body(classification);
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
