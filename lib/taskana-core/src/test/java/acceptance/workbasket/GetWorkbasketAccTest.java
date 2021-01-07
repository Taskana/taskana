package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_4;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "get workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class GetWorkbasketAccTest extends AbstractAccTest {

  private static final WorkbasketService WORKBASKET_SERVICE = taskanaEngine.getWorkbasketService();

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketById() throws Exception {

    Workbasket workbasket =
        WORKBASKET_SERVICE.getWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(workbasket.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(workbasket.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getKey()).isEqualTo("USER-1-2");
    assertThat(workbasket.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasket.getOwner()).isEqualTo("user-1-2");
    assertThat(workbasket.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasket.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasket.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasket.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasket.getCustomAttribute(CUSTOM_1)).isEqualTo("custom1");
    assertThat(workbasket.getCustomAttribute(CUSTOM_2)).isEqualTo("custom2");
    assertThat(workbasket.getCustomAttribute(CUSTOM_3)).isEqualTo("custom3");
    assertThat(workbasket.getCustomAttribute(CUSTOM_4)).isEqualTo("custom4");
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnWorkbasketByKeyAndDomain_When_NoExplicitPermissionButUserHasAdministrativeRole()
      throws Exception {

    Workbasket retrievedWorkbasket =
        WORKBASKET_SERVICE.getWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(retrievedWorkbasket).isNotNull();
    assertThat(retrievedWorkbasket.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnWorkbasketById_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    Workbasket retrievedWorkbasket = WORKBASKET_SERVICE.getWorkbasket("USER-1-2", "DOMAIN_A");
    assertThat(retrievedWorkbasket.getOwner()).isEqualTo("user-1-2");

    assertThat(retrievedWorkbasket).isNotNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketByKeyAndDomain() throws Exception {

    Workbasket workbasket = WORKBASKET_SERVICE.getWorkbasket("USER-1-2", "DOMAIN_A");

    assertThat(workbasket.getId()).isEqualTo("WBI:100000000000000000000000000000000007");
    assertThat(workbasket.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasket.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasket.getOwner()).isEqualTo("user-1-2");
    assertThat(workbasket.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasket.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasket.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasket.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasket.getCustomAttribute(CUSTOM_1)).isEqualTo("custom1");
    assertThat(workbasket.getCustomAttribute(CUSTOM_2)).isEqualTo("custom2");
    assertThat(workbasket.getCustomAttribute(CUSTOM_3)).isEqualTo("custom3");
    assertThat(workbasket.getCustomAttribute(CUSTOM_4)).isEqualTo("custom4");
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void testGetWorkbasketPermissions() {
    List<WorkbasketPermission> permissions =
        WORKBASKET_SERVICE.getPermissionsForWorkbasket("WBI:100000000000000000000000000000000007");

    assertThat(permissions).hasSize(4);
    assertThat(permissions.contains(WorkbasketPermission.READ)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.OPEN)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.TRANSFER)).isTrue();
    assertThat(permissions.contains(WorkbasketPermission.APPEND)).isTrue();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetWorkbasketPermissionsForInvalidWorkbasketId() {
    List<WorkbasketPermission> permissions =
        WORKBASKET_SERVICE.getPermissionsForWorkbasket("WBI:invalid");

    assertThat(permissions).isEmpty();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testGetWorkbasketAsSummary() throws Exception {

    WorkbasketSummary workbasketSummary =
        WORKBASKET_SERVICE.getWorkbasket("WBI:100000000000000000000000000000000007").asSummary();

    assertThat(workbasketSummary.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(workbasketSummary.getDescription()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasketSummary.getName()).isEqualTo("PPK User 2 KSC 1");
    assertThat(workbasketSummary.getKey()).isEqualTo("USER-1-2");
    assertThat(workbasketSummary.getType()).isEqualTo(WorkbasketType.PERSONAL);
    assertThat(workbasketSummary.getOwner()).isEqualTo("user-1-2");
    assertThat(workbasketSummary.getOrgLevel1()).isEqualTo("versicherung");
    assertThat(workbasketSummary.getOrgLevel2()).isEqualTo("abteilung");
    assertThat(workbasketSummary.getOrgLevel3()).isEqualTo("projekt");
    assertThat(workbasketSummary.getOrgLevel4()).isEqualTo("team");
    assertThat(workbasketSummary.getCustomAttribute(CUSTOM_1)).isEqualTo("custom1");
    assertThat(workbasketSummary.getCustomAttribute(CUSTOM_2)).isEqualTo("custom2");
    assertThat(workbasketSummary.getCustomAttribute(CUSTOM_3)).isEqualTo("custom3");
    assertThat(workbasketSummary.getCustomAttribute(CUSTOM_4)).isEqualTo("custom4");
    assertThat(workbasketSummary.isMarkedForDeletion()).isFalse();
  }

  @Test
  void testThrowsExceptionIfIdIsInvalid() {
    assertThatThrownBy(() -> WORKBASKET_SERVICE.getWorkbasket("INVALID_ID"))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @Test
  void testThrowsExceptionIfKeyOrDomainIsInvalid() {

    assertThatThrownBy(() -> WORKBASKET_SERVICE.getWorkbasket("INVALID_KEY", "INVALID_DOMAIN"))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @Test
  void testGetByIdNotAuthorized() {
    ThrowingCallable call =
        () -> WORKBASKET_SERVICE.getWorkbasket("WBI:100000000000000000000000000000000001");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @Test
  void testGetByKeyDomainNotAuthorized() {
    assertThatThrownBy(() -> WORKBASKET_SERVICE.getWorkbasket("GPK_KSC", "DOMAIN_A"))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetWorkbasketByIdNotExisting() {
    assertThatThrownBy(() -> WORKBASKET_SERVICE.getWorkbasket("NOT EXISTING ID"))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }
}
