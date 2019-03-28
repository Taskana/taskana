package pro.taskana.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.rest.resource.TaskanaUserInfoResource;
import pro.taskana.rest.resource.VersionResource;
import pro.taskana.security.CurrentUserContext;

import java.util.List;

/**
 * Controller for TaskanaEngine related tasks.
 */
@RestController
public class TaskanaEngineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineController.class);

    TaskanaEngineConfiguration taskanaEngineConfiguration;

    TaskanaEngine taskanaEngine;

    TaskanaEngineController(TaskanaEngineConfiguration taskanaEngineConfiguration, TaskanaEngine taskanaEngine) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        this.taskanaEngine = taskanaEngine;
    }

    @GetMapping(path = "/v1/domains", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getDomains() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getDomains(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationCategories(String type) {
        if (type != null) {
            return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationCategoriesByType(type),
                HttpStatus.OK);
        }
        return new ResponseEntity<>(taskanaEngineConfiguration.getAllClassificationCategories(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/classification-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationTypes() {
        return new ResponseEntity<>(taskanaEngineConfiguration.getClassificationTypes(), HttpStatus.OK);
    }

    @GetMapping(path = "/v1/current-user-info", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TaskanaUserInfoResource> getCurrentUserInfo() {
        LOGGER.debug("Entry to getCurrentUserInfo()");
        TaskanaUserInfoResource resource = new TaskanaUserInfoResource();
        resource.setUserId(CurrentUserContext.getUserid());
        resource.setGroupIds(CurrentUserContext.getGroupIds());
        for (TaskanaRole role : taskanaEngineConfiguration.getRoleMap().keySet()) {
            if (taskanaEngine.isUserInRole(role)) {
                resource.getRoles().add(role);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getCurrentUserInfo(), returning {}", new ResponseEntity<>(resource, HttpStatus.OK));
        }

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(path = "/v1/history-provider-enabled", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
        return new ResponseEntity<>(((TaskanaEngineImpl) taskanaEngine).getHistoryEventProducer().isEnabled(),
            HttpStatus.OK);
    }

    /**
     * Get the current application version.
     *
     * @return The current version.
     */
    @GetMapping(path = "/v1/version", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VersionResource> currentVersion() {
        LOGGER.debug("Entry to currentVersion()");
        VersionResource resource = new VersionResource();
        resource.setVersion(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
        LOGGER.debug("Exit from currentVersion(), returning {}", new ResponseEntity<>(resource, HttpStatus.OK));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
