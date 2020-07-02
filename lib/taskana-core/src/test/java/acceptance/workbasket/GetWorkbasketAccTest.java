package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "get workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class GetWorkbasketAccTest extends AbstractAccTest {

  GetWorkbasketAccTest() {
    super();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketById() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(workbasket.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(workbasket.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getKey()).isEqualTo("USER-1-2");
    assertThat(workbasket.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasket.getOwner()).isEqualTo("Peter Maier");
    assertThat(workbasket.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasket.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasket.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasket.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasket.getCustom1()).isEqualTo("custom1");
    assertThat(workbasket.getCustom2()).isEqualTo("custom2");
    assertThat(workbasket.getCustom3()).isEqualTo("custom3");
    assertThat(workbasket.getCustom4()).isEqualTo("custom4");
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnWorkbasketByKeyAndDomain_When_NoExplicitPermissionButUserHasAdministrativeRole()
      throws Exception {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket retrievedWorkbasket =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(retrievedWorkbasket).isNotNull();
    assertThat(retrievedWorkbasket.getOwner()).isEqualTo("Peter Maier");
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnWorkbasketById_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket retrievedWorkbasket = workbasketService.getWorkbasket("USER-1-2", "DOMAIN_A");
    assertThat(retrievedWorkbasket.getOwner()).isEqualTo("Peter Maier");

    assertThat(retrievedWorkbasket).isNotNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketByKeyAndDomain() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.getWorkbasket("USER-1-2", "DOMAIN_A");

    assertThat(workbasket.getId()).isEqualTo("WBI:100000000000000000000000000000000007");
    assertThat(workbasket.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasket.getOwner()).isEqualTo("Peter Maier");
    assertThat(workbasket.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasket.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasket.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasket.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasket.getCustom1()).isEqualTo("custom1");
    assertThat(workbasket.getCustom2()).isEqualTo("custom2");
    assertThat(workbasket.getCustom3()).isEqualTo("custom3");
    assertThat(workbasket.getCustom4()).isEqualTo("custom4");
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void testGetWorkbasketPermissions() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketPermission> permissions =
        workbasketService.getPermissionsForWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(permissions).hasSize(4);
    assertThat(permissions.contains(WorkbasketPermission.READ)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.OPEN)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.TRANSFER)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.APPEND)).isTrue();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetWorkbasketPermissionsForInvalidWorkbasketId() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketPermission> permissions =
        workbasketService.getPermissionsForWorkbasket("WBI:invalid");

    assertThat(permissions).isEmpty();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketAsSummary() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    WorkbasketSummary workbasketSummary =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000007").asSummary();

    assertThat(workbasketSummary.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(workbasketSummary.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasketSummary.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasketSummary.getKey()).isEqualTo("USER-1-2");
    assertThat(workbasketSummary.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasketSummary.getOwner()).isEqualTo("Peter Maier");
    assertThat(workbasketSummary.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasketSummary.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasketSummary.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasketSummary.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasketSummary.getCustom1()).isEqualTo("custom1");
    assertThat(workbasketSummary.getCustom2()).isEqualTo("custom2");
    assertThat(workbasketSummary.getCustom3()).isEqualTo("custom3");
    assertThat(workbasketSummary.getCustom4()).isEqualTo("custom4");
    assertThat(workbasketSummary.isMarkedForDeletion()).isEqualTo(false);
  }

  @Test
  void testThrowsExceptionIfIdIsInvalid() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("INVALID_ID");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @Test
  void testThrowsExceptionIfKeyOrDomainIsInvalid() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("INVALID_KEY", "INVALID_DOMAIN");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @Test
  void testGetByIdNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @Test
  void testGetByKeyDomainNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetWorkbasketByIdNotExisting() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("NOT EXISTING ID");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }
}
