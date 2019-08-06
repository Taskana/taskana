package pro.taskana.wildfly.security;

import java.io.IOException;
import java.security.AccessController;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.security.authz.Roles;

import pro.taskana.security.GroupPrincipal;

/**
 * Simple Filter to map all Elytron Roles to JAAS-Principals.
 */
public class ElytronToJaasFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        SecurityIdentity securityIdentity = getSecurityIdentity();
        if (securityIdentity != null) {
            Roles roles = securityIdentity.getRoles();
            Subject subject = obtainSubject(request);
            if (subject != null) {
                if (subject.getPrincipals().size() == 0) {
                    subject.getPrincipals().add(securityIdentity.getPrincipal());
                }
                if (subject.getPrincipals(GroupPrincipal.class).size() == 0) {
                    roles.forEach(role -> subject.getPrincipals().add(new GroupPrincipal(role)));
                }
            }
        }
        chain.doFilter(request, response);
    }

    private SecurityIdentity getSecurityIdentity() {
        SecurityDomain current = SecurityDomain.getCurrent();
        if (current != null) {
            return current.getCurrentSecurityIdentity();
        }
        return null;
    }

    /**
     * <p>
     * Obtains the <code>Subject</code> to run as or <code>null</code> if no <code>Subject</code> is available.
     * </p>
     * <p>
     * The default implementation attempts to obtain the <code>Subject</code> from the <code>SecurityContext</code>'s
     * <code>Authentication</code>. If it is of type <code>JaasAuthenticationToken</code> and is authenticated, the
     * <code>Subject</code> is returned from it. Otherwise, <code>null</code> is returned.
     * </p>
     *
     * @param request
     *            the current <code>ServletRequest</code>
     * @return the Subject to run as or <code>null</code> if no <code>Subject</code> is available.
     */
    protected Subject obtainSubject(ServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (logger.isDebugEnabled()) {
            logger.debug("Attempting to obtainSubject using authentication : " + authentication);
        }
        if (authentication == null) {
            return null;
        }
        if (!authentication.isAuthenticated()) {
            return null;
        }

        return Subject.getSubject(AccessController.getContext());

    }
}
