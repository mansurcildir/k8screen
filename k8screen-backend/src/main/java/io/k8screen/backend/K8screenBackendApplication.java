package io.k8screen.backend;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class K8screenBackendApplication {

  public K8screenBackendApplication() {
    log.warn("k8screen-backend started successfully!");
  }

  public static void main(final @NotNull String[] args) {
    SpringApplication.run(K8screenBackendApplication.class, args);
  }
}
