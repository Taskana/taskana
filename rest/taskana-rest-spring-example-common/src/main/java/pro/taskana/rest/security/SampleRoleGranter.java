package pro.taskana.rest.security;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.authentication.jaas.AuthorityGranter;

/**
 * TODO.
 */
public class SampleRoleGranter implements AuthorityGranter {

    @Override
    public Set<String> grant(Principal principal) {
        return Collections.singleton(principal.getName());
    }
}
