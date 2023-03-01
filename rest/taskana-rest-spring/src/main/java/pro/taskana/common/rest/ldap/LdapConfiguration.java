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
package pro.taskana.common.rest.ldap;

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
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.bindDn:uid=admin}") String ldapBindDn,
      @Value("${taskana.ldap.bindPassword:secret}") String ldapBindPassword) {
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
