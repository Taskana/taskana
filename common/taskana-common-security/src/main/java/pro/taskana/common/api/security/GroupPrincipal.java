package pro.taskana.common.api.security;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Represents a group with a name. */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GroupPrincipal implements Principal {

  private final String name;
}
