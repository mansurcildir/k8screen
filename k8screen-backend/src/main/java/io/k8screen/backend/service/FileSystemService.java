package io.k8screen.backend.service;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FileSystemService {

  @Value("${k8screen.config.path}")
  private String configPath;

  @Value("${k8screen.trash.path}")
  private String trashPath;

  public void uploadConfig(
      final @NotNull InputStream inputStream,
      final @NotNull String filename,
      final @NotNull UUID userUuid)
      throws IOException {

    final File targetDirectory = new File(this.configPath + File.separator + userUuid);
    if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    final File targetFile = new File(targetDirectory, filename);

    try (final OutputStream outputStream = new FileOutputStream(targetFile)) {
      final byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    }
  }

  public void deleteConfig(final @NotNull String fileName, final @NotNull UUID userUuid)
      throws IOException {
    final File targetDirectory = new File(this.configPath + File.separator + userUuid);
    if (!targetDirectory.exists()) {
      throw new IOException("directoryNotFound");
    }

    final File targetFile = new File(targetDirectory, fileName);
    if (!targetFile.exists()) {
      throw new IOException("fileNotFound");
    }

    final File trashDirectory = new File(this.trashPath + File.separator + userUuid);
    if (!trashDirectory.exists() && !trashDirectory.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    final File trashFile = new File(trashDirectory, fileName);
    if (!targetFile.renameTo(trashFile)) {
      throw new IOException();
    }
  }
}
