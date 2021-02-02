package pro.taskana.example.boot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import pro.taskana.common.rest.SpringSecurityToJaasFilter;

/** Default basic configuration for taskana web example. */
@EnableWebSecurity
public class BootWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final LdapAuthoritiesPopulator ldapAuthoritiesPopulator;
  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapGroupSearchBase;
  private final String ldapUserDnPatterns;

  private final boolean devMode;

  public BootWebSecurityConfigurer(
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.groupSearchBase:cn=groups}") String ldapGroupSearchBase,
      @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}") String ldapUserDnPatterns,
      LdapAuthoritiesPopulator ldapAuthoritiesPopulator,
      GrantedAuthoritiesMapper grantedAuthoritiesMapper,
      @Value("${devMode:false}") boolean devMode) {
    this.ldapAuthoritiesPopulator = ldapAuthoritiesPopulator;
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapGroupSearchBase = ldapGroupSearchBase;
    this.ldapUserDnPatterns = ldapUserDnPatterns;
    this.devMode = devMode;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.ldapAuthentication()
        .userDnPatterns(ldapUserDnPatterns)
        .groupSearchBase(ldapGroupSearchBase)
        .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator)
        .authoritiesMapper(grantedAuthoritiesMapper)
        .contextSource()
        .url(ldapServerUrl + "/" + ldapBaseDn)
        .and()
        .passwordCompare()
        .passwordAttribute("userPassword");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .antMatchers("/css/**", "/img/**")
        .permitAll()
        .and()
        .csrf()
        .disable()
        .httpBasic()
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/docs/**")
        .permitAll()
        .and()
        .addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class);

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

  protected void addLoginPageConfiguration(HttpSecurity http) throws Exception {
    http.authorizeRequests()
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

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
