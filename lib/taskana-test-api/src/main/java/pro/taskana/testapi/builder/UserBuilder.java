package pro.taskana.testapi.builder;

import java.util.Set;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;

public class UserBuilder implements EntityBuilder<User, UserService> {

  private final UserImpl testUser = new UserImpl();

  private UserBuilder() {}

  public static UserBuilder newUser() {
    return new UserBuilder();
  }

  public UserBuilder id(String id) {
    testUser.setId(id);
    return this;
  }

  public UserBuilder groups(Set<String> groups) {
    testUser.setGroups(groups);
    return this;
  }

  public UserBuilder firstName(String firstName) {
    testUser.setFirstName(firstName);
    return this;
  }

  public UserBuilder lastName(String lastName) {
    testUser.setLastName(lastName);
    return this;
  }

  public UserBuilder fullName(String fullName) {
    testUser.setFullName(fullName);
    return this;
  }

  public UserBuilder longName(String longName) {
    testUser.setLongName(longName);
    return this;
  }

  public UserBuilder email(String email) {
    testUser.setEmail(email);
    return this;
  }

  public UserBuilder phone(String phone) {
    testUser.setPhone(phone);
    return this;
  }

  public UserBuilder mobilePhone(String mobilePhone) {
    testUser.setMobilePhone(mobilePhone);
    return this;
  }

  public UserBuilder orgLevel1(String orgLevel1) {
    testUser.setOrgLevel1(orgLevel1);
    return this;
  }

  public UserBuilder orgLevel2(String orgLevel2) {
    testUser.setOrgLevel2(orgLevel2);
    return this;
  }

  public UserBuilder orgLevel3(String orgLevel3) {
    testUser.setOrgLevel3(orgLevel3);
    return this;
  }

  public UserBuilder orgLevel4(String orgLevel4) {
    testUser.setOrgLevel4(orgLevel4);
    return this;
  }

  public UserBuilder data(String data) {
    testUser.setData(data);
    return this;
  }

  @Override
  public User buildAndStore(UserService userService)
      throws UserAlreadyExistException, InvalidArgumentException, MismatchedRoleException {
    return userService.createUser(testUser);
  }
}
