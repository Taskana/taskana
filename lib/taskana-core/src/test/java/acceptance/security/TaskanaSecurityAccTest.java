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
package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import acceptance.TaskanaEngineProxy;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class TaskanaSecurityAccTest extends AbstractAccTest {

  TaskanaSecurityAccTest() {
    super();
  }

  @Test
  void should_ThrowException_When_AccessIdIsUnauthenticated() {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_RunAsAdminOnlyTemorarily_When_RunAsAdminMethodIsCalled() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();

    new TaskanaEngineProxy(taskanaEngine)
        .getEngine()
        .getEngine()
        .runAsAdmin(() -> assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue());

    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CheckingNormalUserForAdminRoles() {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void should_ConfirmBusinessAdminRole_When_AccessIdIsBusinessAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
  }

  @WithAccessId(user = "user-1-1", groups = "taskadmin")
  @Test
  void should_ConfirmTaskAdminRole_When_AccessIdIsTaskAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.TASK_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    taskanaEngine.checkRoleMembership(TaskanaRole.TASK_ADMIN);
  }

  @WithAccessId(user = "user-1-1", groups = "admin")
  @Test
  void should_ConfirmAdminRole_When_AccessIdIsAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue();
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
  }
}
