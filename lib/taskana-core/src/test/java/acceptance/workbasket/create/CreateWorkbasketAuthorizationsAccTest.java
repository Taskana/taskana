package acceptance.workbasket.create;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** Acceptance test for all "set workbasket access item" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
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
