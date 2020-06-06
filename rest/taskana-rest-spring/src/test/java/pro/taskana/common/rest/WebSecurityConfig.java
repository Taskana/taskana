package pro.taskana.common.rest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pro.taskana.rest.security.SpringSecurityToJaasFilter;

/** Default basic configuration for taskana web example. */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}")
  private String ldapServerUrl;

  @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}")
  private String ldapBaseDn;

  @Value("${taskana.ldap.groupSearchBase:cn=groups}")
  private String ldapGroupSearchBase;

  @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}")
  private String ldapUserDnPatterns;

  @Value("${taskana.ldap.groupSearchFilter:uniqueMember={0}}")
  private String ldapGroupSearchFilter;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new CorsWebMvcConfigurer();
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
        .addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.ldapAuthentication()
        .userDnPatterns(ldapUserDnPatterns)
        .groupSearchBase(ldapGroupSearchBase)
        .ldapAuthoritiesPopulator(authoritiesPopulator())
        .authoritiesMapper(grantedAuthoritiesMapper())
        .contextSource()
        .url(ldapServerUrl + "/" + ldapBaseDn)
        .and()
        .passwordCompare()
        .passwordAttribute("userPassword");
  }

  @Bean
  public LdapAuthoritiesPopulator authoritiesPopulator() {
    Function<Map<String, List<String>>, GrantedAuthority> authorityMapper =
        record -> {
          String role = record.get("spring.security.ldap.dn").get(0);
          return new SimpleGrantedAuthority(role);
        };

    DefaultLdapAuthoritiesPopulator populator =
        new DefaultLdapAuthoritiesPopulator(
            defaultSpringSecurityContextSource(), ldapGroupSearchBase);
    populator.setGroupSearchFilter(ldapGroupSearchFilter);
    populator.setSearchSubtree(true);
    populator.setRolePrefix("");
    populator.setAuthorityMapper(authorityMapper);
    return populator;
  }

  @Bean
  public DefaultSpringSecurityContextSource defaultSpringSecurityContextSource() {
    return new DefaultSpringSecurityContextSource(ldapServerUrl + "/" + ldapBaseDn);
  }

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    SimpleAuthorityMapper grantedAuthoritiesMapper = new SimpleAuthorityMapper();
    grantedAuthoritiesMapper.setPrefix("");
    return grantedAuthoritiesMapper;
  }

  private static class CorsWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins("*");
    }
  }

  private JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
