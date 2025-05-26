package io.k8screen.backend.config;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.util.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
  @Bean
  public ResponseFactory responseFactory(final @NotNull MessageSource messageSource) {
    return new ResponseFactory(messageSource);
  }

  @Bean
  public JwtUtil jwtUtil() {
    return new JwtUtil();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
