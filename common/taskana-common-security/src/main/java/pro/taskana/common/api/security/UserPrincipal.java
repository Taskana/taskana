package pro.taskana.common.api.security;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Represents a user with a name. */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserPrincipal implements Principal {

  private final String name;
}
