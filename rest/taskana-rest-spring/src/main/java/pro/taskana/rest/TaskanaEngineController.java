package pro.taskana.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaEngine;
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
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskanaEngineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineController.class);

    TaskanaEngineConfiguration taskanaEngineConfiguration;

    TaskanaEngine taskanaEngine;

    TaskanaEngineController(
        TaskanaEngineConfiguration taskanaEngineConfiguration, TaskanaEngine taskanaEngine) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        this.taskanaEngine = taskanaEngine;

    }

    @Value("${version:Local build}")
    private String version;

    @GetMapping(path = "/domains")
    public ResponseEntity<List<String>> getDomains() {
        ResponseEntity<List<String>> response = ResponseEntity.ok(taskanaEngineConfiguration.getDomains());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getDomains(), returning {}", response);
        }
        return response;
    }

    @GetMapping(path = "/classification-categories")
    public ResponseEntity<List<String>> getClassificationCategories(String type) {
        LOGGER.debug("Entry to getClassificationCategories(type = {})", type);
        ResponseEntity<List<String>> response;
        if (type != null) {
            response = ResponseEntity.ok(taskanaEngineConfiguration.getClassificationCategoriesByType(type));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from getClassificationCategories(), returning {}", response);
            }
            return response;
        }
        response = ResponseEntity.ok(taskanaEngineConfiguration.getAllClassificationCategories());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getClassificationCategories(), returning {}", response);
        }
        return response;
    }

    @GetMapping(path = "/classification-types")
    public ResponseEntity<List<String>> getClassificationTypes() {
        ResponseEntity<List<String>> response = ResponseEntity.ok(
            taskanaEngineConfiguration.getClassificationTypes());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getClassificationTypes(), returning {}", response);
        }
        return response;
    }

    @GetMapping(path = "/current-user-info")
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
        ResponseEntity<TaskanaUserInfoResource> response = ResponseEntity.ok(resource);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getCurrentUserInfo(), returning {}", response);
        }

        return response;
    }

    @GetMapping(path = "/history-provider-enabled")
    public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
        ResponseEntity<Boolean> response = ResponseEntity.ok(taskanaEngine.isHistoryEnabled());
        LOGGER.debug("Exit from getIsHistoryProviderEnabled(), returning {}", response);
        return response;
    }

    /**
     * Get the current application version.
     *
     * @return The current version.
     */
    @GetMapping(path = "/version")
    public ResponseEntity<VersionResource> currentVersion() {
        LOGGER.debug("Entry to currentVersion()");
        VersionResource resource = new VersionResource();
        resource.setVersion(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
        ResponseEntity<VersionResource> response = ResponseEntity.ok(resource);
        LOGGER.debug("Exit from currentVersion(), returning {}", response);
        return response;
    }
}
