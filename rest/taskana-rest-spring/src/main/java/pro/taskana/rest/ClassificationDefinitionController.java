package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import pro.taskana.rest.resource.ClassificationResourceAssembler;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller for Importing / Exporting classifications.
 */
@RestController
@RequestMapping(path = "/v1/classification-definitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ClassificationDefinitionController {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<ClassificationResource>> exportClassifications(
        @RequestParam(required = false) String domain)
        throws ClassificationNotFoundException, DomainNotFoundException, ConcurrencyException, InvalidArgumentException,
        NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationQuery query = classificationService.createClassificationQuery();

        List<ClassificationSummary> summaries = domain != null ? query.domainIn(domain).list() : query.list();
        List<ClassificationResource> export = new ArrayList<>();

        for (ClassificationSummary summary : summaries) {
            Classification classification = classificationService.getClassification(summary.getKey(),
                summary.getDomain());

            export.add(classificationResourceAssembler.toDefinition(classification));
        }
        return new ResponseEntity<>(export, HttpStatus.OK);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> importClassifications(
        @RequestParam("file") MultipartFile file)
        throws InvalidArgumentException, NotAuthorizedException, ConcurrencyException, ClassificationNotFoundException,
        ClassificationAlreadyExistException, DomainNotFoundException, IOException {
        Map<String, String> systemIds = classificationService.createClassificationQuery()
            .list()
            .stream()
            .collect(Collectors.toMap(i -> i.getKey() + "|" + i.getDomain(), ClassificationSummary::getId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ClassificationResource> classificationsDefinitions = mapper.readValue(file.getInputStream(),
            new TypeReference<List<ClassificationResource>>() {

            });

        for (ClassificationResource classification : classificationsDefinitions) {
            if (systemIds.containsKey(classification.getKey() + "|" + classification.getDomain())) {
                classificationService.updateClassification(classificationResourceAssembler.toModel(classification));
            } else {
                classificationService.createClassification(classificationResourceAssembler.toModel(classification));
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
