package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/classificationdefinitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ClassificationDefinitionController {

    @Autowired
    private ClassificationService classificationService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Classification>> getClassifications(@RequestParam(required = false) String domain) {
        try {
            ClassificationQuery query = classificationService.createClassificationQuery();
            List<ClassificationSummary> summaries = domain != null ? query.domainIn(domain).list() : query.list();
            List<Classification> export = new ArrayList<>();

            for (ClassificationSummary summary : summaries) {
                export.add(classificationService.getClassification(summary.getKey(), summary.getDomain()));
            }

            return new ResponseEntity<>(export, HttpStatus.OK);
        } catch (ClassificationNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
