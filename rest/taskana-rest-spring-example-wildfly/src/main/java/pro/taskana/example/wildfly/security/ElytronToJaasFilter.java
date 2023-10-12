package pro.taskana.example.wildfly.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import javax.security.auth.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.security.authz.Roles;
import pro.taskana.common.api.security.GroupPrincipal;
import pro.taskana.example.wildfly.AdditionalUserProperties;

/** Simple Filter to map all Elytron Roles to JAAS-Principals. */
@Component
public class ElytronToJaasFilter extends GenericFilterBean {

  private static AdditionalUserProperties additionalUserProperties;

  @Autowired
  public void setAdditionalUserProperties(AdditionalUserProperties prop) {
    additionalUserProperties = prop;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    SecurityIdentity securityIdentity = getSecurityIdentity(request);
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

  private SecurityIdentity getSecurityIdentity(ServletRequest request) {
    SecurityDomain current = SecurityDomain.getCurrent();
    SecurityIdentity identity = null;
    if (current != null) {
      if (request instanceof HttpServletRequest) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userId = httpRequest.getHeader("userid");
        Boolean enableUserIdHeader = additionalUserProperties.getEnableUserIdHeader();
        List<String> authorizedUsers = additionalUserProperties.getAuthorizedUsers();
        if (userId != null
            && enableUserIdHeader
            && authorizedUsers.contains(
                current.getCurrentSecurityIdentity().getPrincipal().getName())) {
          identity = current.getCurrentSecurityIdentity().createRunAsIdentity(userId, false);
        } else {
          identity = current.getCurrentSecurityIdentity();
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Current Elytron SecurityIdentity: " + identity);
    }

    return identity;
  }
}
