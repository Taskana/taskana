package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "create workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateWorkbasketAccTest extends AbstractAccTest {

  CreateWorkbasketAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testCreateWorkbasket()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
    wbai.setPermRead(true);
    workbasketService.createWorkbasketAccessItem(wbai);

    int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
    assertEquals(before + 1, after);
    Workbasket createdWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
    assertNotNull(createdWorkbasket);
    assertNotNull(createdWorkbasket.getId());
    assertTrue(createdWorkbasket.getId().startsWith("WBI"));
    assertEquals(workbasket, createdWorkbasket);
    Workbasket createdWorkbasket2 = workbasketService.getWorkbasket(createdWorkbasket.getId());
    assertNotNull(createdWorkbasket);
    assertEquals(createdWorkbasket, createdWorkbasket2);
  }

  @WithAccessId(userName = "dummy")
  @Test
  void testCreateWorkbasketNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");

    Assertions.assertThrows(
        NotAuthorizedException.class, () -> workbasketService.createWorkbasket(workbasket));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testCreateWorkbasketWithInvalidDomain() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "UNKNOWN_DOMAIN");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    Assertions.assertThrows(
        DomainNotFoundException.class, () -> workbasketService.createWorkbasket(workbasket));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testCreateWorkbasketWithMissingRequiredField()
      throws NotAuthorizedException, WorkbasketAlreadyExistException, DomainNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket(null, "novatec");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    // missing key
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket));

    Workbasket workbasket2 = workbasketService.newWorkbasket("key", "novatec");
    workbasket2.setType(WorkbasketType.GROUP);
    workbasket2.setOrgLevel1("company");
    // missing name
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket2));

    Workbasket workbasket3 = workbasketService.newWorkbasket("key", "novatec");
    workbasket3.setName("Megabasket");
    workbasket3.setOrgLevel1("company");
    // missing type
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket3));

    Workbasket workbasket4 = workbasketService.newWorkbasket("key", null);
    workbasket4.setName("Megabasket");
    workbasket4.setType(WorkbasketType.GROUP);
    workbasket4.setOrgLevel1("company");
    // missing domain
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket4));

    Workbasket workbasket5 = workbasketService.newWorkbasket("", "novatec");
    workbasket5.setName("Megabasket");
    workbasket5.setType(WorkbasketType.GROUP);
    workbasket5.setOrgLevel1("company");
    // empty key
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket5));

    Workbasket workbasket6 = workbasketService.newWorkbasket("key", "novatec");
    workbasket6.setName("");
    workbasket6.setType(WorkbasketType.GROUP);
    workbasket6.setOrgLevel1("company");
    // empty name
    Assertions.assertThrows(
        InvalidWorkbasketException.class, () -> workbasketService.createWorkbasket(workbasket));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testThrowsExceptionIfWorkbasketWithCaseInsensitiveSameKeyDomainIsCreated()
      throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("X123456", "DOMAIN_A");
    workbasket.setName("Personal Workbasket for UID X123456");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket = workbasketService.createWorkbasket(workbasket);

    Workbasket duplicateWorkbasketWithSmallX =
        workbasketService.newWorkbasket("x123456", "DOMAIN_A");
    duplicateWorkbasketWithSmallX.setName("Personal Workbasket for UID X123456");
    duplicateWorkbasketWithSmallX.setType(WorkbasketType.PERSONAL);

    Assertions.assertThrows(
        WorkbasketAlreadyExistException.class,
        () -> workbasketService.createWorkbasket(duplicateWorkbasketWithSmallX));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testCreateWorkbasketWithAlreadyExistingKeyAndDomainAndEmptyIdUpdatesOlderWorkbasket()
      throws DomainNotFoundException, InvalidWorkbasketException, NotAuthorizedException,
          WorkbasketAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    // First create a new Workbasket.
    Workbasket wb = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    wb.setType(WorkbasketType.GROUP);
    wb.setName("this name");
    wb = workbasketService.createWorkbasket(wb);

    // Second create a new Workbasket with same Key and Domain.
    Workbasket sameKeyAndDomain = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    sameKeyAndDomain.setType(WorkbasketType.TOPIC);
    sameKeyAndDomain.setName("new name");

    Assertions.assertThrows(
        WorkbasketAlreadyExistException.class,
        () -> workbasketService.createWorkbasket(sameKeyAndDomain));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testWorkbasketAccessItemSetName()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("WBAIT1234", "DOMAIN_A");
    workbasket.setName("MyNewBasket");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
    wbai.setPermRead(true);
    wbai.setAccessName("Karl Napf");
    workbasketService.createWorkbasketAccessItem(wbai);

    Workbasket createdWorkbasket = workbasketService.getWorkbasket("WBAIT1234", "DOMAIN_A");
    assertNotNull(createdWorkbasket);
    assertNotNull(createdWorkbasket.getId());

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(createdWorkbasket.getId());
    WorkbasketAccessItem item =
        accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(null);
    assertEquals("Karl Napf", item.getAccessName());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testCreateDuplicateWorkbasketAccessListFails()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT4321", "DOMAIN_A");
    workbasket.setName("Terabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_3_2");
    wbai.setPermRead(true);
    workbasketService.createWorkbasketAccessItem(wbai);

    Assertions.assertThrows(
        WorkbasketAccessItemAlreadyExistException.class,
        () -> workbasketService.createWorkbasketAccessItem(wbai));
  }
}
