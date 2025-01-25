package io.k8screen.backend.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FileSystemService {

  @Value("${k8screen.config.path}")
  private String configPath;

  public void uploadConfig(final @NotNull MultipartFile file, final @NotNull String userId)
      throws IOException {
    final File targetDirectory = new File(this.configPath + File.separator + userId);
    if (!targetDirectory.exists()) {
      final boolean created = targetDirectory.mkdirs();
      if (!created) {
        throw new IOException();
      }
    }

    final File targetFile =
        new File(targetDirectory, Objects.requireNonNull(file.getOriginalFilename()));
    file.transferTo(targetFile);
  }

  public void deleteConfig(final @NotNull String fileName, final @NotNull String userId)
          throws IOException {
    final File targetDirectory = new File(this.configPath + File.separator + userId);
    if (!targetDirectory.exists()) {
      throw new IOException("Directory not found for user: " + userId);
    }

    final File targetFile = new File(targetDirectory, fileName);
    if (!targetFile.exists()) {
      throw new IOException("File not found: " + fileName);
    }

    final boolean deleted = targetFile.delete();
    if (!deleted) {
      throw new IOException("Failed to delete file: " + fileName);
    }
  }

}
