package pro.taskana.rest.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.core.Authentication;

import pro.taskana.security.GroupPrincipal;
import pro.taskana.security.UserPrincipal;

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
		    String userName = jaasAuthenticationToken.getPrincipal().toString();       
            jaasAuthenticationToken.getLoginContext().getSubject().getPrincipals().add(new UserPrincipal(userName));
            jaasAuthenticationToken.getLoginContext().getSubject().getPrincipals().add(new GroupPrincipal("group1"));
            jaasAuthenticationToken.getLoginContext().getSubject().getPrincipals().add(new GroupPrincipal("group2"));
            jaasAuthenticationToken.getLoginContext().getSubject().getPrincipals().add(new GroupPrincipal("group3"));
			return jaasAuthenticationToken;
		} else {
			return null;
		}
	}

    @Override
	public boolean supports(Class<?> authentication) {
		return delegate.supports(authentication);
	}
}
