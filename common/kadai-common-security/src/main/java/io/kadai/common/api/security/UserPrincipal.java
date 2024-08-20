package io.kadai.common.api.security;

import java.security.Principal;
import java.util.Objects;

/** Represents a user with a name. */
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
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof UserPrincipal)) {
      return false;
    }
    UserPrincipal other = (UserPrincipal) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "UserPrincipal [name=" + name + "]";
  }
}
