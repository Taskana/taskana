package pro.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * Controller for TaskanaEngine related tasks.
 */
@RestController
public class TaskanaEngineController {

    @Autowired
    TaskanaEngineConfiguration taskanaEngineConfiguration;

    @GetMapping(path = "/v1/domains", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getDomains() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getDomains(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationCategories() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationCategories(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationTypes() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationTypes(), HttpStatus.OK);
    }
}
