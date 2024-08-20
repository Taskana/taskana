package io.kadai.classification.api.exceptions;

import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import java.util.Map;

/**
 * This exception is thrown when a {@linkplain Classification} does already exits, but was tried to
 * be created with the same {@linkplain Classification#getId() id} and {@linkplain
 * Classification#getDomain() domain}.
 */
public class ClassificationAlreadyExistException extends KadaiException {

  public static final String ERROR_KEY = "CLASSIFICATION_ALREADY_EXISTS";
  private final String domain;
  private final String classificationKey;

  public ClassificationAlreadyExistException(Classification classification) {
    this(classification.getKey(), classification.getDomain());
  }

  public ClassificationAlreadyExistException(String key, String domain) {
    super(
        String.format("A Classification with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("classificationKey", ensureNullIsHandled(key)),
                Map.entry("domain", ensureNullIsHandled(domain)))));
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
