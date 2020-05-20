package pro.taskana.common.rest.ldap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Configuration for Ldap access.
 */
@Configuration
public class LdapConfiguration {

  private final Environment env;

  public LdapConfiguration(Environment env) {
    this.env = env;
  }

  @Bean
  public LdapContextSource contextSource() {

    LdapContextSource contextSource = new LdapContextSource();
    boolean useLdap;
    String useLdapConfigValue = env.getProperty("taskana.ldap.useLdap");
    if (useLdapConfigValue == null || useLdapConfigValue.isEmpty()) {
      useLdap = false;
    } else {
      useLdap = Boolean.parseBoolean(useLdapConfigValue);
    }
    if (useLdap) {
      contextSource.setUrl(env.getRequiredProperty("taskana.ldap.serverUrl"));
      contextSource.setBase(env.getRequiredProperty("taskana.ldap.baseDn"));
      contextSource.setUserDn(env.getRequiredProperty("taskana.ldap.bindDn"));
      contextSource.setPassword(env.getRequiredProperty("taskana.ldap.bindPassword"));
    } else {
      contextSource.setUrl("ldap://localhost:9999");
      contextSource.setBase("o=taskana");
      contextSource.setUserDn("user");
      contextSource.setPassword("secret");
    }
    return contextSource;
  }

  @Bean(name = "ldapTemplate")
  @Conditional(WithLdapCondition.class)
  public LdapTemplate getActiveLdapTemplate() {
    return new LdapTemplate(contextSource());
  }

  /** Helper class to control conditional provision of LdapTemplate. */
  public static class WithLdapCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      String useLdap =
          context.getEnvironment().getProperty(LdapSettings.TASKANA_LDAP_USE_LDAP.getKey());
      if (useLdap == null || useLdap.isEmpty()) {
        return false;
      } else {
        return Boolean.parseBoolean(useLdap);
      }
    }
  }
}
