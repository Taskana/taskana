package acceptance.workbasket.get;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;

/** Acceptance test for all "get workbasket authorizations" scenarios. */
@ExtendWith(JaasExtension.class)
class GetWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowNotAuthorizedException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable retrieveWorkbasketAccessItemCall =
        () -> {
          workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000008");
        };

    assertThatThrownBy(retrieveWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }
}
