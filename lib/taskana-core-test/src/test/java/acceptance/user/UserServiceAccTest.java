package acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;

/** Acceptance test which tests the functionality of the UserService. */
@TaskanaIntegrationTest
class UserServiceAccTest {

  @TaskanaInject UserService userService;

  protected User createExampleUser(String id) {
    User user = userService.newUser();
    user.setId(id);
    user.setFirstName("Hans");
    user.setLastName("Georg");
    user.setFullName("Georg, Hans");
    user.setLongName("Georg, Hans - (user-10-20)");
    user.setEmail("hans.georg@web.com");
    user.setPhone("1234");
    user.setMobilePhone("01574275632");
    user.setOrgLevel4("level4");
    user.setOrgLevel3("level3");
    user.setOrgLevel2("level2");
    user.setOrgLevel1("level1");
    user.setData("ab");

    return user;
  }

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    User testuser1 = userService.newUser();
    testuser1.setId("testuser1");
    testuser1.setFirstName("Max");
    testuser1.setLastName("Mustermann");
    testuser1.setFullName("Max, Mustermann");
    testuser1.setLongName("Max, Mustermann - (testuser1)");
    testuser1.setEmail("max.mustermann@web.com");
    testuser1.setPhone("040-2951854");
    testuser1.setMobilePhone("015637683197");
    testuser1.setOrgLevel4("Novatec");
    testuser1.setOrgLevel3("BPM");
    testuser1.setOrgLevel2("Human Workflow");
    testuser1.setOrgLevel1("TASKANA");
    testuser1.setData("");
    userService.createUser(testuser1);

