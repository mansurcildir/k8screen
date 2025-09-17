package io.k8screen.backend.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystem implements StorageStrategy {
  private static final int BUFFER_SIZE = 8192;

  @Value("${k8screen.trash.path}")
  private String trashPath;

  @Override
  public void upload(
      final @NotNull InputStream inputStream,
      final @NotNull String path,
      final @NotNull String fileName)
      throws IOException {

    final File parent = new File(path);

    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    final File file = new File(parent, fileName);
    this.uploadFile(inputStream, file);
  }

  @Override
  public void delete(final @NotNull String path, final @NotNull String fileName)
      throws IOException {
    final Path resource = this.getFile(path + File.separator + fileName).toPath();
    final Path trash = this.getTrashDir(this.trashPath, fileName).toPath();

    Files.move(resource, trash, StandardCopyOption.REPLACE_EXISTING);
  }

  private void uploadFile(final @NotNull InputStream inputStream, final @NotNull File file)
      throws IOException {
    try (final OutputStream outputStream = new FileOutputStream(file)) {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    }
  }

  private @NotNull File getFile(final @NotNull String path) throws IOException {
    final File file = new File(path);

    if (!file.exists()) {
      throw new IOException("fileNotFound");
    }

    return file;
  }

  private @NotNull File getTrashDir(final @NotNull String path, final @NotNull String fileName)
      throws IOException {
    final File file = new File(path);

    if (!file.exists() && !file.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    return new File(path, fileName);
  }
}
