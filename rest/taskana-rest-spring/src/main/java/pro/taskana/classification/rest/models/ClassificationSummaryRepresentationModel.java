package pro.taskana.classification.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;
import pro.taskana.classification.api.models.ClassificationSummary;

/** EntityModel class for {@link ClassificationSummary}. */
public class ClassificationSummaryRepresentationModel
    extends RepresentationModel<ClassificationSummaryRepresentationModel> {

  /** Unique Id. */
  @Schema(name = "classificationId", description = "Unique Id.")
  @NotNull
  protected String classificationId;
  /**
   * The key of the Classification. This is typically an externally known code or abbreviation of
   * the Classification.
   */
  @Schema(
      name = "key",
      description =
          "The key of the Classification. This is typically an externally known code or "
              + "abbreviation of the Classification.")
  @NotNull
  protected String key;
  /**
   * The logical name of the entry point. This is needed by the task list application to determine
   * the redirect to work on a task of this Classification.
   */
  @Schema(
      name = "applicationEntryPoint",
      description =
          "The logical name of the entry point. This is needed by the task list application to "
              + "determine the redirect to work on a task of this Classification.")
  protected String applicationEntryPoint;
  /**
   * The category of the classification. Categories can be configured in the file
   * 'taskana.properties'.
   */
  @Schema(
      name = "category",
      description =
          "The category of the classification. Categories can be configured in the file "
              + "'taskana.properties'.")
  @NotNull
  protected String category;
  /** The domain for which this classification is specified. */
  @Schema(name = "domain", description = "The domain for which this classification is specified.")
  protected String domain;
  /** The name of the classification. */
  @Schema(name = "name", description = "The name of the classification.")
  @NotNull
  protected String name;
  /** The Id of the parent classification. Empty string ("") if this is a root classification. */
  @Schema(
      name = "parentId",
      description =
          "The Id of the parent classification. Empty string (\"\") if this is a root "
              + "classification.")
  protected String parentId;
  /** The key of the parent classification. Empty string ("") if this is a root classification. */
  @Schema(
      name = "parentKey",
      description =
          "The key of the parent classification. Empty string (\"\") if this is a root "
              + "classification.")
  protected String parentKey;
  /** The priority of the classification. */
  @Schema(name = "priority", description = "The priority of the classification.")
  @NotNull
  protected int priority;
  /**
   * The service level of the classification.
   *
   * <p>This is stated according to ISO 8601.
   */
  @Schema(
      name = "serviceLevel",
      description =
          "The service level of the classification.<p>This is stated according to ISO 8601.")
  @NotNull
  protected String serviceLevel;
  /** The type of classification. Types can be configured in the file 'taskana.properties'. */
  @Schema(
      name = "type",
      description =
          "The type of classification. Types can be configured in the file 'taskana.properties'.")
  protected String type;
  /** A custom property with name "1". */
  @Schema(name = "custom1", description = "A custom property with name \"1\".")
  protected String custom1;
  /** A custom property with name "2". */
  @Schema(name = "custom2", description = "A custom property with name \"2\".")
  protected String custom2;
  /** A custom property with name "3". */
  @Schema(name = "custom3", description = "A custom property with name \"3\".")
  protected String custom3;
  /** A custom property with name "4". */
  @Schema(name = "custom4", description = "A custom property with name \"4\".")
  protected String custom4;
  /** A custom property with name "5". */
  @Schema(name = "custom5", description = "A custom property with name \"5\".")
  protected String custom5;
  /** A custom property with name "6". */
  @Schema(name = "custom6", description = "A custom property with name \"6\".")
  protected String custom6;
  /** A custom property with name "7". */
  @Schema(name = "custom7", description = "A custom property with name \"7\".")
  protected String custom7;
  /** A custom property with name "8". */
  @Schema(name = "custom8", description = "A custom property with name \"8\".")
  protected String custom8;

  public String getClassificationId() {
    return classificationId;
  }

  public void setClassificationId(String classificationId) {
    this.classificationId = classificationId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getParentKey() {
    return parentKey;
  }

  public void setParentKey(String parentKey) {
    this.parentKey = parentKey;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getApplicationEntryPoint() {
    return applicationEntryPoint;
  }

  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint = applicationEntryPoint;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  public String getCustom6() {
    return custom6;
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6;
  }

  public String getCustom7() {
    return custom7;
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7;
  }

  public String getCustom8() {
    return custom8;
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8;
  }
}
