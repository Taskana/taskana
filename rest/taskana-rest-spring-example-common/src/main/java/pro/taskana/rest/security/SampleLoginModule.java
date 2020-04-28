package pro.taskana.rest.security;

import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.spi.LoginModule;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pro.taskana.common.internal.security.GroupPrincipal;
import pro.taskana.common.internal.security.UserPrincipal;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.rest.resource.AccessIdRepresentationModel;

/** TODO. */
public class SampleLoginModule extends UsernamePasswordAuthenticationFilter implements LoginModule {

  private NameCallback nameCallback;

  private PasswordCallback passwordCallback;

  private Subject subject;

  @Override
  public void initialize(
      Subject subject,
      CallbackHandler callbackHandler,
      Map<String, ?> sharedState,
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
  public boolean commit() {
    addUserPrincipalToSubject();
    addGroupSubjectsDerivedFromUsername();
    return true;
  }

  @Override
  public boolean abort() {
    return true;
  }

  @Override
  public boolean logout() {
    return true;
  }

  private void addGroupSubjectsDerivedFromUsername() {
    LdapCacheTestImpl ldapCacheTest = new LdapCacheTestImpl();
    String username = nameCallback.getName().toLowerCase();
    List<AccessIdRepresentationModel> groups =
        ldapCacheTest.findGroupsOfUser(username, Integer.MAX_VALUE);
    groups.forEach(
        (AccessIdRepresentationModel group) -> {
          if (group.getAccessId().contains("ou=groups")) {
            subject.getPrincipals().add(new GroupPrincipal(group.getName()));
          }
        });
  }

  private void addUserPrincipalToSubject() {
    subject.getPrincipals().add(new UserPrincipal(nameCallback.getName()));
  }
}
