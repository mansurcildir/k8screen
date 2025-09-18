package io.k8screen.backend.storage;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public class S3 implements StorageStrategy {

  @Override
  public void upload(final @NotNull InputStream inputStream, final @NotNull String path)
      throws IOException {}

  @Override
  public byte[] download(final @NotNull String path) throws IOException {
    return new byte[0];
  }

  @Override
  public void delete(final @NotNull String path) throws IOException {}
}
