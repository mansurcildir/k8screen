package io.k8screen.backend;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class K8screenBackendApplication {

  public static void main(final @NotNull String[] args) {
    SpringApplication.run(K8screenBackendApplication.class, args);
  }
}
