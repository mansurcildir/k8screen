package io.k8screen.backend.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystem implements StorageStrategy {
  @Value("${k8screen.storage.file-system.route}")
  private String route;

  @Value("${k8screen.storage.trash.path}")
  private String trashPath;

  @Override
  public void upload(final @NotNull String path, final byte[] bytes) throws IOException {

    final File file = new File(this.route + File.separator + path);
    final File parent = file.getParentFile();

    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    this.uploadFile(file, bytes);
  }

  @Override
  public byte[] download(final @NotNull String path) throws IOException {
    final File file = this.getFile(this.route + File.separator + path);
    return Files.readAllBytes(file.toPath());
  }

  @Override
  public void delete(final @NotNull String path) throws IOException {
    final Path resource = this.getFile(this.route + File.separator + path).toPath();
    final Path trash =
        this.getTrashDir(this.route + File.separator + this.trashPath + File.separator + path)
            .toPath();

    Files.move(resource, trash, StandardCopyOption.REPLACE_EXISTING);
  }

  private void uploadFile(final @NotNull File file, final byte[] bytes) throws IOException {
    try (OutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(bytes);
    }
  }

  private @NotNull File getFile(final @NotNull String path) throws IOException {
    final File file = new File(path);

    if (!file.exists()) {
      throw new IOException("fileNotFound");
    }

    return file;
  }

  private @NotNull File getTrashDir(final @NotNull String path) throws IOException {
    final File file = new File(path);
    final File parent = file.getParentFile();

    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    return file;
  }
}
