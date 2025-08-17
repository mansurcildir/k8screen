package io.k8screen.backend.config;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.util.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {
  @Bean
  public @NotNull ResponseFactory responseFactory(final @NotNull MessageSource messageSource) {
    return new ResponseFactory(messageSource);
  }

  @Bean
  public @NotNull JwtUtil jwtUtil() {
    return new JwtUtil();
  }

  @Bean
  public @NotNull PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
