package pro.taskana.rest.security;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.spi.LoginModule;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pro.taskana.security.GroupPrincipal;
import pro.taskana.security.UserPrincipal;

/**
 * TODO.
 */
public class SampleLoginModule extends UsernamePasswordAuthenticationFilter implements LoginModule {

    private NameCallback nameCallback;

    private PasswordCallback passwordCallback;

    private Subject subject;

    @Override
    public boolean abort() {
        return true;
    }

    @Override
    public boolean commit() {
        addUserPrincipalToSubject();
        addGroupSubjectsDerivedFromUsername();
        return true;
    }

    private void addGroupSubjectsDerivedFromUsername() {
        String username = nameCallback.getName().toLowerCase();
        char role = username.charAt(1);
        switch (role) {
            case 'u':
                subject.getPrincipals()
                    .add(new GroupPrincipal("user" + "_domain_" + username.charAt(0)));
                break;
            case 'm':
                subject.getPrincipals()
                    .add(new GroupPrincipal("manager" + "_domain_" + username.charAt(0)));
                break;
            case 'e':
                subject.getPrincipals()
                    .add(new GroupPrincipal("businessadmin"));
                break;
            default:
                // necessary for checkstyle
        }
        if (username.length() > 6) {
            subject.getPrincipals().add(new GroupPrincipal("team_" + username.substring(2, 6)));
        }
    }

    private void addUserPrincipalToSubject() {
        subject.getPrincipals().add(new UserPrincipal(nameCallback.getName()));
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
        Map<String, ?> options) {

        this.subject = subject;

        try {
            nameCallback = new NameCallback("prompt");
            passwordCallback = new PasswordCallback("prompt", false);

            callbackHandler.handle(new Callback[] {nameCallback, passwordCallback});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean login() {
        return nameCallback.getName().equals(new String(passwordCallback.getPassword()));
    }

    @Override
    public boolean logout() {
        return true;
    }

}
