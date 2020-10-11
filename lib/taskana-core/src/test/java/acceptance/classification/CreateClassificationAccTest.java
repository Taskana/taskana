package acceptance.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;

/** Acceptance test for all "create classification" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateClassificationAccTest extends AbstractAccTest {

  private static final ClassificationService CLASSIFICATION_SERVICE =
      taskanaEngine.getClassificationService();

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateMasterClassification() throws Exception {
    long amountOfClassificationsBefore = CLASSIFICATION_SERVICE.createClassificationQuery().count();
    Classification classification = CLASSIFICATION_SERVICE.newClassification("Key0", "", "TASK");
    classification.setIsValidInDomain(true);
    classification = CLASSIFICATION_SERVICE.createClassification(classification);

    // check only 1 created
    long amountOfClassificationsAfter = CLASSIFICATION_SERVICE.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 1);

    classification = CLASSIFICATION_SERVICE.getClassification(classification.getId());
    assertThat(classification).isNotNull();

    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isFalse();
    assertThat(classification.getId()).startsWith(IdGenerator.ID_PREFIX_CLASSIFICATION);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithMasterCopy() throws Exception {
    final long countClassificationsBefore =
        CLASSIFICATION_SERVICE.createClassificationQuery().count();
    Classification classification =
        CLASSIFICATION_SERVICE.newClassification("Key1", "DOMAIN_A", "TASK");
    classification.setIsValidInDomain(true);
    classification = CLASSIFICATION_SERVICE.createClassification(classification);

    // Check returning one is the "original"
    Classification createdClassification =
        CLASSIFICATION_SERVICE.getClassification(classification.getId());
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getId()).startsWith(IdGenerator.ID_PREFIX_CLASSIFICATION);
    assertThat(createdClassification.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(createdClassification.getKey()).isEqualTo("Key1");

    // Check 2 new created
    long amountOfClassificationsAfter = CLASSIFICATION_SERVICE.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter).isEqualTo(countClassificationsBefore + 2);

    // Check main
    classification = CLASSIFICATION_SERVICE.getClassification(classification.getId());
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getId()).startsWith(IdGenerator.ID_PREFIX_CLASSIFICATION);

    // Check master-copy
    classification = CLASSIFICATION_SERVICE.getClassification(classification.getKey(), "");
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isFalse();
    assertThat(classification.getId()).startsWith(IdGenerator.ID_PREFIX_CLASSIFICATION);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithExistingMaster() throws Exception {

    CLASSIFICATION_SERVICE.createClassification(
        CLASSIFICATION_SERVICE.newClassification("Key0815", "", "TASK"));

    long amountOfClassificationsBefore = CLASSIFICATION_SERVICE.createClassificationQuery().count();
    Classification expected =
        CLASSIFICATION_SERVICE.newClassification("Key0815", "DOMAIN_B", "TASK");
    Classification actual = CLASSIFICATION_SERVICE.createClassification(expected);
    long amountOfClassificationsAfter = CLASSIFICATION_SERVICE.createClassificationQuery().count();

    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 1);
    assertThat(actual).isNotNull();
    assertThat(actual).isSameAs(expected);
    assertThat(actual.getIsValidInDomain()).isTrue();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateChildInDomainAndCopyInMaster() throws Exception {
    Classification parent = CLASSIFICATION_SERVICE.newClassification("Key0816", "DOMAIN_A", "TASK");
    Classification actualParent = CLASSIFICATION_SERVICE.createClassification(parent);
    assertThat(actualParent).isNotNull();

    long amountOfClassificationsBefore = CLASSIFICATION_SERVICE.createClassificationQuery().count();
    Classification child = CLASSIFICATION_SERVICE.newClassification("Key0817", "DOMAIN_A", "TASK");
    child.setParentId(actualParent.getId());
    child.setParentKey(actualParent.getKey());
    Classification actualChild = CLASSIFICATION_SERVICE.createClassification(child);
    long amountOfClassificationsAfter = CLASSIFICATION_SERVICE.createClassificationQuery().count();

    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 2);
    assertThat(actualChild).isNotNull();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidValues() {
    CLASSIFICATION_SERVICE.createClassificationQuery().count();

    // Check key NULL
    Classification classification =
        CLASSIFICATION_SERVICE.newClassification(null, "DOMAIN_A", "TASK");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);

    // Check invalid ServiceLevel

    Classification classification2 =
        CLASSIFICATION_SERVICE.newClassification("Key2", "DOMAIN_B", "TASK");
    classification2.setServiceLevel("abc");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification2))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationAlreadyExisting() throws Exception {
    Classification classification = CLASSIFICATION_SERVICE.newClassification("Key3", "", "TASK");
    Classification classificationCreated =
        CLASSIFICATION_SERVICE.createClassification(classification);
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classificationCreated))
        .isInstanceOf(ClassificationAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationInUnknownDomain() {
    Classification classification =
        CLASSIFICATION_SERVICE.newClassification("Key3", "UNKNOWN_DOMAIN", "TASK");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(DomainNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationOfUnknownType() {
    Classification classification =
        CLASSIFICATION_SERVICE.newClassification("Key3", "DOMAIN_A", "UNKNOWN_TYPE");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationOfUnknownCategory() {
    Classification classification =
        CLASSIFICATION_SERVICE.newClassification("Key4", "DOMAIN_A", "TASK");
    classification.setCategory("UNKNOWN_CATEGORY");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidParentId() {
    Classification classification = CLASSIFICATION_SERVICE.newClassification("Key5", "", "TASK");
    classification.setParentId("ID WHICH CANT BE FOUND");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidParentKey() {
    Classification classification = CLASSIFICATION_SERVICE.newClassification("Key5", "", "TASK");
    classification.setParentKey("KEY WHICH CANT BE FOUND");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithExplicitId() {
    ClassificationImpl classification =
        (ClassificationImpl) CLASSIFICATION_SERVICE.newClassification("Key0818", "", "TASK");
    classification.setId("EXPLICIT ID");
    assertThatThrownBy(() -> CLASSIFICATION_SERVICE.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_BeAbleToCreateNewClassification_When_ClassificationCopy() throws Exception {
    ClassificationImpl oldClassification =
        (ClassificationImpl) CLASSIFICATION_SERVICE.getClassification("T2100", "DOMAIN_B");
    Classification newClassification = oldClassification.copy("T9949");

    newClassification = CLASSIFICATION_SERVICE.createClassification(newClassification);

    assertThat(newClassification.getId()).isNotNull();
    assertThat(newClassification.getId()).isNotEqualTo(oldClassification.getId());
  }

  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    ClassificationImpl classification =
        (ClassificationImpl) CLASSIFICATION_SERVICE.newClassification("newKey718", "", "TASK");

    ThrowingCallable createClassificationCall =
        () -> {
          CLASSIFICATION_SERVICE.createClassification(classification);
        };

    assertThatThrownBy(createClassificationCall).isInstanceOf(NotAuthorizedException.class);
  }
}
