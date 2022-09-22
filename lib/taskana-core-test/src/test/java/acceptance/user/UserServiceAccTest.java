package acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static pro.taskana.testapi.DefaultTestEntities.randomTestUser;
import static pro.taskana.testapi.builder.UserBuilder.newUser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** Acceptance test which tests the functionality of the UserService. */
@TaskanaIntegrationTest
class UserServiceAccTest {

  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  private WorkbasketAccessItem createAccessItem(
      User user, Workbasket workbasket, WorkbasketPermission... permissions) throws Exception {
    WorkbasketAccessItemBuilder builder =
        WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
            .accessId(user.getId())
            .workbasketId(workbasket.getId());
    for (WorkbasketPermission permission : permissions) {
      builder.permission(permission);
    }

    return builder.buildAndStore(workbasketService, "businessadmin");
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class GetUser {

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ThrowUserNotFoundException_When_TryingToGetUserWithNonExistingId() {
      ThrowingCallable callable = () -> userService.getUser("NOT_EXISTING");

      assertThatThrownBy(callable)
          .isInstanceOf(UserNotFoundException.class)
          .extracting(UserNotFoundException.class::cast)
          .extracting(UserNotFoundException::getUserId)
          .isEqualTo("NOT_EXISTING");
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnUserWithAllFields_When_TryingToGetUserWithIdExisting() throws Exception {
      final User userToGet =
          newUser()
              .id("max-mustermann")
              .firstName("Max")
              .lastName("Mustermann")
              .fullName("Max Mustermann")
              .longName("Mustermann, Max")
              .email("max@mustermann.de")
              .phone("123456798")
              .mobilePhone("987654321")
              .orgLevel1("org1")
              .orgLevel2("org2")
              .orgLevel3("org3")
              .orgLevel4("org4")
              .data("this is some extra data about max")
              .buildAndStore(userService, "businessadmin");

      User userInDatabase = userService.getUser(userToGet.getId());

      assertThat(userInDatabase).hasNoNullFieldsOrProperties().isEqualTo(userToGet);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CreateUser {

    @WithAccessId(user = "businessadmin")
    @Test
    void should_InsertUserInDatabase_When_CreatingUser() throws Exception {
      User userToCreate = userService.newUser();
      userToCreate.setId("anton");
      userToCreate.setFirstName("Anton");
      userToCreate.setLastName("Miller");
      userService.createUser(userToCreate);

      User userInDatabase = userService.getUser(userToCreate.getId());
      assertThat(userToCreate).isNotSameAs(userInDatabase).isEqualTo(userInDatabase);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_SetTheLongAndFullNameAccordingToRules_When_CreatingUserWithThoseFieldsEmpty()
        throws Exception {
      User userToCreate = userService.newUser();
      userToCreate.setId("martina");
      userToCreate.setFirstName("Martina");
      userToCreate.setLastName("Schmidt");

      User createdUser = userService.createUser(userToCreate);

      assertThat(createdUser.getLongName()).isEqualTo("Schmidt, Martina - (martina)");
      assertThat(createdUser.getFullName()).isEqualTo("Schmidt, Martina");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithFirstNameNull() {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-1");
      userToCreate.setFirstName(null);
      userToCreate.setLastName("Schmidt");

      ThrowingCallable callable = () -> userService.createUser(userToCreate);

      assertThatThrownBy(callable)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessage("First and last name of User must be set or empty.");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithLastNameNull() {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-2");
      userToCreate.setFirstName("User 1");
      userToCreate.setLastName(null);

      ThrowingCallable callable = () -> userService.createUser(userToCreate);

      assertThatThrownBy(callable)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessage("First and last name of User must be set or empty.");
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_ThrowInvalidArgumentException_When_TryingToCreateUserWithNotSetId() {
      Iterator<String> iterator = Arrays.asList("", null).iterator();

      ThrowingConsumer<String> test =
          userId -> {
            User userToCreate = userService.newUser();
            userToCreate.setFirstName("firstName");
            userToCreate.setLastName("lastName");
            userToCreate.setId(userId);

            ThrowingCallable callable = () -> userService.createUser(userToCreate);

            assertThatThrownBy(callable)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("UserId must not be empty when creating User.");
          };

      return DynamicTest.stream(iterator, c -> "for " + c, test);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowUserAlreadyExistException_When_TryingToCreateUserWithExistingId()
        throws Exception {
      User existingUser = randomTestUser().buildAndStore(userService);

      ThrowingCallable callable =
          () -> {
            User userToCreate = userService.newUser();
            userToCreate.setId(existingUser.getId());
            userToCreate.setFirstName("firstName");
            userToCreate.setLastName("lastName");

            userService.createUser(userToCreate);
          };

      assertThatThrownBy(callable)
          .isInstanceOf(UserAlreadyExistException.class)
          .extracting(UserAlreadyExistException.class::cast)
          .extracting(UserAlreadyExistException::getUserId)
          .isEqualTo(existingUser.getId());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ThrowNotAuthorizedException_When_TryingToCreateUserWithoutAdminRole() {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-3");
      userToCreate.setFirstName("firstName");
      userToCreate.setLastName("lastName");
      ThrowingCallable callable = () -> userService.createUser(userToCreate);

      MismatchedRoleException ex = catchThrowableOfType(callable, MismatchedRoleException.class);
      assertThat(ex.getCurrentUserId()).isEqualTo("user-1-1");
      assertThat(ex.getRoles())
          .isEqualTo(new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN});
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class UpdateUser {

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateUserInDatabase_When_IdExisting() throws Exception {
      User userToUpdate = randomTestUser().buildAndStore(userService);

      userToUpdate.setFirstName("Anton");
      userService.updateUser(userToUpdate);

      User userInDatabase = userService.getUser(userToUpdate.getId());
      assertThat(userInDatabase).isNotSameAs(userToUpdate).isEqualTo(userToUpdate);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ThrowNotAuthorizedException_When_TryingToUpdateUserWithNoAdminRole()
        throws Exception {
      User userToUpdate = randomTestUser().buildAndStore(userService, "businessadmin");

      ThrowingCallable callable =
          () -> {
            userToUpdate.setLastName("updated last name");
            userService.updateUser(userToUpdate);
          };

      MismatchedRoleException ex = catchThrowableOfType(callable, MismatchedRoleException.class);
      assertThat(ex.getCurrentUserId()).isEqualTo("user-1-1");
      assertThat(ex.getRoles())
          .isEqualTo(new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN});
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowUserNotFoundException_When_TryingToUpdateUserWithNonExistingId() {
      User userToUpdate = userService.newUser();
      userToUpdate.setId("user-4");
      userToUpdate.setFirstName("firstName");
      userToUpdate.setLastName("lastName");

      ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

      assertThatThrownBy(callable)
          .isInstanceOf(UserNotFoundException.class)
          .extracting(UserNotFoundException.class::cast)
          .extracting(UserNotFoundException::getUserId)
          .isEqualTo(userToUpdate.getId());
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class DeleteUser {

    @WithAccessId(user = "businessadmin")
    @Test
    void should_DeleteUserFromDatabase_When_IdExisting() throws Exception {
      User userToDelete = randomTestUser().buildAndStore(userService);

      userService.deleteUser(userToDelete.getId());

      // Validate that user is indeed deleted
      ThrowingCallable callable = () -> userService.getUser(userToDelete.getId());
      assertThatThrownBy(callable)
          .isInstanceOf(UserNotFoundException.class)
          .extracting(UserNotFoundException.class::cast)
          .extracting(UserNotFoundException::getUserId)
          .isEqualTo(userToDelete.getId());
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowUserNotFoundException_When_TryingToDeleteUserWithNonExistingId() {
      ThrowingCallable callable = () -> userService.deleteUser("NOT_EXISTING");

      assertThatThrownBy(callable)
          .isInstanceOf(UserNotFoundException.class)
          .extracting(UserNotFoundException.class::cast)
          .extracting(UserNotFoundException::getUserId)
          .isEqualTo("NOT_EXISTING");
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ThrowNotAuthorizedException_When_TryingToDeleteUserWithNoAdminRole()
        throws Exception {
      User userToDelete = randomTestUser().buildAndStore(userService, "businessadmin");

      ThrowingCallable callable = () -> userService.deleteUser(userToDelete.getId());

      MismatchedRoleException ex = catchThrowableOfType(callable, MismatchedRoleException.class);
      assertThat(ex.getCurrentUserId()).isEqualTo("user-1-1");
      assertThat(ex.getRoles())
          .isEqualTo(new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN});
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class DynamicDomainComputation {

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnEmptyDomains_When_UserHasInsufficientMinimalPermissionsToAssignDomains()
        throws Exception {
      User user = randomTestUser().buildAndStore(userService, "businessadmin");
      Workbasket workbasket =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      createAccessItem(user, workbasket, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).isEmpty();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnEmptyDomains_When_UserHasNoPermissions() throws Exception {
      User user = randomTestUser().buildAndStore(userService, "businessadmin");
      defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).isEmpty();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnOneDomain_When_UserHasSufficientMinimalPermissionsToAssignDomains()
        throws Exception {
      User user = randomTestUser().buildAndStore(userService, "businessadmin");
      Workbasket workbasket =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      createAccessItem(user, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).containsExactly(workbasket.getDomain());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnEmptyDomains_When_UserHasSufficientPermissionsWhichThenGetRevoked()
        throws Exception {
      User user = randomTestUser().buildAndStore(userService, "businessadmin");
      Workbasket workbasket =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      WorkbasketAccessItem wai =
          createAccessItem(user, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).containsExactly(workbasket.getDomain());

      // then permission gets revoked

      wai.setPermission(WorkbasketPermission.OPEN, false);
      taskanaEngine.runAsAdmin(wrap(() -> workbasketService.updateWorkbasketAccessItem(wai)));

      userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).isEmpty();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnMultipleDomains_When_UserHasSufficientMinimalPermissionsForMultipleDomains()
        throws Exception {
      User user = randomTestUser().buildAndStore(userService, "businessadmin");
      Workbasket workbasket1 =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      Workbasket workbasket2 =
          defaultTestWorkbasket()
              .domain("DOMAIN_B")
              .buildAndStore(workbasketService, "businessadmin");
      createAccessItem(user, workbasket1, WorkbasketPermission.OPEN, WorkbasketPermission.READ);
      createAccessItem(user, workbasket2, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains())
          .containsExactlyInAnyOrder(workbasket1.getDomain(), workbasket2.getDomain());
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class DifferentMinimalPermissionsToAssignDomains implements TaskanaEngineConfigurationModifier {

      @TaskanaInject UserService userService;
      @TaskanaInject WorkbasketService workbasketService;

      @Override
      public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        taskanaEngineConfiguration.setMinimalPermissionsToAssignDomains(
            List.of(WorkbasketPermission.APPEND));
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyDomains_When_UserHasInsufficientMinimalPermissionsToAssignDomains()
          throws Exception {
        User user = randomTestUser().buildAndStore(userService, "businessadmin");
        Workbasket workbasket =
            defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
        createAccessItem(user, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnOneDomain_When_UserHasSufficientMinimalPermissionsToAssignDomains()
          throws Exception {
        User user = randomTestUser().buildAndStore(userService, "businessadmin");
        Workbasket workbasket =
            defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
        createAccessItem(user, workbasket, WorkbasketPermission.APPEND);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PropertyMinimalPermissionsToAssignDomainsIsNotSet
        implements TaskanaEngineConfigurationModifier {

      @TaskanaInject UserService userService;
      @TaskanaInject WorkbasketService workbasketService;

      @Override
      public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        taskanaEngineConfiguration.setMinimalPermissionsToAssignDomains(null);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyDomains_When_PropertyIsNotSet() throws Exception {
        User user = randomTestUser().buildAndStore(userService, "businessadmin");
        Workbasket workbasket =
            defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
        createAccessItem(user, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }
    }
  }
}
