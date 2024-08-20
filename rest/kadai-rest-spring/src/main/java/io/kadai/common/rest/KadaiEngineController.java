package io.kadai.common.rest;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.rest.models.CustomAttributesRepresentationModel;
import io.kadai.common.rest.models.KadaiUserInfoRepresentationModel;
import io.kadai.common.rest.models.VersionRepresentationModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for KadaiEngine related tasks. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class KadaiEngineController {

  private final KadaiConfiguration kadaiConfiguration;
  private final KadaiEngine kadaiEngine;
  private final CurrentUserContext currentUserContext;
  private final ConfigurationService configurationService;

  @Autowired
  KadaiEngineController(
      KadaiConfiguration kadaiConfiguration,
      KadaiEngine kadaiEngine,
      CurrentUserContext currentUserContext,
      ConfigurationService configurationService) {
    this.kadaiConfiguration = kadaiConfiguration;
    this.kadaiEngine = kadaiEngine;
    this.currentUserContext = currentUserContext;
    this.configurationService = configurationService;
  }

  @Operation(
      summary = "This endpoint retrieves all configured Domains.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "An array with the domain-names as strings",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getDomains() {
    return ResponseEntity.ok(kadaiConfiguration.getDomains());
  }

  @Operation(
      summary =
          "This endpoint retrieves the configured classification categories for a specific "
              + "classification type.",
      parameters =
          @Parameter(
              name = "type",
              description =
                  "The classification type whose categories should be determined. If not specified "
                      + "all classification categories will be returned."),
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The classification categories for the requested type.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getClassificationCategories(
      @RequestParam(value = "type", required = false) String type) {
    if (type != null) {
      return ResponseEntity.ok(kadaiConfiguration.getClassificationCategoriesByType(type));
    }
    return ResponseEntity.ok(kadaiConfiguration.getAllClassificationCategories());
  }

  @Operation(
      summary = "This endpoint retrieves the configured classification types.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The configured classification types.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getClassificationTypes() {
    return ResponseEntity.ok(kadaiConfiguration.getClassificationTypes());
  }

  @Operation(
      summary =
          "This endpoint retrieves all configured classification categories grouped by each "
              + "classification type.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The configured classification categories.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(ref = "#/components/schemas/TypeMapSchema"))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Map<String, List<String>>> getClassificationCategoriesByTypeMap() {
    return ResponseEntity.ok(kadaiConfiguration.getClassificationCategoriesByType());
  }

  @Operation(
      summary = "This endpoint computes all information of the current user.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The information of the current user.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = KadaiUserInfoRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<KadaiUserInfoRepresentationModel> getCurrentUserInfo() {
    KadaiUserInfoRepresentationModel resource = new KadaiUserInfoRepresentationModel();
    resource.setUserId(currentUserContext.getUserid());
    resource.setGroupIds(currentUserContext.getGroupIds());
    kadaiConfiguration.getRoleMap().keySet().stream()
        .filter(kadaiEngine::isUserInRole)
        .forEach(resource.getRoles()::add);
    return ResponseEntity.ok(resource);
  }

  @Operation(
      summary = "This endpoint checks if the history module is in use.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "True, when the history is enabled, otherwise false",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = Boolean.class))))
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
    return ResponseEntity.ok(kadaiEngine.isHistoryEnabled());
  }

  @Operation(
      summary = "Get custom configuration",
      description = "This endpoint retrieves the saved custom configuration.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The custom configuration.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema =
                          @Schema(implementation = CustomAttributesRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> getCustomAttributes() {
    Map<String, Object> allCustomAttributes = configurationService.getAllCustomAttributes();
    return ResponseEntity.ok(new CustomAttributesRepresentationModel(allCustomAttributes));
  }

  @Operation(
      summary = "Set all custom configuration",
      description = "This endpoint overrides the custom configuration.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The new custom configuration",
              required = true,
              content =
                  @Content(
                      schema = @Schema(implementation = CustomAttributesRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"customAttributes\": {\n"
                                      + "    \"schema\": {\n"
                                      + "      \"Filter\": {\n"
                                      + "        \"displayName\": "
                                      + "\"Filter for Task-Priority-Report\",\n"
                                      + "        \"members\": {\n"
                                      + "          \"filter\": {\n"
                                      + "            \"displayName\": \"Filter values\",\n"
                                      + "            \"type\": \"json\",\n"
                                      + "            \"min\": \"1\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      },\n"
                                      + "      \"filter\": \"{ \\\"Tasks with state READY\\\": "
                                      + "{ \\\"state\\\": [\\\"READY\\\"]}, \\\"Tasks with state "
                                      + "CLAIMED\\\": {\\\"state\\\": [\\\"CLAIMED\\\"] }}\"\n"
                                      + "    }\n"
                                      + "  }\n"
                                      + "}"))),
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The new custom configuration.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema =
                          @Schema(implementation = CustomAttributesRepresentationModel.class))))
  @PutMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> setCustomAttributes(
      @RequestBody CustomAttributesRepresentationModel customAttributes) {
    configurationService.setAllCustomAttributes(customAttributes.getCustomAttributes());
    return ResponseEntity.ok(customAttributes);
  }

  @Operation(
      summary = "Get the current application version",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The current version.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = VersionRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_VERSION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(KadaiConfiguration.class.getPackage().getImplementationVersion());
    return ResponseEntity.ok(resource);
  }
}
