package io.k8screen.backend.service;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
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

  @Value("${k8screen.trash.path}")
  private String trashPath;

  public void uploadConfig(final @NotNull MultipartFile file, final @NotNull UUID userUuid)
      throws IOException {
    final File targetDirectory = new File(this.configPath + File.separator + userUuid);
    if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
      throw new IOException("directoryNotFound");
    }

    final File targetFile =
        new File(targetDirectory, Objects.requireNonNull(file.getOriginalFilename()));
    file.transferTo(targetFile);
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
