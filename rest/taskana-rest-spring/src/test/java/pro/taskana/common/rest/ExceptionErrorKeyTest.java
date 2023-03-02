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
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.UnsupportedDatabaseException;
import pro.taskana.common.api.exceptions.WrongCustomHolidayFormatException;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidCallbackStateException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketMarkedForDeletionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/**
 * IMPORTANT NOTICE: Whenever a test from this class has to be modified in the Frontend and our rest
 * documentation.
 */
@SuppressWarnings("TwistedAssertion")
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
    assertThat(NotAuthorizedException.ERROR_KEY).isEqualTo("NOT_AUTHORIZED");
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
    assertThat(NotAuthorizedOnTaskCommentException.ERROR_KEY)
        .isEqualTo("NOT_AUTHORIZED_ON_TASK_COMMENT");
    assertThat(TaskAlreadyExistException.ERROR_KEY).isEqualTo("TASK_ALREADY_EXISTS");
    assertThat(TaskCommentNotFoundException.ERROR_KEY).isEqualTo("TASK_COMMENT_NOT_FOUND");
    assertThat(TaskNotFoundException.ERROR_KEY).isEqualTo("TASK_NOT_FOUND");
  }

  @Test
  void should_ProvideConsistentErrorKey_For_WorkbasketExceptions() {
    assertThat(NotAuthorizedOnWorkbasketException.ERROR_KEY_ID)
        .isEqualTo("NOT_AUTHORIZED_ON_WORKBASKET_WITH_ID");
    assertThat(NotAuthorizedOnWorkbasketException.ERROR_KEY_KEY_DOMAIN)
        .isEqualTo("NOT_AUTHORIZED_ON_WORKBASKET_WITH_KEY_AND_DOMAIN");
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
