package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.TaskanaException;

/** Thrown, when a classification does already exits, but wanted to create with same ID+domain. */
public class ClassificationAlreadyExistException extends TaskanaException {

  public ClassificationAlreadyExistException(Classification classification) {
    super(
        "ID='"
            + classification.getId()
            + "', KEY='"
            + classification.getKey()
            + "', DOMAIN='"
            + classification.getDomain()
            + "';");
  }
}
