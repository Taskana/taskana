package io.kadai.common.rest.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/** Configuration for Ldap access. */
@Configuration
public class LdapConfiguration {

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapBindDn;
  private final String ldapBindPassword;

  public LdapConfiguration(
      @Value("${kadai.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${kadai.ldap.baseDn:OU=Test,O=KADAI}") String ldapBaseDn,
      @Value("${kadai.ldap.bindDn:uid=admin}") String ldapBindDn,
      @Value("${kadai.ldap.bindPassword:secret}") String ldapBindPassword) {
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapBindDn = ldapBindDn;
    this.ldapBindPassword = ldapBindPassword;
  }

  @Bean
  @ConditionalOnMissingBean(LdapContextSource.class)
  public LdapContextSource ldapContextSource() {
    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl(ldapServerUrl);
    contextSource.setBase(ldapBaseDn);
    contextSource.setUserDn(ldapBindDn);
    contextSource.setPassword(ldapBindPassword);
    return contextSource;
  }

  @Bean
  @ConditionalOnMissingBean(LdapTemplate.class)
  public LdapTemplate ldapTemplate(LdapContextSource ldapContextSource) {
    return new LdapTemplate(ldapContextSource);
  }
}
