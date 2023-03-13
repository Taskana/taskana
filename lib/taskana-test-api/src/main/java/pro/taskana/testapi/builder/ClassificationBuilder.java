package pro.taskana.testapi.builder;

import java.time.Instant;
import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.testapi.builder.EntityBuilder.SummaryEntityBuilder;

public class ClassificationBuilder
    implements SummaryEntityBuilder<ClassificationSummary, Classification, ClassificationService> {

  private final ClassificationTestImpl testClassification = new ClassificationTestImpl();

  private ClassificationBuilder() {}

  public static ClassificationBuilder newClassification() {
    return new ClassificationBuilder();
  }

  public ClassificationBuilder applicationEntryPoint(String applicationEntryPoint) {
    testClassification.setApplicationEntryPoint(applicationEntryPoint);
    return this;
  }

  public ClassificationBuilder category(String category) {
    testClassification.setCategory(category);
    return this;
  }

  public ClassificationBuilder domain(String domain) {
    testClassification.setDomain(domain);
    return this;
  }

  public ClassificationBuilder key(String key) {
    testClassification.setKey(key);
    return this;
  }

  public ClassificationBuilder name(String name) {
    testClassification.setName(name);
    return this;
  }

  public ClassificationBuilder parentId(String parentId) {
    testClassification.setParentId(parentId);
    return this;
  }

  public ClassificationBuilder parentKey(String parentKey) {
    testClassification.setParentKey(parentKey);
    return this;
  }

  public ClassificationBuilder priority(int priority) {
    testClassification.setPriority(priority);
    return this;
  }

  public ClassificationBuilder serviceLevel(String serviceLevel) {
    testClassification.setServiceLevel(serviceLevel);
    return this;
  }

  public ClassificationBuilder type(String type) {
    testClassification.setType(type);
    return this;
  }

  public ClassificationBuilder customAttribute(
      ClassificationCustomField customField, String value) {
    testClassification.setCustomField(customField, value);
    return this;
  }

  public ClassificationBuilder isValidInDomain(boolean isValidInDomain) {
    testClassification.setIsValidInDomain(isValidInDomain);
    return this;
  }

  public ClassificationBuilder created(Instant created) {
    testClassification.setCreatedIgnoreFreeze(created);
    if (created != null) {
      testClassification.freezeCreated();
    } else {
      testClassification.unfreezeCreated();
    }
    return this;
  }

  public ClassificationBuilder modified(Instant modified) {
    testClassification.setModifiedIgnoreFreeze(modified);
    if (modified != null) {
      testClassification.freezeModified();
    } else {
      testClassification.unfreezeModified();
    }
    return this;
  }

  public ClassificationBuilder description(String description) {
    testClassification.setDescription(description);
    return this;
  }

  @Override
  public ClassificationSummary entityToSummary(Classification classification) {
    return classification.asSummary();
  }

  @Override
  public Classification buildAndStore(ClassificationService classificationService)
      throws InvalidArgumentException,
          ClassificationAlreadyExistException,
          DomainNotFoundException,
          MalformedServiceLevelException,
          ClassificationNotFoundException,
          NotAuthorizedException {
    try {
      Classification c = classificationService.createClassification(testClassification);
      return classificationService.getClassification(c.getId());
    } finally {
      testClassification.setId(null);
    }
  }
}
