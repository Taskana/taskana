package pro.taskana.rest.security;

import java.util.Collections;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import org.springframework.security.authentication.jaas.JaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.JaasNameCallbackHandler;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Default basic configuration for taskana web example. */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public JaasAuthenticationProvider jaasAuthProvider() {
    JaasAuthenticationProvider authenticationProvider = new JaasAuthenticationProvider();
    authenticationProvider.setAuthorityGranters(
        new AuthorityGranter[] {p -> Collections.singleton(p.getName())});
    authenticationProvider.setCallbackHandlers(
        new JaasAuthenticationCallbackHandler[] {
          new JaasNameCallbackHandler(), new JaasPasswordCallbackHandler()
        });
    authenticationProvider.setLoginContextName("taskana");
    authenticationProvider.setLoginConfig(new ClassPathResource("pss_jaas.config"));
    return authenticationProvider;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {

      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addAllowedMethod("POST");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .and()
        .csrf()
        .disable()
        .httpBasic()
        .and()
        .authenticationProvider(jaasAuthProvider())
        .authorizeRequests()
        .and()
        .addFilter(new JaasApiIntegrationFilter())
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated();
  }
}
