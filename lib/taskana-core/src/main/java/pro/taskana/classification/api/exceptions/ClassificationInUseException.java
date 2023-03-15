package pro.taskana.classification.api.exceptions;

import java.util.Map;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;

/**
 * This exception is thrown when a specific {@linkplain Classification} was tried to be deleted
 * while still being in use. <br>
 * This could mean that there are either {@linkplain Task Tasks} or {@linkplain Attachment
 * Attachments} associated with it.
 */
public class ClassificationInUseException extends TaskanaException {

  public static final String ERROR_KEY = "CLASSIFICATION_IN_USE";
  private final String classificationKey;
  private final String domain;

  public ClassificationInUseException(Classification classification, Throwable cause) {
    super(
        String.format(
            "The Classification with id = '%s' and key = '%s' in domain = '%s' "
                + "is in use and cannot be deleted. There are either Tasks or "
                + "Attachments associated with the Classification.",
            classification.getId(), classification.getKey(), classification.getDomain()),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("classificationKey", ensureNullIsHandled(classification.getKey())),
                Map.entry("domain", ensureNullIsHandled(classification.getDomain())))),
        cause);
    classificationKey = classification.getKey();
    domain = classification.getDomain();
  }

  public String getClassificationKey() {
    return classificationKey;
  }

  public String getDomain() {
    return domain;
  }
}
