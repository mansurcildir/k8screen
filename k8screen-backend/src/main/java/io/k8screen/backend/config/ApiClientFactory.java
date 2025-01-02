package io.k8screen.backend.config;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiClientFactory {

  @Value("${k8screen.config.path}")
  private String configPath;

  public ApiClient apiClient(final @NotNull String config, final @NotNull String userId)
      throws IOException {
    return Config.fromConfig(this.configPath + File.separator + userId + File.separator + config);
  }

  public CoreV1Api coreV1Api(final @NotNull String config, final @NotNull String userId)
      throws IOException {
    return new CoreV1Api(this.apiClient(config, userId));
  }

  public AppsV1Api appsV1Api(final @NotNull String config, final @NotNull String userId)
      throws IOException {
    return new AppsV1Api(this.apiClient(config, userId));
  }

  public Exec exec(final @NotNull String config, final @NotNull String userId) throws IOException {
    return new Exec(this.apiClient(config, userId));
  }
}
