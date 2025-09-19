package io.k8screen.backend.config;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.util.JwtAuthenticationFilter;
import io.k8screen.backend.util.JwtUtil;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${k8screen.success-url}")
  private String apiSuccessURL;

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull OAuthSuccessHandler oAuthSuccessHandler;

  public SecurityConfig(
      final @NotNull JwtUtil jwtUtil,
      final @NotNull ResponseFactory responseFactory,
      final @NotNull OAuthSuccessHandler oAuthSuccessHandler) {
    this.jwtUtil = jwtUtil;
    this.responseFactory = responseFactory;
    this.oAuthSuccessHandler = oAuthSuccessHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      final @NotNull HttpSecurity http, final @NotNull ClientRegistrationRepository repo)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(this::cors)
        .authorizeHttpRequests(this::authorizeHttpRequests)
        .oauth2Login(this::oAuth2Login)
        .oauth2Login(
            oauth2 ->
                oauth2
                    .successHandler(this.oAuthSuccessHandler)
                    .authorizationEndpoint(
                        auth -> auth.authorizationRequestResolver(new AuthRequestResolver(repo))))
        .addFilterBefore(
            new JwtAuthenticationFilter(this.jwtUtil, this.responseFactory),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private void cors(final @NotNull CorsConfigurer<HttpSecurity> corsConfigurer) {
    corsConfigurer.configurationSource(this.configurationSource());
  }

  private @NotNull CorsConfigurationSource configurationSource() {
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

  private void authorizeHttpRequests(
      final @NotNull AuthorizeHttpRequestsConfigurer<HttpSecurity>
                  .AuthorizationManagerRequestMatcherRegistry
              auth) {
    auth.requestMatchers(
            "/auth/google/callback",
            "/v1/auth/password-recovery",
            "/v1/auth/recover-password",
            "/v1/auth/register",
            "/v1/auth/login",
            "/v1/auth/login/google",
            "/v1/auth/refresh",
            "/v1/auth/token/**",
            "/v1/webhooks/**",
            "/ws/**")
        .permitAll()
        .anyRequest()
        .authenticated();
  }

  private void oAuth2Login(final @NotNull OAuth2LoginConfigurer<HttpSecurity> oauth2) {
    oauth2.defaultSuccessUrl(this.apiSuccessURL);
  }
}
