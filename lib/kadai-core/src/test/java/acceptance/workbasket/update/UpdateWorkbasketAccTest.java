package acceptance.workbasket.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "update workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class UpdateWorkbasketAccTest extends AbstractAccTest {

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateWorkbasket() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    final Instant modified = workbasket.getModified();

    workbasket.setName("new name");
    workbasket.setDescription("new description");
    workbasket.setType(WorkbasketType.TOPIC);
    workbasket.setOrgLevel1("new level 1");
    workbasket.setOrgLevel2("new level 2");
    workbasket.setOrgLevel3("new level 3");
    workbasket.setOrgLevel4("new level 4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "new custom 1");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "new custom 2");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "new custom 3");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "new custom 4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_5, "new custom 5");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_6, "new custom 6");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_7, "new custom 7");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_8, "new custom 8");
    workbasket.setDescription("new description");
    workbasketService.updateWorkbasket(workbasket);

    Workbasket updatedWorkbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    assertThat(updatedWorkbasket.getId()).isEqualTo(workbasket.getId());
    assertThat(updatedWorkbasket.getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(updatedWorkbasket.getName()).isEqualTo("new name");
    assertThat(updatedWorkbasket.getType()).isEqualTo(WorkbasketType.TOPIC);
    assertThat(updatedWorkbasket.getModified()).isNotEqualTo(modified);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_UpdatingWorkbasketWithInvalidName() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");
    workbasket.setName(null);
    assertThatThrownBy(() -> workbasketService.updateWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);

    workbasket.setName("");
    assertThatThrownBy(() -> workbasketService.updateWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_UpdatingWorkbasketWithTypeNull() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");
    workbasket.setType(null);

    assertThatThrownBy(() -> workbasketService.updateWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateWorkbasketWithConcurrentModificationShouldThrowException() throws Exception {

    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    workbasket.setModified(workbasket.getModified().minus(1, ChronoUnit.SECONDS));

    assertThatExceptionOfType(ConcurrencyException.class)
        .isThrownBy(() -> workbasketService.updateWorkbasket(workbasket));
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateWorkbasketOfNonExistingWorkbasketShouldThrowException() throws Exception {

    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    workbasket.setDomain("InvalidDomain");
    workbasket.setKey("InvalidKey");

    assertThatExceptionOfType(WorkbasketNotFoundException.class)
        .isThrownBy(() -> workbasketService.updateWorkbasket(workbasket));
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToUpdateUnknownWorkbasket() {

    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("InvalidKey", "InvalidDomain");
    workbasket.setName("bla bla");
    workbasket.setType(WorkbasketType.PERSONAL);

    assertThatThrownBy(() -> workbasketService.updateWorkbasket(workbasket))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("USER-1-1", "DOMAIN_A");

    workbasket.setName("new name");

    assertThatThrownBy(() -> workbasketService.updateWorkbasket(workbasket))
        .isInstanceOf(NotAuthorizedException.class);
  }
}
