package io.k8screen.backend.user;

import io.k8screen.backend.storage.StorageStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserStorageService {

  private static final String AVATAR_SUFFIX = "avatar.png";

  @Value("${k8screen.storage.avatar.path}")
  private String avatarPath;

  private final @NotNull StorageStrategy storageStrategy;

  public void uploadAvatar(final @NotNull InputStream inputStream, final @NotNull UUID userUuid)
      throws IOException {

    final String path =
        this.avatarPath + File.separator + userUuid + File.separator + AVATAR_SUFFIX;
    this.storageStrategy.upload(inputStream, path);
  }

  public byte[] getAvatar(final @NotNull UUID userUuid) {
    try {
      final String path =
          this.avatarPath + File.separator + userUuid + File.separator + AVATAR_SUFFIX;
      return this.storageStrategy.download(path);
    } catch (final IOException e) {
      return new byte[0];
    }
  }
}
