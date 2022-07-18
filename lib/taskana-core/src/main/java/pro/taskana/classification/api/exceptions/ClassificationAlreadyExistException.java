package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;

/**
 * The ClassificationAlreadyExistException is thrown when a {@linkplain Classification} does already
 * exits, but was tried to be created with the same {@linkplain Classification#getId() id} and
 * {@linkplain Classification#getDomain() domain}.
 */
public class ClassificationAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "CLASSIFICATION_ALREADY_EXISTS";
  private final String domain;
  private final String classificationKey;

  public ClassificationAlreadyExistException(Classification classification) {
    this(classification.getKey(), classification.getDomain());
  }

  public ClassificationAlreadyExistException(String key, String domain) {
    super(
        String.format("A Classification with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("classificationKey", key, "domain", domain)));
    classificationKey = key;
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }

  public String getClassificationKey() {
    return classificationKey;
  }
}
