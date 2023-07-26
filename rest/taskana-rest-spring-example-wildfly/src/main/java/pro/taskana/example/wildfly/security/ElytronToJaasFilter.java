package pro.taskana.example.wildfly.security;

import java.io.IOException;
import java.security.AccessController;
import java.util.List;
import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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

  private Subject obtainSubject() {
    Subject subject = Subject.getSubject(AccessController.getContext());
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
