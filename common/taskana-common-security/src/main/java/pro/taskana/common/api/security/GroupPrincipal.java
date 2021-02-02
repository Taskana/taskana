package pro.taskana.common.api.security;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/** Represents a group with a name and a set of members. */
public class GroupPrincipal implements Principal {

  private final String name;
  private final Set<Principal> members;

  public GroupPrincipal(String name) {
    this.name = name;
    members = new HashSet<>();
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean addMember(Principal user) {
    return members.add(user);
  }

  public boolean removeMember(Principal user) {
    return members.remove(user);
  }

  public boolean isMember(Principal member) {
    return members.contains(member);
  }

  public Enumeration<Principal> members() {
    return Collections.enumeration(members);
  }

  @Override
  public String toString() {
    return "GroupPrincipal [name=" + name + ", members=" + members + "]";
  }
}
