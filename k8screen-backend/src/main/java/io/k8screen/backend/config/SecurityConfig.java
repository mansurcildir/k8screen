package io.k8screen.backend.config;

import io.k8screen.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${k8screen-frontend.login-url}")
  private String loginURL;

  @Value("${k8screen-frontend.success-url}")
  private String apiSuccessURL;

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;

  public SecurityConfig(
      final @NotNull JwtUtil jwtUtil,
      final @NotNull CustomUserDetailsService customUserDetailsService) {
    this.jwtUtil = jwtUtil;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(final @NotNull HttpSecurity http)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(c -> c.configurationSource(this.corsConfigurationSource()))
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        "/api/v1/auth/login/google")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/auth/access-token")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/v1/auth/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          response.setStatus(HttpServletResponse.SC_OK);
                          response.getWriter().write("Logout successful");
                          response.sendRedirect(this.loginURL);
                        }))
        .authenticationProvider(this.authenticationProvider())
        .addFilterBefore(
            new JwtAuthenticationFilter(this.jwtUtil, this.customUserDetailsService),
            UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(
            oauth2 -> {
              oauth2.defaultSuccessUrl(this.apiSuccessURL);
            });

    return http.build();
  }

  @NotNull
  CorsConfigurationSource corsConfigurationSource() {
    final var configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedMethods(
        List.of(
            HttpMethod.GET.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.HEAD.name()));
    configuration.setAllowedHeaders(List.of("*"));

    final var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      final @NotNull AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(this.customUserDetailsService);
    authenticationProvider.setPasswordEncoder(this.passwordEncoder());
    return authenticationProvider;
  }
}
