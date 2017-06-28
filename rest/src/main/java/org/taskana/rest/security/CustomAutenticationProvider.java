package org.taskana.rest.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.core.Authentication;

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
			jaasAuthenticationToken.getLoginContext().getSubject().getPublicCredentials().add(jaasAuthenticationToken.getPrincipal());
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
