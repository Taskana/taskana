package acceptance.workbasket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketAccessItem;
import pro.taskana.workbasket.api.WorkbasketService;

/** Acceptance test for all "update workbasket" scenarios that need a fresh database. */
@ExtendWith(JaasExtension.class)
public class UpdateWorkbasketAuthorizations2AccTest extends AbstractAccTest {

  UpdateWorkbasketAuthorizations2AccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testUpdatedAccessItemListToEmptyList() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final String wbId = "WBI:100000000000000000000000000000000004";
    List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAccessItems(wbId);
    int countBefore = accessItems.size();
    assertThat(3, equalTo(countBefore));

    workbasketService.setWorkbasketAccessItems(wbId, new ArrayList<>());

    List<WorkbasketAccessItem> updatedAccessItems =
        workbasketService.getWorkbasketAccessItems(wbId);
    int countAfter = updatedAccessItems.size();
    assertThat(0, equalTo(countAfter));
  }
}