    User testuser2 = userService.newUser();
    testuser2.setId("testuser2");
    testuser2.setFirstName("Elena");
    testuser2.setLastName("Eifrig");
    testuser2.setFullName("Elena, Eifrig");
    testuser2.setLongName("Elena, Eifrig - (testuser2)");
    testuser2.setEmail("elena.eifrig@web.com");
    testuser2.setPhone("040-2951854");
    testuser2.setMobilePhone("015637683197");
    testuser2.setOrgLevel4("Novatec");
    testuser2.setOrgLevel3("BPM");
    testuser2.setOrgLevel2("Human Workflow");
    testuser2.setOrgLevel1("TASKANA");
    testuser2.setData("");
    userService.createUser(testuser2);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnUserWithAllFields_When_IdExisting() throws Exception {
    User userInDatabase = userService.getUser("testuser1");

    User userToCompare = userService.newUser();
    userToCompare.setId("testuser1");
    userToCompare.setFirstName("Max");
    userToCompare.setLastName("Mustermann");
    userToCompare.setFullName("Max, Mustermann");
    userToCompare.setLongName("Max, Mustermann - (testuser1)");
    userToCompare.setEmail("max.mustermann@web.com");
    userToCompare.setPhone("040-2951854");
    userToCompare.setMobilePhone("015637683197");
    userToCompare.setOrgLevel4("Novatec");
    userToCompare.setOrgLevel3("BPM");
    userToCompare.setOrgLevel2("Human Workflow");
    userToCompare.setOrgLevel1("TASKANA");
    userToCompare.setData("");

    assertThat(userInDatabase).hasNoNullFieldsOrProperties().isEqualTo(userToCompare);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToGetUserWithNonExistingId() {
    ThrowingCallable callable = () -> userService.getUser("NOT_EXISTING");

    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasFieldOrPropertyWithValue("userId", "NOT_EXISTING")
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_InsertUserInDatabase_When_CreatingUser() throws Exception {
    User userToCreate = createExampleUser("user-10-20");

    userService.createUser(userToCreate);

    User userInDatabse = userService.getUser(userToCreate.getId());

    assertThat(userToCreate)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(userInDatabse)
        .isEqualTo(userInDatabse);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetTheLongAndFullNameAccordingToRules_When_CreatingUserWithThoseFieldsEmpty()
      throws Exception {
    User userToCreate = createExampleUser("user-10-21");
    userToCreate.setLongName(null);
    userToCreate.setFullName(null);

    String fullName = userToCreate.getLastName() + ", " + userToCreate.getFirstName();
    String longName =
        userToCreate.getLastName()
            + ", "
            + userToCreate.getFirstName()
            + " - ("
            + userToCreate.getId()
            + ")";

    User createdUser = userService.createUser(userToCreate);

    assertThat(createdUser.getLongName()).isEqualTo(longName);
    assertThat(createdUser.getFullName()).isEqualTo(fullName);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithFirstOrLastNameNull()
      throws Exception {
    User userToCreate = createExampleUser("user-10-20");
    userToCreate.setFirstName(null);

    ThrowingCallable callable = () -> userService.createUser(userToCreate);

    assertThatThrownBy(callable)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("First and last name of User must be set or empty.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithLastNameNull() {
    User userToCreate = createExampleUser("user-10-20");
    userToCreate.setLastName(null);

    ThrowingCallable callable = () -> userService.createUser(userToCreate);

    assertThatThrownBy(callable)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("First and last name of User must be set or empty.");
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_ThrowInvalidArgumentException_When_TryingToCreateUserWithNotSetId()
      throws Exception {
    Iterator<String> iterator = Arrays.asList("", null).iterator();

    ThrowingConsumer<String> test =
        userId -> {
          User userToCreate = createExampleUser("user-10-20");
          userToCreate.setId(userId);

          ThrowingCallable callable = () -> userService.createUser(userToCreate);

          assertThatThrownBy(callable)
              .isInstanceOf(InvalidArgumentException.class)
              .hasMessage("UserId must not be empty when creating User.");
        };

    return DynamicTest.stream(iterator, c -> "for " + c, test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserAlreadyExistException_When_TryingToCreateUserWithExistingId() {
    User userToCreate = createExampleUser("testuser1"); // existing userId

    ThrowingCallable callable = () -> userService.createUser(userToCreate);

    assertThatThrownBy(callable)
        .isInstanceOf(UserAlreadyExistException.class)
        .hasFieldOrPropertyWithValue("userId", "testuser1")
        .hasMessage("User with id 'testuser1' already exists.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToCreateUserWithoutAdminRole() {
    User userToCreate = createExampleUser("user-10-22");

    ThrowingCallable callable = () -> userService.createUser(userToCreate);

    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasFieldOrPropertyWithValue("currentUserId", "user-1-2")
        .hasFieldOrPropertyWithValue(
            "roles", new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN})
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_UpdateUserInDatabase_When_IdExisting() throws Exception {
    User userToUpdate = createExampleUser("testuser1"); // existing userId

    userService.updateUser(userToUpdate);

    User userInDatabase = userService.getUser("testuser1");

    assertThat(userToUpdate).isNotSameAs(userInDatabase).isEqualTo(userInDatabase);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToUpdateUserWithNonExistingId() {
    User userToUpdate = createExampleUser("NOT_EXISTING");

    ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasFieldOrPropertyWithValue("userId", "NOT_EXISTING")
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToUpdateUserWithNoAdminRole() {
    User userToUpdate = createExampleUser("testuser1"); // existing userId

    ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasFieldOrPropertyWithValue("currentUserId", "user-1-2")
        .hasFieldOrPropertyWithValue(
            "roles", new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN})
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteUserFromDatabase_When_IdExisting() throws Exception {
    String id = "testuser2";
    userService.getUser(id); // User existing

    userService.deleteUser(id);

    ThrowingCallable callable = () -> userService.getUser(id); // User deleted

    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasFieldOrPropertyWithValue("userId", "testuser2")
        .hasMessage("User with id 'testuser2' was not found.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToDeleteUserWithNonExistingId() {
    ThrowingCallable callable = () -> userService.deleteUser("NOT_EXISTING");

    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasFieldOrPropertyWithValue("userId", "NOT_EXISTING")
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToDeleteUserWithNoAdminRole() {
    ThrowingCallable callable = () -> userService.deleteUser("testuser1");

    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasFieldOrPropertyWithValue("currentUserId", "user-1-2")
        .hasFieldOrPropertyWithValue(
            "roles", new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN})
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }
}
