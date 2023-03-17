package pro.taskana.example.wildfly.security;

import java.io.IOException;
import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.security.authz.Roles;
import pro.taskana.common.api.security.GroupPrincipal;

/** Simple Filter to map all Elytron Roles to JAAS-Principals. */
public class ElytronToJaasFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    SecurityIdentity securityIdentity = getSecurityIdentity();
    if (securityIdentity != null) {
      applySecurityIdentityToSubject(securityIdentity);
    }
    chain.doFilter(request, response);
  }

  private void applySecurityIdentityToSubject(SecurityIdentity securityIdentity) {
    Roles roles = securityIdentity.getRoles();
    Subject subject = obtainSubject();
    if (subject != null) {
      if (subject.getPrincipals().isEmpty()) {
        subject.getPrincipals().add(securityIdentity.getPrincipal());
      }
      if (subject.getPrincipals(GroupPrincipal.class).isEmpty()) {
        roles.forEach(role -> subject.getPrincipals().add(new GroupPrincipal(role)));
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Current JAAS subject after applying Elytron SecurityIdentity: " + subject);
      }
    }
  }

  @SuppressWarnings("removal")
  private Subject obtainSubject() {
    // TODO replace with Subject.current() when migrating to newer Version then 17
    Subject subject = Subject.getSubject(java.security.AccessController.getContext());
    if (logger.isDebugEnabled()) {
      logger.debug("Current JAAS subject: " + subject);
    }
    return subject;
  }

  private SecurityIdentity getSecurityIdentity() {
    SecurityDomain current = SecurityDomain.getCurrent();
    SecurityIdentity identity = null;
    if (current != null) {
      identity = current.getCurrentSecurityIdentity();
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Current Elytron SecurityIdentity: " + identity);
    }

    return identity;
  }
}
