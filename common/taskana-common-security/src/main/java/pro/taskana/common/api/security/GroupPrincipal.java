package pro.taskana.common.api.security;

import java.security.Principal;
import java.util.Objects;

/** The GroupPrincipal represents a group with its name. */
public class GroupPrincipal implements Principal {

  private final String name;

  public GroupPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
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
    if (!(obj instanceof GroupPrincipal)) {
      return false;
    }
    GroupPrincipal other = (GroupPrincipal) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "GroupPrincipal [name=" + name + "]";
  }
}
