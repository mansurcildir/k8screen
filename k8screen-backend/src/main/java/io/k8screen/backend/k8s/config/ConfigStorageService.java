package io.k8screen.backend.k8s.config;

import io.k8screen.backend.storage.StorageStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigStorageService {

  @Value("${k8screen.storage.config.path}")
  private String configPath;

  private final @NotNull StorageStrategy storageStrategy;

  public void uploadConfig(
      final @NotNull InputStream inputStream,
      final @NotNull String filename,
      final @NotNull UUID userUuid)
      throws IOException {
    final byte[] fileBytes = inputStream.readAllBytes();

    final String relativePath =
        this.configPath + File.separator + userUuid + File.separator + filename;
    this.storageStrategy.upload(relativePath, fileBytes);
  }

  public void deleteConfig(final @NotNull String fileName, final @NotNull UUID userUuid)
      throws IOException {
    final String relativePath =
        this.configPath + File.separator + userUuid + File.separator + fileName;
    this.storageStrategy.delete(relativePath);
  }
}
