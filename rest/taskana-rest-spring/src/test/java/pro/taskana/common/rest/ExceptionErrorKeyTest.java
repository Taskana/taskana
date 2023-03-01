/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.common.api.exceptions.AutocommitFailedException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.ConnectionNotSetException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.UnsupportedDatabaseException;
import pro.taskana.common.api.exceptions.WrongCustomHolidayFormatException;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidCallbackStateException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.MismatchedTaskCommentCreatorException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketMarkedForDeletionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

class ExceptionErrorKeyTest {

  @Test
  void should_ProvideConsistentErrorKey_For_ClassificationExceptions() {
    assertThat(ClassificationAlreadyExistException.ERROR_KEY)
        .isEqualTo("CLASSIFICATION_ALREADY_EXISTS");
    assertThat(ClassificationInUseException.ERROR_KEY).isEqualTo("CLASSIFICATION_IN_USE");
    assertThat(ClassificationNotFoundException.ERROR_KEY_ID)
        .isEqualTo("CLASSIFICATION_WITH_ID_NOT_FOUND");
    assertThat(ClassificationNotFoundException.ERROR_KEY_KEY_DOMAIN)
        .isEqualTo("CLASSIFICATION_WITH_KEY_NOT_FOUND");
    assertThat(MalformedServiceLevelException.ERROR_KEY)
        .isEqualTo("CLASSIFICATION_SERVICE_LEVEL_MALFORMED");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_CommonExceptions() {
    assertThat(AutocommitFailedException.ERROR_KEY).isEqualTo("CONNECTION_AUTOCOMMIT_FAILED");
    assertThat(ConcurrencyException.ERROR_KEY).isEqualTo("ENTITY_NOT_UP_TO_DATE");
    assertThat(ConnectionNotSetException.ERROR_KEY).isEqualTo("CONNECTION_NOT_SET");
    assertThat(DomainNotFoundException.ERROR_KEY).isEqualTo("DOMAIN_NOT_FOUND");
    assertThat(MismatchedRoleException.ERROR_KEY).isEqualTo("ROLE_MISMATCHED");
    assertThat(SystemException.ERROR_KEY).isEqualTo("CRITICAL_SYSTEM_ERROR");
    assertThat(UnsupportedDatabaseException.ERROR_KEY).isEqualTo("DATABASE_UNSUPPORTED");
    assertThat(WrongCustomHolidayFormatException.ERROR_KEY)
        .isEqualTo("CUSTOM_HOLIDAY_WRONG_FORMAT");
    assertThat(InvalidArgumentException.ERROR_KEY).isEqualTo("INVALID_ARGUMENT");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_SpiExceptions() {
    assertThat(TaskanaHistoryEventNotFoundException.ERROR_KEY).isEqualTo("HISTORY_EVENT_NOT_FOUND");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_TaskExceptions() {
    assertThat(AttachmentPersistenceException.ERROR_KEY).isEqualTo("ATTACHMENT_ALREADY_EXISTS");
    assertThat(InvalidCallbackStateException.ERROR_KEY).isEqualTo("TASK_INVALID_CALLBACK_STATE");
    assertThat(InvalidOwnerException.ERROR_KEY).isEqualTo("TASK_INVALID_OWNER");
    assertThat(InvalidTaskStateException.ERROR_KEY).isEqualTo("TASK_INVALID_STATE");
    assertThat(MismatchedTaskCommentCreatorException.ERROR_KEY)
        .isEqualTo("TASK_COMMENT_CREATOR_MISMATCHED");
    assertThat(TaskAlreadyExistException.ERROR_KEY).isEqualTo("TASK_ALREADY_EXISTS");
    assertThat(TaskCommentNotFoundException.ERROR_KEY).isEqualTo("TASK_COMMENT_NOT_FOUND");
    assertThat(TaskNotFoundException.ERROR_KEY).isEqualTo("TASK_NOT_FOUND");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_WorkbasketExceptions() {
    assertThat(MismatchedWorkbasketPermissionException.ERROR_KEY_ID)
        .isEqualTo("WORKBASKET_WITH_ID_MISMATCHED_PERMISSION");
    assertThat(MismatchedWorkbasketPermissionException.ERROR_KEY_KEY_DOMAIN)
        .isEqualTo("WORKBASKET_WITH_KEY_MISMATCHED_PERMISSION");
    assertThat(WorkbasketAccessItemAlreadyExistException.ERROR_KEY)
        .isEqualTo("WORKBASKET_ACCESS_ITEM_ALREADY_EXISTS");
    assertThat(WorkbasketAlreadyExistException.ERROR_KEY).isEqualTo("WORKBASKET_ALREADY_EXISTS");
    assertThat(WorkbasketInUseException.ERROR_KEY).isEqualTo("WORKBASKET_IN_USE");
    assertThat(WorkbasketMarkedForDeletionException.ERROR_KEY)
        .isEqualTo("WORKBASKET_MARKED_FOR_DELETION");
    assertThat(WorkbasketNotFoundException.ERROR_KEY_ID).isEqualTo("WORKBASKET_WITH_ID_NOT_FOUND");
    assertThat(WorkbasketNotFoundException.ERROR_KEY_KEY_DOMAIN)
        .isEqualTo("WORKBASKET_WITH_KEY_NOT_FOUND");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_RestExceptions() {
    assertThat(TaskanaRestExceptionHandler.ERROR_KEY_PAYLOAD).isEqualTo("PAYLOAD_TOO_LARGE");
    assertThat(TaskanaRestExceptionHandler.ERROR_KEY_QUERY_MALFORMED)
        .isEqualTo("QUERY_PARAMETER_MALFORMED");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_UserExceptions() {
    assertThat(UserNotFoundException.ERROR_KEY).isEqualTo("USER_NOT_FOUND");
    assertThat(UserAlreadyExistException.ERROR_KEY).isEqualTo("USER_ALREADY_EXISTS");
  }
}
