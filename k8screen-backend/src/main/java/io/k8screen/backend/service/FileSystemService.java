package io.k8screen.backend.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
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
}
