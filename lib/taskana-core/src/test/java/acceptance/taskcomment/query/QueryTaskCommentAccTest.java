package acceptance.taskcomment.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCommentQuery;
import pro.taskana.task.api.TaskCommentQueryColumnName;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;

/** Test for TaskComment queries. */
@ExtendWith(JaasExtension.class)
class QueryTaskCommentAccTest extends AbstractAccTest {

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_IdIn() {
    List<TaskComment> comments =
        taskService
            .createTaskCommentQuery()
            .idIn(
                "TCI:000000000000000000000000000000000000",
                "TCI:000000000000000000000000000000000002")
            .list();
    assertThat(comments).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_TaskIdIn() {
    List<TaskComment> comments =
        taskService
            .createTaskCommentQuery()
            .taskIdIn("TKI:000000000000000000000000000000000000")
            .list();
    assertThat(comments).hasSize(3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_NotAuthorizedToReadTaskComments() {

    ThrowingCallable call =
        () ->
            taskService
                .createTaskCommentQuery()
                .taskIdIn("TKI:000000000000000000000000000000000020")
                .list();

    assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatorIn() {
    List<TaskComment> comments = taskService.createTaskCommentQuery().creatorIn("user-1-2").list();
    assertThat(comments).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatedWithin() {
    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().createdWithin(timeInterval).list();
    assertThat(comments).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_ModifiedWithin() {
    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().modifiedWithin(timeInterval).list();
    assertThat(comments).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatorNotIn() {
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().creatorNotIn("user-1-2").list();
    assertThat(comments).hasSize(11);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatedNotWithin() {
    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().createdNotWithin(timeInterval).list();
    assertThat(comments).hasSize(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_ModiefiedNotWithin() {
    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().modifiedNotWithin(timeInterval).list();
    assertThat(comments).hasSize(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_IdLike() {
    List<TaskComment> comments = taskService.createTaskCommentQuery().idLike("%000001%").list();
    assertThat(comments).hasSize(4);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_TextFieldLike() {
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().textFieldLike("%other%").list();
    assertThat(comments).hasSize(6);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatorLike() {
    List<TaskComment> comments = taskService.createTaskCommentQuery().creatorLike("%1-1%").list();
    assertThat(comments).hasSize(10);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_IdNotLike() {
    List<TaskComment> comments = taskService.createTaskCommentQuery().idNotLike("%000001%").list();
    assertThat(comments).hasSize(9);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_TextFieldNotLike() {
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().textFieldNotLike("%other%").list();
    assertThat(comments).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_FilterTaskComments_For_CreatorNotLike() {
    List<TaskComment> comments =
        taskService.createTaskCommentQuery().creatorNotLike("%1-1%").list();
    assertThat(comments).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnCountOfEvents_When_UsingCountMethod() {
    long count = taskService.createTaskCommentQuery().creatorIn("user-1-1").count();
    assertThat(count).isEqualTo(10);

    count = taskService.createTaskCommentQuery().creatorNotIn("user-1-1").count();
    assertThat(count).isEqualTo(3);

    count = taskService.createTaskCommentQuery().count();
    assertThat(count).isEqualTo(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnId() {
    List<String> listedValues =
        taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.ID, null);
    assertThat(listedValues).hasSize(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnTaskId() {
    List<String> listedValues =
        taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.TASK_ID, null);
    assertThat(listedValues).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnTextField() {
    List<String> listedValues =
        taskService
            .createTaskCommentQuery()
            .listValues(TaskCommentQueryColumnName.TEXT_FIELD, null);
    assertThat(listedValues).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnCreator() {
    List<String> listedValues =
        taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.CREATOR, null);
    assertThat(listedValues).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnCreatorLongName() throws Exception {
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .addAdditionalUserInfo(false)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
    List<String> listedValues =
        taskanaEngine
            .getTaskService()
            .createTaskCommentQuery()
            .listValues(TaskCommentQueryColumnName.CREATOR_FULL_NAME, null);
    assertThat(listedValues).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnCreated() {
    List<String> listedValues =
        taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.CREATED, null);
    assertThat(listedValues).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnListedValues_For_QueryColumnModified() {
    List<String> listedValues =
        taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.MODIFIED, null);
    assertThat(listedValues).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ConfirmQueryListOffset_When_ProvidingOffsetAndLimit() {
    List<TaskComment> offsetAndLimitResult = taskService.createTaskCommentQuery().list(1, 2);
    List<TaskComment> regularResult = taskService.createTaskCommentQuery().list();

    assertThat(offsetAndLimitResult).hasSize(2);
    assertThat(offsetAndLimitResult.get(0))
        .isNotEqualTo(regularResult.get(0))
        .isEqualTo(regularResult.get(1));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnEmptyList_When_ProvidingWrongConstraints() {
    List<TaskComment> result = taskService.createTaskCommentQuery().list(1, 1000);
    assertThat(result).hasSize(12);

    result = taskService.createTaskCommentQuery().list(100, 1000);
    assertThat(result).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnSingleTaskComment_When_UsingSingleMethod() {
    TaskComment single =
        taskService
            .createTaskCommentQuery()
            .taskIdIn("TKI:000000000000000000000000000000000000")
            .creatorIn("user-1-2")
            .single();

    assertThat(single.getId()).isEqualTo("TCI:000000000000000000000000000000000001");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowException_When_SingleMethodRetrievesMoreThanOneEventFromDatabase() {
    TaskCommentQuery query = taskService.createTaskCommentQuery().creatorIn("user-1-1");
    assertThatThrownBy(query::single).isInstanceOf(TooManyResultsException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetTaskCreatorFullNameOfTaskComment_When_PropertyEnabled() throws Exception {
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .addAdditionalUserInfo(true)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
    List<TaskComment> taskComments =
        taskanaEngine
            .getTaskService()
            .createTaskCommentQuery()
            .idIn("TCI:000000000000000000000000000000000000")
            .list();

    assertThat(taskComments).hasSize(1);
    String creatorFullName =
        taskanaEngine.getUserService().getUser(taskComments.get(0).getCreator()).getFullName();
    assertThat(taskComments.get(0))
        .extracting(TaskComment::getCreatorFullName)
        .isEqualTo(creatorFullName);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotSetTaskCreatorFullNameOfTaskComment_When_PropertyDisabled() throws Exception {
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .addAdditionalUserInfo(false)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
    List<TaskComment> taskComments =
        taskanaEngine
            .getTaskService()
            .createTaskCommentQuery()
            .idIn("TCI:000000000000000000000000000000000000")
            .list();

    assertThat(taskComments).hasSize(1);
    assertThat(taskComments.get(0)).extracting(TaskComment::getCreatorFullName).isNull();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetTaskCreatorFullNameOfTaskCommentToNull_When_NotExistingAsUserInDatabase()
      throws Exception {
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .addAdditionalUserInfo(true)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
    List<TaskComment> taskComments =
        taskanaEngine
            .getTaskService()
            .createTaskCommentQuery()
            .idIn("TCI:000000000000000000000000000000000008")
            .list();

    assertThat(taskComments).hasSize(1);
    assertThat(taskComments.get(0)).extracting(TaskComment::getCreatorFullName).isNull();
  }
}
