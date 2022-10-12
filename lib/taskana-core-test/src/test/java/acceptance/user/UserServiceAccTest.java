package acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static pro.taskana.testapi.DefaultTestEntities.randomTestUser;
import static pro.taskana.testapi.builder.UserBuilder.newUser;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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
import pro.taskana.common.internal.util.Triplet;
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
      String accessId, Workbasket workbasket, WorkbasketPermission... permissions)
      throws Exception {
    WorkbasketAccessItemBuilder builder =
        WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
            .accessId(accessId)
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
              .groups(
                  Set.of(
                      "cn=ksc-users,cn=groups,OU=Test,O=TASKANA",
                      "cn=Organisationseinheit KSC 1,cn=Organisationseinheit"
                          + " KSC,cn=organisation,OU=Test,O=TASKANA"))
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

      assertThat(userInDatabase)
          .hasNoNullFieldsOrProperties()
          .isNotSameAs(userToGet)
          .isEqualTo(userToGet);
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
    void should_InsertUserInDatabase_When_CreatingUserWithGroups() throws Exception {
      User userToCreate = userService.newUser();
      userToCreate.setId("anton2");
      userToCreate.setFirstName("Anton");
      userToCreate.setLastName("Miller");
      userToCreate.setGroups(Set.of("groupX", "groupY"));
      userService.createUser(userToCreate);

      User userInDatabase = userService.getUser(userToCreate.getId());
      assertThat(userToCreate).isNotSameAs(userInDatabase).isEqualTo(userInDatabase);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_AutomaticallySetTheLongName_When_CreatingUserWithEmptyOrNullLongName() {
      Stream<String> longNames = Stream.of(null, "");

      ThrowingConsumer<String> test =
          longName -> {
            User userToCreate = userService.newUser();
            userToCreate.setId(UUID.randomUUID().toString().replace("-", ""));
            userToCreate.setFirstName("Martina");
            userToCreate.setLastName("Schmidt");
            userToCreate.setLongName(longName);

            User updatedUser = userService.createUser(userToCreate);

            assertThat(updatedUser.getLongName())
                .isEqualTo("Schmidt, Martina - (%s)", userToCreate.getId());
          };

      return DynamicTest.stream(longNames, l -> "for " + l, test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_AutomaticallySetTheFullName_When_CreatingUserWithEmptyOrNullFullName() {
      Stream<String> fullNames = Stream.of(null, "");

      ThrowingConsumer<String> test =
          fullName -> {
            User userToCreate = userService.newUser();
            userToCreate.setId(UUID.randomUUID().toString().replace("-", ""));
            userToCreate.setFirstName("Martina");
            userToCreate.setLastName("Schmidt");
            userToCreate.setFullName(fullName);

            User updatedUser = userService.createUser(userToCreate);

            assertThat(updatedUser.getFullName()).isEqualTo("Schmidt, Martina");
          };

      return DynamicTest.stream(fullNames, l -> "for " + l, test);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_KeepLongName_When_CreatingUserWithLongNameDefined() throws Exception {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-1");
      userToCreate.setFirstName("Martina");
      userToCreate.setLastName("Schmidt");
      userToCreate.setLongName("long name");

      User updatedUser = userService.createUser(userToCreate);

      assertThat(updatedUser.getLongName()).isEqualTo("long name");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_KeepFullName_When_CreatingUserWithFullNameDefined() throws Exception {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-2");
      userToCreate.setFirstName("Martina");
      userToCreate.setLastName("Schmidt");
      userToCreate.setFullName("full name");

      User updatedUser = userService.createUser(userToCreate);

      assertThat(updatedUser.getFullName()).isEqualTo("full name");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowInvalidArgumentException_When_TryingToCreateUserWithFirstNameNull() {
      User userToCreate = userService.newUser();
      userToCreate.setId("user-3");
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
      userToCreate.setId("user-4");
      userToCreate.setFirstName("User 4");
      userToCreate.setLastName(null);

      ThrowingCallable callable = () -> userService.createUser(userToCreate);

      assertThatThrownBy(callable)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessage("First and last name of User must be set or empty.");
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_ThrowInvalidArgumentException_When_TryingToCreateUserWithNotSetId() {
      Stream<String> userIds = Stream.of("", null);

      ThrowingConsumer<String> test =
          userId -> {
            User userToCreate = userService.newUser();
            userToCreate.setFirstName("firstName");
            userToCreate.setLastName("lastName");
            userToCreate.setId(userId);

            ThrowingCallable callable = () -> userService.createUser(userToCreate);

            assertThatThrownBy(callable)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("UserId must not be empty when creating or updating User.");
          };

      return DynamicTest.stream(userIds, c -> "for " + c, test);
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
      userToCreate.setId("user-5");
      userToCreate.setFirstName("firstName");
      userToCreate.setLastName("lastName");
      ThrowingCallable callable = () -> userService.createUser(userToCreate);

      MismatchedRoleException ex = catchThrowableOfType(callable, MismatchedRoleException.class);
      assertThat(ex.getCurrentUserId()).isEqualTo("user-1-1");
      assertThat(ex.getRoles())
          .isEqualTo(new TaskanaRole[] {TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN});
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_MakeAccessIdsLowerCase_When_ConfigurationPropertyIsSet() throws Exception {
      User existingUser =
          randomTestUser()
              .id("USER-ID-WITH-CAPS")
              .groups(Set.of("GROUP1-ID-WITH-CAPS", "Group2-Id-With-Caps"))
              .buildAndStore(userService);

      User userInDatabase = userService.getUser(existingUser.getId());

      assertThat(userInDatabase.getId()).isEqualTo("user-id-with-caps");
      assertThat(userInDatabase.getGroups())
          .containsExactlyInAnyOrder("group1-id-with-caps", "group2-id-with-caps");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class UpdateUser {

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateUserInDatabase() throws Exception {
      User userToUpdate = randomTestUser().buildAndStore(userService);

      userToUpdate.setFirstName("Anton");
      userService.updateUser(userToUpdate);

      User userInDatabase = userService.getUser(userToUpdate.getId());
      assertThat(userInDatabase).isNotSameAs(userToUpdate).isEqualTo(userToUpdate);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_UpdateGroups() {
      Stream<Triplet<String, Set<String>, Set<String>>> testCases =
          Stream.of(
              Triplet.of(
                  "User has no groups before updating", Set.of(), Set.of("group1", "group2")),
              Triplet.of("new groups differ all", Set.of("group1"), Set.of("group2", "group3")),
              Triplet.of("some new groups differ", Set.of("group1"), Set.of("group1", "group2")),
              Triplet.of("new groups are all the same", Set.of("group1"), Set.of("group1")));

      ThrowingConsumer<Triplet<String, Set<String>, Set<String>>> test =
          t -> {
            Set<String> existingGroups = t.getMiddle();
            Set<String> newGroups = t.getMiddle();
            User userToUpdate = randomTestUser().groups(existingGroups).buildAndStore(userService);

            userToUpdate.setGroups(newGroups);
            userService.updateUser(userToUpdate);

            User userInDatabase = userService.getUser(userToUpdate.getId());
            assertThat(userInDatabase.getGroups()).containsExactlyInAnyOrderElementsOf(newGroups);
          };

      return DynamicTest.stream(testCases, Triplet::getLeft, test);
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
    @TestFactory
    Stream<DynamicTest>
        should_AutomaticallySetTheLongName_When_UpdatingUserWithEmptyOrNullLongName() {
      Stream<String> longNames = Stream.of(null, "", "old longName");

      ThrowingConsumer<String> test =
          longName -> {
            User userToUpdate = randomTestUser().buildAndStore(userService);
            userToUpdate.setFirstName("Martina");
            userToUpdate.setLastName("Schmidt");
            if (longName == null || !longName.equals("old longName")) {
              userToUpdate.setLongName(longName);
            }

            User updatedUser = userService.updateUser(userToUpdate);

            assertThat(updatedUser.getLongName())
                .isEqualTo("Schmidt, Martina - (%s)", userToUpdate.getId());
          };

      return DynamicTest.stream(longNames, l -> "for " + l, test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_AutomaticallySetTheFullName_When_UpdatingUserWithEmptyOrNullOrOldFullName() {
      Stream<String> fullNames = Stream.of(null, "", "old fullName");

      ThrowingConsumer<String> test =
          fullName -> {
            User userToUpdate = randomTestUser().buildAndStore(userService);
            userToUpdate.setFirstName("Martina");
            userToUpdate.setLastName("Schmidt");
            if (fullName == null || !fullName.equals("old fullName")) {
              userToUpdate.setFullName(fullName);
            }

            User updatedUser = userService.updateUser(userToUpdate);

            assertThat(updatedUser.getFullName()).isEqualTo("Schmidt, Martina");
          };

      return DynamicTest.stream(fullNames, l -> "for " + l, test);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_KeepLongName_When_UpdatingUserWithLongNameDefined() throws Exception {
      User userToUpdate = randomTestUser().buildAndStore(userService);
      userToUpdate.setFirstName("Martina");
      userToUpdate.setLastName("Schmidt");
      userToUpdate.setLongName("long name");

      User updatedUser = userService.updateUser(userToUpdate);

      assertThat(updatedUser.getLongName()).isEqualTo("long name");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_KeepFullName_When_UpdatingUserWithFullNameDefined() throws Exception {
      User userToUpdate = randomTestUser().buildAndStore(userService);
      userToUpdate.setFirstName("Martina");
      userToUpdate.setLastName("Schmidt");
      userToUpdate.setFullName("full name");

      User updatedUser = userService.updateUser(userToUpdate);

      assertThat(updatedUser.getFullName()).isEqualTo("full name");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowInvalidArgumentException_When_TryingToUpdateUserWithFirstNameNull() {
      User userToUpdate = userService.newUser();
      userToUpdate.setId("user-3");
      userToUpdate.setFirstName(null);
      userToUpdate.setLastName("Schmidt");

      ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

      assertThatThrownBy(callable)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessage("First and last name of User must be set or empty.");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowInvalidArgumentException_When_TryingToUpdateUserWithLastNameNull() {
      User userToUpdate = userService.newUser();
      userToUpdate.setId("user-4");
      userToUpdate.setFirstName("User 4");
      userToUpdate.setLastName(null);

      ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

      assertThatThrownBy(callable)
          .isInstanceOf(InvalidArgumentException.class)
          .hasMessage("First and last name of User must be set or empty.");
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_ThrowInvalidArgumentException_When_TryingToUpdateUserWithNotSetId() {
      Stream<String> userIds = Stream.of("", null);

      ThrowingConsumer<String> test =
          userId -> {
            User userToUpdate = userService.newUser();
            userToUpdate.setFirstName("firstName");
            userToUpdate.setLastName("lastName");
            userToUpdate.setId(userId);

            ThrowingCallable callable = () -> userService.updateUser(userToUpdate);

            assertThatThrownBy(callable)
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("UserId must not be empty when creating or updating User.");
          };

      return DynamicTest.stream(userIds, c -> "for " + c, test);
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
    void should_DeleteUserFromDatabase() throws Exception {
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
    void should_DeleteGroupsFromDatabase_When_UserHadGroups() throws Exception {
      User userToDelete =
          randomTestUser().groups(Set.of("group1", "group2")).buildAndStore(userService);

      userService.deleteUser(userToDelete.getId());

      // verify that groups are deleted by creating a new user with the same id and check its groups
      User newUserWithSameId = randomTestUser().id(userToDelete.getId()).buildAndStore(userService);
      User userInDatabase = userService.getUser(newUserWithSameId.getId());
      assertThat(userInDatabase.getGroups()).isEmpty();
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
      createAccessItem(user.getId(), workbasket, WorkbasketPermission.READ);

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
      createAccessItem(
          user.getId(), workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).containsExactly(workbasket.getDomain());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnOneDomain_When_GroupHasSufficientMinimalPermissionsToAssignDomains()
        throws Exception {
      String groupId = UUID.randomUUID().toString();
      User user =
          randomTestUser().groups(Set.of(groupId)).buildAndStore(userService, "businessadmin");
      Workbasket workbasket =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      createAccessItem(groupId, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

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
          createAccessItem(
              user.getId(), workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).containsExactly(workbasket.getDomain());

      // then permission gets revoked

      wai.setPermission(WorkbasketPermission.OPEN, false);
      taskanaEngine.runAsAdmin(wrap(() -> workbasketService.updateWorkbasketAccessItem(wai)));

      userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).isEmpty();
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ReturnEmptyDomains_When_GroupHasSufficientPermissionsAndThenGroupIsUpdated()
        throws Exception {
      String groupId = UUID.randomUUID().toString();
      User user = randomTestUser().groups(Set.of(groupId)).buildAndStore(userService);
      Workbasket workbasket = defaultTestWorkbasket().buildAndStore(workbasketService);
      createAccessItem(groupId, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains()).containsExactly(workbasket.getDomain());

      // then user is updated and other group is assigned

      user.setGroups(Set.of("new group"));
      userService.updateUser(user);

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
      createAccessItem(
          user.getId(), workbasket1, WorkbasketPermission.OPEN, WorkbasketPermission.READ);
      createAccessItem(
          user.getId(), workbasket2, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

      User userInDatabase = userService.getUser(user.getId());

      assertThat(userInDatabase.getDomains())
          .containsExactlyInAnyOrder(workbasket1.getDomain(), workbasket2.getDomain());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void
        should_ReturnMultipleDomains_When_UserAndGroupHaveSufficientMinimalPermsForMultipleDomains()
            throws Exception {
      String groupId = UUID.randomUUID().toString();
      User user =
          randomTestUser().groups(Set.of(groupId)).buildAndStore(userService, "businessadmin");
      Workbasket workbasket1 =
          defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
      Workbasket workbasket2 =
          defaultTestWorkbasket()
              .domain("DOMAIN_B")
              .buildAndStore(workbasketService, "businessadmin");
      createAccessItem(
          user.getId(), workbasket1, WorkbasketPermission.OPEN, WorkbasketPermission.READ);
      createAccessItem(groupId, workbasket2, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

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
        createAccessItem(
            user.getId(), workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

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
        createAccessItem(user.getId(), workbasket, WorkbasketPermission.APPEND);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnOneDomain_When_GroupHasSufficientMinimalPermissionsToAssignDomains()
          throws Exception {
        String groupId = UUID.randomUUID().toString();
        User user =
            randomTestUser().groups(Set.of(groupId)).buildAndStore(userService, "businessadmin");
        Workbasket workbasket =
            defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
        createAccessItem(groupId, workbasket, WorkbasketPermission.APPEND);

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
        createAccessItem(
            user.getId(), workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyDomains_When_PropertyIsNotSetAndGroupHasPermission() throws Exception {
        String groupId = UUID.randomUUID().toString();
        User user =
            randomTestUser().groups(Set.of(groupId)).buildAndStore(userService, "businessadmin");
        Workbasket workbasket =
            defaultTestWorkbasket().buildAndStore(workbasketService, "businessadmin");
        createAccessItem(groupId, workbasket, WorkbasketPermission.OPEN, WorkbasketPermission.READ);

        User userInDatabase = userService.getUser(user.getId());

        assertThat(userInDatabase.getDomains()).isEmpty();
      }
    }
  }
}
