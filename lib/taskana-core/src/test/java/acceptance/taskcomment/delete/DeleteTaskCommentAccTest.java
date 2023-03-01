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
package acceptance.taskcomment.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.MismatchedTaskCommentCreatorException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.models.TaskComment;

@ExtendWith(JaasExtension.class)
class DeleteTaskCommentAccTest extends AbstractAccTest {

  DeleteTaskCommentAccTest() {
    super();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteTaskComment_For_TaskCommentId() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskComments).hasSize(2);

    taskService.deleteTaskComment("TCI:000000000000000000000000000000000004");

    // make sure the task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(user = "user-1-2", groups = "user-1-1") // to read comments
  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);

    ThrowingCallable lambda =
        () -> taskService.deleteTaskComment("TCI:000000000000000000000000000000000000");

    assertThatThrownBy(lambda).isInstanceOf(MismatchedTaskCommentCreatorException.class);

    // make sure the task comment was not deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterDeletion).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_DeleteTaskComment_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    resetDb(false);

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    taskService.deleteTaskComment("TCI:000000000000000000000000000000000006");

    // make sure the task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIdIsInvalid() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);

    assertThatThrownBy(() -> taskService.deleteTaskComment(""))
        .isInstanceOf(InvalidArgumentException.class);

    assertThatThrownBy(() -> taskService.deleteTaskComment(null))
        .isInstanceOf(InvalidArgumentException.class);

    // make sure that no task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterDeletion).hasSize(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIsNotExisting() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);

    ThrowingCallable lambda = () -> taskService.deleteTaskComment("non existing task comment id");
    assertThatThrownBy(lambda).isInstanceOf(TaskCommentNotFoundException.class);

    // make sure the task comment was not deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterDeletion).hasSize(3);
  }
}
