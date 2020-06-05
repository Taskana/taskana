package acceptance.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;

/** Acceptance test for all "delete classification" scenarios. */
@ExtendWith(JaasExtension.class)
class DeleteClassificationAccTest extends AbstractAccTest {

  private ClassificationService classificationService;

  DeleteClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testDeleteClassificationInDomain()
      throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
    classificationService.deleteClassification("L140101", "DOMAIN_A");

    Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
    assertThat(classification).isNotNull();
    assertThat(classification.getDomain()).isEqualTo("");
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "group_2"})
  @Test
  void testDeleteClassificationInDomainUserIsNotAuthorized() {
    ThrowingCallable call = () -> classificationService.deleteClassification("L140101", "DOMAIN_A");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    ThrowingCallable call = () -> classificationService.deleteClassification("L140101", "DOMAIN_A");

    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);

    call =
        () ->
            classificationService.deleteClassification("CLI:000000000000000000000000000000000009");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testThrowExeptionIfDeleteClassificationWithExistingTasks() {
    ThrowingCallable call = () -> classificationService.deleteClassification("L1050", "DOMAIN_A");
    assertThatThrownBy(call).isInstanceOf(ClassificationInUseException.class);
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testThrowExeptionIfDeleteMasterClassificationWithExistingTasks() {
    ThrowingCallable call = () -> classificationService.deleteClassification("L1050", "");
    assertThatThrownBy(call).isInstanceOf(ClassificationInUseException.class);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testDeleteMasterClassification()
      throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {

    classificationService.deleteClassification("L3060", "");
    ThrowingCallable call = () -> classificationService.getClassification("L3060", "DOMAIN_A");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testDeleteMasterClassificationWithExistingAttachment() {
    ThrowingCallable call = () -> classificationService.deleteClassification("L12010", "");
    assertThatThrownBy(call).isInstanceOf(ClassificationInUseException.class);
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testThrowExceptionWhenChildClassificationIsInUseAndRollback()
      throws ClassificationNotFoundException {

    ThrowingCallable call = () -> classificationService.deleteClassification("L11010", "DOMAIN_A");
    assertThatThrownBy(call).isInstanceOf(ClassificationInUseException.class);

    Classification rollback = classificationService.getClassification("L11010", "DOMAIN_A");
    assertThat(rollback.getDomain()).isEqualTo("DOMAIN_A");

    call = () -> classificationService.deleteClassification("L11010", "");
    assertThatThrownBy(call).isInstanceOf(ClassificationInUseException.class);

    Classification rollbackMaster = classificationService.getClassification("L11010", "");
    Classification rollbackA = classificationService.getClassification("L11010", "DOMAIN_A");
    assertThat(rollbackA.getKey()).isEqualTo(rollbackMaster.getKey());
    assertThat(rollbackA.getDomain()).isNotEqualTo(rollbackMaster.getDomain());
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testThrowClassificationNotFoundIfClassificationNotExists() {
    ThrowingCallable call =
        () -> classificationService.deleteClassification("not existing classification key", "");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testThrowClassificationNotFoundIfClassificationNotExistsInDomain() {
    ThrowingCallable call = () -> classificationService.deleteClassification("L10000", "DOMAIN_B");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }
}
