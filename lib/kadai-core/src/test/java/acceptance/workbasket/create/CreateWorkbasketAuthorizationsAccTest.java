package acceptance.workbasket.create;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "set workbasket access item" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000001", "user1");
    accessItem.setPermission(WorkbasketPermission.APPEND, true);
    accessItem.setPermission(WorkbasketPermission.CUSTOM_11, true);
    accessItem.setPermission(WorkbasketPermission.READ, true);
    assertThatThrownBy(() -> workbasketService.createWorkbasketAccessItem(accessItem))
        .isInstanceOf(NotAuthorizedException.class);
  }
}
