package pro.taskana.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.rest.resource.TaskanaUserInfoResource;
import pro.taskana.rest.resource.VersionResource;

/** Controller for TaskanaEngine related tasks. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class TaskanaEngineController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineController.class);

  private TaskanaEngineConfiguration taskanaEngineConfiguration;

  private TaskanaEngine taskanaEngine;

  @Value("${version:Local build}")
  private String version;

  TaskanaEngineController(
      TaskanaEngineConfiguration taskanaEngineConfiguration, TaskanaEngine taskanaEngine) {
    this.taskanaEngineConfiguration = taskanaEngineConfiguration;
    this.taskanaEngine = taskanaEngine;
  }

  @GetMapping(path = Mapping.URL_DOMAIN)
  public ResponseEntity<List<String>> getDomains() {
    ResponseEntity<List<String>> response =
        ResponseEntity.ok(taskanaEngineConfiguration.getDomains());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getDomains(), returning {}", response);
    }
    return response;
  }

  @GetMapping(path = Mapping.URL_CLASSIFICATIONCATEGORIES)
  public ResponseEntity<List<String>> getClassificationCategories(String type) {
    LOGGER.debug("Entry to getClassificationCategories(type = {})", type);
    ResponseEntity<List<String>> response;
    if (type != null) {
      response =
          ResponseEntity.ok(taskanaEngineConfiguration.getClassificationCategoriesByType(type));
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

  @GetMapping(path = Mapping.URL_CLASSIFICATIONTYPES)
  public ResponseEntity<List<String>> getClassificationTypes() {
    ResponseEntity<List<String>> response =
        ResponseEntity.ok(taskanaEngineConfiguration.getClassificationTypes());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassificationTypes(), returning {}", response);
    }
    return response;
  }

  @GetMapping(path = Mapping.URL_CURRENTUSER)
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

  @GetMapping(path = Mapping.URL_HISTORYENABLED)
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
  @GetMapping(path = Mapping.URL_VERSION)
  public ResponseEntity<VersionResource> currentVersion() {
    LOGGER.debug("Entry to currentVersion()");
    VersionResource resource = new VersionResource();
    resource.setVersion(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
    ResponseEntity<VersionResource> response = ResponseEntity.ok(resource);
    LOGGER.debug("Exit from currentVersion(), returning {}", response);
    return response;
  }
}
