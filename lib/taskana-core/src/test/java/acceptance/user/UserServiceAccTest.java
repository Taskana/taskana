package acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;

/** Acceptance test which tests the functionality of the UserService. */
@ExtendWith(JaasExtension.class)
class UserServiceAccTest extends AbstractAccTest {
  private static final UserService USER_SERVICE = taskanaEngine.getUserService();

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnUserWithAllFields_When_IdExisting() throws Exception {
    User user = USER_SERVICE.getUser("teamlead-1");

    assertThat(user.getFirstName()).isEqualTo("Titus");
    assertThat(user.getLastName()).isEqualTo("Toll");
    assertThat(user.getFullName()).isEqualTo("Toll, Titus");
    assertThat(user.getLongName()).isEqualTo("Toll, Titus - (teamlead-1)");
    assertThat(user.getEmail()).isEqualTo("titus.toll@web.de");
    assertThat(user.getPhone()).isEqualTo("040-2951854");
    assertThat(user.getMobilePhone()).isEqualTo("015637683197");
    assertThat(user.getOrgLevel4()).isEqualTo("Novatec");
    assertThat(user.getOrgLevel3()).isEqualTo("BPM");
    assertThat(user.getOrgLevel2()).isEqualTo("Human Workflow");
    assertThat(user.getOrgLevel1()).isEqualTo("TASKANA");
    assertThat(user.getData()).isEqualTo("xy");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToGetUserWithNonExistingId() {
    ThrowingCallable callable = () -> USER_SERVICE.getUser("NOT_EXISTING");
    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_InsertUserInDatabase_When_CreatingUser() throws Exception {
    User userToCreate = createExampleUser("user-10-20");

    USER_SERVICE.createUser(userToCreate);
    User userInDatabse = USER_SERVICE.getUser(userToCreate.getId());

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

    User createdUser = USER_SERVICE.createUser(userToCreate);
    assertThat(createdUser.getLongName()).isEqualTo(longName);
    assertThat(createdUser.getFullName()).isEqualTo(fullName);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithFirstOrLastNameNull()
      throws Exception {
    User userToCreate = createExampleUser("user-10-20");
    userToCreate.setFirstName(null);

    ThrowingCallable callable = () -> USER_SERVICE.createUser(userToCreate);
    assertThatThrownBy(callable)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("First and last name of User must be set or empty.");

    userToCreate.setFirstName("xy");
    userToCreate.setLastName(null);
    callable = () -> USER_SERVICE.createUser(userToCreate);
    assertThatThrownBy(callable).isInstanceOf(InvalidArgumentException.class);
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
          ThrowingCallable callable = () -> USER_SERVICE.createUser(userToCreate);
          assertThatThrownBy(callable)
              .isInstanceOf(InvalidArgumentException.class)
              .hasMessage("UserId must not be empty when creating User.");
        };

    return DynamicTest.stream(iterator, c -> "for " + c, test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserAlreadyExistException_When_TryingToCreateUserWithExistingId() {
    User userToCreate = createExampleUser("teamlead-1"); // existing userId

    ThrowingCallable callable = () -> USER_SERVICE.createUser(userToCreate);
    assertThatThrownBy(callable)
        .isInstanceOf(UserAlreadyExistException.class)
        .hasMessage("User with id 'teamlead-1' already exists.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToCreateUserWithoutAdminRole() {
    User userToCreate = createExampleUser("user-10-22");

    ThrowingCallable callable = () -> USER_SERVICE.createUser(userToCreate);
    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_UpdateUserInDatabase_When_IdExisting() throws Exception {
    User userToUpdate = createExampleUser("teamlead-1"); // existing userId

    USER_SERVICE.updateUser(userToUpdate);
    User userInDatabase = USER_SERVICE.getUser("teamlead-1");

    assertThat(userToUpdate)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(userInDatabase)
        .isEqualTo(userInDatabase);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToUpdateUserWithNonExistingId() {
    User userToUpdate = createExampleUser("NOT_EXISTING");

    ThrowingCallable callable = () -> USER_SERVICE.updateUser(userToUpdate);
    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToUpdateUserWithNoAdminRole() {
    User userToUpdate = createExampleUser("teamlead-1"); // existing userId

    ThrowingCallable callable = () -> USER_SERVICE.updateUser(userToUpdate);
    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteUserFromDatabase_When_IdExisting() throws Exception {
    String id = "teamlead-1";
    USER_SERVICE.getUser(id); // User existing

    USER_SERVICE.deleteUser(id);
    ThrowingCallable callable = () -> USER_SERVICE.getUser(id); // User deleted
    assertThatThrownBy(callable).isInstanceOf(UserNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowUserNotFoundException_When_TryingToDeleteUserWithNonExistingId() {
    ThrowingCallable callable = () -> USER_SERVICE.deleteUser("NOT_EXISTING");
    assertThatThrownBy(callable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User with id 'NOT_EXISTING' was not found.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowNotAuthorizedException_When_TryingToDeleteUserWithNoAdminRole() {
    ThrowingCallable callable = () -> USER_SERVICE.deleteUser("teamlead-1");
    assertThatThrownBy(callable)
        .isInstanceOf(MismatchedRoleException.class)
        .hasMessage(
            "Not authorized. The current user 'user-1-2' is not member of role(s) "
                + "'[BUSINESS_ADMIN, ADMIN]'.");
  }
}
