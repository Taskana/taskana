package pro.taskana.wildfly.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;

/**
 * Default basic configuration for taskana web example running on Wildfly / JBoss with Elytron or
 * JAAS Security.
 */
@EnableWebSecurity
public class WildflyWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new ElytronToJaasFilter(), JaasApiIntegrationFilter.class)
        .csrf()
        .disable();
  }

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }

}
