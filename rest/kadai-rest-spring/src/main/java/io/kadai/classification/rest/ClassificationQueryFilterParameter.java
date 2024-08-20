package io.kadai.classification.rest;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;
import static io.kadai.common.internal.util.CheckedConsumer.wrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationQuery;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.rest.QueryParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Optional;
import java.util.stream.Stream;

public class ClassificationQueryFilterParameter
    implements QueryParameter<ClassificationQuery, Void> {

  public String[] getName() {
    return name;
  }

  public String[] getNameLike() {
    return nameLike;
  }

  public String[] getKey() {
    return key;
  }

  public String[] getCategory() {
    return category;
  }

  public String[] getDomain() {
    return domain;
  }

  public String[] getType() {
    return type;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  public String[] getCustom5Like() {
    return custom5Like;
  }

  public String[] getCustom6Like() {
    return custom6Like;
  }

  public String[] getCustom7Like() {
    return custom7Like;
  }

  public String[] getCustom8Like() {
    return custom8Like;
  }

  @Schema(
      name = "name",
      description = "Filter by the name of the Classification. This is an exact match.")
  @JsonProperty("name")
  private final String[] name;

  @Schema(
      name = "name-like",
      description =
          "Filter by the name of the Classification. This results in a substring search. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("name-like")
  private final String[] nameLike;

  @Schema(
      name = "key",
      description = "Filter by the key of the Classification. This is an exact match.")
  @JsonProperty("key")
  private final String[] key;

  @Schema(
      name = "category",
      description = "Filter by the category of the Classification. This is an exact match.")
  @JsonProperty("category")
  private final String[] category;

  @Schema(
      name = "domain",
      description = "Filter by the domain of the Classification. This is an exact match.")
  @JsonProperty("domain")
  private final String[] domain;

  @Schema(
      name = "type",
      description = "Filter by the type of the Classification. This is an exact match.")
  @JsonProperty("type")
  private final String[] type;

  @Schema(
      name = "custom-1-like",
      description =
          "Filter by the value of the field custom1. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  @Schema(
      name = "custom-2-like",
      description =
          "Filter by the value of the field custom2. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  @Schema(
      name = "custom-3-like",
      description =
          "Filter by the value of the field custom3. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  @Schema(
      name = "custom-4-like",
      description =
          "Filter by the value of the field custom4. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  @Schema(
      name = "custom-5-like",
      description =
          "Filter by the value of the field custom5. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  @Schema(
      name = "custom-6-like",
      description =
          "Filter by the value of the field custom6. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;
  @Schema(
      name = "custom-7-like",
      description =
          "Filter by the value of the field custom7. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  @Schema(
      name = "custom-8-like",
      description =
          "Filter by the value of the field custom8. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
    "name",
    "name-like",
    "key",
    "category",
    "domain",
    "type",
    "custom-1-like",
    "custom-2-like",
    "custom-3-like",
    "custom-4-like",
    "custom-5-like",
    "custom-6-like",
    "custom-7-like",
    "custom-8-like"
  })
  public ClassificationQueryFilterParameter(
      String[] name,
      String[] nameLike,
      String[] key,
      String[] category,
      String[] domain,
      String[] type,
      String[] custom1Like,
      String[] custom2Like,
      String[] custom3Like,
      String[] custom4Like,
      String[] custom5Like,
      String[] custom6Like,
      String[] custom7Like,
      String[] custom8Like) {
    this.name = name;
    this.nameLike = nameLike;
    this.key = key;
    this.category = category;
    if (domain != null && domain.length == 0) {
      this.domain = new String[] {MASTER_DOMAIN};
    } else {
      this.domain = domain;
    }
    this.type = type;
    this.custom1Like = custom1Like;
    this.custom2Like = custom2Like;
    this.custom3Like = custom3Like;
    this.custom4Like = custom4Like;
    this.custom5Like = custom5Like;
    this.custom6Like = custom6Like;
    this.custom7Like = custom7Like;
    this.custom8Like = custom8Like;
  }

  @Override
  public Void apply(ClassificationQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(key).ifPresent(query::keyIn);
    Optional.ofNullable(category).ifPresent(query::categoryIn);
    Optional.ofNullable(domain).ifPresent(query::domainIn);
    Optional.ofNullable(type).ifPresent(query::typeIn);
    Stream.of(
            Pair.of(ClassificationCustomField.CUSTOM_1, custom1Like),
            Pair.of(ClassificationCustomField.CUSTOM_2, custom2Like),
            Pair.of(ClassificationCustomField.CUSTOM_3, custom3Like),
            Pair.of(ClassificationCustomField.CUSTOM_4, custom4Like),
            Pair.of(ClassificationCustomField.CUSTOM_5, custom5Like),
            Pair.of(ClassificationCustomField.CUSTOM_6, custom6Like),
            Pair.of(ClassificationCustomField.CUSTOM_7, custom7Like),
            Pair.of(ClassificationCustomField.CUSTOM_8, custom8Like))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .map(this::wrapElementsInLikeStatement)
                    .ifPresent(wrap(l -> query.customAttributeLike(pair.getLeft(), l))));
    return null;
  }
}
