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
        ResponseEntity<List<String>> response = new ResponseEntity<>(taskanaEngineConfiguration.getDomains(),
            HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getDomains(), returning {}", response);
        }
        return response;
    }

    @GetMapping(path = "/v1/classification-categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationCategories(String type) {
        LOGGER.debug("Entry to getClassificationCategories(type = {})", type);
        ResponseEntity<List<String>> response;
        if (type != null) {
            response = new ResponseEntity<>(taskanaEngineConfiguration.getClassificationCategoriesByType(type),
                HttpStatus.OK);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from getClassificationCategories(), returning {}", response);
            }
            return response;
        }
        response = new ResponseEntity<>(taskanaEngineConfiguration.getAllClassificationCategories(), HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getClassificationCategories(), returning {}", response);
        }
        return response;
    }

    @GetMapping(path = "/v1/classification-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getClassificationTypes() {
        ResponseEntity<List<String>> response = new ResponseEntity<>(
            taskanaEngineConfiguration.getClassificationTypes(), HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getClassificationTypes(), returning {}", response);
        }
        return response;
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
        ResponseEntity<TaskanaUserInfoResource> response = new ResponseEntity<>(resource, HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getCurrentUserInfo(), returning {}", response);
        }

        return response;
    }

    @GetMapping(path = "/v1/history-provider-enabled", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
        ResponseEntity<Boolean> response = new ResponseEntity<>(
            ((TaskanaEngineImpl) taskanaEngine).getHistoryEventProducer().isEnabled(),
            HttpStatus.OK);
        LOGGER.debug("Exit from getIsHistoryProviderEnabled(), returning {}", response);
        return response;
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
        ResponseEntity<VersionResource> response = new ResponseEntity<>(resource, HttpStatus.OK);
        LOGGER.debug("Exit from currentVersion(), returning {}", response);
        return response;
    }
}
