package acceptance.workbasket.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** Acceptance test for all "create workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateWorkbasketAccTest extends AbstractAccTest {

  CreateWorkbasketAccTest() {
    super();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasket() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
    assertThat(after).isEqualTo(before + 1);
    Workbasket createdWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket.getId()).startsWith("WBI");
    assertThat(createdWorkbasket).isEqualTo(workbasket);
    Workbasket createdWorkbasket2 = workbasketService.getWorkbasket(createdWorkbasket.getId());
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket2).isEqualTo(createdWorkbasket);
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");

    ThrowingCallable call = () -> workbasketService.createWorkbasket(workbasket);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_BeAbleToCreateNewWorkbasket_When_WorkbasketCopy() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket oldWorkbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    Workbasket newWorkbasket = oldWorkbasket.copy("keyname");
    newWorkbasket = workbasketService.createWorkbasket(newWorkbasket);

    assertThat(newWorkbasket.getId()).isNotNull();
    assertThat(newWorkbasket.getId()).isNotEqualTo(oldWorkbasket.getId());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithInvalidDomain() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "UNKNOWN_DOMAIN");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    ThrowingCallable call = () -> workbasketService.createWorkbasket(workbasket);
    assertThatThrownBy(call).isInstanceOf(DomainNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithMissingRequiredField() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket(null, "novatec");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    // missing key
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket2 = workbasketService.newWorkbasket("key", "novatec");
    workbasket2.setType(WorkbasketType.GROUP);
    workbasket2.setOrgLevel1("company");
    // missing name
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket2))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket3 = workbasketService.newWorkbasket("key", "novatec");
    workbasket3.setName("Megabasket");
    workbasket3.setOrgLevel1("company");
    // missing type
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket3))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket4 = workbasketService.newWorkbasket("key", null);
    workbasket4.setName("Megabasket");
    workbasket4.setType(WorkbasketType.GROUP);
    workbasket4.setOrgLevel1("company");
    // missing domain
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket4))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket5 = workbasketService.newWorkbasket("", "novatec");
    workbasket5.setName("Megabasket");
    workbasket5.setType(WorkbasketType.GROUP);
    workbasket5.setOrgLevel1("company");
    // empty key
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket5))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket6 = workbasketService.newWorkbasket("key", "novatec");
    workbasket6.setName("");
    workbasket6.setType(WorkbasketType.GROUP);
    workbasket6.setOrgLevel1("company");
    // empty name
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testThrowsExceptionIfWorkbasketWithCaseInsensitiveSameKeyDomainIsCreated() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("X123456", "DOMAIN_A");
    workbasket.setName("Personal Workbasket for UID X123456");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasketService.createWorkbasket(workbasket);

    Workbasket duplicateWorkbasketWithSmallX =
        workbasketService.newWorkbasket("x123456", "DOMAIN_A");
    duplicateWorkbasketWithSmallX.setName("Personal Workbasket for UID X123456");
    duplicateWorkbasketWithSmallX.setType(WorkbasketType.PERSONAL);

    assertThatThrownBy(() -> workbasketService.createWorkbasket(duplicateWorkbasketWithSmallX))
        .isInstanceOf(WorkbasketAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithAlreadyExistingKeyAndDomainAndEmptyIdUpdatesOlderWorkbasket()
      throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    // First create a new Workbasket.
    Workbasket wb = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    wb.setType(WorkbasketType.GROUP);
    wb.setName("this name");
    workbasketService.createWorkbasket(wb);

    // Second create a new Workbasket with same Key and Domain.
    Workbasket sameKeyAndDomain = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    sameKeyAndDomain.setType(WorkbasketType.TOPIC);
    sameKeyAndDomain.setName("new name");

    assertThatThrownBy(() -> workbasketService.createWorkbasket(sameKeyAndDomain))
        .isInstanceOf(WorkbasketAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testWorkbasketAccessItemSetName() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("WBAIT1234", "DOMAIN_A");
    workbasket.setName("MyNewBasket");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    wbai.setAccessName("Karl Napf");
    workbasketService.createWorkbasketAccessItem(wbai);

    Workbasket createdWorkbasket = workbasketService.getWorkbasket("WBAIT1234", "DOMAIN_A");
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket.getId()).isNotNull();

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(createdWorkbasket.getId());
    WorkbasketAccessItem item =
        accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(null);
    assertThat(item)
        .isNotNull()
        .extracting(WorkbasketAccessItem::getAccessName)
        .isEqualTo("Karl Napf");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateDuplicateWorkbasketAccessListFails() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("NT4321", "DOMAIN_A");
    workbasket.setName("Terabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-3-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    assertThatThrownBy(() -> workbasketService.createWorkbasketAccessItem(wbai))
        .isInstanceOf(WorkbasketAccessItemAlreadyExistException.class);
  }
}
