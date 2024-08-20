package io.kadai.testapi.builder;

import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.exceptions.ClassificationAlreadyExistException;
import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.exceptions.MalformedServiceLevelException;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.testapi.builder.EntityBuilder.SummaryEntityBuilder;
import java.time.Instant;

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
