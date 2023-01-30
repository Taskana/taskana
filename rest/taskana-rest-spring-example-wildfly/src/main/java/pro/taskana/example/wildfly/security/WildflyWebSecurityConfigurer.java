package pro.taskana.example.wildfly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;

/**
 * Default basic configuration for taskana web example running on Wildfly / JBoss with Elytron or
 * JAAS Security.
 */
@Configuration
public class WildflyWebSecurityConfigurer {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new ElytronToJaasFilter(), JaasApiIntegrationFilter.class)
        .csrf()
        .disable();
    return http.build();
  }

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
