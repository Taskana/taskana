package pro.taskana.common.internal.security;

import java.security.Principal;

/** Represents a user principal with a name. */
public class UserPrincipal implements Principal {

  private final String name;

  public UserPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return "UserPrincipal [name= " + this.getName() + "]";
  }
}
