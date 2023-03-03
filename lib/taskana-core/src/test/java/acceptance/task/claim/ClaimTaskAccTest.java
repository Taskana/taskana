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
package acceptance.task.claim;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;

@ExtendWith(JaasExtension.class)
class ClaimTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_TaskIsReady() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000025");

    taskService.claim(task.getId());

    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000025");
    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_TaskIsReadyForReview() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:100000000000000000000000000000000025");

    taskService.claim(task.getId());

    Task claimedTask = taskService.getTask("TKI:100000000000000000000000000000000025");
    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.IN_REVIEW);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsAlreadyClaimed() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000026");

    ThrowingCallable call = () -> taskService.claim(task.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsAlreadyInReviewByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:600000000000000000000000000000000028");

    ThrowingCallable call = () -> taskService.claim(task.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_AlreadyClaimedByCaller() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

    assertThat(task.getState()).isSameAs(TaskState.CLAIMED);
    assertThatCode(() -> taskService.claim(task.getId())).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_AlreadyInReviewByCaller() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:200000000000000000000000000000000025");

    assertThat(task.getState()).isSameAs(TaskState.IN_REVIEW);
    assertThatCode(() -> taskService.claim(task.getId())).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceClaimTask_When_TaskIsAlreadyClaimedByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000028");

    ThrowingCallable call = () -> taskService.claim(task.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceClaimTask_When_InReviewByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:200000000000000000000000000000000028");

    Task claimedTask = taskService.forceClaim(task.getId());
    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.IN_REVIEW);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowNotAuthorizedException_When_UserHasNoReadPermissionAndTaskIsReady() {
    assertThatThrownBy(() -> taskService.claim("TKI:000000000000000000000000000000000000"))
        .isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowNotAuthorizedException_When_UserHasNoReadPermissionAndTaskIsReadyForReview() {
    assertThatThrownBy(() -> taskService.claim("TKI:500000000000000000000000000000000028"))
        .isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimTask_When_TaskIsClaimed() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");

    taskService.cancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimTask_When_TaskIsInReview() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:200000000000000000000000000000000025");

    taskService.cancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:200000000000000000000000000000000025");
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();

    // Claim Task, so it can be used in a different test
    taskService.claim(unclaimedTask.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CancelClaimingATaskClaimedByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000030");

    ThrowingCallable call = () -> taskService.cancelClaim(claimedTask.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_CancelClaimingATaskInReviewByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:200000000000000000000000000000000025");

    ThrowingCallable call = () -> taskService.cancelClaim(claimedTask.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceCancelClaim_When_TaskClaimedByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");

    taskService.forceCancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceCancelClaimTask_When_InReviewByAnotherUser() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:100000000000000000000000000000000028");

    Task unclaimedTask = taskService.forceCancelClaim(task.getId());
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }
}
