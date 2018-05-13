package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import pro.taskana.rest.resource.assembler.ClassificationResourceAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for Importing / Exporting classifications.
 */
@RestController
@RequestMapping(path = "/v1/classificationdefinitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ClassificationDefinitionController {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<ClassificationResource>> getClassifications(
        @RequestParam(required = false) String domain) throws ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, ConcurrencyException, DomainNotFoundException, InvalidArgumentException {
        ClassificationQuery query = classificationService.createClassificationQuery();
        List<ClassificationSummary> summaries = domain != null ? query.domainIn(domain).list() : query.list();
        List<ClassificationResource> export = new ArrayList<>();

        for (ClassificationSummary summary : summaries) {
            Classification classification = classificationService.getClassification(summary.getKey(),
                summary.getDomain());
            export.add(classificationResourceAssembler.toResource(classification));
        }
        return new ResponseEntity<>(export, HttpStatus.OK);
    }

    @PostMapping(path = "/import")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> importClassifications(
        @RequestBody List<ClassificationResource> classificationResources) throws InvalidArgumentException {
        Map<String, String> systemIds = classificationService.createClassificationQuery()
            .list()
            .stream()
            .collect(Collectors.toMap(i -> i.getKey() + "|" + i.getDomain(), ClassificationSummary::getId));

        try {
            for (ClassificationResource classificationResource : classificationResources) {
                if (systemIds.containsKey(classificationResource.key + "|" + classificationResource.domain)) {
                    classificationService.updateClassification(classificationResourceAssembler.toModel(classificationResource));

                } else {
                    classificationResource.classificationId = null;
                    classificationService.createClassification(classificationResourceAssembler.toModel(classificationResource));
                }
            }
        } catch (NotAuthorizedException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ClassificationNotFoundException | DomainNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ClassificationAlreadyExistException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
            // TODO why is this occuring???
        } catch (ConcurrencyException e) {
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
