package io.kadai.testapi.builder;

import static io.kadai.testapi.builder.UserBuilder.newUser;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.user.api.models.User;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class UserBuilderTest {

  @KadaiInject UserService userService;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PersistUser_When_UsingUserBuilder() throws Exception {
    User user =
        newUser().id("user-1").firstName("Max").lastName("Mustermann").buildAndStore(userService);

    User userInDatabase = userService.getUser("user-1");
    assertThat(userInDatabase).isNotSameAs(user).isEqualTo(user);
  }

  @Test
  void should_PersistUserEntityAsUser_When_UsingUserBuilder() throws Exception {
    User user =
        newUser()
            .id("user-2")
            .firstName("Max")
            .lastName("Mustermann")
            .buildAndStore(userService, "businessadmin");

    User userInDatabase = userService.getUser("user-2");
    assertThat(userInDatabase).isNotSameAs(user).isEqualTo(user);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PopulateUserEntity_When_UsingEveryBuilderFunction() throws Exception {
    final User user =
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
            .buildAndStore(userService);

    User expectedUser = userService.newUser();
    expectedUser.setId("max-mustermann");
    expectedUser.setFirstName("Max");
    expectedUser.setLastName("Mustermann");
    expectedUser.setFullName("Max Mustermann");
    expectedUser.setLongName("Mustermann, Max");
    expectedUser.setEmail("max@mustermann.de");
    expectedUser.setPhone("123456798");
    expectedUser.setMobilePhone("987654321");
    expectedUser.setOrgLevel1("org1");
    expectedUser.setOrgLevel2("org2");
    expectedUser.setOrgLevel3("org3");
    expectedUser.setOrgLevel4("org4");
    expectedUser.setData("this is some extra data about max");

    assertThat(user).hasNoNullFieldsOrProperties().isEqualTo(expectedUser);
  }
}
