package pro.taskana.common.rest.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/** Configuration for Ldap access. */
@Configuration
public class LdapConfiguration {

  private final Environment env;
  @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}")
  private String ldapServerUrl;
  @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}")
  private String ldapBaseDn;
  @Value("${taskana.ldap.bindDn:uid=admin}")
  private String ldapBindDn;
  @Value("${taskana.ldap.bindPassword:secret}")
  private String ldapBindPassowrd;

  public LdapConfiguration(Environment env) {
    this.env = env;
  }

  @Bean
  public LdapContextSource ldapContextSource() {

    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl(ldapServerUrl);
    contextSource.setBase(ldapBaseDn);
    contextSource.setUserDn(ldapBindDn);
    contextSource.setPassword(ldapBindPassowrd);
    return contextSource;
  }

  @Bean(name = "ldapTemplate")
  public LdapTemplate getActiveLdapTemplate() {
    return new LdapTemplate(ldapContextSource());
  }
}
