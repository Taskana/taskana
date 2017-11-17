package pro.taskana.rest.security;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.jaas.AuthorityGranter;

public class RoleGranterFromMap implements AuthorityGranter {

	private static Map<String, String> USER_ROLES = new HashMap<String, String>();

	static {
		USER_ROLES.put("test", "ROLE_ADMINISTRATOR");
		// USER_ROLES.put("test", "TRUE");
	}

	public Set<String> grant(Principal principal) {
		return Collections.singleton("DUMMY");
	}
}
