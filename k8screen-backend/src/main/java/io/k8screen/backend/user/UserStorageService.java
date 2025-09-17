package io.k8screen.backend.user;

import io.k8screen.backend.storage.StorageStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStorageService {

  private static final String AVATAR_SUFFIX = "avatar.png";

  @Value("${k8screen.user.avatar.path}")
  private String avatarPath;

  private final @NotNull StorageStrategy storageStrategy;

  public void uploadAvatar(final @NotNull InputStream inputStream, final @NotNull UUID userUuid)
      throws IOException {

    final String relativePath = this.avatarPath + File.separator + userUuid;

    this.storageStrategy.upload(inputStream, relativePath, AVATAR_SUFFIX);
  }

  public byte[] getAvatar(final @NotNull UUID userUuid) throws IOException {
    final String relativePath = this.avatarPath + File.separator + userUuid;
    return this.storageStrategy.download(relativePath, AVATAR_SUFFIX);
  }
}
