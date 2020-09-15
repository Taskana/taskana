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
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;

/** Acceptance test for all "create classification" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateClassificationAccTest extends AbstractAccTest {

  private static final String ID_PREFIX_CLASSIFICATION = "CLI";

  private ClassificationService classificationService;

  CreateClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateMasterClassification() throws Exception {
    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification classification = classificationService.newClassification("Key0", "", "TASK");
    classification.setIsValidInDomain(true);
    classification = classificationService.createClassification(classification);

    // check only 1 created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 1);

    classification = classificationService.getClassification(classification.getId());
    assertThat(classification).isNotNull();

    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isFalse();
    assertThat(classification.getId()).startsWith(ID_PREFIX_CLASSIFICATION);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithMasterCopy() throws Exception {
    final long countClassificationsBefore =
        classificationService.createClassificationQuery().count();
    Classification classification =
        classificationService.newClassification("Key1", "DOMAIN_A", "TASK");
    classification.setIsValidInDomain(true);
    classification = classificationService.createClassification(classification);

    // Check returning one is the "original"
    Classification createdClassification =
        classificationService.getClassification(classification.getId());
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getId()).startsWith(ID_PREFIX_CLASSIFICATION);
    assertThat(createdClassification.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(createdClassification.getKey()).isEqualTo("Key1");

    // Check 2 new created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter).isEqualTo(countClassificationsBefore + 2);

    // Check main
    classification = classificationService.getClassification(classification.getId());
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getId()).startsWith(ID_PREFIX_CLASSIFICATION);

    // Check master-copy
    classification = classificationService.getClassification(classification.getKey(), "");
    assertThat(classification).isNotNull();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getIsValidInDomain()).isFalse();
    assertThat(classification.getId()).startsWith(ID_PREFIX_CLASSIFICATION);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithExistingMaster() throws Exception {

    classificationService.createClassification(
        classificationService.newClassification("Key0815", "", "TASK"));

    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification expected =
        classificationService.newClassification("Key0815", "DOMAIN_B", "TASK");
    Classification actual = classificationService.createClassification(expected);
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();

    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 1);
    assertThat(actual).isNotNull();
    assertThat(actual).isSameAs(expected);
    assertThat(actual.getIsValidInDomain()).isTrue();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateChildInDomainAndCopyInMaster() throws Exception {
    Classification parent = classificationService.newClassification("Key0816", "DOMAIN_A", "TASK");
    Classification actualParent = classificationService.createClassification(parent);
    assertThat(actualParent).isNotNull();

    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification child = classificationService.newClassification("Key0817", "DOMAIN_A", "TASK");
    child.setParentId(actualParent.getId());
    child.setParentKey(actualParent.getKey());
    Classification actualChild = classificationService.createClassification(child);
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();

    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 2);
    assertThat(actualChild).isNotNull();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidValues() {
    classificationService.createClassificationQuery().count();

    // Check key NULL
    Classification classification =
        classificationService.newClassification(null, "DOMAIN_A", "TASK");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);

    // Check invalid ServiceLevel

    Classification classification2 =
        classificationService.newClassification("Key2", "DOMAIN_B", "TASK");
    classification2.setServiceLevel("abc");
    assertThatThrownBy(() -> classificationService.createClassification(classification2))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationAlreadyExisting() throws Exception {
    Classification classification = classificationService.newClassification("Key3", "", "TASK");
    Classification classificationCreated =
        classificationService.createClassification(classification);
    assertThatThrownBy(() -> classificationService.createClassification(classificationCreated))
        .isInstanceOf(ClassificationAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationInUnknownDomain() {
    Classification classification =
        classificationService.newClassification("Key3", "UNKNOWN_DOMAIN", "TASK");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(DomainNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationOfUnknownType() {
    Classification classification =
        classificationService.newClassification("Key3", "DOMAIN_A", "UNKNOWN_TYPE");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationOfUnknownCategory() {
    Classification classification =
        classificationService.newClassification("Key4", "DOMAIN_A", "TASK");
    classification.setCategory("UNKNOWN_CATEGORY");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidParentId() {
    Classification classification = classificationService.newClassification("Key5", "", "TASK");
    classification.setParentId("ID WHICH CANT BE FOUND");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithInvalidParentKey() {
    Classification classification = classificationService.newClassification("Key5", "", "TASK");
    classification.setParentKey("KEY WHICH CANT BE FOUND");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateClassificationWithExplicitId() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("Key0818", "", "TASK");
    classification.setId("EXPLICIT ID");
    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_BeAbleToCreateNewClassification_When_ClassificationCopy() throws Exception {
    ClassificationImpl oldClassification =
        (ClassificationImpl) classificationService.getClassification("T2100", "DOMAIN_B");
    Classification newClassification = oldClassification.copy("T9949");

    newClassification = classificationService.createClassification(newClassification);

    assertThat(newClassification.getId()).isNotNull();
    assertThat(newClassification.getId()).isNotEqualTo(oldClassification.getId());
  }

  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("newKey718", "", "TASK");

    ThrowingCallable createClassificationCall =
        () -> {
          classificationService.createClassification(classification);
        };

    assertThatThrownBy(createClassificationCall).isInstanceOf(NotAuthorizedException.class);
  }
}
