package io.k8screen.backend.util;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiClientFactory {

  @Value("${k8screen.config.path}")
  private String configPath;

  public @NotNull ApiClient apiClient(final @Nullable String config, final @NotNull UUID userUuid)
      throws IOException {
    return Config.fromConfig(this.configPath + File.separator + userUuid + File.separator + config);
  }

  public @NotNull CoreV1Api coreV1Api(final @Nullable String config, final @NotNull UUID userUuid)
      throws IOException {
    return new CoreV1Api(this.apiClient(config, userUuid));
  }

  public @NotNull AppsV1Api appsV1Api(final @Nullable String config, final @NotNull UUID userUuid)
      throws IOException {
    return new AppsV1Api(this.apiClient(config, userUuid));
  }

  public @NotNull Exec exec(final @Nullable String config, final @NotNull UUID userUuid)
      throws IOException {
    return new Exec(this.apiClient(config, userUuid));
  }
}
