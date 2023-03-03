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
package acceptance.workbasket.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for workbasket queries and authorization. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryAccTest extends AbstractAccTest {

  @Test
  void testQueryWorkbasketByUnauthenticated() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermissions(
                  List.of(WorkbasketPermission.TRANSFER), "teamlead-1", GROUP_1_DN, GROUP_2_DN)
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void testQueryWorkbasketByUnknownUser() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermissions(
                  List.of(WorkbasketPermission.TRANSFER), "teamlead-1", GROUP_1_DN, GROUP_2_DN)
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryWorkbasketByBusinessAdmin() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(26);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.TRANSFER), "teamlead-1", GROUP_1_DN, GROUP_2_DN)
            .list();

    assertThat(results).hasSize(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryWorkbasketByAdmin() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(26);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.TRANSFER), "teamlead-1", GROUP_1_DN, GROUP_2_DN)
            .list();

    assertThat(results).hasSize(13);
  }
}
