/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.workbasket.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
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

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(MismatchedRoleException.class);

    deleteWorkbasketAccessItemCall =
        () -> {
          WORKBASKET_SERVICE.deleteWorkbasketAccessItem("WAI:100000000000000000000000000000000001");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(MismatchedRoleException.class);
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
