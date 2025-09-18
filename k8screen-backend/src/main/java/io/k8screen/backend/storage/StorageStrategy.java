package io.k8screen.backend.storage;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public interface StorageStrategy {
  void upload(@NotNull InputStream inputStream, @NotNull String path) throws IOException;

  byte[] download(@NotNull String path) throws IOException;

  void delete(@NotNull String relativePath) throws IOException;
}
