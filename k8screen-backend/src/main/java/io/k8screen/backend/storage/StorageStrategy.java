package io.k8screen.backend.storage;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public interface StorageStrategy {
  void upload(@NotNull InputStream inputStream, @NotNull String path, @NotNull String fileName)
      throws IOException;

  void delete(@NotNull String path, @NotNull String fileName) throws IOException;
}
