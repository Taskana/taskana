package pro.taskana.rest.security;

import java.io.IOException;
import java.security.AccessController;
import java.util.Optional;
import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.security.GroupPrincipal;
import pro.taskana.common.internal.security.UserPrincipal;

/** Simple Filter to map all Spring Security Roles to JAAS-Principals. */
public class SpringSecurityToJaasFilter extends GenericFilterBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityToJaasFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Optional<Authentication> authentication = getCurrentAuthentication();
    if (authentication.isPresent()) {
      LOGGER.debug("Authentication found in Spring security context: {}", authentication);
      obtainSubject()
          .ifPresent(
              subject -> {
                initializeUserPrincipalFromAuthentication(authentication.get(), subject);
                initializeGroupPrincipalsFromAuthentication(authentication.get(), subject);
              });
    } else {
      LOGGER.debug(
          "No authentication found in Spring security context. Continuing unauthenticatic.");
    }

    chain.doFilter(request, response);
  }

  private void initializeUserPrincipalFromAuthentication(
      Authentication authentication, Subject subject) {
    if (subject.getPrincipals().isEmpty()) {
      LOGGER.debug("Setting the principal of the subject with {}.", authentication.getPrincipal());
      subject
          .getPrincipals()
          .add(new UserPrincipal(((UserDetails) authentication.getPrincipal()).getUsername()));
    } else {
      LOGGER.debug("Principal of the subject is already set to {}.", subject.getPrincipals());
      throw new SystemException("Finding an existing principal is unexpected. Please investigate.");
    }
  }

  private void initializeGroupPrincipalsFromAuthentication(
      Authentication authentication, Subject subject) {

    LOGGER.debug("Adding roles {} to subject.", authentication.getAuthorities());

    authentication
        .getAuthorities()
        .forEach(
            grantedAuthority ->
                subject.getPrincipals().add(new GroupPrincipal(grantedAuthority.getAuthority())));

    LOGGER.debug("{}", subject.getPublicCredentials(GroupPrincipal.class));
  }

  /**
   * Obtains the <code>Subject</code> to run as or <code>null</code> if no <code>Subject</code> is
   * available.
   *
   * <p>The default implementation attempts to obtain the <code>Subject</code> from the <code>
   * SecurityContext</code>'s <code>Authentication</code>. If it is of type <code>
   * JaasAuthenticationToken</code> and is authenticated, the <code>Subject</code> is returned from
   * it.
   *
   * @return the Subject to run.
   */
  protected Optional<Subject> obtainSubject() {
    Optional<Authentication> authentication = getCurrentAuthentication();
    if (logger.isDebugEnabled()) {
      logger.debug("Attempting to obtainSubject using authentication : " + authentication);
    }
    if (!authentication.isPresent()) {
      return Optional.empty();
    }
    if (!authentication.get().isAuthenticated()) {
      return Optional.empty();
    }

    return Optional.of(Subject.getSubject(AccessController.getContext()));
  }

  Optional<Authentication> getCurrentAuthentication() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
  }
}
