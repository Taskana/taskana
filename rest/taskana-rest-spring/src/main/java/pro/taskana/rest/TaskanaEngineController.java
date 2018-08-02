package pro.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaRole;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.rest.resource.TaskanaUserInfoResource;
import pro.taskana.rest.resource.VersionResource;
import pro.taskana.security.CurrentUserContext;

/**
 * Controller for TaskanaEngine related tasks.
 */
@RestController
public class TaskanaEngineController {

    @Autowired
    TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Autowired
    TaskanaEngineImpl taskanaEngineImpl;

    @GetMapping(path = "/v1/domains", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getDomains() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getDomains(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationCategories(String type) {
        if (type != null) {
            return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationCategoriesByType(type), HttpStatus.OK);
        }
        return new ResponseEntity<>(taskanaEngineConfiguration.getAllClassificationCategories(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationTypes() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationTypes(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/current-user-info", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TaskanaUserInfoResource> getCurrentUserInfo() {
        TaskanaUserInfoResource resource = new TaskanaUserInfoResource();
        resource.setUserId(CurrentUserContext.getUserid());
        resource.setGroupIds(CurrentUserContext.getGroupIds());
        for (TaskanaRole role : taskanaEngineConfiguration.getRoleMap().keySet()) {
            if (taskanaEngineImpl.isUserInRole(role)) {
                resource.getRoles().add(role);
            }
        }
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Get the current application version.
     *
     * @return The current version.
     */
    @GetMapping(path = "/v1/version", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VersionResource> currentVersion() {
        String version = TaskanaEngineController.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "1.0-DEFAULT";
        }
        VersionResource resource = new VersionResource();
        resource.setVersion(version);
        return new ResponseEntity<>(resource,
            HttpStatus.OK);
    }
}
