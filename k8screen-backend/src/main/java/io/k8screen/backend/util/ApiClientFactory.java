package io.k8screen.backend.util;

import io.k8screen.backend.exception.ItemNotFoundException;
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
  private static final String FILE_SYSTEM = "file-system";

  @Value("${k8screen.storage.strategy}")
  private String storageStrategy;

  @Value("${k8screen.storage.file-system.route}")
  private String fileSystemRoute;

  @Value("${k8screen.storage.config.path}")
  private String configPath;

  public @NotNull ApiClient apiClient(final @Nullable String config, final @NotNull UUID userUuid)
      throws IOException {
    return Config.fromConfig(
        this.getRoute()
            + File.separator
            + this.configPath
            + File.separator
            + userUuid
            + File.separator
            + config);
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

  private @NotNull String getRoute() {
    if (FILE_SYSTEM.equals(this.storageStrategy)) {
      return this.fileSystemRoute;
    }

    throw new ItemNotFoundException();
  }
}
