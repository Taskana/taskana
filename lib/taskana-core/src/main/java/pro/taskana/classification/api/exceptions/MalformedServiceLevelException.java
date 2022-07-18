package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;

/**
 * The MalformedServiceLevelException is thrown when the {@linkplain
 * Classification#getServiceLevel() service level} of the {@linkplain Classification} has not the
 * required format. The {@linkplain Classification#getServiceLevel() service level} has to be a
 * positive ISO-8601 duration format and TASKANA only supports whole days. The format must be 'PnD'.
 */
public class MalformedServiceLevelException extends TaskanaException {

  public static final String ERROR_KEY = "CLASSIFICATION_SERVICE_LEVEL_MALFORMED";
  private final String serviceLevel;
  private final String classificationKey;
  private final String domain;

  public MalformedServiceLevelException(
      String serviceLevel, String classificationKey, String domain) {
    super(
        String.format(
            "The provided service level '%s' of the "
                + "Classification with key '%s' and domain '%s' is invalid."
                + "The service level has to be a positive ISO-8601 duration format. "
                + "Furthermore, TASKANA only supports whole days; "
                + "the service level must be in the format 'PnD'",
            serviceLevel, classificationKey, domain),
        ErrorCode.of(
            ERROR_KEY,
            MapCreator.of(
                "classificationKey",
                classificationKey,
                "domain",
                domain,
                "serviceLevel",
                serviceLevel)));
    this.serviceLevel = serviceLevel;
    this.classificationKey = classificationKey;
    this.domain = domain;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public String getClassificationKey() {
    return classificationKey;
  }

  public String getDomain() {
    return domain;
  }
}
