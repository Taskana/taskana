package acceptance.workbasket.get;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "get workbasket authorizations" scenarios. */
@ExtendWith(JaasExtension.class)
class GetWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowNotAuthorizedException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    final WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    ThrowingCallable retrieveWorkbasketAccessItemCall =
        () -> {
          workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000008");
        };

    assertThatThrownBy(retrieveWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }
}
