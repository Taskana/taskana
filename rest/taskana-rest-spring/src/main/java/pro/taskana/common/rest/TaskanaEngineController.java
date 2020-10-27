package pro.taskana.common.rest;

import java.util.List;
import java.util.Map;
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
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
import pro.taskana.common.rest.models.VersionRepresentationModel;

/** Controller for TaskanaEngine related tasks. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class TaskanaEngineController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineController.class);

  private final TaskanaEngineConfiguration taskanaEngineConfiguration;

  private final TaskanaEngine taskanaEngine;

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

  @GetMapping(path = Mapping.URL_CLASSIFICATION_CATEGORIES)
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

  @GetMapping(path = Mapping.URL_CLASSIFICATION_TYPES)
  public ResponseEntity<List<String>> getClassificationTypes() {
    ResponseEntity<List<String>> response =
        ResponseEntity.ok(taskanaEngineConfiguration.getClassificationTypes());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassificationTypes(), returning {}", response);
    }
    return response;
  }

  @GetMapping(path = Mapping.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  public ResponseEntity<Map<String, List<String>>> getClassificationCategoriesByTypeMap() {
    ResponseEntity<Map<String, List<String>>> response =
        ResponseEntity.ok(taskanaEngineConfiguration.getClassificationCategoriesByTypeMap());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassificationCategoriesByTypeMap(), returning {}", response);
    }
    return response;
  }

  @GetMapping(path = Mapping.URL_CURRENT_USER)
  public ResponseEntity<TaskanaUserInfoRepresentationModel> getCurrentUserInfo() {
    LOGGER.debug("Entry to getCurrentUserInfo()");
    TaskanaUserInfoRepresentationModel resource = new TaskanaUserInfoRepresentationModel();
    resource.setUserId(taskanaEngine.getCurrentUserContext().getUserid());
    resource.setGroupIds(taskanaEngine.getCurrentUserContext().getGroupIds());
    for (TaskanaRole role : taskanaEngineConfiguration.getRoleMap().keySet()) {
      if (taskanaEngine.isUserInRole(role)) {
        resource.getRoles().add(role);
      }
    }
    ResponseEntity<TaskanaUserInfoRepresentationModel> response = ResponseEntity.ok(resource);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getCurrentUserInfo(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_HISTORY_ENABLED)
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
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    LOGGER.debug("Entry to currentVersion()");
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
    ResponseEntity<VersionRepresentationModel> response = ResponseEntity.ok(resource);
    LOGGER.debug("Exit from currentVersion(), returning {}", response);
    return response;
  }
}
