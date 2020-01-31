package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import acceptance.AbstractAccTest;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "update workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
public class UpdateWorkbasketAccTest extends AbstractAccTest {

  public UpdateWorkbasketAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  public void testUpdateWorkbasket() throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");
    final Instant modified = workbasket.getModified();

    workbasket.setName("new name");
    workbasket.setDescription("new description");
    workbasket.setType(WorkbasketType.TOPIC);
    workbasket.setOrgLevel1("new level 1");
    workbasket.setOrgLevel2("new level 2");
    workbasket.setOrgLevel3("new level 3");
    workbasket.setOrgLevel4("new level 4");
    workbasket.setCustom1("new custom 1");
    workbasket.setCustom2("new custom 2");
    workbasket.setCustom3("new custom 3");
    workbasket.setCustom4("new custom 4");
    workbasket.setDescription("new description");
    workbasketService.updateWorkbasket(workbasket);

    Workbasket updatedWorkbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");
    assertEquals(workbasket.getId(), updatedWorkbasket.getId());
    assertEquals(workbasket.getCreated(), updatedWorkbasket.getCreated());
    assertNotEquals(modified, updatedWorkbasket.getModified());
    assertEquals("new name", updatedWorkbasket.getName());
    assertEquals(WorkbasketType.TOPIC, updatedWorkbasket.getType());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  public void testCheckAuthorizationToUpdateWorkbasket()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("USER_1_1", "DOMAIN_A");

    workbasket.setName("new name");

    Assertions.assertThrows(
        NotAuthorizedException.class, () -> workbasketService.updateWorkbasket(workbasket));
  }
}
