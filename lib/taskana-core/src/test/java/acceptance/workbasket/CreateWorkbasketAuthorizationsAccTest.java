package acceptance.workbasket;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/**
 * Acceptance test for all "set workbasket access item" scenarios.
 */
@ExtendWith(JaasExtension.class)
public class CreateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user_1_1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000001", "user1");
    accessItem.setPermAppend(true);
    accessItem.setPermCustom11(true);
    accessItem.setPermRead(true);
    ThrowingCallable call = () -> {
      workbasketService.createWorkbasketAccessItem(accessItem);
    };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }
}
