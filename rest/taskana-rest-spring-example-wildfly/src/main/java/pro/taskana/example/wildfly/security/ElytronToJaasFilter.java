package pro.taskana.example.wildfly.security;

import java.io.IOException;
import java.security.AccessController;
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
      if (subject.getPrincipals().size() == 0) {
        subject.getPrincipals().add(securityIdentity.getPrincipal());
      }
      if (subject.getPrincipals(GroupPrincipal.class).size() == 0) {
        roles.forEach(role -> subject.getPrincipals().add(new GroupPrincipal(role)));
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Current JAAS subject after applying Elytron SecurityIdentity: " + subject);
      }
    }
  }

  private Subject obtainSubject() {
    Subject subject = Subject.getSubject(AccessController.getContext());
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
