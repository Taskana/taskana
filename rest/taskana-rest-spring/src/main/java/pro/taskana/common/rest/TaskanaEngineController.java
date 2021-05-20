package pro.taskana.common.rest;

import java.util.List;
import java.util.Map;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  private final TaskanaEngineConfiguration taskanaEngineConfiguration;

  private final TaskanaEngine taskanaEngine;

  TaskanaEngineController(
      TaskanaEngineConfiguration taskanaEngineConfiguration, TaskanaEngine taskanaEngine) {
    this.taskanaEngineConfiguration = taskanaEngineConfiguration;
    this.taskanaEngine = taskanaEngine;
  }

  /**
   * This endpoint retrieves all configured Domains.
   *
   * @return An array with the domain-names as strings
   */
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  public ResponseEntity<List<String>> getDomains() {
    return ResponseEntity.ok(taskanaEngineConfiguration.getDomains());
  }

  /**
   * This endpoint retrieves the configured classification categories for a specific classification
   * type.
   *
   * @param type the classification type whose categories should be determined. If not specified all
   *     classification categories will be returned.
   * @return the classification categories for the requested type.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES)
  public ResponseEntity<List<String>> getClassificationCategories(
      @RequestParam(required = false) String type) {
    ResponseEntity<List<String>> response;
    if (type != null) {
      return ResponseEntity.ok(taskanaEngineConfiguration.getClassificationCategoriesByType(type));
    }
    return ResponseEntity.ok(taskanaEngineConfiguration.getAllClassificationCategories());
  }

  /**
   * This endpoint retrieves the configured classification types.
   *
   * @return the configured classification types.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_TYPES)
  public ResponseEntity<List<String>> getClassificationTypes() {
    return ResponseEntity.ok(taskanaEngineConfiguration.getClassificationTypes());
  }

  /**
   * This endpoint retrieves all configured classification categories grouped by each classification
   * type.
   *
   * @return the configured classification categories
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  public ResponseEntity<Map<String, List<String>>> getClassificationCategoriesByTypeMap() {
    return ResponseEntity.ok(taskanaEngineConfiguration.getClassificationCategoriesByTypeMap());
  }

  /**
   * This endpoint computes all information of the current user.
   *
   * @return the information of the current user.
   */
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  public ResponseEntity<TaskanaUserInfoRepresentationModel> getCurrentUserInfo() {
    TaskanaUserInfoRepresentationModel resource = new TaskanaUserInfoRepresentationModel();
    resource.setUserId(taskanaEngine.getCurrentUserContext().getUserid());
    resource.setGroupIds(taskanaEngine.getCurrentUserContext().getGroupIds());
    for (TaskanaRole role : taskanaEngineConfiguration.getRoleMap().keySet()) {
      if (taskanaEngine.isUserInRole(role)) {
        resource.getRoles().add(role);
      }
    }
    return ResponseEntity.ok(resource);
  }

  /**
   * This endpoint checks if the history module is in use.
   *
   * @return true, when the history is enabled, otherwise false
   */
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
    return ResponseEntity.ok(taskanaEngine.isHistoryEnabled());
  }

  /**
   * Get the current application version.
   *
   * @return The current version.
   */
  @GetMapping(path = RestEndpoints.URL_VERSION)
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
    return ResponseEntity.ok(resource);
  }
}
