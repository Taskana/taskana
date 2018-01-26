package pro.taskana.security;

import java.security.Principal;

/**
 * Represents a user principal with a name.
 */
public class UserPrincipal implements Principal {

    private String name;

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
