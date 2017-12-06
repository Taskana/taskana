package pro.taskana.rest.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.core.Authentication;

import pro.taskana.security.TaskanaPrincipal;

public class CustomAutenticationProvider implements AuthenticationProvider {
	private AuthenticationProvider delegate;

	public CustomAutenticationProvider(AuthenticationProvider delegate) {
		this.delegate = delegate;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		JaasAuthenticationToken jaasAuthenticationToken = (JaasAuthenticationToken) delegate
				.authenticate(authentication);

		if (jaasAuthenticationToken.isAuthenticated()) {
            final String name = jaasAuthenticationToken.getPrincipal().toString();
            final List<String> groupNames = getGroupNames(name);
            TaskanaPrincipal tp = new TaskanaPrincipal() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public List<String> getGroupNames() {
                    return groupNames;
                }
            };
            jaasAuthenticationToken.getLoginContext().getSubject().getPrincipals().add(tp);
			return jaasAuthenticationToken;
		} else {
			return null;
		}
	}

    private List<String> getGroupNames(String name) {
        List<String> groupNames = new ArrayList<String>();
        groupNames.add("group1");
        groupNames.add("group2");
        return groupNames;
    }

    @Override
	public boolean supports(Class<?> authentication) {
		return delegate.supports(authentication);
	}
}
