package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if a {@linkplain Classification} does already exist, but wanted to create with same
 * ID+domain.
 */
public class ClassificationAlreadyExistException extends TaskanaException {

  public ClassificationAlreadyExistException(Classification classification) {
    super(
        "A classification with key '"
            + classification.getKey()
            + "' already exists in domain '"
            + classification.getDomain()
            + "'.");
  }
}
