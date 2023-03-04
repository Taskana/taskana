package acceptance.taskcomment.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.task.api.TaskCommentQuery;
import pro.taskana.task.api.TaskCommentQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.TaskCommentBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class QueryTaskCommentAccTest {

  private static final String ACCESS_USER_1_1_ID = "user-1-1";

  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;

  Classification defaultClassification;
  Workbasket defaultWorkbasket;
  Task task1;
  TaskComment comment1;
  TaskComment comment2;
  TaskComment comment3;

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @BeforeEach
  public void createTaskAndTaskComments() throws Exception {

    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    comment2 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text2")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    comment3 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text3")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService, "admin");
    List<TaskComment> taskComments = taskService.getTaskComments(task1.getId());
    assertThat(taskComments).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @AfterEach
  public void deleteTask() throws Exception {
    taskService.cancelTask(task1.getId());
    taskService.deleteTask(task1.getId());
  }

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {

    User userWithName = userService.newUser();
    userWithName.setId(ACCESS_USER_1_1_ID);
    userWithName.setFirstName("Max");
    userWithName.setLastName("Mustermann");
    userWithName.setFullName("Max Mustermann");
    userService.createUser(userWithName);

    defaultClassification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    defaultWorkbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasket.getId())
        .accessId(ACCESS_USER_1_1_ID)
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class FilterTaskComments {

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_IdIn() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().idIn(comment1.getId(), comment2.getId()).list();
      assertThat(comments).hasSize(2);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_TaskIdIn() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().taskIdIn(task1.getId()).list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_ThrowException_When_NotAuthorizedToReadTaskComments() {

      ThrowingCallable call =
          () -> taskService.createTaskCommentQuery().taskIdIn(task1.getId()).list();

      assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatorIn() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().creatorIn(ACCESS_USER_1_1_ID).list();
      assertThat(comments).hasSize(2);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatedWithin() {
      TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(5), Instant.now());
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().createdWithin(timeInterval).list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_ModifiedWithin() {
      TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().modifiedWithin(timeInterval).list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatorNotIn() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().creatorNotIn("user-1-2").list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatedNotWithin() {
      TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().createdNotWithin(timeInterval).list();
      assertThat(comments).isEmpty();
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_ModiefiedNotWithin() {
      TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().modifiedNotWithin(timeInterval).list();
      assertThat(comments).isEmpty();
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_IdLike() {
      String searchId = "%" + comment1.getId().substring(1, comment1.getId().length() - 2) + "%";

      List<TaskComment> comments = taskService.createTaskCommentQuery().idLike(searchId).list();
      assertThat(comments).hasSize(1);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_TextFieldLike() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().textFieldLike("%ext%").list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatorLike() {
      List<TaskComment> comments = taskService.createTaskCommentQuery().creatorLike("%-1-%").list();
      assertThat(comments).hasSize(2);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_IdNotLike() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().idNotLike("%ABC-123456%").list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_TextFieldNotLike() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().textFieldNotLike("%other%").list();
      assertThat(comments).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_FilterTaskComments_For_CreatorNotLike() {
      List<TaskComment> comments =
          taskService.createTaskCommentQuery().creatorNotLike("%dmi%").list();
      assertThat(comments).hasSize(2);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CountTaskComments {
    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnCountOfEvents_When_UsingCountMethod() {
      long count = taskService.createTaskCommentQuery().count();
      assertThat(count).isEqualTo(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnCountOfEvents_When_UsingCountMethodAndCreatorIn() {
      long count = taskService.createTaskCommentQuery().creatorIn("admin").count();
      assertThat(count).isEqualTo(1);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnCountOfEvents_When_UsingCountMethodAndCreatorNotIn() {
      long count = taskService.createTaskCommentQuery().creatorNotIn("admin").count();
      assertThat(count).isEqualTo(2);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class QueryColumnTaskComments {
    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnId() {
      List<String> listedValues =
          taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.ID, null);
      assertThat(listedValues)
          .hasSize(3)
          .containsExactlyInAnyOrder(comment1.getId(), comment2.getId(), comment3.getId());
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnTaskId() {
      List<String> listedValues =
          taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.TASK_ID, null);
      assertThat(listedValues).hasSize(1).containsExactly(comment1.getTaskId());
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnTextField() {
      List<String> listedValues =
          taskService
              .createTaskCommentQuery()
              .listValues(TaskCommentQueryColumnName.TEXT_FIELD, null);
      assertThat(listedValues)
          .hasSize(3)
          .containsExactlyInAnyOrder(
              comment1.getTextField(), comment2.getTextField(), comment3.getTextField());
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnCreator() {
      List<String> listedValues =
          taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.CREATOR, null);
      assertThat(listedValues).hasSize(2).containsExactlyInAnyOrder(ACCESS_USER_1_1_ID, "admin");
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnCreatorLongName() {
      List<String> listedValues =
          taskService
              .createTaskCommentQuery()
              .listValues(TaskCommentQueryColumnName.CREATOR_FULL_NAME, null);
      assertThat(listedValues).hasSize(2);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnCreated() {
      List<String> listedValues =
          taskService.createTaskCommentQuery().listValues(TaskCommentQueryColumnName.CREATED, null);
      assertThat(listedValues).hasSize(3);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnListedValues_For_QueryColumnModified() {
      List<String> listedValues =
          taskService
              .createTaskCommentQuery()
              .listValues(TaskCommentQueryColumnName.MODIFIED, null);
      assertThat(listedValues).hasSize(3);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class OffsetConstraintsTaskComments {
    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ConfirmQueryListOffset_When_ProvidingOffsetAndLimit() {
      List<TaskComment> offsetAndLimitResult = taskService.createTaskCommentQuery().list(1, 2);
      List<TaskComment> regularResult = taskService.createTaskCommentQuery().list();

      assertThat(offsetAndLimitResult).hasSize(2);
      assertThat(offsetAndLimitResult.get(0))
          .isNotEqualTo(regularResult.get(0))
          .isEqualTo(regularResult.get(1));
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnEmptyList_When_ProvidingWrongConstraints() {
      List<TaskComment> result = taskService.createTaskCommentQuery().list(0, 1000);
      assertThat(result).hasSize(3);

      result = taskService.createTaskCommentQuery().list(100, 1000);
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class SingleResultTaskComments {

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ReturnSingleTaskComment_When_UsingSingleMethod() {
      TaskComment single =
          taskService
              .createTaskCommentQuery()
              .idIn(comment1.getId())
              .creatorIn(ACCESS_USER_1_1_ID)
              .single();

      assertThat(single.getId()).isEqualTo(comment1.getId());
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_ThrowException_When_SingleMethodRetrievesMoreThanOneEventFromDatabase() {
      TaskCommentQuery query = taskService.createTaskCommentQuery().creatorIn(ACCESS_USER_1_1_ID);
      assertThatThrownBy(query::single).isInstanceOf(TooManyResultsException.class);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class TaskCreatorFullNameTaskCommentsWithAdditionalUserInfo
      implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskService taskService;
    @TaskanaInject UserService userService;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      taskanaEngineConfiguration.setAddAdditionalUserInfo(true);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_SetTaskCreatorFullNameOfTaskComment_When_PropertyEnabled() throws Exception {
      List<TaskComment> taskComments =
          taskService.createTaskCommentQuery().idIn(comment1.getId()).list();

      String creatorFullName = userService.getUser(taskComments.get(0).getCreator()).getFullName();
      assertThat(taskComments.get(0))
          .extracting(TaskComment::getCreatorFullName)
          .isEqualTo(creatorFullName);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_SetTaskCreatorFullNameOfTaskCommentToNull_When_NotExistingAsUserInDatabase() {
      List<TaskComment> taskComments =
          taskService.createTaskCommentQuery().idIn(comment3.getId()).list();

      assertThat(taskComments).hasSize(1);
      assertThat(taskComments.get(0)).extracting(TaskComment::getCreatorFullName).isNull();
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class TaskCreatorFullNameTaskCommentsWithoutAdditionalUserInfo
      implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      taskanaEngineConfiguration.setAddAdditionalUserInfo(false);
    }

    @WithAccessId(user = ACCESS_USER_1_1_ID)
    @Test
    void should_NotSetTaskCreatorFullNameOfTaskComment_When_PropertyDisabled() throws Exception {
      List<TaskComment> taskComments =
          taskService.createTaskCommentQuery().idIn(comment1.getId()).list();

      assertThat(taskComments.get(0)).extracting(TaskComment::getCreatorFullName).isNull();
    }
  }
}
