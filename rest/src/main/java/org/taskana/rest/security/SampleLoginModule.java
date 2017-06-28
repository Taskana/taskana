package org.taskana.rest.security;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class SampleLoginModule implements LoginModule {

	private Subject subject;
	private String password;
	private String username;

	public boolean abort() throws LoginException {
		return true;
	}

	public boolean commit() throws LoginException {
		return true;
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.subject = subject;

		try {
			NameCallback nameCallback = new NameCallback("prompt");
			PasswordCallback passwordCallback = new PasswordCallback("prompt", false);

			callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });

			this.password = new String(passwordCallback.getPassword());
			this.username = nameCallback.getName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean login() throws LoginException {
		return true;
	}

	public boolean logout() throws LoginException {
		return true;
	}

}
