package pro.taskana.example.boot.security;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pro.taskana.common.rest.SpringSecurityToJaasFilter;

/** Default basic configuration for taskana web example. */
@Configuration
public class BootWebSecurityConfigurer {

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapUserDnPatterns;
  private final String ldapGroupSearchBase;
  private final String ldapGroupSearchFilter;
  private final String ldapPermissionSearchBase;
  private final String ldapPermissionSearchFilter;

  private final boolean devMode;
  private final boolean enableCsrf;

  public BootWebSecurityConfigurer(
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}") String ldapUserDnPatterns,
      @Value("${taskana.ldap.groupSearchBase:cn=groups}") String ldapGroupSearchBase,
      @Value("${taskana.ldap.groupSearchFilter:uniqueMember={0}}") String ldapGroupSearchFilter,
      @Value("${taskana.ldap.permissionSearchBase:cn=permissions}") String ldapPermissionSearchBase,
      @Value("${taskana.ldap.permissionSearchFilter:uniqueMember={0}}")
      String ldapPermissionSearchFilter,
      @Value("${enableCsrf:false}") boolean enableCsrf,
      @Value("${devMode:false}") boolean devMode) {
    this.enableCsrf = enableCsrf;
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapGroupSearchBase = ldapGroupSearchBase;
    this.ldapGroupSearchFilter = ldapGroupSearchFilter;
    this.ldapPermissionSearchBase = ldapPermissionSearchBase;
    this.ldapPermissionSearchFilter = ldapPermissionSearchFilter;
    this.ldapUserDnPatterns = ldapUserDnPatterns;
    this.devMode = devMode;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers("/css/**", "/img/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/docs/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET, "/api-docs", "/api-docs/**", "swagger-ui", "/swagger-ui/**")
                    .permitAll())
        .cors(Customizer.withDefaults())
        .addFilter(jaasApiIntegrationFilter())
        .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class);

    if (enableCsrf) {
      CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
      csrfTokenRepository.setCookiePath("/");
      http.csrf(
              csrf ->
                  csrf.csrfTokenRepository(csrfTokenRepository)
                      .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
          .addFilterAfter(new CsrfCookieFilter(), SpringSecurityToJaasFilter.class);
    } else {
      http.csrf(AbstractHttpConfigurer::disable).httpBasic(Customizer.withDefaults());
    }

    if (devMode) {
      http.headers(
              headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
          .authorizeHttpRequests(
              authorizeHttpRequests ->
                  authorizeHttpRequests
                      .requestMatchers("/h2-console/**")
                      .permitAll()
                      .anyRequest()
                      .fullyAuthenticated())
          .logout(logout -> logout.logoutSuccessUrl("http://localhost:4200/#").permitAll());
    } else {
      addLoginPageConfiguration(http);
    }
    http.requestCache(RequestCacheConfigurer::disable);
    return http.build();
  }

  protected void addLoginPageConfiguration(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeHttpRequests -> authorizeHttpRequests.anyRequest().fullyAuthenticated())
        .formLogin(
            formLogin ->
                formLogin
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    .defaultSuccessUrl("/index.html")
                    .permitAll())
        .logout(
            logout ->
                logout
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .deleteCookies("JSESSIONID")
                    .permitAll());
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
  public DefaultSpringSecurityContextSource defaultSpringSecurityContextSource() {
    return new DefaultSpringSecurityContextSource(ldapServerUrl + "/" + ldapBaseDn);
  }

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    SimpleAuthorityMapper grantedAuthoritiesMapper = new SimpleAuthorityMapper();
    grantedAuthoritiesMapper.setPrefix("");
    return grantedAuthoritiesMapper;
  }

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }

  @Bean
  AuthenticationManager ldapAuthenticationManager(
      BaseLdapPathContextSource contextSource, LdapAuthoritiesPopulator authorities) {
    @SuppressWarnings("deprecation")
    LdapPasswordComparisonAuthenticationManagerFactory factory =
        new LdapPasswordComparisonAuthenticationManagerFactory(
            contextSource, NoOpPasswordEncoder.getInstance());
    factory.setUserDnPatterns(ldapUserDnPatterns);
    factory.setLdapAuthoritiesPopulator(authorities);
    factory.setAuthoritiesMapper(grantedAuthoritiesMapper());
    factory.setPasswordAttribute("userPassword");
    return factory.createAuthenticationManager();
  }
}
