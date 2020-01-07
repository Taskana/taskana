package acceptance.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "delete classification" scenarios. */
@ExtendWith(JaasExtension.class)
class DeleteClassificationAccTest extends AbstractAccTest {

  private ClassificationService classificationService;

  DeleteClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteClassificationInDomain()
      throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
    classificationService.deleteClassification("L140101", "DOMAIN_A");

    Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
    assertNotNull(classification);
    assertEquals("", classification.getDomain());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testDeleteClassificationInDomainUserIsNotAuthorized() {
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> classificationService.deleteClassification("L140101", "DOMAIN_A"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testThrowExeptionIfDeleteClassificationWithExistingTasks() {
    Assertions.assertThrows(
        ClassificationInUseException.class,
        () -> classificationService.deleteClassification("L1050", "DOMAIN_A"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testThrowExeptionIfDeleteMasterClassificationWithExistingTasks() {
    Assertions.assertThrows(
        ClassificationInUseException.class,
        () -> classificationService.deleteClassification("L1050", ""));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteMasterClassification()
      throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {

    classificationService.deleteClassification("L3060", "");
    Assertions.assertThrows(
        ClassificationNotFoundException.class,
        () -> classificationService.getClassification("L3060", "DOMAIN_A"));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteMasterClassificationWithExistingAttachment() {
    Assertions.assertThrows(
        ClassificationInUseException.class,
        () -> classificationService.deleteClassification("L12010", ""));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testThrowExceptionWhenChildClassificationIsInUseAndRollback()
      throws ClassificationNotFoundException {

    Assertions.assertThrows(
        ClassificationInUseException.class,
        () -> classificationService.deleteClassification("L11010", "DOMAIN_A"));

    Classification rollback = classificationService.getClassification("L11010", "DOMAIN_A");
    assertEquals("DOMAIN_A", rollback.getDomain());

    Assertions.assertThrows(
        ClassificationInUseException.class,
        () -> classificationService.deleteClassification("L11010", ""));

    Classification rollbackMaster = classificationService.getClassification("L11010", "");
    Classification rollbackA = classificationService.getClassification("L11010", "DOMAIN_A");
    assertEquals(rollbackMaster.getKey(), rollbackA.getKey());
    assertNotEquals(rollbackMaster.getDomain(), rollbackA.getDomain());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testThrowClassificationNotFoundIfClassificationNotExists() {
    Assertions.assertThrows(
        ClassificationNotFoundException.class,
        () -> classificationService.deleteClassification("not existing classification key", ""));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testThrowClassificationNotFoundIfClassificationNotExistsInDomain() {
    Assertions.assertThrows(
        ClassificationNotFoundException.class,
        () -> classificationService.deleteClassification("L10000", "DOMAIN_B"));
  }
}
