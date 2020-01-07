package acceptance.classification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "create classification" scenarios. */
@ExtendWith(JAASExtension.class)
class CreateClassificationAccTest extends AbstractAccTest {

  private static final String ID_PREFIX_CLASSIFICATION = "CLI";

  private ClassificationService classificationService;

  CreateClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateMasterClassification()
      throws ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification classification = classificationService.newClassification("Key0", "", "TASK");
    classification.setIsValidInDomain(true);
    classification = classificationService.createClassification(classification);

    // check only 1 created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 1));

    classification = classificationService.getClassification(classification.getId());
    assertNotNull(classification);
    assertNotNull(classification.getCreated());
    assertNotNull(classification.getModified());
    assertNotNull(classification.getId());
    assertThat(classification.getIsValidInDomain(), equalTo(false));
    assertTrue(classification.getId().startsWith(ID_PREFIX_CLASSIFICATION));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithMasterCopy()
      throws ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification classification =
        classificationService.newClassification("Key1", "DOMAIN_A", "TASK");
    classification.setIsValidInDomain(true);
    classification = classificationService.createClassification(classification);

    // Check returning one is the "original"
    Classification createdClassification =
        classificationService.getClassification(classification.getId());
    assertNotNull(createdClassification.getId());
    assertNotNull(createdClassification.getCreated());
    assertNotNull(createdClassification.getModified());
    assertThat(createdClassification.getIsValidInDomain(), equalTo(true));
    assertThat(createdClassification.getDomain(), equalTo("DOMAIN_A"));
    assertEquals(createdClassification.getKey(), "Key1");

    // Check 2 new created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 2));

    // Check main
    classification = classificationService.getClassification(classification.getId());
    assertNotNull(classification);
    assertNotNull(classification.getCreated());
    assertNotNull(classification.getModified());
    assertNotNull(classification.getId());
    assertThat(classification.getIsValidInDomain(), equalTo(true));
    assertTrue(classification.getId().startsWith(ID_PREFIX_CLASSIFICATION));

    // Check master-copy
    classification = classificationService.getClassification(classification.getKey(), "");
    assertNotNull(classification);
    assertNotNull(classification.getCreated());
    assertNotNull(classification.getModified());
    assertNotNull(classification.getId());
    assertThat(classification.getIsValidInDomain(), equalTo(false));
    assertTrue(classification.getId().startsWith(ID_PREFIX_CLASSIFICATION));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithExistingMaster()
      throws DomainNotFoundException, ClassificationAlreadyExistException, NotAuthorizedException,
          InvalidArgumentException {

    classificationService.createClassification(
        classificationService.newClassification("Key0815", "", "TASK"));

    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification expected =
        classificationService.newClassification("Key0815", "DOMAIN_B", "TASK");
    Classification actual = classificationService.createClassification(expected);
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();

    assertEquals(amountOfClassificationsBefore + 1, amountOfClassificationsAfter);
    assertNotNull(actual);
    assertEquals(actual, expected);
    assertTrue(actual.getIsValidInDomain());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateChildInDomainAndCopyInMaster() throws TaskanaException {
    Classification parent = classificationService.newClassification("Key0816", "DOMAIN_A", "TASK");
    Classification actualParent = classificationService.createClassification(parent);
    assertNotNull(actualParent);

    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification child = classificationService.newClassification("Key0817", "DOMAIN_A", "TASK");
    child.setParentId(actualParent.getId());
    child.setParentKey(actualParent.getKey());
    Classification actualChild = classificationService.createClassification(child);
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();

    assertEquals(amountOfClassificationsBefore + 2, amountOfClassificationsAfter);
    assertNotNull(actualChild);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithInvalidValues() {
    classificationService.createClassificationQuery().count();

    // Check key NULL
    Classification classification =
        classificationService.newClassification(null, "DOMAIN_A", "TASK");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));

    // Check invalid ServiceLevel

    Classification classification2 =
        classificationService.newClassification("Key2", "DOMAIN_B", "TASK");
    classification2.setServiceLevel("abc");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationAlreadyExisting() throws TaskanaException {
    Classification classification = classificationService.newClassification("Key3", "", "TASK");
    Classification classificationCreated =
        classificationService.createClassification(classification);
    Assertions.assertThrows(
        ClassificationAlreadyExistException.class,
        () -> classificationService.createClassification(classificationCreated));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationInUnknownDomain() {
    Classification classification =
        classificationService.newClassification("Key3", "UNKNOWN_DOMAIN", "TASK");
    Assertions.assertThrows(
        DomainNotFoundException.class,
        () -> classificationService.createClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationOfUnknownType() {
    Classification classification =
        classificationService.newClassification("Key3", "DOMAIN_A", "UNKNOWN_TYPE");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationOfUnknownCategory() {
    Classification classification =
        classificationService.newClassification("Key4", "DOMAIN_A", "TASK");
    classification.setCategory("UNKNOWN_CATEGORY");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithInvalidParentId() {
    Classification classification = classificationService.newClassification("Key5", "", "TASK");
    classification.setParentId("ID WHICH CANT BE FOUND");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithInvalidParentKey() {
    Classification classification = classificationService.newClassification("Key5", "", "TASK");
    classification.setParentKey("KEY WHICH CANT BE FOUND");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testCreateClassificationWithExplicitId() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("Key0818", "", "TASK");
    classification.setId("EXPLICIT ID");
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.createClassification(classification));
  }
}
