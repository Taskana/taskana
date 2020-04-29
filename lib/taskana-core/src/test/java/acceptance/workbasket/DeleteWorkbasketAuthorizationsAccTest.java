package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;

/**
 * Acceptance test for all "delete workbasket authorizations" scenarios.
 */
@ExtendWith(JaasExtension.class)
public class DeleteWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user_1_1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  public void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable deleteWorkbasketAccessItemCall =
        () -> {
          workbasketService.deleteWorkbasketAccessItemsForAccessId("group_1");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);

    deleteWorkbasketAccessItemCall =
        () -> {
          workbasketService.deleteWorkbasketAccessItem("WAI:100000000000000000000000000000000001");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

}

