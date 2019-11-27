package pro.taskana.wildfly.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.security.authz.Roles;

import pro.taskana.rest.security.WebSecurityConfig;

/**
 * Default basic configuration for taskana web example running on Wildfly / JBoss with Elytron or JAAS Security.
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class WildflyWebSecurityConfig extends WebSecurityConfig {

    @Value("${devMode:false}")
    private boolean devMode;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(
                "/css/**",
                "/img/**")
            .permitAll()
            .and()
            .csrf()
            .disable()
            .httpBasic()
            .and()
            .authenticationProvider(preauthAuthProvider())
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/docs/**")
            .permitAll()
            .and()
            .addFilter(preAuthFilter())
            .addFilterAfter(new ElytronToJaasFilter(), JaasApiIntegrationFilter.class)
            .addFilter(jaasApiIntegrationFilter());

        if (devMode) {
            http.headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**")
                .permitAll();
        } else {
            addLoginPageConfiguration(http);
        }
    }

    private JaasApiIntegrationFilter jaasApiIntegrationFilter() {
        JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
        filter.setCreateEmptySubject(true);
        return filter;
    }

    @Bean
    public J2eePreAuthenticatedProcessingFilter preAuthFilter() throws Exception {
        J2eePreAuthenticatedProcessingFilter filter = new J2eePreAuthenticatedProcessingFilter();
        filter.setAuthenticationManager(preAuthManager());
        return filter;
    }

    @Bean
    public AuthenticationManager preAuthManager() {
        return new AuthenticationManager() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return preauthAuthProvider().authenticate(authentication);
            }
        };
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
        PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
        preauthAuthProvider.setPreAuthenticatedUserDetailsService(
            authenticationUserDetailsService());
        return preauthAuthProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService() {
        return new AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>() {

            @Override
            public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
                throws UsernameNotFoundException {
                return new UserDetails() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return true;
                    }

                    @Override
                    public String getUsername() {
                        return token.getName();
                    }

                    @Override
                    public String getPassword() {
                        return (String) token.getCredentials();
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        SecurityIdentity securityIdentity = getSecurityIdentity();
                        if (securityIdentity != null) {
                            Roles roles = securityIdentity.getRoles();
                            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                        }
                        return authorities;
                    }

                    private SecurityIdentity getSecurityIdentity() {
                        SecurityDomain current = SecurityDomain.getCurrent();
                        if (current != null) {
                            return current.getCurrentSecurityIdentity();
                        }
                        return null;
                    }
                };
            }
        };
    }

    private void addLoginPageConfiguration(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest()
            .fullyAuthenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .failureUrl("/login?error")
            .defaultSuccessUrl("/")
            .permitAll()
            .and()
            .logout()
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/login?logout")
            .deleteCookies("JSESSIONID")
            .permitAll();
    }
}
