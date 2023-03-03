/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.rest;

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
import pro.taskana.common.api.security.GroupPrincipal;
import pro.taskana.common.api.security.UserPrincipal;

/** Simple Filter to map all Spring Security Roles to JAAS-Principals. */
public class SpringSecurityToJaasFilter extends GenericFilterBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityToJaasFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Optional<Authentication> authentication = getCurrentAuthentication();
    if (authentication.isPresent()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Authentication found in Spring security context: {}", authentication);
      }
      obtainSubject()
          .ifPresent(
              subject -> {
                initializeUserPrincipalFromAuthentication(authentication.get(), subject);
                initializeGroupPrincipalsFromAuthentication(authentication.get(), subject);
              });
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "No authentication found in Spring security context. Continuing unauthenticatic.");
      }
    }

    chain.doFilter(request, response);
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
    if (authentication.isEmpty() || !authentication.get().isAuthenticated()) {
      return Optional.empty();
    }

    return Optional.of(Subject.getSubject(AccessController.getContext()));
  }

  Optional<Authentication> getCurrentAuthentication() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
  }

  private void initializeUserPrincipalFromAuthentication(
      Authentication authentication, Subject subject) {
    if (subject.getPrincipals().isEmpty()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Setting the principal of the subject with {}.", authentication.getPrincipal());
      }
      subject
          .getPrincipals()
          .add(new UserPrincipal(((UserDetails) authentication.getPrincipal()).getUsername()));
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Principal of the subject is already set to {}.", subject.getPrincipals());
      }
      throw new SystemException("Finding an existing principal is unexpected. Please investigate.");
    }
  }

  private void initializeGroupPrincipalsFromAuthentication(
      Authentication authentication, Subject subject) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Adding roles {} to subject.", authentication.getAuthorities());
    }

    authentication
        .getAuthorities()
        .forEach(
            grantedAuthority ->
                subject.getPrincipals().add(new GroupPrincipal(grantedAuthority.getAuthority())));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{}", subject.getPublicCredentials(GroupPrincipal.class));
    }
  }
}
