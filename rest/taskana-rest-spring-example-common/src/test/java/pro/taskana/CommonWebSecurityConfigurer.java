package pro.taskana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;

import pro.taskana.common.rest.SpringSecurityToJaasFilter;

@EnableWebSecurity
public class CommonWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final LdapAuthoritiesPopulator ldapAuthoritiesPopulator;
  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapGroupSearchBase;
  private final String ldapUserDnPatterns;

  @Autowired
  public CommonWebSecurityConfigurer(
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.groupSearchBase:cn=groups}") String ldapGroupSearchBase,
      @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}") String ldapUserDnPatterns,
      LdapAuthoritiesPopulator ldapAuthoritiesPopulator,
      GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapGroupSearchBase = ldapGroupSearchBase;
    this.ldapUserDnPatterns = ldapUserDnPatterns;
    this.ldapAuthoritiesPopulator = ldapAuthoritiesPopulator;
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
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
        .and()
        .csrf()
        .disable()
        .httpBasic()
        .and()
        .addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated();
  }

  private JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
