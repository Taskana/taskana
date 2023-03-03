/*-
 * #%L
 * pro.taskana:taskana-rest-spring-example-wildfly
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
