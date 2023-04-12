package pro.taskana.common.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.rest.models.CustomAttributesRepresentationModel;
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
import pro.taskana.common.rest.models.VersionRepresentationModel;

/** Controller for TaskanaEngine related tasks. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class TaskanaEngineController {

  private final TaskanaConfiguration taskanaConfiguration;
  private final TaskanaEngine taskanaEngine;
  private final CurrentUserContext currentUserContext;
  private final ConfigurationService configurationService;

  @Autowired
  TaskanaEngineController(
      TaskanaConfiguration taskanaConfiguration,
      TaskanaEngine taskanaEngine,
      CurrentUserContext currentUserContext,
      ConfigurationService configurationService) {
    this.taskanaConfiguration = taskanaConfiguration;
    this.taskanaEngine = taskanaEngine;
    this.currentUserContext = currentUserContext;
    this.configurationService = configurationService;
  }

  /**
   * This endpoint retrieves all configured Domains.
   *
   * @return An array with the domain-names as strings
   */
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getDomains() {
    return ResponseEntity.ok(taskanaConfiguration.getDomains());
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
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Set<String>> getClassificationCategories(
      @RequestParam(required = false) String type) {
    if (type != null) {
      return ResponseEntity.ok(taskanaConfiguration.getClassificationCategoriesByType(type));
    }
    return ResponseEntity.ok(taskanaConfiguration.getAllClassificationCategories());
  }

  /**
   * This endpoint retrieves the configured classification types.
   *
   * @return the configured classification types.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Set<String>> getClassificationTypes() {
    return ResponseEntity.ok(taskanaConfiguration.getClassificationTypes());
  }

  /**
   * This endpoint retrieves all configured classification categories grouped by each classification
   * type.
   *
   * @return the configured classification categories
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Map<String, Set<String>>> getClassificationCategoriesByTypeMap() {
    return ResponseEntity.ok(taskanaConfiguration.getClassificationCategoriesByType());
  }

  /**
   * This endpoint computes all information of the current user.
   *
   * @return the information of the current user.
   */
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaUserInfoRepresentationModel> getCurrentUserInfo() {
    TaskanaUserInfoRepresentationModel resource = new TaskanaUserInfoRepresentationModel();
    resource.setUserId(currentUserContext.getUserid());
    resource.setGroupIds(currentUserContext.getGroupIds());
    taskanaConfiguration.getRoleMap().keySet().stream()
        .filter(taskanaEngine::isUserInRole)
        .forEach(resource.getRoles()::add);
    return ResponseEntity.ok(resource);
  }

  /**
   * This endpoint checks if the history module is in use.
   *
   * @return true, when the history is enabled, otherwise false
   */
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
    return ResponseEntity.ok(taskanaEngine.isHistoryEnabled());
  }

  /**
   * This endpoint retrieves the saved custom configuration.
   *
   * @title Get custom configuration
   * @return custom configuration
   */
  @GetMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> getCustomAttributes() {
    Map<String, Object> allCustomAttributes = configurationService.getAllCustomAttributes();
    return ResponseEntity.ok(new CustomAttributesRepresentationModel(allCustomAttributes));
  }

  /**
   * This endpoint overrides the custom configuration.
   *
   * @param customAttributes the new custom configuration
   * @title Set all custom configuration
   * @return the new custom configuration
   */
  @PutMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> setCustomAttributes(
      @RequestBody CustomAttributesRepresentationModel customAttributes) {
    configurationService.setAllCustomAttributes(customAttributes.getCustomAttributes());
    return ResponseEntity.ok(customAttributes);
  }

  /**
   * Get the current application version.
   *
   * @return The current version.
   */
  @GetMapping(path = RestEndpoints.URL_VERSION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(TaskanaConfiguration.class.getPackage().getImplementationVersion());
    return ResponseEntity.ok(resource);
  }
}
