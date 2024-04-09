package pro.taskana.rest.test;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Default basic configuration for taskana web example. */
@Configuration
public class TestWebSecurityConfig {

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapUserDnPatterns;
  private final String ldapGroupSearchBase;
  private final String ldapGroupSearchFilter;

  @Autowired
  public TestWebSecurityConfig(
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}") String ldapUserDnPatterns,
      @Value("${taskana.ldap.groupSearchBase:cn=groups}") String ldapGroupSearchBase,
      @Value("${taskana.ldap.groupSearchFilter:uniqueMember={0}}") String ldapGroupSearchFilter,
      @Value("${taskana.ldap.permissionSearchBase:cn=groups}") String ldapPermissionSearchBase,
      @Value("${taskana.ldap.permissionSearchFilter:uniqueMember={0}}")
      String ldapPermissionSearchFilter) {
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapUserDnPatterns = ldapUserDnPatterns;
    this.ldapGroupSearchBase = ldapGroupSearchBase;
    this.ldapGroupSearchFilter = ldapGroupSearchFilter;
  }

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
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }

  @Bean
  public LdapAuthoritiesPopulator authoritiesPopulator(
      DefaultSpringSecurityContextSource contextSource) {
    Function<Map<String, List<String>>, GrantedAuthority> authorityMapper =
        recordVar -> new SimpleGrantedAuthority(recordVar.get("spring.security.ldap.dn").get(0));

    DefaultLdapAuthoritiesPopulator populator =
        new DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase);
    populator.setGroupSearchFilter(ldapGroupSearchFilter);
    populator.setSearchSubtree(true);
    populator.setRolePrefix("");
    populator.setAuthorityMapper(authorityMapper);
    return populator;
  }

  @Bean
  @Primary
  public DefaultSpringSecurityContextSource defaultSpringSecurityContextSource() {
    return new DefaultSpringSecurityContextSource(ldapServerUrl + "/" + ldapBaseDn);
  }

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    SimpleAuthorityMapper grantedAuthoritiesMapper = new SimpleAuthorityMapper();
    grantedAuthoritiesMapper.setPrefix("");
    return grantedAuthoritiesMapper;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(withDefaults())
        .addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class)
        .authorizeHttpRequests(
            authorizeHttpRequests -> authorizeHttpRequests.anyRequest().fullyAuthenticated());
    return http.build();
  }

  @Bean
  AuthenticationManager ldapAuthenticationManager(
      BaseLdapPathContextSource contextSource,
      LdapAuthoritiesPopulator authorities,
      GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    @SuppressWarnings("deprecation")
    LdapPasswordComparisonAuthenticationManagerFactory factory =
        new LdapPasswordComparisonAuthenticationManagerFactory(
            contextSource, NoOpPasswordEncoder.getInstance());
    factory.setUserDnPatterns(ldapUserDnPatterns);
    factory.setLdapAuthoritiesPopulator(authorities);
    factory.setAuthoritiesMapper(grantedAuthoritiesMapper);
    factory.setPasswordAttribute("userPassword");
    return factory.createAuthenticationManager();
  }

  private JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }

  private static class CorsWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins("*");
    }
  }
}
