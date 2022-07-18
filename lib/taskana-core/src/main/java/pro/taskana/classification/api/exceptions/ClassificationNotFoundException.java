package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.internal.util.MapCreator;

/**
 * The ClassificationNotFoundException is thrown if a specific {@linkplain Classification} is not in
 * the database.
 */
public class ClassificationNotFoundException extends NotFoundException {

  public static final String ERROR_KEY_ID = "CLASSIFICATION_WITH_ID_NOT_FOUND";
  public static final String ERROR_KEY_KEY_DOMAIN = "CLASSIFICATION_WITH_KEY_NOT_FOUND";
  private final String classificationId;
  private final String classificationKey;
  private final String domain;

  public ClassificationNotFoundException(String classificationId) {
    super(
        String.format("Classification with id '%s' wasn't found", classificationId),
        ErrorCode.of(ERROR_KEY_ID, MapCreator.of("classificationId", classificationId)));
    this.classificationId = classificationId;
    classificationKey = null;
    domain = null;
  }

  public ClassificationNotFoundException(String key, String domain) {
    super(
        String.format(
            "Classification with key '%s' and domain '%s' could not be found", key, domain),
        ErrorCode.of(
            ERROR_KEY_KEY_DOMAIN, MapCreator.of("classificationKey", key, "domain", domain)));
    this.classificationKey = key;
    this.domain = domain;
    classificationId = null;
  }

  public String getClassificationKey() {
    return classificationKey;
  }

  public String getDomain() {
    return domain;
  }

  public String getClassificationId() {
    return classificationId;
  }
}
