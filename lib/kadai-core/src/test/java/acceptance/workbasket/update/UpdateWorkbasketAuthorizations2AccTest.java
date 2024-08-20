package acceptance.workbasket.update;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "update workbasket" scenarios that need a fresh database. */
@ExtendWith(JaasExtension.class)
class UpdateWorkbasketAuthorizations2AccTest extends AbstractAccTest {

  private static final WorkbasketService WORKBASKET_SERVICE = kadaiEngine.getWorkbasketService();

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdatedAccessItemListToEmptyList() throws Exception {
    final String wbId = "WBI:100000000000000000000000000000000004";
    List<WorkbasketAccessItem> accessItems = WORKBASKET_SERVICE.getWorkbasketAccessItems(wbId);
    assertThat(accessItems).hasSize(3);

    WORKBASKET_SERVICE.setWorkbasketAccessItems(wbId, List.of());

    List<WorkbasketAccessItem> updatedAccessItems =
        WORKBASKET_SERVICE.getWorkbasketAccessItems(wbId);
    assertThat(updatedAccessItems).isEmpty();
  }
}
