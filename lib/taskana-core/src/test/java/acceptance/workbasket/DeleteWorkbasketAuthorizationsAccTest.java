package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;

/** Acceptance test for all "delete workbasket authorizations" scenarios. */
@ExtendWith(JaasExtension.class)
class DeleteWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  private static final WorkbasketService WORKBASKET_SERVICE = taskanaEngine.getWorkbasketService();

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    ThrowingCallable deleteWorkbasketAccessItemCall =
        () -> {
          WORKBASKET_SERVICE.deleteWorkbasketAccessItemsForAccessId("group-1");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);

    deleteWorkbasketAccessItemCall =
        () -> {
          WORKBASKET_SERVICE.deleteWorkbasketAccessItem("WAI:100000000000000000000000000000000001");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteAccessItem_When_AccessIdIsNotLowercase() throws Exception {
    String workbasketId = "WBI:100000000000000000000000000000000004";

    int beforeDeletingAccessId = WORKBASKET_SERVICE.getWorkbasketAccessItems(workbasketId).size();
    WORKBASKET_SERVICE.deleteWorkbasketAccessItemsForAccessId("TEAMLEAD-2");
    int afterDeletingAccessId = WORKBASKET_SERVICE.getWorkbasketAccessItems(workbasketId).size();

    assertThat(beforeDeletingAccessId).isGreaterThan(afterDeletingAccessId);
  }
}
