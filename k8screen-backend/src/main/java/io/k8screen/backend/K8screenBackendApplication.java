package io.k8screen.backend;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Slf4j
@SpringBootApplication
@EnableMethodSecurity
@EnableAsync
public class K8screenBackendApplication {

  public K8screenBackendApplication() {
    log.warn("k8screen-app started successfully!");
  }

  public static void main(final @NotNull String[] args) {
    SpringApplication.run(K8screenBackendApplication.class, args);
  }
}
